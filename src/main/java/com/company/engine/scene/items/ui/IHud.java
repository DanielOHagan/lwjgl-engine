package com.company.engine.scene.items.ui;

import com.company.engine.IUsesResources;
import com.company.engine.scene.items.GameItem;

public interface IHud extends IUsesResources {

    /*
    Each Game needs to define its own HUD which implements this Interface
     */

    GameItem[] getGameItems();

    @Override
    default void cleanUp() {
        for (GameItem gameItem : getGameItems()) {
            gameItem.cleanUp();
        }
    }
}