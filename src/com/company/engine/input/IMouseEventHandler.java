package com.company.engine.input;

import com.company.engine.Window;

public interface IMouseEventHandler {
     default void onCursorPositionCallback(Window window, MouseInput mouseInput, double posX, double posY) {}
     default void onMouseEnter(Window window, MouseInput mouseInput, boolean entered) {}
     default void onMouseButtonPress(Window window, MouseInput mouseInput, int button, int action, int mode) {}
}
