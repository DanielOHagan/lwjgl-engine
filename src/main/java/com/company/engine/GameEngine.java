package com.company.engine;

import com.company.engine.input.KeyboardInput;
import com.company.engine.input.MouseInput;
import com.company.engine.window.Window;
import com.company.engine.window.WindowMode;
import com.company.engine.window.WindowOptions;

public class GameEngine implements Runnable {

    private static final int TARGET_FPS = 120;
    private static final int TARGET_UPS = 30;

    private final Window mWindow;
    private final Thread mGameLoopThread;
    private final Timer mTimer;
    private final IGameLogic mGameLogic;
    private final MouseInput mMouseInput;
    private final KeyboardInput mKeyboardInput;

    private double mLastFpsTimeMillis;
    private int mFps;

    public GameEngine(
            String windowTitle,
            String windowIconPath,
            WindowMode windowMode,
            WindowOptions options,
            IGameLogic gameLogic
    ) throws Exception {
        this(windowTitle, windowIconPath, 0, 0, windowMode, options, gameLogic);
    }

    public GameEngine(
            String windowTitle,
            WindowMode windowMode,
            WindowOptions options,
            IGameLogic gameLogic
    ) throws Exception {
        this(windowTitle, "", 0, 0, windowMode, options, gameLogic);
    }

    public GameEngine(
            String windowTitle,
            int width,
            int height,
            WindowMode windowMode,
            WindowOptions options,
            IGameLogic gameLogic
    ) throws Exception {
        this(windowTitle, "", width, height, windowMode, options, gameLogic);
    }

    public GameEngine(
            String windowTitle,
            String windowIconPath,
            int width,
            int height,
            WindowMode windowMode,
            WindowOptions options,
            IGameLogic gameLogic
    ) throws Exception {
        mGameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
        mWindow = new Window(windowTitle, windowIconPath, width, height, windowMode, options);
        mGameLogic = gameLogic;
        mTimer = new Timer();
        mMouseInput = new MouseInput();
        mKeyboardInput = new KeyboardInput();
    }

    public void start() {
        String osName = System.getProperty("os.name");

        //OSX doesn't work great with GLFW and some GLFW functions can only run on the Main Thread on OSX
        if (osName.contains("Mac")) {
            mGameLoopThread.run();
        } else {
            mGameLoopThread.start();
        }
    }

    @Override
    public void run() {
        try {
            init();
            gameLoop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mGameLogic.cleanUp();
        }
    }

    private void init() throws Exception {
        mWindow.init();
        mTimer.init();
        mLastFpsTimeMillis = mTimer.getTime();
        mFps = 0;
        mMouseInput.init(mWindow);
        mKeyboardInput.init(mWindow);
        mGameLogic.init(mWindow);
    }

    private void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;
        boolean running = true;

        while (running && !mWindow.shouldWindowClose()) {
            elapsedTime = mTimer.getElapsedTime();
            accumulator += elapsedTime;

            input();

            while(accumulator >= interval) {
                update(interval);
                accumulator -= interval;
            }

            render();

            if (!mWindow.isVSyncEnabled()) {
                sync();
            }
        }
    }

    private void sync() {
        float loopSlot = 1f / TARGET_FPS; //max amount of time for how long each loop cycle should last
        double endTime = mTimer.getLastLoopTime() + loopSlot; //when this loop cycle should end

        while (mTimer.getTime() < endTime) { //if the end of the slot has not been reached then put the thread to sleep
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void input() {
        mMouseInput.input(mWindow);
        mKeyboardInput.input(mWindow);
        mGameLogic.input(mWindow, mMouseInput, mKeyboardInput);
    }

    private void update(float interval) {
        mGameLogic.update(interval, mMouseInput, mKeyboardInput);
    }

    private void render() {
        if (mWindow.getOptions().showFps && mTimer.getLastLoopTime() - mLastFpsTimeMillis > 1) {
            mLastFpsTimeMillis = mTimer.getLastLoopTime();
            mWindow.showFps(mFps);
            mFps = 0;
        }
        mFps++;
        mGameLogic.render(mWindow);
        mWindow.render();
    }
}