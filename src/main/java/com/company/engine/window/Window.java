package com.company.engine.window;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private static final int WINDOW_DEFAULT_WIDTH = 800;
    private static final int WINDOW_DEFAULT_HEIGHT = 600;

    private final String WINDOW_ICON_PATH; //the path to the window icon resource

    private String mWindowTitle; //the text that will display at the top of the window
    private int mWidth;
    private int mHeight;
    private float mAspectRatio;
    private long mWindowHandle; //this window is referenced using this
    private long mCurrentMonitor; //the current monitor is referenced using this, set to primary monitor by default
    private WindowMode mWindowMode;
    private boolean mResized;
    private boolean mMaximised;
    private boolean mFocused;
    private WindowOptions mOptions;
    private Matrix4f mProjectionMatrix; //holds data to be used to for displaying to this window

    public Window(
            String title,
            String windowIconPath,
            int width,
            int height,
            WindowMode windowMode,
            WindowOptions options
    ) {
        mWindowTitle = title;
        WINDOW_ICON_PATH = windowIconPath;
        mWidth = width;
        mHeight = height;
        mAspectRatio = (float) width / (float) height;
        mWindowMode = windowMode;
        mMaximised = false;
        mProjectionMatrix = new Matrix4f();

        //set Window Options
        mOptions = options;
        mOptions.fullscreen = mWindowMode == WindowMode.FULLSCREEN;
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
        glfwWindowHint(GLFW_RESIZABLE, mOptions.resizable ? GLFW_TRUE : GLFW_FALSE);
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
                mWindowTitle,
                mOptions.fullscreen ? mCurrentMonitor : NULL,
                NULL
        );

        if (mWindowHandle == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        //make the OpenGL context current
        glfwMakeContextCurrent(mWindowHandle);

        GL.createCapabilities();
        configureWindowOptions();
        configureWindowCallbacks();

        //make the window visible
        glfwShowWindow(mWindowHandle);

        //set the window icon
        if (WINDOW_ICON_PATH != null) {
            //TODO: do this
        }

        //set the clear colour (the colour that is show when nothing is rendered)
        setClearColour(0.0f, 0.0f, 0.0f, 0.0f);

        glEnable(GL_DEPTH_TEST);

        //support transparencies
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    private void preConfigureWindow() {
        if (mWindowMode == WindowMode.FULLSCREEN) {
            GLFWVidMode vidMode = glfwGetVideoMode(mCurrentMonitor);

            if (vidMode == null) {
                throw new IllegalStateException("Unable to get monitor information");
            }

            mWidth = vidMode.width();
            mHeight = vidMode.height();
            setWindowSize(mWidth, mHeight);
        }

        //if no size has been specified and window, set it to a default amount
        if (mWidth == 0 || mHeight == 0) {
            setWindowSize(WINDOW_DEFAULT_WIDTH, WINDOW_DEFAULT_HEIGHT);
        }
    }

    public void configureWindowOptions() {
        //configure window settings for creation
        GLFWVidMode vidMode = glfwGetVideoMode(mCurrentMonitor);

        if (vidMode == null) {
            throw new IllegalStateException("Unable to get monitor information");
        }

        //check if min values are larger than max values if both have been set
        if (((mOptions.maxWidth > 0) && (mOptions.minWidth > mOptions.maxWidth)) ||
                (mOptions.maxHeight > 0) && (mOptions.minHeight > mOptions.maxHeight)) {
            throw new IllegalStateException("Min window size value can not be larger than max value");
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

            mWidth = vidMode.width();
            mHeight = vidMode.height();
        }

        if (!mMaximised && !mOptions.fullscreen) {
            //centre the window
            glfwSetWindowPos(
                    mWindowHandle,
                    (vidMode.width() - mWidth) / 2,
                    (vidMode.height() - mHeight) / 2
            );
        }

        //set the min and max size of the window
        glfwSetWindowSizeLimits(
                mWindowHandle,
                mOptions.minWidth <= 0 ? GLFW_DONT_CARE : mOptions.minWidth,
                mOptions.minHeight <= 0 ? GLFW_DONT_CARE : mOptions.minHeight,
                mOptions.maxWidth <= 0 ? GLFW_DONT_CARE : mOptions.maxWidth,
                mOptions.maxHeight <= 0 ? GLFW_DONT_CARE : mOptions.maxHeight
        );

        if (mOptions.vSync) {
            //enable v-sync
            glfwSwapInterval(1);
        }

        //changes the display of models, only use for debugging, looks cool though
        if (mOptions.showMeshLines) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        }

        if (mOptions.cullFace) {
            glEnable(GL_CULL_FACE);
            glEnable(GL_BACK);
        }
    }

    private void configureWindowCallbacks() {
        //setup resize callback
        glfwSetFramebufferSizeCallback(mWindowHandle, (window, width, height) -> {
            mWidth = width;
            mHeight = height;
            mResized = true;
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
                mOptions.fullscreen = true;
                mMaximised = true;
                glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
                break;
            case WINDOWED:
                //resize the window to default values
                setWindowSize(WINDOW_DEFAULT_WIDTH, WINDOW_DEFAULT_HEIGHT);
                //centre the window
                posX = (vidMode.width() - mWidth) / 2;
                posY = (vidMode.height() - mHeight) / 2;

                mOptions.fullscreen = false;
                mMaximised = false;
                glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);
                break;
            case BORDERLESS_WINDOWED:
                setWindowSize(vidMode.width(), vidMode.height());
                mOptions.fullscreen = true;
                mMaximised = true;
                glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
                break;
            default:
                setWindowSize(WINDOW_DEFAULT_WIDTH, WINDOW_DEFAULT_HEIGHT);
                break;
        }

        glfwSetWindowMonitor(
                mWindowHandle,
                mOptions.fullscreen ? mCurrentMonitor : NULL,
                posX,
                posY,
                mWidth,
                mHeight,
                vidMode.refreshRate()
        );
    }

    public void updateProjectionMatrix(float fov, float zNear, float zFar) {
        mAspectRatio = (float) mWidth / (float) mHeight;
        mProjectionMatrix.setPerspective(fov, mAspectRatio, zNear, zFar);
    }

    private void setWindowSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public void render() {
        glfwSwapBuffers(mWindowHandle);
        glfwPollEvents();
    }

    public void showFps(int fps) {
        glfwSetWindowTitle(mWindowHandle, mWindowTitle + " - " + fps);
    }

    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(mWindowHandle, keyCode) == GLFW_PRESS;
    }

    public boolean isKeyReleased(int keyCode) {
        return glfwGetKey(mWindowHandle, keyCode) == GLFW_RELEASE;
    }

    public boolean isMouseButtonPressed(int buttonCode) {
        return glfwGetMouseButton(mWindowHandle, buttonCode) == GLFW_PRESS;
    }

    public boolean isMouseButtonReleased(int buttonCode) {
        return glfwGetMouseButton(mWindowHandle, buttonCode) == GLFW_RELEASE;
    }

    public boolean shouldWindowClose() {
        return glfwWindowShouldClose(mWindowHandle);
    }

    public void setShouldWindowClose(boolean shouldWindowClose) {
        glfwSetWindowShouldClose(mWindowHandle, shouldWindowClose);
    }

    public void setClearColour(float red, float green, float blue, float alpha) {
        glClearColor(red, green, blue, alpha);
    }

    public void setFullscreen(boolean fullscreen) {
        mOptions.fullscreen = fullscreen;
    }

    public void setCurrentMonitor(long currentMonitor) {
        mCurrentMonitor = currentMonitor;
    }

    public boolean isFullscreen() {
        return mOptions.fullscreen;
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
        return mOptions.vSync;
    }

    public WindowMode getWindowMode() {
        return mWindowMode;
    }

    public float getAspectRatio() {
        return mAspectRatio;
    }

    public boolean isFocused() {
        return mFocused;
    }

    public Matrix4f getProjectionMatrix() {
        return mProjectionMatrix;
    }

    public WindowOptions getOptions() {
        return mOptions;
    }
}