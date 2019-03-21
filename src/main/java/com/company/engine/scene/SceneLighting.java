package com.company.engine.scene;

import com.company.engine.graph.lighting.DirectionalLight;
import com.company.engine.graph.lighting.PointLight;
import com.company.engine.graph.lighting.SpotLight;
import org.joml.Vector3f;

import java.util.List;

public class SceneLighting {

    public static final Vector3f DEFAULT_AMBIENT_LIGHT = new Vector3f(0.3f, 0.3f, 0.3f);

    private Vector3f mAmbientLight;
    private Vector3f mSkyBoxLight;
    private List<PointLight> mPointLightList;
    private List<SpotLight> mSpotLightList;
    private DirectionalLight mDirectionLight;

    public SceneLighting() {}

    public SceneLighting(
            Vector3f ambientLight,
            List<PointLight> pointLightList,
            List<SpotLight> spotLightList,
            DirectionalLight directionalLight
    ) {
        mAmbientLight = ambientLight;
        mPointLightList = pointLightList;
        mSpotLightList = spotLightList;
        mDirectionLight = directionalLight;
    }

    public Vector3f getAmbientLight() {
        return mAmbientLight;
    }

    public Vector3f getSkyBoxLight() {
        return mSkyBoxLight;
    }

    public List<PointLight> getPointLightList() {
        return mPointLightList;
    }

    public List<SpotLight> getSpotLightList() {
        return mSpotLightList;
    }

    public DirectionalLight getDirectionLight() {
        return mDirectionLight;
    }

    public void setAmbientLight(Vector3f ambientLight) {
        mAmbientLight = ambientLight;
    }

    public void setSkyBoxLight(Vector3f skyBoxLight) {
        mSkyBoxLight = skyBoxLight;
    }

    public void setPointLightArray(List<PointLight> pointLightArray) {
        mPointLightList = pointLightArray;
    }

    public void setSpotLightList(List<SpotLight> spotLightList) {
        mSpotLightList = spotLightList;
    }

    public void setDirectionalLight(DirectionalLight directionLight) {
        mDirectionLight = directionLight;
    }
}