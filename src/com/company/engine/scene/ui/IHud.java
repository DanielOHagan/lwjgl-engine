package com.company.engine.scene.ui;

import com.company.engine.scene.items.GameItem;

public interface IHud {

    /*
    What a HUD needs to implement to be rendered by the engine
    Each Game needs to define its own HUD
     */

    GameItem[] getGameItems();

    default void cleanUp() {
        for (GameItem gameItem : getGameItems()) {
            gameItem.cleanUp();
        }
    }
}
