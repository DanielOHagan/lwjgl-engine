package com.company.engine.graph;

import com.company.engine.Utils;
import com.company.engine.graph.mesh.Mesh;
import com.company.engine.graph.particles.IParticleEmitter;
import com.company.engine.graph.particles.Particle;
import com.company.engine.scene.items.SkyBox;
import com.company.engine.window.Window;
import com.company.engine.scene.Scene;
import com.company.engine.scene.items.GameItem;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    private final Transformation mTransformation;

    private ShaderProgram mDepthShaderProgram;
    private ShaderProgram mSkyBoxShaderProgram;
    private ShaderProgram mSceneShaderProgram;
    private ShaderProgram mParticleShaderProgram;
    private ShaderProgram mHudShaderProgram;

    public Renderer() {
        mTransformation = new Transformation();
    }

    public void init(Window window) throws Exception {

        //setUpDepthShader();
        //setUpSkyBoxShader();
        setUpSceneShader();
        setUpParticleShader();
        setUpHudShader();

    }

    public void setUpSkyBoxShader() throws Exception {
        mSkyBoxShaderProgram = new ShaderProgram();
        mSkyBoxShaderProgram.createVertexShader(Utils.loadResource("/shaders/background_vertex.vs"));
        mSkyBoxShaderProgram.createFragmentShader(Utils.loadResource("/shaders/background_fragment.fs"));
        mSkyBoxShaderProgram.link();

        setUpShaderUniforms(
                mSkyBoxShaderProgram,
                new String[] {
                        "textureSampler",
                        "useTexture",
                        "colour",
                        "projectionModelMatrix"
                }
        );
    }

    public void setUpSceneShader() throws Exception {
        mSceneShaderProgram = new ShaderProgram();
        mSceneShaderProgram.createVertexShader(Utils.loadResource("/shaders/scene_vertex.vs"));
        mSceneShaderProgram.createFragmentShader(Utils.loadResource("/shaders/scene_fragment.fs"));
        mSceneShaderProgram.link();

        setUpShaderUniforms(
                mSceneShaderProgram,
                new String[] {
                        "textureSampler",
                        "useTexture",
                        "colour",
                        "projectionMatrix",
                        "modelViewMatrix"
                }
        );
    }

    private void setUpParticleShader() throws Exception {
        mParticleShaderProgram = new ShaderProgram();
        mParticleShaderProgram.createVertexShader(Utils.loadResource("/shaders/particle_vertex.vs"));
        mParticleShaderProgram.createFragmentShader(Utils.loadResource("/shaders/particle_fragment.fs"));
        mParticleShaderProgram.link();

        setUpShaderUniforms(
                mParticleShaderProgram,
                new String[] {
                        "projectionMatrix",
                        "modelViewMatrix",
                        "useTexture",
//                        "viewMatrix",
                        "numColumns",
                        "numRows",
                        "textOffsetX",
                        "textOffsetY",
                        "textureSampler",
                        "particleColour"
                }
        );
    }

    private void setUpHudShader() throws Exception {
        mHudShaderProgram = new ShaderProgram();
        mHudShaderProgram.createVertexShader(Utils.loadResource("/shaders/hud_vertex.vs"));
        mHudShaderProgram.createFragmentShader(Utils.loadResource("/shaders/hud_fragment.fs"));
        mHudShaderProgram.link();

        setUpShaderUniforms(
                mHudShaderProgram,
                new String[] {
                        "projectionModelMatrix",
                        "colour",
                        "textureSampler"
                }
        );
    }

    private void setUpShaderUniforms(ShaderProgram shaderProgram, String[] uniformNames) throws Exception {
        if (shaderProgram != null && uniformNames != null && uniformNames.length > 0) {
            for (String uniformName : uniformNames) {
                shaderProgram.createUniform(uniformName);
            }
        }
    }

    public void render(Window window, Camera camera, Scene scene, boolean sceneChanged) {
        clear();

        //TODO: frustum culling
//        if (window.getOptions().frustumCulling) {
//            frustumFilter.updateFrustum(window.getViewMatrix(), camera.getViewMatrix());
//            frustumFilter.filter(scene.getGameMeshes());
//            frustumFilter.filter(scene.getGameInstancedMeshes());
//        }
//
//        // Render depth map before view ports has been set up
//        if (scene.isRenderShadows() && sceneChanged) {
//            shadowRenderer.render(window, scene, camera, transformation, this);
//        }

        //set the viewport for the window each cycle
        glViewport(0, 0, window.getWidth(), window.getHeight());

        //Update projection matrix once per render cycle
        window.updateProjectionMatrix(camera.getFov(), camera.getViewDistanceStart(), camera.getViewDistanceEnd());

        //update camera view matrix each cycle
        camera.updateViewMatrix();

        renderScene(window, camera, scene);

        if (scene.getSkyBox() != null) {
            renderSkyBox(window, camera, scene);
        }

        if (scene.getParticleEmitters() != null && scene.getParticleEmitters().length > 0) {
            renderParticles(window, camera, scene);
        }

        //TODO: light rendering
//        if (scene.getSceneLighting() != null) {
//            SceneLighting sl = scene.getSceneLighting();
//
//            initLightRendering();
//
//            if (sl.getPointLights() != null) {
//                renderPointLights(window, camera, scene);
//            }
//
//            if (sl.getDirectionLights() != null) {
//                renderDirectional(window, camera, scene);
//            }
//
//            endLightRendering();
//        }

        if (scene.getHud() != null) {
            renderHud(window, camera, scene);
        }
    }

    public void renderScene(Window window, Camera camera, Scene scene) {
        mSceneShaderProgram.bind();

        mSceneShaderProgram.setUniform("textureSampler", 0);
        mSceneShaderProgram.setUniform("projectionMatrix", window.getProjectionMatrix());

        /* TODO: Change this to Instanced and Non Instanced game item rendering */
        renderGameItems(camera, scene);


        mSceneShaderProgram.unbind();
    }

    private void renderGameItems(Camera camera, Scene scene) {
        Map<Mesh, List<GameItem>> meshesMap = scene.getGameItemMeshMap();
        for (Mesh mesh : meshesMap.keySet()) {
            if (mesh.getMaterial() != null) {
                //set mesh's material related uniforms
                boolean isUsingTexture = mesh.getMaterial().isUsingTexture();

                mSceneShaderProgram.setUniform("useTexture", isUsingTexture ? 1 : 0);

                if (isUsingTexture) {
                    //set mesh's texture related uniforms if the texture is selected to be rendered

                } else {
                    mSceneShaderProgram.setUniform("colour", mesh.getMaterial().getColour());
                }
            }

            mesh.renderList(meshesMap.get(mesh), (GameItem gameItem) -> {
                mSceneShaderProgram.setUniform(
                        "modelViewMatrix",
                        mTransformation.generateModelViewMatrix(gameItem, camera.getViewMatrix())
                );
            });
        }
    }

    private void renderHud(Window window, Camera camera, Scene scene) {
        mHudShaderProgram.bind();

        Matrix4f orthoProjection = mTransformation.generateOrtho2DProjectionMatrix(
                0, window.getWidth(), window.getHeight(), 0
        );

        /*
        TODO: MIGHT BE ABLE TO CHANGE THIS TO RENDERING PER MESH TYPE
         */
        for (GameItem gameItem : scene.getHud().getGameItems()) {
            mHudShaderProgram.setUniform(
                    "projectionModelMatrix",
                    mTransformation.generateOrthoProjectionModelMatrix(gameItem, orthoProjection)
            );
            mHudShaderProgram.setUniform(
                    "colour",
                    gameItem.getMesh().getMaterial().getColour()
            );

            gameItem.getMesh().render();
        }

        mHudShaderProgram.unbind();
    }

    private void renderSkyBox(Window window, Camera camera, Scene scene) {
        SkyBox skybox = scene.getSkyBox();

        mSkyBoxShaderProgram.bind();



        mSkyBoxShaderProgram.unbind();
    }

    private void renderParticles(Window window, Camera camera, Scene scene) {
        mParticleShaderProgram.bind();

        mParticleShaderProgram.setUniform("textureSampler", 0);
        mParticleShaderProgram.setUniform(
                "projectionMatrix",
                window.getProjectionMatrix()
        );
//        mParticleShaderProgram.setUniform("viewMatrix", camera.getViewMatrix());

        IParticleEmitter[] emitters = scene.getParticleEmitters();
        int emittersLength = emitters != null ? emitters.length : 0;

        glDepthMask(false);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);

        for (int i = 0; i < emittersLength; i++) {
            IParticleEmitter emitter = emitters[i];
            Mesh mesh = emitter.getBaseParticle().getMesh();
            boolean useTexture = emitter.getBaseParticle().isUsingTexture();
            Texture texture = mesh.getMaterial().getTexture();

            mParticleShaderProgram.setUniform("numColumns", texture.getNumColumns());
            mParticleShaderProgram.setUniform("numRows", texture.getNumRows());

            mParticleShaderProgram.setUniform("useTexture", useTexture ? 1 : 0);

            mesh.renderList((emitter.getParticles()), (GameItem gameItem) -> {
                if (useTexture) {
                    int column = gameItem.getTexturePos() % texture.getNumColumns();
                    int row = gameItem.getTexturePos() / texture.getNumColumns();
                    float textOffsetX = (float) column / texture.getNumColumns();
                    float textOffsetY = (float) row / texture.getNumRows();

                    mParticleShaderProgram.setUniform("textOffsetX", textOffsetX);
                    mParticleShaderProgram.setUniform("textOffsetY", textOffsetY);
                } else {
                    mParticleShaderProgram.setUniform("particleColour", ((Particle) gameItem).getColour());
                }

                Matrix4f modelMatrix = mTransformation.generateModelMatrix(gameItem);

                camera.getViewMatrix().transpose3x3(modelMatrix);
                camera.getViewMatrix().scale(gameItem.getScale());

                Matrix4f modelViewMatrix = mTransformation.generateModelViewMatrix(
                        modelMatrix, camera.getViewMatrix()
                );
                modelViewMatrix.scale(gameItem.getScale());
                mParticleShaderProgram.setUniform(
                        "modelViewMatrix",
                        modelViewMatrix
                );
            });
        }

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDepthMask(true);

        mParticleShaderProgram.unbind();
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void cleanUp() {
        if (mSceneShaderProgram != null) {
            mSceneShaderProgram.cleanUp();
        }

        if (mSkyBoxShaderProgram != null) {
            mSkyBoxShaderProgram.cleanUp();
        }

        if (mDepthShaderProgram != null) {
            mDepthShaderProgram.cleanUp();
        }

        if (mParticleShaderProgram != null) {
            mParticleShaderProgram.cleanUp();
        }
    }
}