package com.company.game;

import com.company.engine.IGameLogic;
import com.company.engine.audio.*;
import com.company.engine.graph.lighting.Attenuation;
import com.company.engine.graph.lighting.DirectionalLight;
import com.company.engine.graph.lighting.PointLight;
import com.company.engine.graph.lighting.SpotLight;
import com.company.engine.graph.material.Material;
import com.company.engine.graph.material.Texture;
import com.company.engine.graph.mesh.*;
import com.company.engine.graph.particles.*;
import com.company.engine.graph.rendering.Camera;
import com.company.engine.graph.rendering.Renderer;
import com.company.engine.loaders.ObjLoader;
import com.company.engine.loaders.assimp.StaticMeshesLoader;
import com.company.engine.scene.SceneLighting;
import com.company.engine.scene.items.*;
import com.company.engine.utils.ArrayUtils;
import com.company.engine.utils.MeshUtils;
import com.company.engine.window.Window;
import com.company.engine.input.*;
import com.company.engine.scene.Scene;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.openal.AL11;

import java.util.ArrayList;
import java.util.List;

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

    private float spotAngle = 0;
    private float spotInc = 1;
    private Vector3f ambientLight;

    private List<PointLight> pointLightList;

    private List<SpotLight> spotLightList;

    private DirectionalLight directionalLight;
    private PointLight camPointLight;

    private float lightAngle;

    public TestGame() {
        mRenderer = new Renderer();
        mCamera = new Camera();
        mMouseOptions = new MouseOptions();
        mInitialCycle = true;
        mAudioManager = new AudioManager();
        pointLightList = new ArrayList<>();
        spotLightList = new ArrayList<>();
    }

    @Override
    public void init(Window window) throws Exception {
        mRenderer.init(window);
        mAudioManager.init();
        mScene = new Scene();
        setUpMouseOptions();

//        float blockScale = 0.5f;
//        float skyBoxScale = 50.0f;
//        float extension = 2.0f;
//
//        float startx = extension * (-skyBoxScale + blockScale);
//        float startz = extension * (skyBoxScale - blockScale);
//        float starty = -1.0f;
//        float inc = blockScale * 2;
//
//        float posx = startx;
//        float posz = startz;
//        float incy = 0.0f;
//        int NUM_ROWS = (int)(extension * skyBoxScale * 2 / inc);
//        int NUM_COLS = (int)(extension * skyBoxScale * 2/ inc);
//        GameItem[] gameItems  = new GameItem[NUM_ROWS * NUM_COLS];
//
//        float reflectance = 1f;
//        int instances = NUM_ROWS * NUM_COLS;
//        Texture texture = new Texture("/textures/grassblock.png");
//        Material material = new Material(texture, reflectance);
//        Mesh[] mesh = StaticMeshesLoader.loadMeshes(
//                "src/main/resources/models/cube.obj",
//                material,
//                instances,
//                MeshType.INSTANCED
//        );
//        MeshUtils.setBoundingRadius(mesh, 1.5f);
//
//        for(int i = 0; i< NUM_ROWS; i++) {
//            for(int j = 0; j < NUM_COLS; j++) {
//                GameItem gameItem = new GameItem(mesh);
//                gameItem.setScale(blockScale);
//                incy = Math.random() > 0.9f ? blockScale * 2 : 0f;
//                gameItem.setPosition(posx, starty + incy, posz);
//                gameItems[i*NUM_COLS + j] = gameItem;
//
//                posx += inc;
//            }
//            posx = startx;
//            posz -= inc;
//        }
//
//        Texture particleTexture = new Texture("/textures/particle_anim.png", 4, 4);
//        Material particleMaterial = new Material(particleTexture, reflectance);
//        particleMaterial.setUsingTexture(true);
//        particleMaterial.setColour(new Vector4f(1, 0, 1, 1));
//
//        int maxParticleCount = 20;
//
//        Mesh particleMesh = ObjLoader.loadMesh(
//                "/models/particle.obj",
//                maxParticleCount,
//                MeshType.INSTANCED
//        );
//        particleMesh.setMaterial(particleMaterial);
//        Particle particle = new Particle(particleMesh, new Vector3f(0, 3, 0), 3000, 300);
//        particle.setAnimated(true);
//        testParticleEmitter = new TestParticleEmitter(particle, maxParticleCount, 300);
//        testParticleEmitter.setActive(true);
//        testParticleEmitter.setFrustumCullingParticles(true);
//
////        mScene.addSceneGameItems(gameItems);
//
        Mesh[] legoManMeshes = StaticMeshesLoader.loadMeshes(
                "src/main/resources/models/walker/Neck_Mech_Walker_by_3DHaupt-(Wavefront OBJ).obj",
                "src/main/resources/models/walker",
                1,
                MeshType.STANDARD
        );
//
        MeshUtils.setBoundingRadius(legoManMeshes, 15f);
//
        GameItem gameItem = new GameItem(legoManMeshes);
        gameItem.setIgnoresFrustumCulling(true);
        gameItem.setUsingTexture(true);
//        gameItem.getMeshes()[1].getMaterial().setColour(new Vector4f(1, 0, 1, 1));
//        gameItem.getMesh().getMaterial().setUsingTexture(true);
//        gameItem.getMeshes()[1].getMaterial().setUsingTexture(false);
//        gameItem.getMeshes()[1].getMaterial().setColour(new Vector4f(1, 0, 1, 1));
//
        mScene.addSceneGameItems(new GameItem[] {gameItem});
//
        Mesh particleMesh = ObjLoader.loadMesh("/models/particle.obj", 16, MeshType.INSTANCED);
        Texture particleTexture = new Texture("/textures/particle_anim.png", 4, 4);
        Material particleMaterial = new Material(particleTexture, Material.DEFAULT_REFLECTANCE);
        particleMaterial.setUsingTexture(true);
        particleMaterial.setColour(new Vector4f(1, 0, 1, 1));
        particleMesh.setMaterial(particleMaterial);
        Particle particle = new Particle(particleMesh, new Vector3f(0, 3, 0), 3000, 300);
        particle.setAnimated(true);
        testParticleEmitter = new TestParticleEmitter(particle, 20, 200);
        testParticleEmitter.setActive(true);
        testParticleEmitter.setFrustumCullingParticles(true);
//
//        mScene.addSceneGameItems(gameItems);
//
//        setUpSounds();
//        mAudioManager.playAudioSource(Sounds.MUSIC.toString());
//        mAudioManager.playAudioSource(Sounds.FIRE.toString());

//        mScene.addSceneGameItems(gameItems);
//        mScene.setHud(testHud);
//        mScene.setSkyBox(skyBox);
        mScene.setParticleEmitters(new IParticleEmitter[]{ testParticleEmitter });
//
        ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);

        // Point Light
        Vector3f lightPosition = new Vector3f(0, 0, 1);
        float lightIntensity = 1.0f;
        PointLight pointLight = new PointLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity, true);
        Attenuation att = new Attenuation(0.0f, 0.0f, 1.0f);
        pointLight.setAttenuation(att);
        pointLightList.add(pointLight);

        camPointLight = new PointLight(new Vector3f(mCamera.getPosition()), new Vector3f(1, 0, 1), lightIntensity, true);
        pointLightList.add(camPointLight);

        // Spot Light
        lightPosition = new Vector3f(0, 0.0f, 10f);
        pointLight = new PointLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity, true);
        att = new Attenuation(0.0f, 0.0f, 0.02f);
        pointLight.setAttenuation(att);
        Vector3f coneDir = new Vector3f(0, 0, -1);
        float cutoff = (float) Math.cos(Math.toRadians(140));
        SpotLight spotLight = new SpotLight(pointLight, coneDir, cutoff);
        spotLightList.add(new SpotLight(spotLight));

        lightPosition = new Vector3f(-1, 0, 0);
        directionalLight = new DirectionalLight(new Vector3f(0, 0, 0), lightPosition, lightIntensity / 10);


        mScene.setSceneLighting(new SceneLighting(null, pointLightList, null, null));

        // Setup  GameItems
//        float reflectance = 0.65f;
//        Texture normalMap = new Texture("src/main/resources/textures/rock/rock_normals.png");
//
//        Mesh[] quadMesh1 = StaticMeshesLoader.loadMeshes(
//                "src/main/resources/models/quad.obj",
//                "src/main/resources/textures/rock",
//                1,
//                MeshType.STANDARD
//        );
//        Texture texture = new Texture("src/main/resources/textures/rock/rock.png");
//        Material quadMaterial1 = new Material(texture, reflectance);
//        quadMesh1[0].setMaterial(quadMaterial1);
//        GameItem quadGameItem1 = new GameItem(quadMesh1);
//        quadGameItem1.setPosition(-3f, 0, 0);
//        quadGameItem1.setScale(2.0f);
//        quadGameItem1.setRotation(new Quaternionf(90, 0, 0, 1));
//
//        Mesh[] quadMesh2 = StaticMeshesLoader.loadMeshes(
//                "src/main/resources/models/quad.obj",
//                "src/main/resources/textures/rock",
//                1,
//                MeshType.STANDARD
//        );
//        Material quadMaterial2 = new Material(texture, reflectance);
//        quadMaterial2.setNormalMap(normalMap);
//        quadMesh2[0].setMaterial(quadMaterial2);
//        GameItem quadGameItem2 = new GameItem(quadMesh2);
//        quadGameItem2.setPosition(3f, 0, 0);
//        quadGameItem2.setScale(2.0f);
//        quadGameItem2.setRotation(new Quaternionf(90, 0, 0, 0));

//        mScene.addSceneGameItems(new GameItem[]{quadGameItem1, quadGameItem2});
//        setupLights();
    }

    private void setupLights() {
        SceneLighting sceneLight = new SceneLighting();
        mScene.setSceneLighting(sceneLight);

        // Ambient Light
        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
        sceneLight.setSkyBoxLight(new Vector3f(1.0f, 1.0f, 1.0f));

        // Directional Light
        float lightIntensity = 1.0f;
        Vector3f lightPosition = new Vector3f(1, 1, 0);
        sceneLight.setDirectionalLight(new DirectionalLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity));
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

//        float lightPos = spotLightList.get(0).getPointLight().getPosition().z;
//        if (window.isKeyPressed(GLFW_KEY_N)) {
//            this.spotLightList.get(0).getPointLight().getPosition().z = lightPos + 0.1f;
//        } else if (window.isKeyPressed(GLFW_KEY_M)) {
//            this.spotLightList.get(0).getPointLight().getPosition().z = lightPos - 0.1f;
//        }

    }

    @Override
    public void update(float interval, MouseInput mouseInput, KeyboardInput keyboardInput) {
        testParticleEmitter.update((long) (interval * 1000));
//        mAudioManager.updateListenerPosition(mCamera);
        //update camera view matrix each cycle
        mCamera.updateViewMatrix();

//        // Update spot light direction
//        spotAngle += spotInc * 0.05f;
//        if (spotAngle > 2) {
//            spotInc = -1;
//        } else if (spotAngle < -2) {
//            spotInc = 1;
//        }
//        double spotAngleRad = Math.toRadians(spotAngle);
//        Vector3f coneDir = spotLightList.get(0).getConeDirection();
//        coneDir.y = (float) Math.sin(spotAngleRad);

        // Update directional light direction, intensity and colour
//        lightAngle += 1.1f;
//        if (lightAngle > 90) {
//            directionalLight.setIntensity(0);
//            if (lightAngle >= 360) {
//                lightAngle = -90;
//            }
//        } else if (lightAngle <= -80 || lightAngle >= 80) {
//            float factor = 1 - (float) (Math.abs(lightAngle) - 80) / 10.0f;
//            directionalLight.setIntensity(factor);
//            directionalLight.getColour().y = Math.max(factor, 0.9f);
//            directionalLight.getColour().z = Math.max(factor, 0.5f);
//        } else {
//            directionalLight.setIntensity(1);
//            directionalLight.getColour().x = 1;
//            directionalLight.getColour().y = 1;
//            directionalLight.getColour().z = 1;
//        }
//        double angRad = Math.toRadians(lightAngle);
//        directionalLight.getDirection().x = (float) Math.sin(angRad);
//        directionalLight.getDirection().y = (float) Math.cos(angRad);
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