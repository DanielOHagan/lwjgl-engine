package com.company.engine.graph;

import com.company.engine.graph.optimisations.FrustumFilter;
import com.company.engine.graph.particles.Particle;
import com.company.engine.utils.FileUtils;
import com.company.engine.graph.mesh.InstancedMesh;
import com.company.engine.graph.mesh.Mesh;
import com.company.engine.graph.particles.IParticleEmitter;
import com.company.engine.scene.items.SkyBox;
import com.company.engine.utils.ShaderUtils;
import com.company.engine.window.Window;
import com.company.engine.scene.Scene;
import com.company.engine.scene.items.GameItem;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    private final Transformation mTransformation;

    //shader programs
    private ShaderProgram mDepthShaderProgram;
    private ShaderProgram mSkyBoxShaderProgram;
    private ShaderProgram mSceneShaderProgram;
    private ShaderProgram mParticleShaderProgram;
    private ShaderProgram mHudShaderProgram;

    //frustum culling
    private final FrustumFilter mFrustumFilter;
    private final List<GameItem> mFilteredGameItemList;
    private final List<IParticleEmitter> mFilteredParticleEmitterList;

    public Renderer() {
        mTransformation = new Transformation();
        mFrustumFilter = new FrustumFilter();
        mFilteredGameItemList = new ArrayList<>();
        mFilteredParticleEmitterList = new ArrayList<>();
    }

    public void init(Window window) throws Exception {
        //setUpDepthShader();
        setUpSkyBoxShader();
        setUpSceneShader();
        setUpParticleShader();
        setUpHudShader();

    }

    public void setUpSkyBoxShader() throws Exception {
        mSkyBoxShaderProgram = new ShaderProgram();
        mSkyBoxShaderProgram.createVertexShader(FileUtils.loadResource("/shaders/skyBox_vertex.vs"));
        mSkyBoxShaderProgram.createFragmentShader(FileUtils.loadResource("/shaders/skyBox_fragment.fs"));
        mSkyBoxShaderProgram.link();

        ShaderUtils.setUpShaderUniforms(
                mSkyBoxShaderProgram,
                new String[] {
                        "textureSampler",
                        "useTexture",
                        "colour",
                        "projectionMatrix",
                        "modelViewMatrix"
                }
        );
    }

    public void setUpSceneShader() throws Exception {
        mSceneShaderProgram = new ShaderProgram();
        mSceneShaderProgram.createVertexShader(FileUtils.loadResource("/shaders/scene_vertex.vs"));
        mSceneShaderProgram.createFragmentShader(FileUtils.loadResource("/shaders/scene_fragment.fs"));
        mSceneShaderProgram.link();

        ShaderUtils.setUpShaderUniforms(
                mSceneShaderProgram,
                new String[] {
                        "textureSampler",
                        "projectionMatrix",
                        "nonInstancedModelViewMatrix",
//                        "nonInstancedModelLightViewMatrix",
                        "isInstanced"
                }
        );
        ShaderUtils.setUpMaterialUniforms(
                mSceneShaderProgram,
                new String[] {
                        "material"
                }
        );
    }

    private void setUpParticleShader() throws Exception {
        mParticleShaderProgram = new ShaderProgram();
        mParticleShaderProgram.createVertexShader(FileUtils.loadResource("/shaders/particle_vertex.vs"));
        mParticleShaderProgram.createFragmentShader(FileUtils.loadResource("/shaders/particle_fragment.fs"));
        mParticleShaderProgram.link();

        ShaderUtils.setUpShaderUniforms(
                mParticleShaderProgram,
                new String[] {
                        "projectionMatrix",
                        "nonInstancedModelViewMatrix",
//                        "viewMatrix",
                        "numColumns",
                        "numRows",
                        "textureSampler",
                        "nonInstancedTextOffsetX",
                        "nonInstancedTextOffsetY",
                        "isInstanced",
                        "nonInstancedModelViewMatrix",
                        "useTexture",
                        "nonInstancedParticleColour"
                }
        );
    }

    private void setUpHudShader() throws Exception {
        mHudShaderProgram = new ShaderProgram();
        mHudShaderProgram.createVertexShader(FileUtils.loadResource("/shaders/hud_vertex.vs"));
        mHudShaderProgram.createFragmentShader(FileUtils.loadResource("/shaders/hud_fragment.fs"));
        mHudShaderProgram.link();

        ShaderUtils.setUpShaderUniforms(
                mHudShaderProgram,
                new String[] {
                        "projectionModelMatrix",
                        "colour",
                        "textureSampler"
                }
        );
    }

    public void render(Window window, Camera camera, Scene scene, boolean sceneChanged) throws Exception {
        clear();

        filter(window, scene, camera.getViewMatrix());

//        // Render depth map before view ports has been set up
//        if (scene.isRenderShadows() && sceneChanged) {
//            shadowRenderer.render(window, scene, camera, transformation, this);
//        }

        //set the viewport for the window each cycle
        glViewport(0, 0, window.getWidth(), window.getHeight());

        //Update projection matrix once per render cycle
        window.updateProjectionMatrix(camera.getFov(), camera.getViewDistanceStart(), camera.getViewDistanceEnd());

        //this has been left here as a reminder that the camera can also be updated in the game logic where the dev has more control
        //camera.updateViewMatrix();

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

    private void filter(Window window, Scene scene, Matrix4f viewMatrix) {
        //filter items outside of the camera's view frustum before rendering
        if (window.getOptions().frustumCulling) {
            mFrustumFilter.updateFrustum(window.getProjectionMatrix(), viewMatrix);
            mFrustumFilter.filter(scene.getGameItemMeshMap());
            mFrustumFilter.filter(scene.getGameItemInstancedMeshMap());

            if (scene.getParticleEmitters() != null && scene.getParticleEmitters().length > 0) {
                mFrustumFilter.filterParticleEmitters(scene.getParticleEmitters());
            }
        }
    }

    public void renderScene(Window window, Camera camera, Scene scene) {
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f lightViewMatrix = mTransformation.getLightViewMatrix();

        mSceneShaderProgram.bind();

        mSceneShaderProgram.setUniform(
                "textureSampler",
                0
        );
        mSceneShaderProgram.setUniform(
                "projectionMatrix",
                window.getProjectionMatrix()
        );

        if (scene.getGameItemMeshMap().size() > 0) {
            renderNonInstancedMeshes(
                    scene,
                    mSceneShaderProgram,
                    viewMatrix,
                    lightViewMatrix
            );
        }

        if (scene.getGameItemInstancedMeshMap().size() > 0) {
            renderInstancedMeshes(
                    scene,
                    mSceneShaderProgram,
                    viewMatrix,
                    lightViewMatrix
            );
        }

        mSceneShaderProgram.unbind();
    }

    private void renderNonInstancedMeshes(
            Scene scene,
            ShaderProgram shaderProgram,
            Matrix4f viewMatrix,
            Matrix4f lightViewMatrix
    ) {
        shaderProgram.setUniform("isInstanced", 0);

        Map<Mesh, List<GameItem>> meshGameItemMap = scene.getGameItemMeshMap();

        for (Mesh mesh : meshGameItemMap.keySet()) {
            if (viewMatrix != null) {
                shaderProgram.setUniform("material", mesh.getMaterial());

//                glActiveTexture(GL_TEXTURE_2D);
//                glBindTexture(GL_TEXTURE_2D, mShadowMap.getDepthMapTexture().getId());
            }

            Texture texture = mesh.getMaterial().getTexture();

            if (texture != null) {
//                shaderProgram.setUniform("columnNumber", texture.getNumColumns());
//                shaderProgram.setUniform("rowNumber", texture.getNumRows());
            }

            mFilteredGameItemList.clear();
            for (GameItem gameItem : meshGameItemMap.get(mesh)) {
                if (gameItem.isInsideFrustum()) {
                    mFilteredGameItemList.add(gameItem);
                }
            }

            mesh.renderList(mFilteredGameItemList, (GameItem gameItem) -> {
                Matrix4f modelMatrix =
                        mTransformation.generateModelMatrix(gameItem);
//                Matrix4f modelLightViewMatrix =
//                        mTransformation.generateModelLightViewMatrix(modelMatrix, lightViewMatrix);


//                shaderProgram.setUniform(
//                        "nonInstancedModelLightViewMatrix",
//                        modelLightViewMatrix
//                );

                if (viewMatrix != null) {
                    shaderProgram.setUniform(
                            "nonInstancedModelViewMatrix",
                            mTransformation.generateModelViewMatrix(modelMatrix, viewMatrix)
                    );
                }

//                if (gameItem instanceof AnimGameItem) {
//                    AnimGameItem animGameItem = (AnimGameItem) gameItem;
//                    AnimatedFrame animatedFrame = animGameItem.getCurrentFrame();
//
//                    shaderProgram.setUniform("jointMatrices", animFrame.getJointMatrices());
//                }
            });
        }
    }

    /*
    TODO:
    Currently does not support animations
     */
    private void renderInstancedMeshes(
            Scene scene,
            ShaderProgram shaderProgram,
            Matrix4f viewMatrix,
            Matrix4f lightViewMatrix
    ) {
        shaderProgram.setUniform("isInstanced", 1);

        Map<InstancedMesh, List<GameItem>> instancedMeshMap = scene.getGameItemInstancedMeshMap();

        for (InstancedMesh mesh : instancedMeshMap.keySet()) {
            Texture texture = mesh.getMaterial().getTexture();

            if (texture != null) {
//                shaderProgram.setUniform("columnNumber", texture.getNumColumns());
//                shaderProgram.setUniform("rowNumber", texture.getNumRows());
            }

            if (viewMatrix != null) {
                shaderProgram.setUniform("material", mesh.getMaterial());

//                glActiveTexture(GL_TEXTURE2);
//                glBindTexture(GL_TEXTURE_2D, mShadowMap.getDepthMapTexture().getId());
            }

            mFrustumFilter.populateFilteredList(
                    instancedMeshMap.get(mesh),
                    mFilteredGameItemList
            );

            mesh.renderInstancedList(
                    mFilteredGameItemList,
                    mTransformation,
                    viewMatrix,
                    lightViewMatrix
            );
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
        Matrix4f vm = camera.getViewMatrix();

        //store the translation elements of the view matrix
        float m30 = vm.m30();
        float m31 = vm.m31();
        float m32 = vm.m32();

        if (skybox.isInFixedPosition()) {
            //remove the translation elements of the view matrix
            vm.m30(0);
            vm.m31(0);
            vm.m32(0);
        }

        mSkyBoxShaderProgram.bind();

        mSkyBoxShaderProgram.setUniform("textureSampler", 0);
        mSkyBoxShaderProgram.setUniform("projectionMatrix", window.getProjectionMatrix());
        mSkyBoxShaderProgram.setUniform(
                "modelViewMatrix",
                mTransformation.generateModelViewMatrix(skybox, vm)
        );
        mSkyBoxShaderProgram.setUniform(
                "useTexture",
                skybox.getMesh().getMaterial().isUsingTexture() ? 1 : 0
        );
        mSkyBoxShaderProgram.setUniform("colour", skybox.getMesh().getMaterial().getColour());

        skybox.getMesh().render();

        if (skybox.isInFixedPosition()) {
            //put the stored elements back into view matrix
            vm.m30(m30);
            vm.m31(m31);
            vm.m32(m32);
        }

        mSkyBoxShaderProgram.unbind();
    }

    private void renderParticles(Window window, Camera camera, Scene scene) throws Exception {
        Matrix4f viewMatrix = camera.getViewMatrix();

        mParticleShaderProgram.bind();

        mParticleShaderProgram.setUniform("textureSampler", 0);
        mParticleShaderProgram.setUniform(
                "projectionMatrix",
                window.getProjectionMatrix()
        );
//        mParticleShaderProgram.setUniform("viewMatrix", camera.getViewMatrix());

        glDepthMask(false);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);

        mFrustumFilter.populateFilteredList(
                scene.getParticleEmitters(),
                mFilteredParticleEmitterList
        );

        renderParticleEmitters(mFilteredParticleEmitterList, viewMatrix);

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDepthMask(true);

        mParticleShaderProgram.unbind();
    }

    private void renderParticleEmitters(
            List<IParticleEmitter> particleEmitterList,
            Matrix4f viewMatrix
    ) throws Exception {
        for (IParticleEmitter emitter : particleEmitterList) {

            if (
                    (!emitter.isActive() || !emitter.isInsideFrustum()) &&
                    !emitter.isFrustumCullingParticles()
            ) {
                continue;
            }

            Mesh mesh = emitter.getBaseParticle().getMesh();
            boolean useTexture = mesh.getMaterial().isUsingTexture();
            Texture texture = mesh.getMaterial().getTexture();

            mParticleShaderProgram.setUniform(
                    "numColumns",
                    useTexture ? texture.getNumColumns() : 1
            );
            mParticleShaderProgram.setUniform(
                    "numRows",
                    useTexture ? texture.getNumRows() : 1
            );
            mParticleShaderProgram.setUniform(
                    "useTexture",
                    useTexture ? 1 : 0
            );

            if (mesh instanceof InstancedMesh) {
                renderInstancedParticleEmitter(emitter, mesh, viewMatrix, useTexture);
            } else {
                renderNonInstancedParticleEmitter(emitter, mesh, viewMatrix, texture, useTexture);
            }
        }
    }

    private void renderInstancedParticleEmitter(
            IParticleEmitter emitter,
            Mesh mesh,
            Matrix4f viewMatrix,
            boolean useTexture
    ) throws Exception {
        if (!useTexture) {
            throw new Exception("Instanced Particles must use a texture");
        }

        InstancedMesh instancedMesh = (InstancedMesh) mesh;

        mParticleShaderProgram.setUniform("isInstanced", 1);
        mParticleShaderProgram.setUniform("useTexture", 1);

        mFrustumFilter.populateFilteredList(
                emitter,
                mFilteredGameItemList
        );

        instancedMesh.renderInstancedList(
                emitter.isFrustumCullingParticles() ? mFilteredGameItemList : emitter.getParticles(),
                true,
                mTransformation,
                viewMatrix,
                null
        );
    }

    private void renderNonInstancedParticleEmitter(
            IParticleEmitter emitter,
            Mesh mesh,
            Matrix4f viewMatrix,
            Texture texture,
            boolean useTexture
    ) {
        mParticleShaderProgram.setUniform("isInstanced", 0);

        mFrustumFilter.populateFilteredList(
                emitter,
                mFilteredGameItemList
        );

        mesh.renderList(
                emitter.isFrustumCullingParticles() ? mFilteredGameItemList : emitter.getParticles(),
                (GameItem gameItem) -> {
                    if (useTexture) {
                        int column = gameItem.getTexturePos() % texture.getNumColumns();
                        int row = gameItem.getTexturePos() / texture.getNumColumns();
                        float textOffsetX = (float) column / texture.getNumColumns();
                        float textOffsetY = (float) row / texture.getNumRows();

                        mParticleShaderProgram.setUniform(
                                "nonInstancedTextOffsetX",
                                textOffsetX
                        );
                        mParticleShaderProgram.setUniform(
                                "nonInstancedTextOffsetY",
                                textOffsetY
                        );
                    }

                    mParticleShaderProgram.setUniform(
                            "nonInstancedParticleColour",
                            ((Particle) gameItem).getParticleColour()
                    );

                    Matrix4f modelMatrix = mTransformation.generateModelMatrix(gameItem);
                    viewMatrix.transpose3x3(modelMatrix);
                    viewMatrix.scale(gameItem.getScale());
                    Matrix4f modelViewMatrix = mTransformation.generateModelViewMatrix(
                            modelMatrix, viewMatrix
                    );
                    modelViewMatrix.scale(gameItem.getScale());
                    mParticleShaderProgram.setUniform(
                            "nonInstancedModelViewMatrix",
                            modelViewMatrix
                    );
                });
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void cleanUp() {
        if (mSceneShaderProgram != null) {
            mSceneShaderProgram.cleanUp();
        }

        if (mHudShaderProgram != null) {
            mHudShaderProgram.cleanUp();
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