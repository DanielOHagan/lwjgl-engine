package com.company.game;

import com.company.engine.IGameLogic;
import com.company.engine.audio.*;
import com.company.engine.graph.mesh.*;
import com.company.engine.graph.particles.*;
import com.company.engine.loaders.ObjLoader;
import com.company.engine.loaders.assimp.StaticMeshesLoader;
import com.company.engine.scene.items.*;
import com.company.engine.utils.MeshUtils;
import com.company.engine.window.Window;
import com.company.engine.graph.*;
import com.company.engine.input.*;
import com.company.engine.scene.Scene;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.openal.AL11;

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
    private AudioManager mAudioManager;
    private enum Sounds {
        MUSIC,
        BEEP,
        FIRE
    }

    public TestGame() {
        mRenderer = new Renderer();
        mCamera = new Camera();
        mMouseOptions = new MouseOptions();
        mInitialCycle = true;
        mAudioManager = new AudioManager();
    }

    @Override
    public void init(Window window) throws Exception {
        mRenderer.init(window);
        mAudioManager.init();
        mScene = new Scene();
        setUpMouseOptions();

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
        Texture texture = new Texture("/textures/grassblock.png");
        Material material = new Material(texture, reflectance);
        Mesh[] mesh = StaticMeshesLoader.loadMeshes(
                "src/main/resources/models/cube.obj",
                material,
                instances,
                MeshType.INSTANCED
        );
        MeshUtils.setBoundingRadius(mesh, 1.5f);

        for(int i = 0; i< NUM_ROWS; i++) {
            for(int j = 0; j < NUM_COLS; j++) {
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

        Texture particleTexture = new Texture("/textures/particle_anim.png", 4, 4);
        Material particleMaterial = new Material(particleTexture, reflectance);
        particleMaterial.setUsingTexture(true);
        particleMaterial.setColour(new Vector4f(1, 0, 1, 1));

        int maxParticleCount = 20;

        Mesh particleMesh = ObjLoader.loadMesh(
                "/models/particle.obj",
                maxParticleCount,
                MeshType.INSTANCED
        );
        particleMesh.setMaterial(particleMaterial);
        Particle particle = new Particle(particleMesh, new Vector3f(0, 3, 0), 3000, 300);
        particle.setAnimated(true);
        testParticleEmitter = new TestParticleEmitter(particle, maxParticleCount, 300);
        testParticleEmitter.setActive(true);
        testParticleEmitter.setFrustumCullingParticles(true);

        mScene.addSceneGameItems(gameItems);

        Mesh[] legoManMeshes = StaticMeshesLoader.loadMeshes(
                "src/main/resources/models/city/City Template.obj",
                new Material(new Vector4f(1, 0, 1, 1)),
                1,
                MeshType.STANDARD
        );

//        MeshUtils.setBoundingRadius(legoManMeshes, 15f);

        GameItem gameItem = new GameItem(legoManMeshes);
        gameItem.setIgnoresFrustumCulling(true);
//        gameItem.getMesh().getMaterial().setUsingTexture(true);
//        gameItem.getMeshes()[1].getMaterial().setUsingTexture(false);
//        gameItem.getMeshes()[1].getMaterial().setColour(new Vector4f(1, 0, 1, 1));

//        mScene.addSceneGameItems(new GameItem[] {gameItem});

//        Mesh particleMesh = ObjLoader.loadMesh("/models/particle.obj", 16, MeshType.INSTANCED);
//        Texture particleTexture = new Texture("/textures/particle_anim.png", 4, 4);
//        Material particleMaterial = new Material(particleTexture, Material.DEFAULT_REFLECTANCE);
//        particleMaterial.setUsingTexture(true);
//        particleMaterial.setColour(new Vector4f(1, 0, 1, 1));
//        particleMesh.setMaterial(particleMaterial);
//        Particle particle = new Particle(particleMesh, new Vector3f(0, 3, 0), 3000, 300);
//        particle.setAnimated(true);
//        testParticleEmitter = new TestParticleEmitter(particle, 20, 200);
//        testParticleEmitter.setActive(true);
//        testParticleEmitter.setFrustumCullingParticles(true);

//        mScene.addSceneGameItems(gameItems);

//        setUpSounds();
//        mAudioManager.playAudioSource(Sounds.MUSIC.toString());
//        mAudioManager.playAudioSource(Sounds.FIRE.toString());

        //mScene.addSceneGameItems(gameItems);
        //mScene.setHud(testHud);
        //mScene.setSkyBox(skyBox);
        mScene.setParticleEmitters(new IParticleEmitter[]{ testParticleEmitter });
    }

    private void setUpSounds() throws Exception {
        mAudioManager.setAttenuationModel(AL11.AL_EXPONENT_DISTANCE);

        AudioBuffer buffBack = new AudioBuffer("/audio/background.ogg");
        AudioSource sourceBack = new AudioSource(true, true);

        mAudioManager.addAudioBufer(buffBack);
        sourceBack.setBuffer(buffBack.getBufferId());
        mAudioManager.addAudioSource(Sounds.MUSIC.toString(), sourceBack);

        AudioBuffer buffFire = new AudioBuffer("/audio/fire.ogg");
        mAudioManager.addAudioBufer(buffFire);
        AudioSource sourceFire = new AudioSource(true, false);
        Vector3f pos = testParticleEmitter.getBaseParticle().getPosition();
        sourceFire.setPosition(pos);
        sourceFire.setBuffer(buffFire.getBufferId());
        mAudioManager.addAudioSource(Sounds.FIRE.toString(), sourceFire);

        mAudioManager.setAudioListener(new AudioListener(new Vector3f(0, 0, 0)));
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
    }

    @Override
    public void update(float interval, MouseInput mouseInput, KeyboardInput keyboardInput) {
        testParticleEmitter.update((long) (interval * 1000));
//        mAudioManager.updateListenerPosition(mCamera);
        //update camera view matrix each cycle
        mCamera.updateViewMatrix();
    }

    @Override
    public void render(Window window) {
//        testHud.updateSize(window);
        if (mInitialCycle) {
            mSceneChanged = true;
            mInitialCycle = false;
        }
        try {
            mRenderer.render(window, mCamera, mScene, mSceneChanged);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cleanUp() {
        if (mScene != null) {
            mScene.cleanUp();
        }

        if (mAudioManager != null) {
            mAudioManager.cleanUp();
        }
    }

    private void setUpMouseOptions() {
        mMouseOptions.setSensitivity(0.2f);
    }
}