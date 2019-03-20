package com.company.engine.graph.lighting;

import org.joml.Vector3f;

public class SpotLight {

    private PointLight mPointLight;
    private Vector3f mConeDirection;
    private float mCutOff;

    public SpotLight(
            PointLight pointLight,
            Vector3f coneDirection,
            float cutOffDegrees
    ) {
        mPointLight = pointLight;
        mConeDirection = coneDirection;
        setCutOffDegrees(cutOffDegrees);
    }

    public SpotLight(SpotLight spotLight) {
        this(
                new PointLight(spotLight.getPointLight()),
                new Vector3f(spotLight.getConeDirection()),
                0
        );

        setCutOff(spotLight.getCutOff());
    }

    public void setCutOffDegrees(float cutOffDegrees) {
        mCutOff = (float) Math.cos(Math.toRadians(cutOffDegrees));
    }

    public PointLight getPointLight() {
        return mPointLight;
    }

    public Vector3f getConeDirection() {
        return mConeDirection;
    }

    public float getCutOff() {
        return mCutOff;
    }

    public void setConeDirection(Vector3f coneDirection) {
        mConeDirection = coneDirection;
    }

    public void setPointLight(PointLight pointLight) {
        mPointLight = pointLight;
    }

    public void setCutOff(float cutOff) {
        mCutOff = cutOff;
    }
}
