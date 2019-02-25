package com.company.game;

import com.company.engine.scene.items.GameItem;
import com.company.engine.scene.items.TextItem;
import com.company.engine.scene.items.ui.IHud;
import com.company.engine.window.Window;

public class TestHud implements IHud {

    private static final int FONT_COLUMN_COUNT = 16;
    private static final int FONT_ROW_COUNT = 16;
    private static final String FONT_TEXTURE = "/textures/font_texture.png";

    private final GameItem[] mGameItems;
    private final TextItem mTestTextItem;

    public TestHud(String text) throws Exception {
        mTestTextItem = new TextItem(text, FONT_TEXTURE, FONT_COLUMN_COUNT, FONT_ROW_COUNT);
        mTestTextItem.getMesh().getMaterial().setColour(1, 1, 0, 1);

        mGameItems = new GameItem[] { mTestTextItem };
    }

    @Override
    public GameItem[] getGameItems() {
        return mGameItems;
    }

    public void updateSize(Window window) {
        mTestTextItem.setPosition(10f, window.getHeight() - 50f, 0f);
    }
}
