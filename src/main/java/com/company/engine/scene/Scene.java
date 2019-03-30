package com.company.engine.scene;

import com.company.engine.graph.mesh.InstancedMesh;
import com.company.engine.graph.mesh.Mesh;
import com.company.engine.graph.particles.IParticleEmitter;
import com.company.engine.input.KeyboardInput;
import com.company.engine.input.MouseInput;
import com.company.engine.scene.items.SkyBox;
import com.company.engine.scene.items.ui.IHud;
import com.company.engine.scene.items.GameItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {

    private IHud mHud;
    private Map<Mesh, List<GameItem>> mGameItemMeshMap; //stores GameItems based on their Mesh
    private Map<InstancedMesh, List<GameItem>> mGameItemInstancedMesh;
    private IParticleEmitter[] mParticleEmitters;
    private MouseInput mMouseInput;
    private KeyboardInput mKeyboardInput;
    private SceneLighting mSceneLighting;
    private SkyBox mSkyBox;
//    private boolean mRenderingShadows;

    public Scene() {
        mGameItemMeshMap = new HashMap<>();
        mGameItemInstancedMesh = new HashMap<>();
//        mRenderingShadows = true;
    }

    public void addSceneGameItems(GameItem[] gameItems) {
        int gameItemsLength = gameItems != null ? gameItems.length : 0;

        for (int i = 0; i < gameItemsLength; i++) {
            GameItem gameItem = gameItems[i];
            Mesh[] meshes = gameItem.getMeshArray();

            for (Mesh mesh : meshes) {
                boolean isInstanced = mesh instanceof InstancedMesh;
                List<GameItem> gameItemList = isInstanced ?
                        mGameItemInstancedMesh.get(mesh) : mGameItemMeshMap.get(mesh);

                if (gameItemList == null) {
                    gameItemList = new ArrayList<>();
                    if (isInstanced) {
                        mGameItemInstancedMesh.put((InstancedMesh) mesh, gameItemList);
                    } else {
                        mGameItemMeshMap.put(mesh, gameItemList);
                    }
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

        if (mParticleEmitters != null) {
            for (IParticleEmitter particleEmitter : mParticleEmitters) {
                particleEmitter.cleanUp();
            }
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

    public IParticleEmitter[] getParticleEmitters() {
        return mParticleEmitters;
    }

    public void setParticleEmitters(IParticleEmitter[] particleEmitters) {
        mParticleEmitters = particleEmitters;
    }

    public SkyBox getSkyBox() {
        return mSkyBox;
    }

    public void setSkyBox(SkyBox skyBox) {
        mSkyBox = skyBox;
    }

    public Map<InstancedMesh, List<GameItem>> getGameItemInstancedMeshMap() {
        return mGameItemInstancedMesh;
    }

//    public boolean isRenderingShadows() {
//        return mRenderingShadows;
//    }

//    public void setRenderingShadows(boolean renderingShadows) {
//        mRenderingShadows = renderingShadows;
//    }
}