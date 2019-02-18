package com.company.engine.scene;

import com.company.engine.graph.Mesh;
import com.company.engine.input.KeyboardInput;
import com.company.engine.input.MouseInput;
import com.company.engine.scene.items.ui.IHud;
import com.company.engine.scene.items.GameItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {

    private IHud mHud;
    private Map<Mesh, List<GameItem>> mGameItemMeshMap; //stores GameItemss based on their Mesh
    private MouseInput mMouseInput;
    private KeyboardInput mKeyboardInput;
    private SceneLighting mSceneLighting;

    public Scene() {
        mGameItemMeshMap = new HashMap<>();
    }

    public void setSceneGameItems(GameItem[] gameItems) {
        int gameItemsLength = gameItems != null ? gameItems.length : 0;

        for (int i = 0; i < gameItemsLength; i++) {
            GameItem gameItem = gameItems[i];
            Mesh[] meshes = gameItem.getMeshes();
            for (Mesh mesh : meshes) {
                List<GameItem> gameItemList = mGameItemMeshMap.get(mesh);
                if (gameItemList == null) {//if mesh is not already stored in map
                    gameItemList = new ArrayList<>();
                    mGameItemMeshMap.put(mesh, gameItemList);
                }
                gameItemList.add(gameItem);
            }
        }
    }

    public void cleanUp() {
        if (mHud != null) {
            mHud.cleanUp();
        }

        if (mGameItemMeshMap != null) {
            for (Mesh mesh : mGameItemMeshMap.keySet()) {
                for (GameItem gameItem : mGameItemMeshMap.get(mesh)) {
                    gameItem.cleanUp();
                }
                mesh.cleanUp();
            }
            mGameItemMeshMap = null;
        }
    }

    public IHud getHud() {
        return mHud;
    }

    public void setHud(IHud mHud) {
        this.mHud = mHud;
    }

    public Map<Mesh, List<GameItem>> getGameItemMeshMap() {
        return mGameItemMeshMap;
    }

    public void setGameItemMeshMap(Map<Mesh, List<GameItem>> mGameItemMeshMap) {
        this.mGameItemMeshMap = mGameItemMeshMap;
    }

    public MouseInput getMouseInput() {
        return mMouseInput;
    }

    public void setMouseInput(MouseInput mMouseInput) {
        this.mMouseInput = mMouseInput;
    }

    public KeyboardInput getKeyboardInput() {
        return mKeyboardInput;
    }

    public void setKeyboardInput(KeyboardInput mKeyboardInput) {
        this.mKeyboardInput = mKeyboardInput;
    }

    public SceneLighting getSceneLighting() {
        return mSceneLighting;
    }

    public void setSceneLighting(SceneLighting mSceneLighting) {
        this.mSceneLighting = mSceneLighting;
    }
}
