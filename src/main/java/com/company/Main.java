package com.company;

import com.company.engine.GameEngine;
import com.company.engine.IGameLogic;
import com.company.engine.window.WindowMode;
import com.company.engine.window.WindowOptions;
import com.company.game.audio.AudioSynthTest;

public class Main {

    public static void main(String[] args) {
        try {
            IGameLogic gameLogic = new AudioSynthTest();


            WindowOptions options = new WindowOptions();

            /*Application should set Window Options here*/

            options.vSync = false;
            options.resizable = true;
            options.showMeshLines = false;
            options.cullFace = true;
            options.showFps = true;

            GameEngine gameEngine = new GameEngine(
                    "Game",
                    WindowMode.WINDOWED,
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