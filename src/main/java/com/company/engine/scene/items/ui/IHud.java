package com.company.engine.scene.items.ui;

import com.company.engine.scene.items.GameItem;

public interface IHud {

    /*
    Each Game needs to define its own HUD which implements this Interface
     */

    GameItem[] getGameItems();

    default void cleanUp() {
        for (GameItem gameItem : getGameItems()) {
            gameItem.cleanUp();
        }
    }
}