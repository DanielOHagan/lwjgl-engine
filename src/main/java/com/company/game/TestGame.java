package com.company.game;

import com.company.engine.IGameLogic;
import com.company.engine.graph.mesh.Mesh;
import com.company.engine.graph.particles.IParticleEmitter;
import com.company.engine.graph.particles.Particle;
import com.company.engine.input.MouseOptions;
import com.company.engine.loaders.ObjLoader;
import com.company.engine.scene.items.GameItem;
import com.company.engine.scene.items.SkyBox;
import com.company.engine.window.Window;
import com.company.engine.graph.*;
import com.company.engine.input.KeyboardInput;
import com.company.engine.input.MouseInput;
import com.company.engine.scene.Scene;
import de.matthiasmann.twl.utils.PNGDecoder;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class TestGame implements IGameLogic {

    private final Renderer mRenderer;

    private Scene mScene;
    private Camera mCamera;
    private boolean mSceneChanged;
    private boolean mInitialCycle;
    private MouseOptions mMouseOptions;

    private TestHud testHud;
    private TestParticleEmitter testParticleEmitter;

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

        // Setup  GameItems
        float blockScale = 0.5f;
        float skyBoxScale = 50.0f;
        float extension = 2.0f;

        float startx = extension * (-skyBoxScale + blockScale);
        float startz = extension * (skyBoxScale - blockScale);
        float starty = -1.0f;
        float inc = blockScale * 2;

        float posx = startx;
        float posz = startz;
        float incy = 0.0f;
        int NUM_ROWS = (int)(extension * skyBoxScale * 2 / inc);
        int NUM_COLS = (int)(extension * skyBoxScale * 2/ inc);
        GameItem[] gameItems  = new GameItem[NUM_ROWS * NUM_COLS];

        float reflectance = 1f;
        int instances = NUM_ROWS * NUM_COLS;
        Mesh mesh = ObjLoader.loadMesh("/models/cube.obj", instances);
        Texture texture = new Texture("/textures/grassblock.png");
        Material material = new Material(texture, reflectance);
        mesh.setMaterial(material);

        for(int i=0; i<NUM_ROWS; i++) {
            for(int j=0; j<NUM_COLS; j++) {
                GameItem gameItem = new GameItem(mesh);
                gameItem.setScale(blockScale);
                incy = Math.random() > 0.9f ? blockScale * 2 : 0f;
                gameItem.setPosition(posx, starty + incy, posz);
                gameItems[i*NUM_COLS + j] = gameItem;

                posx += inc;
            }
            posx = startx;
            posz -= inc;
        }
        mScene.addSceneGameItems(gameItems);

        //mScene.addSceneGameItems(gameItems);
        //mScene.setHud(testHud);
        //mScene.setSkyBox(skyBox);
        //mScene.setParticleEmitters(new IParticleEmitter[]{ testParticleEmitter });
    }

    @Override
    public void input(Window window, MouseInput mouseInput, KeyboardInput keyboardInput) {
        /* Handle input here */

        /* This is very bad camera controls, it is purely for test */
        if (window.isKeyPressed(GLFW_KEY_W)) {
            mCamera.getPosition().z += 0.08;
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

        //System.out.println("CAMERA POS: \n\tX: " + mCamera.getPosition().x + "\n\tY: " + mCamera.getPosition().y + "\n\tZ: " + mCamera.getPosition().z);
    }

    @Override
    public void update(float interval, MouseInput mouseInput, KeyboardInput keyboardInput) {
        /* Update the application here */
//        testParticleEmitter.update((long) (interval * 1000));
    }

    @Override
    public void render(Window window) {
//        testHud.updateSize(window);
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
