package com.company.engine;

import com.company.engine.input.KeyboardInput;
import com.company.engine.input.MouseInput;
import com.company.engine.window.Window;

public interface IGameLogic {
    void init(Window window) throws Exception;
    void input(Window window, MouseInput mouseInput, KeyboardInput keyboardInput);
    void update(float interval, MouseInput mouseInput, KeyboardInput keyboardInput);
    void render(Window window);
    void cleanUp();
}