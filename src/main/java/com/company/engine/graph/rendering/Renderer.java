package com.company.engine.graph.rendering;

import com.company.engine.IUsesResources;
import com.company.engine.graph.lighting.*;
import com.company.engine.graph.material.Texture;
import com.company.engine.graph.Transformation;
import com.company.engine.graph.particles.Particle;
import com.company.engine.scene.SceneLighting;
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
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;

public class Renderer implements IUsesResources {

    private static final int MAX_POINT_LIGHTS = 5;
    private static final int MAX_SPOT_LIGHTS = 5;
    private static final float DEFAULT_SPECULAR_POWER = 10;

    /**
     * Integer Keys storing the location of
     * ShaderPrograms in mShaderProgramMap.
     */
    private static final int DEPTH_SHADER_KEY = 0;
    private static final Integer SKY_BOX_SHADER_KEY = 1;
    private static final Integer SCENE_SHADER_KEY = 2;
    private static final Integer PARTICLE_SHADER_KEY = 3;
    private static final Integer HUD_SHADER_KEY = 4;

    /**
     * Location of which texture banks the engine uses to store
     * the model's textures.
     */
    public static final int TEXTURE_BANK_INDEX = 0;
    public static final int NORMAL_MAP_BANK_INDEX = 1;
//    public static final int SHADOW_MAP_BANK_INDEX = 2;

    private final Transformation mTransformation;
    private final float mSpecularPower;

    //Frustum culling
    private final FrustumFilter mFrustumFilter;
    private final List<GameItem> mFilteredGameItemList;
    private final List<IParticleEmitter> mFilteredParticleEmitterList;

    private Map<Integer, ShaderProgram> mShaderProgramMap;

    private boolean mCullingFacesEnabled;

    //Shadows
//    private ShadowMap mShadowMap;

    public Renderer() {
        mShaderProgramMap = new HashMap<>();
        mTransformation = new Transformation();
        mFrustumFilter = new FrustumFilter();
        mFilteredGameItemList = new ArrayList<>();
        mFilteredParticleEmitterList = new ArrayList<>();
        mSpecularPower = DEFAULT_SPECULAR_POWER;
    }

    @Override
    public void cleanUp() {
        for (ShaderProgram shaderProgram : mShaderProgramMap.values()) {
            if (shaderProgram != null) {
                shaderProgram.cleanUp();
            }
        }
    }

    public void init(Window window) throws Exception {
//        mShadowMap = new ShadowMap(window.getOptions().shadowMapSizeMultiplier);

//        setUpDepthShader();
        setUpSkyBoxShader();
        setUpSceneShader();
        setUpParticleShader();
        setUpHudShader();

        mCullingFacesEnabled = window.getOptions().enableCullFaces;
    }

    private void setUpDepthShader() throws Exception {
        ShaderProgram depthShaderProgram = new ShaderProgram();
        depthShaderProgram.createVertexShader(FileUtils.loadResource(
                "/shaders/depth_vertex.vs"
        ));
        depthShaderProgram.createFragmentShader(FileUtils.loadResource(
                "/shaders/depth_fragment.fs"
        ));
        depthShaderProgram.link();

        depthShaderProgram.createUniform("isInstanced");
        depthShaderProgram.createUniform("orthoProjectionMatrix");
        depthShaderProgram.createUniform("nonInstancedModelLightViewMatrix");
//        depthShaderProgram.createUniform("jointsMatrix");

        mShaderProgramMap.put(DEPTH_SHADER_KEY, depthShaderProgram);
    }

    private void setUpSkyBoxShader() throws Exception {
        ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(FileUtils.loadResource(
                "/shaders/skyBox_vertex.vs"
        ));
        shaderProgram.createFragmentShader(FileUtils.loadResource(
                "/shaders/skyBox_fragment.fs"
        ));
        shaderProgram.link();

        ShaderUtils.createShaderUniforms(
                shaderProgram,
                new String[] {
                        "textureSampler",
                        "useTexture",
                        "colour",
                        "projectionMatrix",
                        "modelViewMatrix"
                }
        );

        mShaderProgramMap.put(SKY_BOX_SHADER_KEY, shaderProgram);
    }

    private void setUpSceneShader() throws Exception {
        ShaderProgram sceneShaderProgram = new ShaderProgram();
        sceneShaderProgram.createVertexShader(FileUtils.loadResource(
                "/shaders/scene_vertex.vs"
        ));
        sceneShaderProgram.createFragmentShader(FileUtils.loadResource(
                "/shaders/scene_fragment.fs"
        ));
        sceneShaderProgram.link();

        sceneShaderProgram.createUniform("isInstanced");
//        sceneShaderProgram.createUniform("isRenderingShadows");

        //matrices
        sceneShaderProgram.createUniform("projectionMatrix");
        sceneShaderProgram.createUniform("nonInstancedModelViewMatrix");
//        sceneShaderProgram.createUniform("orthoProjectionMatrix");
//        sceneShaderProgram.createUniform("lightViewMatrix");

        //textures
        sceneShaderProgram.createUniform("textureSampler");
        sceneShaderProgram.createUniform("normalMap");
        sceneShaderProgram.createUniform("textureColumnCount");
        sceneShaderProgram.createUniform("textureRowCount");
//        sceneShaderProgram.createUniform("shadowMap");

        //material
        sceneShaderProgram.createMaterialUniform("material");
        sceneShaderProgram.createUniform("specularPower");

        //lighting
        sceneShaderProgram.createUniform("ambientLight");
        sceneShaderProgram.createPointLightArrayUniform(
                "pointLightArray",
                MAX_POINT_LIGHTS
        );
        sceneShaderProgram.createSpotLightArrayUniform(
                "spotLightArray",
                MAX_SPOT_LIGHTS
        );
        sceneShaderProgram.createDirectionalLightUniform(
                "directionalLight"
        );

        mShaderProgramMap.put(SCENE_SHADER_KEY, sceneShaderProgram);
    }

    private void setUpParticleShader() throws Exception {
        ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(FileUtils.loadResource(
                "/shaders/particle_vertex.vs"
        ));
        shaderProgram.createFragmentShader(FileUtils.loadResource(
                "/shaders/particle_fragment.fs"
        ));
        shaderProgram.link();

        ShaderUtils.createShaderUniforms(
                shaderProgram,
                new String[] {
                        "projectionMatrix",
                        "nonInstancedModelViewMatrix",
//                        "viewMatrix",
                        "textureColumnCount",
                        "textureRowCount",
                        "textureSampler",
                        "nonInstancedTextOffsetX",
                        "nonInstancedTextOffsetY",
                        "isInstanced",
                        "nonInstancedModelViewMatrix",
                        "useTexture",
                        "nonInstancedParticleColour"
                }
        );

        mShaderProgramMap.put(PARTICLE_SHADER_KEY, shaderProgram);
    }

    private void setUpHudShader() throws Exception {
        ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(FileUtils.loadResource(
                "/shaders/hud_vertex.vs"
        ));
        shaderProgram.createFragmentShader(FileUtils.loadResource(
                "/shaders/hud_fragment.fs"
        ));
        shaderProgram.link();

        ShaderUtils.createShaderUniforms(
                shaderProgram,
                new String[] {
                        "projectionModelMatrix",
                        "colour",
                        "textureSampler"
                }
        );

        mShaderProgramMap.put(HUD_SHADER_KEY, shaderProgram);
    }

    public void render(Window window, Camera camera, Scene scene) throws Exception {
        clear();

        filter(window, scene, camera.getViewMatrix());

        //set the viewport for the window each cycle
        glViewport(0, 0, window.getWidth(), window.getHeight());

        //Update projection matrix once per render cycle
        window.updateProjectionMatrix(
                camera.getFov(),
                camera.getViewDistanceStart(),
                camera.getViewDistanceEnd()
        );

        if (!window.getOptions().applicationUpdatesCamera) {
            camera.updateViewMatrix();
        }

        renderScene(window, camera, scene);

        if (scene.getSkyBox() != null) {
            renderSkyBox(window, camera, scene);
        }

        if (scene.getParticleEmitters() != null && scene.getParticleEmitters().length > 0) {
            renderParticles(window, camera, scene);
        }

        if (scene.getHud() != null) {
            renderHud(window, camera, scene);
        }
    }

    /*
    TODO: Shadows
        And Currently only supports DirectionalLight
     */
//    protected void renderDepthMap(Window window, Camera camera, Scene scene) {
//        ShaderProgram depthShaderProgram = mShaderProgramMap.get(DEPTH_SHADER_KEY);
//        DirectionalLight dirLight;
//        OrthoCoords oc;
//        float lightAngleX;
//        float lightAnlgeY;
//        float lightAngleZ;
//        Matrix4f lightViewMatrix = new Matrix4f();
//        Matrix4f orthoProjectionMatrix = new Matrix4f();
//
//        //setup view port to math the texture size
//        glBindFramebuffer(GL_FRAMEBUFFER, mShadowMap.getDepthMapFbo());
//        glViewport(
//                0,
//                0,
//                ShadowMap.SHADOW_MAP_DEFAULT_WIDTH,
//                ShadowMap.SHADOW_MAP_DEFAULT_HEIGHT
//        );
//        glClear(GL_DEPTH_BUFFER_BIT);
//
//        depthShaderProgram.bind();
//
//        //directional light
//        dirLight = scene.getSceneLighting().getDirectionLight();
//        if (dirLight != null) {
//            oc = dirLight.getOrthoCoords();
//            lightAngleX = (float) Math.toDegrees(
//                    Math.cos(dirLight.getDirection().z)
//            );
//            lightAnlgeY = (float) Math.toDegrees(
//                    Math.asin(dirLight.getDirection().x)
//            );
//            lightAngleZ = 0;
//
//            mTransformation.updateLightViewMatrix(
//                    new Vector3f(dirLight.getDirection())
//                            .mul(dirLight.getShadowPositionMultiplier()),
//                    new Vector3f(lightAngleX, lightAnlgeY, lightAngleZ)
//            );
//            lightViewMatrix = mTransformation.getLightViewMatrix();
//
//            mTransformation.updateOrthographicProjectionMatrix(
//                    oc.getLeft(),
//                    oc.getRight(),
//                    oc.getBottom(),
//                    oc.getTop(),
//                    oc.getNear(),
//                    oc.getFar()
//            );
//            orthoProjectionMatrix = mTransformation.getOrthographicProjectionMatrix();
//        }
//
//        depthShaderProgram.setUniform(
//                "orthoProjectionMatrix",
//                orthoProjectionMatrix
//        );
//
//        //TODO: PointLights
//        //TODO: SpotLights
//
//        if (scene.getGameItemMeshMap().size() > 0) {
//            renderNonInstancedMeshes(
//                    scene,
//                    depthShaderProgram,
//                    null,
//                    lightViewMatrix
//            );
//        }
//
//        if (scene.getGameItemInstancedMeshMap().size() > 0) {
//            renderInstancedMeshes(
//                    scene,
//                    depthShaderProgram,
//                    null,
//                    lightViewMatrix
//            );
//        }
//
//        //unbind
//        depthShaderProgram.unbind();
//        glBindFramebuffer(GL_FRAMEBUFFER, 0);
//    }

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

    private void renderScene(Window window, Camera camera, Scene scene) {
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f lightViewMatrix = mTransformation.getLightViewMatrix();
        ShaderProgram sceneShaderProgram = mShaderProgramMap.get(SCENE_SHADER_KEY);

        sceneShaderProgram.bind();

//        if (scene.isRenderingShadows()) {
//            renderDepthMap(window, camera, scene);
//        }

        sceneShaderProgram.setUniform(
                "textureSampler",
                TEXTURE_BANK_INDEX
        );
        sceneShaderProgram.setUniform(
                "normalMap",
                NORMAL_MAP_BANK_INDEX
        );
//        sceneShaderProgram.setUniform(
//                "shadowMap",
//                SHADOW_MAP_BANK_INDEX
//        );
//        sceneShaderProgram.setUniform(
//                "isRenderingShadows",
//                scene.isRenderingShadows() ? ShaderProgram.SHADER_TRUE : ShaderProgram.SHADER_FALSE
//        );
        sceneShaderProgram.setUniform(
                "projectionMatrix",
                window.getProjectionMatrix()
        );

        if (scene.getGameItemMeshMap().size() > 0) {
            renderNonInstancedMeshes(
                    scene,
                    sceneShaderProgram,
                    viewMatrix,
                    lightViewMatrix,
                    window.getOptions().enableCullFaces
            );
        }

        if (scene.getGameItemInstancedMeshMap().size() > 0) {
            renderInstancedMeshes(
                    scene,
                    sceneShaderProgram,
                    viewMatrix,
                    lightViewMatrix,
                    window.getOptions().enableCullFaces
            );
        }

        if (!window.getOptions().disableAllLighting && scene.getSceneLighting() != null) {
            renderSceneLighting(
                    camera.getViewMatrix(),
                    scene.getSceneLighting()
            );
        }

        sceneShaderProgram.unbind();
    }

    private void renderSceneLighting(
            Matrix4f viewMatrix,
            SceneLighting sceneLighting
    ) {
        ShaderProgram sceneShaderProgram = mShaderProgramMap.get(SCENE_SHADER_KEY);
        sceneShaderProgram.setUniform(
                "ambientLight",
                sceneLighting.getAmbientLight() != null ?
                        sceneLighting.getAmbientLight() : SceneLighting.DEFAULT_AMBIENT_LIGHT
        );
        sceneShaderProgram.setUniform("specularPower", mSpecularPower);

        //point lights
        List<PointLight> pointLightList = sceneLighting.getPointLightList();
        int length = pointLightList != null ? pointLightList.size() : 0;

        for (int i = 0; i < length; i++) {
            //get each point light object and transform its position to view coords
            PointLight pointLight = new PointLight(pointLightList.get(i));
            Vector3f lightPosition = pointLight.getPosition();
            Vector4f aux = new Vector4f(lightPosition, 1);

            aux.mul(viewMatrix);
            lightPosition.x = aux.x;
            lightPosition.y = aux.y;
            lightPosition.z = aux.z;

            sceneShaderProgram.setUniform("pointLightArray", pointLight, i);
        }

        //spot lights
        List<SpotLight> spotLightList = sceneLighting.getSpotLightList();
        length = spotLightList != null ? spotLightList.size() : 0;

        for (int i = 0; i < length; i++) {
            SpotLight spotLight = new SpotLight(spotLightList.get(i));
            Vector4f direction = new Vector4f(spotLight.getConeDirection(), 0);

            direction.mul(viewMatrix);
            spotLight.setConeDirection(new Vector3f(
                    direction.x,
                    direction.y,
                    direction.z
            ));

            Vector3f lightPosition = spotLight.getPointLight().getPosition();
            Vector4f aux = new Vector4f(lightPosition, 1);

            aux.mul(viewMatrix);
            lightPosition.x = aux.x;
            lightPosition.y = aux.y;
            lightPosition.z = aux.z;

            sceneShaderProgram.setUniform("spotLightArray", spotLight, i);
        }

        //directional light
        if (sceneLighting.getDirectionLight() != null) {
            DirectionalLight dirLight = new DirectionalLight(sceneLighting.getDirectionLight());
            Vector4f direction = new Vector4f(dirLight.getDirection(), 0);

            direction.mul(viewMatrix);

            dirLight.setDirection(new Vector3f(
                    direction.x,
                    direction.y,
                    direction.z
            ));
            sceneShaderProgram.setUniform("directionalLight", dirLight);
        }
    }

    private void renderNonInstancedMeshes(
            Scene scene,
            ShaderProgram shaderProgram,
            Matrix4f viewMatrix,
            Matrix4f lightViewMatrix,
            boolean windowCullsFaces
    ) {
//        boolean isDepthShader = shaderProgram == mShaderProgramMap.get(DEPTH_SHADER_KEY);
        Map<Mesh, List<GameItem>> meshGameItemMap;

        if (windowCullsFaces) {
            Map<Mesh, List<GameItem>> cullFacesEnabledMeshMap =
                    generateCullFacesMeshMap(
                            scene.getGameItemMeshMap(),
                            true
                    );

            Map<Mesh, List<GameItem>> cullFacesDisabledMeshMap =
                    generateCullFacesMeshMap(
                            scene.getGameItemMeshMap(),
                            false
                    );

            //replace meshGameItemMap with the two above
            meshGameItemMap = new HashMap<>();

            //populate meshGameItemMap with cull faces enabled meshes
            for (Mesh mesh :
                    cullFacesEnabledMeshMap.keySet()
            ) {
                meshGameItemMap.put(
                        mesh,
                        cullFacesEnabledMeshMap.get(mesh)
                );
            }

            //populate meshGameItemMap with cull faces disabled meshes
            for (Mesh mesh :
                    cullFacesDisabledMeshMap.keySet()
            ) {
                meshGameItemMap.put(
                        mesh,
                        cullFacesDisabledMeshMap.get(mesh)
                );
            }
        } else {
            meshGameItemMap = scene.getGameItemMeshMap();
        }

        mShaderProgramMap.get(SCENE_SHADER_KEY).setUniform(
                "isInstanced",
                ShaderProgram.SHADER_FALSE
        );

        for (Mesh mesh : meshGameItemMap.keySet()) {
            if (mesh.isCullingFaces()) {
                enableFaceCulling(true);
            } else {
                enableFaceCulling(false);
            }

            if (viewMatrix != null) {
                shaderProgram.setUniform("material", mesh.getMaterial());

//                if (scene.isRenderingShadows()) {
//                    glActiveTexture(GL_TEXTURE_2D);
//                    glBindTexture(GL_TEXTURE_2D, mShadowMap.getDepthMapTexture().getId());
//                }
            }

            Texture texture = mesh.getMaterial().getTexture();

            if (texture != null) {
                mShaderProgramMap.get(SCENE_SHADER_KEY).setUniform(
                        "textureColumnCount",
                        texture.getNumColumns()
                );
                mShaderProgramMap.get(SCENE_SHADER_KEY).setUniform(
                        "textureRowCount",
                        texture.getNumRows()
                );
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

//                if (isDepthShader) {
//                    Matrix4f modelLightViewMatrix =
//                            mTransformation.generateModelLightViewMatrix(modelMatrix, lightViewMatrix);
//
//
//                    shaderProgram.setUniform(
//                            "nonInstancedModelLightViewMatrix",
//                            modelLightViewMatrix
//                    );
//                }

                if (viewMatrix != null) {
                    mShaderProgramMap.get(SCENE_SHADER_KEY).setUniform(
                            "nonInstancedModelViewMatrix",
                            mTransformation.generateModelViewMatrix(modelMatrix, viewMatrix)
                    );
                }

//                if (lightViewMatrix != null && !isDepthShader) {
//                    mShaderProgramMap.get(SCENE_SHADER_KEY).setUniform("lightViewMatrix", lightViewMatrix);
//                }

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
            Matrix4f lightViewMatrix,
            boolean windowCullsFaces
    ) {
//        boolean isDepthShader = shaderProgram == mShaderProgramMap.get(DEPTH_SHADER_KEY);

        shaderProgram.setUniform("isInstanced", ShaderProgram.SHADER_TRUE);

        Map<InstancedMesh, List<GameItem>> instancedMeshMap;

        if (windowCullsFaces) {
            Map<InstancedMesh, List<GameItem>> cullFacesEnabledInstancedMeshMap =
                    generateCullFacesInstancedMeshMap(
                            scene.getGameItemInstancedMeshMap(),
                            true
                    );

            Map<InstancedMesh, List<GameItem>> cullFacesDisabledInstancedMeshMap =
                    generateCullFacesInstancedMeshMap(
                            scene.getGameItemInstancedMeshMap(),
                            false
                    );

            //replace instancedMeshMap with the two above
            instancedMeshMap = new HashMap<>();

            //populate instancedMeshMap with cull faces enabled meshes
            for (InstancedMesh instancedMesh :
                    cullFacesEnabledInstancedMeshMap.keySet()
            ) {
                instancedMeshMap.put(
                        instancedMesh,
                        cullFacesEnabledInstancedMeshMap.get(instancedMesh)
                );
            }

            //populate instancedMeshMap with cull faces disabled meshes
            for (InstancedMesh instancedMesh :
                    cullFacesDisabledInstancedMeshMap.keySet()
            ) {
                instancedMeshMap.put(
                        instancedMesh,
                        cullFacesDisabledInstancedMeshMap.get(instancedMesh)
                );
            }
        } else {
            instancedMeshMap = scene.getGameItemInstancedMeshMap();
        }

        for (InstancedMesh mesh : instancedMeshMap.keySet()) {
            if (mesh.isCullingFaces()) {
                enableFaceCulling(true);
            } else {
                enableFaceCulling(false);
            }

            Texture texture = mesh.getMaterial().getTexture();

            if (texture != null) {
                shaderProgram.setUniform("textureColumnCount", texture.getNumColumns());
                shaderProgram.setUniform("textureRowCount", texture.getNumRows());
            }

            if (viewMatrix != null) {
                shaderProgram.setUniform("material", mesh.getMaterial());

//                if (scene.isRenderingShadows()) {
//                    glActiveTexture(GL_TEXTURE2);
//                    glBindTexture(GL_TEXTURE_2D, mShadowMap.getDepthMapTexture().getId());
//                }
            }

//            if (lightViewMatrix != null /*&& !isDepthShader*/) {
//                shaderProgram.setUniform("lightViewMatrix", lightViewMatrix);
//            }

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
        ShaderProgram hudShaderProgram = mShaderProgramMap.get(HUD_SHADER_KEY);
        hudShaderProgram.bind();

        Matrix4f orthoProjection = mTransformation.generateOrtho2DProjectionMatrix(
                0, window.getWidth(), window.getHeight(), 0
        );

        /*
        TODO: MIGHT BE ABLE TO CHANGE THIS TO RENDERING PER MESH TYPE
         */
        for (GameItem gameItem : scene.getHud().getGameItems()) {
            hudShaderProgram.setUniform(
                    "projectionModelMatrix",
                    mTransformation.generateOrthoProjectionModelMatrix(
                            gameItem,
                            orthoProjection
                    )
            );
            hudShaderProgram.setUniform(
                    "colour",
                    gameItem.getMesh().getMaterial().getColour()
            );

            gameItem.getMesh().render();
        }

        hudShaderProgram.unbind();
    }

    private void renderSkyBox(Window window, Camera camera, Scene scene) {
        SkyBox skybox = scene.getSkyBox();
        Matrix4f vm = camera.getViewMatrix();
        ShaderProgram skyBoxShaderProgram = mShaderProgramMap.get(SKY_BOX_SHADER_KEY);

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

        skyBoxShaderProgram.bind();

        skyBoxShaderProgram.setUniform("textureSampler", 0);
        skyBoxShaderProgram.setUniform(
                "projectionMatrix",
                window.getProjectionMatrix()
        );
        skyBoxShaderProgram.setUniform(
                "modelViewMatrix",
                mTransformation.generateModelViewMatrix(skybox, vm)
        );
        skyBoxShaderProgram.setUniform(
                "useTexture",
                skybox.getMesh().getMaterial().isUsingTexture() ? 1 : 0
        );
        skyBoxShaderProgram.setUniform(
                "colour",
                skybox.getMesh().getMaterial().getColour()
        );

        for (Mesh mesh : skybox.getMeshArray()) {
            mesh.render();
        }

        if (skybox.isInFixedPosition()) {
            //put the stored elements back into view matrix
            vm.m30(m30);
            vm.m31(m31);
            vm.m32(m32);
        }

        skyBoxShaderProgram.unbind();
    }

    private void renderParticles(
            Window window,
            Camera camera,
            Scene scene
    ) throws Exception {
        Matrix4f viewMatrix = camera.getViewMatrix();
        ShaderProgram particleShaderProgram = mShaderProgramMap.get(PARTICLE_SHADER_KEY);

        particleShaderProgram.bind();

        particleShaderProgram.setUniform("textureSampler", 0);
        particleShaderProgram.setUniform(
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

        renderParticleEmitters(
                mFilteredParticleEmitterList,
                viewMatrix,
                particleShaderProgram
        );

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDepthMask(true);

        particleShaderProgram.unbind();
    }

    private void renderParticleEmitters(
            List<IParticleEmitter> particleEmitterList,
            Matrix4f viewMatrix,
            ShaderProgram particleShaderProgram
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

            particleShaderProgram.setUniform(
                    "textureColumnCount",
                    useTexture ? texture.getNumColumns() : 1
            );
            particleShaderProgram.setUniform(
                    "textureRowCount",
                    useTexture ? texture.getNumRows() : 1
            );
            particleShaderProgram.setUniform(
                    "useTexture",
                    useTexture ? 1 : 0
            );

            if (mesh instanceof InstancedMesh) {
                renderInstancedParticleEmitter(
                        particleShaderProgram,
                        emitter,
                        mesh,
                        viewMatrix,
                        useTexture
                );
            } else {
                renderNonInstancedParticleEmitter(
                        particleShaderProgram,
                        emitter,
                        mesh,
                        viewMatrix,
                        texture,
                        useTexture
                );

            }
        }
    }

    private void renderInstancedParticleEmitter(
            ShaderProgram particleShaderProgram,
            IParticleEmitter emitter,
            Mesh mesh,
            Matrix4f viewMatrix,
            boolean useTexture
    ) throws Exception {
        if (!useTexture) {
            throw new Exception("Instanced Particles must use a texture");
        }

        InstancedMesh instancedMesh = (InstancedMesh) mesh;

        particleShaderProgram.setUniform("isInstanced", 1);
        particleShaderProgram.setUniform("useTexture", 1);

        mFrustumFilter.populateFilteredList(
                emitter,
                mFilteredGameItemList
        );

        instancedMesh.renderInstancedList(
                emitter.isFrustumCullingParticles() ? mFilteredGameItemList : emitter.getParticleList(),
                true,
                mTransformation,
                viewMatrix,
                null
        );
    }

    private void renderNonInstancedParticleEmitter(
            ShaderProgram particleShaderProgram,
            IParticleEmitter emitter,
            Mesh mesh,
            Matrix4f viewMatrix,
            Texture texture,
            boolean useTexture
    ) {
        particleShaderProgram.setUniform("isInstanced", 0);

        mFrustumFilter.populateFilteredList(
                emitter,
                mFilteredGameItemList
        );

        mesh.renderList(
                emitter.isFrustumCullingParticles() ? mFilteredGameItemList : emitter.getParticleList(),
                (GameItem gameItem) -> {
                    if (useTexture) {
                        int column = gameItem.getTexturePos() % texture.getNumColumns();
                        int row = gameItem.getTexturePos() / texture.getNumColumns();
                        float textOffsetX = (float) column / texture.getNumColumns();
                        float textOffsetY = (float) row / texture.getNumRows();

                        particleShaderProgram.setUniform(
                                "nonInstancedTextOffsetX",
                                textOffsetX
                        );
                        particleShaderProgram.setUniform(
                                "nonInstancedTextOffsetY",
                                textOffsetY
                        );
                    }

                    particleShaderProgram.setUniform(
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
                    particleShaderProgram.setUniform(
                            "nonInstancedModelViewMatrix",
                            modelViewMatrix
                    );
                });
    }

    /**
     * Enable GL face culling if it is not already enabled.
     *
     * The use of the member boolean mCullingFacesEnabled is
     * to keep track of whether GL_CULL_FACE is currently enabled,
     * this allows the engine to prevent needless calls to enable or disable
     * GL_CULL_FACE.
     *
     * @param isFaceCulling Whether the current Window is allowing face culling
     */
    private void enableFaceCulling(boolean isFaceCulling) {
        if (isFaceCulling && !mCullingFacesEnabled) {
            glEnable(GL_CULL_FACE);
            glEnable(GL_BACK);
            mCullingFacesEnabled = true;
        } else if (!isFaceCulling && mCullingFacesEnabled){
            glDisable(GL_CULL_FACE);
            glDisable(GL_BACK);
            mCullingFacesEnabled = false;
        }
    }

    /**
     * Sorts a Mesh GameItem Map to return a Map with
     * the Meshes that either allow or disallow Face Culling
     *
     * @param meshGameItemMap The Map to be sorted
     * @param getCullFaceEnableMeshes Whether to sort for Face Culling enable or disabled
     * @return The sorted Map
     */
    private Map<Mesh, List<GameItem>> generateCullFacesMeshMap(
            Map<Mesh, List<GameItem>> meshGameItemMap,
            boolean getCullFaceEnableMeshes
    ) {
        Map<Mesh, List<GameItem>> result = new HashMap<>();

        for (Mesh mesh : meshGameItemMap.keySet()) {
            if (mesh.isCullingFaces() == getCullFaceEnableMeshes) {
                result.put(mesh, meshGameItemMap.get(mesh));
            }
        }

        return result;
    }

    /**
     * Sorts an InstancedMesh GameItem Map to return a Map with
     * the Meshes that either allow or disallow Face Culling
     *
     * @param meshGameItemMap The Map to be sorted
     * @param getCullFaceEnableMeshes Whether to sort for Face Culling enable or disabled
     * @return The sorted Map
     */
    private Map<InstancedMesh, List<GameItem>> generateCullFacesInstancedMeshMap(
            Map<InstancedMesh, List<GameItem>> meshGameItemMap,
            boolean getCullFaceEnableMeshes
    ) {
        Map<InstancedMesh, List<GameItem>> result = new HashMap<>();

        for (InstancedMesh mesh : meshGameItemMap.keySet()) {
            if (mesh.isCullingFaces() == getCullFaceEnableMeshes) {
                result.put(mesh, meshGameItemMap.get(mesh));
            }
        }

        return result;
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
}