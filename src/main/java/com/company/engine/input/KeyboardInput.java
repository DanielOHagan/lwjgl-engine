package com.company.engine.input;

import com.company.engine.window.Window;

import static org.lwjgl.glfw.GLFW.*;

public class KeyboardInput {

    /**
     * NOT IMPLEMENTED SO THERE IS NO REASON TO USE THIS
     */

    private boolean mAcceptingInput;

    public KeyboardInput() {
        mAcceptingInput = true;
    }

    public void init(Window window) {

    }

    public void input(Window window) {

    }

    public boolean isKeyPressed(long windowHandle, int key) {
        return glfwGetKey(windowHandle, key) == GLFW_PRESS;
    }

    public void setAcceptingInput(boolean acceptingInput) {
        mAcceptingInput = acceptingInput;
    }

    public boolean isAcceptingInput() {
        return mAcceptingInput;
    }
}