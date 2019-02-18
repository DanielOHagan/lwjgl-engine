package com.company;

import com.company.engine.GameEngine;
import com.company.engine.IGameLogic;
import com.company.engine.Window;
import com.company.game.TestGame;

public class Main {

    public static void main(String[] args) {
        try {
            IGameLogic gameLogic = new TestGame();
            Window.WindowOptions options = new Window.WindowOptions();
            //options.showMeshLines = true;
            GameEngine gameEngine = new GameEngine(
                    "Game",
                    "",
                    Window.WindowMode.WINDOWED,
                    true,
                    options,
                    gameLogic
            );
            gameEngine.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}