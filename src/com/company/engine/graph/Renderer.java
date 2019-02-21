package com.company.engine.graph;

import com.company.engine.Utils;
import com.company.engine.window.Window;
import com.company.engine.scene.Scene;
import com.company.engine.scene.items.GameItem;
import com.company.engine.scene.items.ui.IHud;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    private final Transformation mTransformation;

    private ShaderProgram mSceneShaderProgram;

    public Renderer() {
        mTransformation = new Transformation();
    }

    public void init(Window window) throws Exception {
        setUpSceneShader();

    }

    public void setUpSceneShader() throws Exception {
        //create shader
        mSceneShaderProgram = new ShaderProgram();
        mSceneShaderProgram.createVertexShader(Utils.loadResource("/shaders/scene_vertex.vs"));
        mSceneShaderProgram.createFragmentShader(Utils.loadResource("/shaders/scene_fragment.fs"));
        mSceneShaderProgram.link();

        mSceneShaderProgram.createUniform("textureSampler");
        mSceneShaderProgram.createUniform("isTextured");
        mSceneShaderProgram.createUniform("useTexture");
        mSceneShaderProgram.createUniform("colour");
        mSceneShaderProgram.createUniform("projectionMatrix");
        mSceneShaderProgram.createUniform("modelViewMatrix");

    }

    public void render(Window window, Camera camera, Scene scene, IHud hud, boolean sceneChanged) {
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
        window.updateProjectionMatrix(camera.getFov(), camera.getZNear(), camera.getZFar());

        //update camera view matrix each cycle
        camera.updateViewMatrix();

        renderScene(window, camera, scene);

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


        if (hud != null) {
            renderHud(hud);
        }
    }

    public void renderScene(Window window, Camera camera, Scene scene) {
        mSceneShaderProgram.bind();

        mSceneShaderProgram.setUniform("textureSampler", 0);
        mSceneShaderProgram.setUniform("projectionMatrix", window.getProjectionMatrix());

        renderGameItems(camera, scene);
    }

    private void renderGameItems(Camera camera, Scene scene) {
        Map<Mesh, List<GameItem>> meshesMap = scene.getGameItemMeshMap();
        for (Mesh mesh : meshesMap.keySet()) {
            if (mesh.getMaterial() != null) {
                //set mesh's material related uniforms
                boolean isTextured = mesh.getMaterial().isTextured();
                boolean useTexture = mesh.getMaterial().useTexture();

                mSceneShaderProgram.setUniform("isTextured", isTextured ? 1 : 0);
                mSceneShaderProgram.setUniform("useTexture", useTexture ? 1 : 0);


                if (isTextured && useTexture) {
                    //set mesh's texture related uniforms if the texture is selected to be rendered

                } else {
                    mSceneShaderProgram.setUniform("colour", mesh.getMaterial().getColour());
                }
            } else {

            }

            mesh.renderList(meshesMap.get(mesh), (GameItem gameItem) -> {
                mSceneShaderProgram.setUniform(
                        "modelViewMatrix",
                        mTransformation.generateModelViewMatrix(gameItem, camera.getViewMatrix())
                );

            });
        }
    }

    public void renderHud(IHud hud) {

    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void cleanUp() {
        if (mSceneShaderProgram != null) {
            mSceneShaderProgram.cleanUp();
        }
    }
}
