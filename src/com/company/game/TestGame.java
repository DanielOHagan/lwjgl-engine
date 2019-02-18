package com.company.game;

import com.company.engine.IGameLogic;
import com.company.engine.Window;
import com.company.engine.graph.*;
import com.company.engine.input.KeyboardInput;
import com.company.engine.input.MouseInput;
import com.company.engine.scene.Scene;
import com.company.engine.scene.items.GameItem;
import com.company.engine.scene.items.ui.IHud;

public class TestGame implements IGameLogic {

    private final Renderer mRenderer;

    private Scene mScene;
    private Camera mCamera;
    private IHud mHud;

    public TestGame() {
        mRenderer = new Renderer();
    }

    @Override
    public void init(Window window) throws Exception {
        mRenderer.init(window);
        mScene = new Scene();
        mCamera = new Camera();

        float[] positions = new float[] {
                -0.5f, 0.5f, 0, //top left
                0.5f, 0.5f, 0, //top right
                0.5f, -0.5f, 0, //bottom right
                -0.5f, -0.5f, 0 //bottom left
        };

        float[] textCoords = new float[] {
                0, 0,
                1, 0,
                1, 1,
                0, 1,
        };

        int[] indices = new int[] {
                0, 1, 2,
                2, 3, 0
        };

        Mesh mesh = new Mesh(positions, textCoords, indices);
        Texture texture = new Texture("/textures/test.png");
        Material material = new Material(texture);
        mesh.setMaterial(material);
        GameItem gameItem = new GameItem(mesh);

        mScene.setSceneGameItems(new GameItem[] { gameItem });
    }

    @Override
    public void input(Window window, MouseInput mouseInput, KeyboardInput keyboardInput) {

    }

    @Override
    public void update(float interval, MouseInput mouseInput, KeyboardInput keyboardInput) {

    }

    @Override
    public void render(Window window) {
        mRenderer.render(window, mCamera, mScene, mHud);
    }

    @Override
    public void cleanUp() {
        if (mScene != null) {
            mScene.cleanUp();
        }
    }
}
