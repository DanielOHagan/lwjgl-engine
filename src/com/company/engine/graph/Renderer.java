package com.company.engine.graph;

import com.company.engine.Utils;
import com.company.engine.Window;
import com.company.engine.scene.Scene;
import com.company.engine.scene.SceneLighting;
import com.company.engine.scene.items.GameItem;
import com.company.engine.scene.ui.IHud;

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

        //mSceneShaderProgram.createUniform("textureSampler");
        //mSceneShaderProgram.createUniform("projectionMatrix");

    }

    public void render(Window window, Camera camera, Scene scene, IHud hud) {
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

        glViewport(0, 0, window.getWidth(), window.getHeight());

        //TODO:
//        // Update projection matrix once per render cycle
//        window.updateProjectionMatrix();

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

        mSceneShaderProgram.setUniform("projectionMatrix", camera.getViewMatrix());

        renderGameItems(scene);
    }

    private void renderGameItems(Scene scene) {
        Map<Mesh, List<GameItem>> meshesMap = scene.getGameItemMeshMap();
        for (Mesh mesh : meshesMap.keySet()) {
            mesh.render();
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
