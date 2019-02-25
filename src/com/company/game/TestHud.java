package com.company.game;

import com.company.engine.scene.items.GameItem;
import com.company.engine.scene.items.ui.IHud;
import com.company.engine.scene.items.ui.UiTextItem;
import com.company.engine.window.Window;
import org.joml.Vector4f;

public class TestHud implements IHud {

    private static final int FONT_COLUMN_COUNT = 16;
    private static final int FONT_ROW_COUNT = 16;
    private static final String FONT_TEXTURE = "/textures/font_texture.png";
    private static final Vector4f DEFAULT_COLOUR = new Vector4f(1, 1, 1, 1);

    private final GameItem[] mGameItems;
//    private final TextItem mTextItem;
    private final UiTextItem mUiTextItem;

    public TestHud(String text) throws Exception {
        mUiTextItem = new UiTextItem(text, FONT_TEXTURE, FONT_COLUMN_COUNT, FONT_ROW_COUNT);
        mUiTextItem.getMesh().getMaterial().setColour(
                new Vector4f(DEFAULT_COLOUR.x, DEFAULT_COLOUR.y, DEFAULT_COLOUR.z, DEFAULT_COLOUR.w)
        );

        mGameItems = new GameItem[] { mUiTextItem };
    }

    public UiTextItem getTestTextItem() {
        return mUiTextItem;
    }

    @Override
    public GameItem[] getGameItems() {
        return mGameItems;
    }

    public void updateSize(Window window) {
        mUiTextItem.setPosition(10f, window.getHeight() - 50f, 0f);
    }
}
