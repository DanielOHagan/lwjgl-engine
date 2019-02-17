package com.company.engine;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private static final int WINDOW_DEFAULT_WIDTH = 800;
    private static final int WINDOW_DEFAULT_HEIGHT = 600;
    private static final int WINDOW_MIN_WIDTH = 200;
    private static final int WINDOW_MIN_HEIGHT = 200;

    private final String WINDOW_TITLE; //the text that will display at the top of the window
    private final String WINDOW_ICON_PATH; //the path to the window icon resource

    private int mWidth;
    private int mHeight;
    private long mWindowHandle; //this window is referenced using this
    private long mCurrentMonitor; //the current monitor is referenced using this, set to primary monitor by default
    private WindowMode mWindowMode;
    private boolean mResized;
    private boolean mVSync;
    private boolean mFullscreen;
    private boolean mMaximised;
    private boolean mFocused;
    private boolean mCursorLock; //TODO: Add FPS counter to HUD, and cursor locking capabilities
    private WindowOptions mOptions;

    public Window(
            String title,
            String windowIconPath,
            int width,
            int height,
            WindowMode windowMode,
            boolean vSync,
            WindowOptions options
    ) {
        WINDOW_TITLE = title;
        WINDOW_ICON_PATH = windowIconPath;
        mWidth = width;
        mHeight = height;
        mWindowMode = windowMode;
        mFullscreen = mWindowMode == WindowMode.FULLSCREEN;
        mMaximised = false;
        mVSync = vSync;
        mOptions = options;
    }

    public void init() {
        //setup error callback to System.err
        GLFWErrorCallback.createPrint(System.err).set();

        //initialise GLFW, most of GLFW do not work without this
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialise GLFW");
        }

        mCurrentMonitor = glfwGetPrimaryMonitor();

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        if (mOptions.compatibleProfile) {
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
        } else {
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        }

        preConfigureWindow();

        //create the window
        mWindowHandle = glfwCreateWindow(
                mWidth,
                mHeight,
                WINDOW_TITLE,
                mFullscreen ? mCurrentMonitor : NULL,
                NULL
        );
        if (mWindowHandle == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        postConfigureWindow();

        //make the window visible
        glfwShowWindow(mWindowHandle);

        GL.createCapabilities();

        //set the clear colour (the colour that is show when nothing is rendered)
        setClearColour(0.0f, 0.0f, 0.0f, 0.0f);

        glEnable(GL_DEPTH_TEST);
        if (mOptions.showTriangles) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        }

        //support transparencies
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        if (mOptions.cullFace) {
            glEnable(GL_CULL_FACE);
            glEnable(GL_BACK);
        }
    }

    private void preConfigureWindow() {
        if (mWindowMode == WindowMode.FULLSCREEN) {
            GLFWVidMode vidMode = glfwGetVideoMode(mCurrentMonitor);

            if (vidMode == null) {
                throw new IllegalStateException("Unable to get monitor information");
            }

            setWindowSize(vidMode.width(), vidMode.height());
        }

        //if no size has been specified and window, set it to a default amount
        if (mWidth < WINDOW_MIN_WIDTH || mHeight < WINDOW_MIN_HEIGHT) {
            setWindowSize(WINDOW_DEFAULT_WIDTH, WINDOW_DEFAULT_HEIGHT);
        }
    }

    private void postConfigureWindow() {
        //configure window settings for creation
        GLFWVidMode vidMode = glfwGetVideoMode(mCurrentMonitor);

        if (vidMode == null) {
            throw new IllegalStateException("Unable to get monitor information");
        }

        if (mWindowMode == WindowMode.BORDERLESS_WINDOWED) {
            glfwSetWindowMonitor(
                    mWindowHandle,
                    mCurrentMonitor,
                    0,
                    0,
                    vidMode.width(),
                    vidMode.height(),
                    vidMode.refreshRate()
            );
        }

        if (!mMaximised && !mFullscreen) {
            //centre the window
            glfwSetWindowPos(
                    mWindowHandle,
                    (vidMode.width() - mWidth) / 2,
                    (vidMode.height() - mHeight) / 2
            );
        }

        //set the window icon
        if (WINDOW_ICON_PATH != null) {
            //TODO: do this
        }

        configureWindowCallbacks();

        //make the OpenGL context current
        glfwMakeContextCurrent(mWindowHandle);

        if (mVSync) {
            //enable v-sync
            glfwSwapInterval(1);
        }
    }

    private void configureWindowCallbacks() {
        //setup resize callback
        glfwSetFramebufferSizeCallback(mWindowHandle, (window, width, height) -> {
            mWidth = width;
            mHeight = height;
            mResized = true;
        });

        //setup a key callback. It will be called every time a key is pressed, repeated or released
        glfwSetKeyCallback(mWindowHandle, (window, key, scanCode, action, mods) -> {
            //closes the window when the ESC key is released
            //TODO: add keyboard input support. maybe have each scene control it?
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
        });

        glfwSetWindowFocusCallback(mWindowHandle, (window, focused) -> {
           mFocused = focused;
        });

        glfwSetWindowMaximizeCallback(mWindowHandle, (window, maximised) -> {
            mMaximised = maximised;
        });
    }

    private void updateWindowMode() {
        //change the window mode to display what the current mWindowMode is
        GLFWVidMode vidMode = glfwGetVideoMode(mCurrentMonitor);
        int posX = 0;
        int posY = 0;

        if (vidMode == null) {
            throw new IllegalStateException("Unable to get monitor information");
        }

        switch (mWindowMode) {
            case FULLSCREEN:
                setWindowSize(vidMode.width(), vidMode.height());
                mFullscreen = true;
                mMaximised = true;
                glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
                break;
            case WINDOWED:
                //resize the window to default values
                setWindowSize(WINDOW_DEFAULT_WIDTH, WINDOW_DEFAULT_HEIGHT);
                //centre the window
                posX = (vidMode.width() - mWidth) / 2;
                posY = (vidMode.height() - mHeight) / 2;

                mFullscreen = false;
                mMaximised = false;
                glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);
                break;
            case BORDERLESS_WINDOWED:
                setWindowSize(vidMode.width(), vidMode.height());
                mFullscreen = true;
                mMaximised = true;
                glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
                break;
            default:
                setWindowSize(WINDOW_DEFAULT_WIDTH, WINDOW_DEFAULT_HEIGHT);
                break;
        }

        glfwSetWindowMonitor(
                mWindowHandle,
                mFullscreen ? mCurrentMonitor : NULL,
                posX,
                posY,
                mWidth,
                mHeight,
                vidMode.refreshRate()
        );
    }

    private void setWindowSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public void update() {
        glfwSwapBuffers(mWindowHandle);
        glfwPollEvents();
    }

    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(mWindowHandle, keyCode) == GLFW_PRESS;
    }

    public boolean shouldWindowClose() {
        return glfwWindowShouldClose(mWindowHandle);
    }

    public void setClearColour(float red, float green, float blue, float alpha) {
        glClearColor(red, green, blue, alpha);
    }

    public void setFullscreen(boolean mFullscreen) {
        this.mFullscreen = mFullscreen;
    }

    public void setCurrentMonitor(long mCurrentMonitor) {
        this.mCurrentMonitor = mCurrentMonitor;
    }

    public boolean isFullscreen() {
        return mFullscreen;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public long getWindowHandle() {
        return mWindowHandle;
    }

    public long getCurrentMonitor() {
        return mCurrentMonitor;
    }

    public boolean isResized() {
        return mResized;
    }

    public boolean isVSyncEnabled() {
        return mVSync;
    }

    public static class WindowOptions {
        public boolean cullFace;
        public boolean showTriangles;
        public boolean showFps;
        public boolean compatibleProfile;
        public boolean antialiasing;
        public boolean frustrumCulling;
    }

    public enum WindowMode {
        FULLSCREEN,
        BORDERLESS_WINDOWED,
        WINDOWED
    }
}