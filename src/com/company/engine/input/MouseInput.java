package com.company.engine.input;

import com.company.engine.Window;
import org.joml.Vector2d;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {

    private final Vector2d mPreviousPosition, mCurrentPosition;
    private final Vector2f mDisplayVector;

    private IMouseEventHandler mMouseEventHandler = new IMouseEventHandler() {};
    private boolean mInWindow = false;
    private boolean mLeftBtnPressed = false;
    private boolean mRightBtnPressed = false;

    public MouseInput() {
        mPreviousPosition = new Vector2d(-1, -1);
        mCurrentPosition = new Vector2d(0, 0);
        mDisplayVector = new Vector2f();
    }

    public void init(Window window) {
        //set the mouse event listeners to the default IMouseEventHandler methods
        glfwSetCursorPosCallback(window.getWindowHandle(), (windowHandle, posX, posY) ->
                mMouseEventHandler.onCursorPositionCallback(window, this, posX, posY)
        );

        glfwSetCursorEnterCallback(window.getWindowHandle(), (windowHandle, entered) ->
                mMouseEventHandler.onMouseEnter(window, this, entered)
        );

        glfwSetMouseButtonCallback(window.getWindowHandle(), (windowHandle, button, action, mode) ->
                mMouseEventHandler.onMouseButtonPress(window, this, button, action, mode)
        );
    }

    public void input(Window window) {

    }

    public Vector2d getPreviousPosition() {
        return mPreviousPosition;
    }

    public Vector2d getCurrentPosition() {
        return mCurrentPosition;
    }

    public Vector2f getDisplayVector() {
        return mDisplayVector;
    }

    public IMouseEventHandler getIMouseInput() {
        return mMouseEventHandler;
    }

    public void setIMouseInput(IMouseEventHandler mIMouseInput) {
        this.mMouseEventHandler = mIMouseInput;
    }

    public boolean isInWindow() {
        return mInWindow;
    }

    public void setInWindow(boolean mInWindow) {
        this.mInWindow = mInWindow;
    }

    public boolean isLeftBtnPressed() {
        return mLeftBtnPressed;
    }

    public void setLeftBtnPressed(boolean mLeftBtnPressed) {
        this.mLeftBtnPressed = mLeftBtnPressed;
    }

    public boolean isRightBtnPressed() {
        return mRightBtnPressed;
    }

    public void setRightBtnPressed(boolean mRightBtnPressed) {
        this.mRightBtnPressed = mRightBtnPressed;
    }
}
