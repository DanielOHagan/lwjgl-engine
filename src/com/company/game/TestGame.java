package com.company.game;

import com.company.engine.IGameLogic;
import com.company.engine.graph.mesh.Mesh;
import com.company.engine.graph.particles.IParticleEmitter;
import com.company.engine.graph.particles.Particle;
import com.company.engine.input.MouseOptions;
import com.company.engine.loaders.ObjLoader;
import com.company.engine.scene.items.Background;
import com.company.engine.window.Window;
import com.company.engine.graph.*;
import com.company.engine.input.KeyboardInput;
import com.company.engine.input.MouseInput;
import com.company.engine.scene.Scene;
import com.company.engine.scene.items.GameItem;
import com.company.engine.scene.items.ui.IHud;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class TestGame implements IGameLogic {

    private final Renderer mRenderer;

    private Scene mScene;
    private Camera mCamera;
    private IHud mHud;
    private boolean mSceneChanged;
    private boolean mInitialCycle;
    private MouseOptions mMouseOptions;


    private Background testBackground;

    public TestGame() {
        mRenderer = new Renderer();
        mCamera = new Camera();
        mMouseOptions = new MouseOptions();
        mInitialCycle = true;
    }

    @Override
    public void init(Window window) throws Exception {
        mRenderer.init(window);
        mScene = new Scene();
        setUpMouseOptions();

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


        Mesh mesh = new Mesh(positions, textCoords, null, indices);
        Material material = new Material(new Vector4f(1, 0, 0, 1));
        mesh.setMaterial(material);
        GameItem gameItem = new GameItem(mesh);

        mScene.setSceneGameItems(new GameItem[] { gameItem });

        testBackground = new Background(positions, textCoords, indices);
        testBackground.setMaterial(new Material(new Texture("/textures/testBackground.png")));

        testBackground = new Background(new Vector4f(1, 1, 1, 1));

        mScene.setBackground(testBackground);
    }

    @Override
    public void input(Window window, MouseInput mouseInput, KeyboardInput keyboardInput) {
        /* Handle input here */

        /* This is very bad camera controls, it is purely to test rendering */
        if (window.isKeyPressed(GLFW_KEY_W)) {
            mCamera.getPosition().z += 0.01;
        }
        if (window.isKeyPressed(GLFW_KEY_S)) {
            mCamera.getPosition().z -= 0.01;
        }

        if (window.isKeyPressed(GLFW_KEY_A)) {
            mCamera.getPosition().x -= 0.01;
        }
        if (window.isKeyPressed(GLFW_KEY_D)) {
            mCamera.getPosition().x += 0.01;
        }

        if (window.isKeyPressed(GLFW_KEY_C)) {
            mCamera.getPosition().y += 0.01;
        }
        if (window.isKeyPressed(GLFW_KEY_SPACE)) {
            mCamera.getPosition().y -= 0.01;
        }

        if (window.isKeyPressed(GLFW_KEY_LEFT)) {
            mCamera.getRotation().y -= 0.4;
        }
        if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
            mCamera.getRotation().y += 0.4;
        }

        if (window.isKeyPressed(GLFW_KEY_UP)) {
            mCamera.getRotation().x -= 0.4;
        }
        if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            mCamera.getRotation().x += 0.4;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput, KeyboardInput keyboardInput) {
        /* Update the application here */

    }

    @Override
    public void render(Window window) {
        if (mInitialCycle) {
            mSceneChanged = true;
            mInitialCycle = false;
        }
        mRenderer.render(window, mCamera, mScene, mSceneChanged);
    }

    @Override
    public void cleanUp() {
        if (mScene != null) {
            mScene.cleanUp();
        }
    }

    private void setUpMouseOptions() {
        mMouseOptions.setSensitivity(0.2f);
    }
}
