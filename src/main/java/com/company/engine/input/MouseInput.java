package com.company.engine.input;

import com.company.engine.window.Window;
import org.joml.Vector2d;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {

    private final Vector2d mPreviousPosition;
    private final Vector2d mCurrentPosition;
    private final Vector2f mDisplayVector;

    private boolean mAcceptingInput;
    private boolean mInWindow;
    private boolean mLeftBtnPressed;
    private boolean mRightBtnPressed;

    public MouseInput() {
        mAcceptingInput = true;
        mPreviousPosition = new Vector2d(-1, -1);
        mCurrentPosition = new Vector2d(0, 0);
        mDisplayVector = new Vector2f();
        mInWindow = false;
        mLeftBtnPressed = false;
        mRightBtnPressed = false;
    }

    public void init(Window window) {
        setUpInputCallbacks(window);
    }

    private void setUpInputCallbacks(Window window) {
        glfwSetMouseButtonCallback(window.getWindowHandle(), (windowHandle, button, action, mods) -> {
            mLeftBtnPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
            mRightBtnPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
        });
        glfwSetCursorEnterCallback(window.getWindowHandle(), (windowHandle, entered) -> {
            mInWindow = entered;
        });
        glfwSetCursorPosCallback(window.getWindowHandle(), (windowHandle, posX, posY) -> {
            mCurrentPosition.x = posX;
            mCurrentPosition.y = posY;
        });
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

    public boolean isInWindow() {
        return mInWindow;
    }

    public void setInWindow(boolean mInWindow) {
        this.mInWindow = mInWindow;
    }

    public boolean isLeftBtnPressed() {
        return mLeftBtnPressed;
    }

    public boolean isRightBtnPressed() {
        return mRightBtnPressed;
    }

    public boolean isAcceptingInput() {
        return mAcceptingInput;
    }

    public void setAcceptingInput(boolean acceptingInput) {
        mAcceptingInput = acceptingInput;
    }
}