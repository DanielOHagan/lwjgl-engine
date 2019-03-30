package com.company.engine.graph.lighting;

import org.joml.Vector3f;

public class DirectionalLight {

    private final static float DEFAULT_SHADOW_POTION_MULTIPLIER = 1;

    private Vector3f mColour;
    private Vector3f mDirection;
    private float mIntensity;
    private OrthoCoords mOrthoCoords;
    private float mShadowPositionMultiplier;

    public DirectionalLight(Vector3f colour, Vector3f direction, float intensity) {
        mColour = colour;
        mDirection = direction;
        mIntensity = intensity;
        mOrthoCoords = new OrthoCoords();
        mShadowPositionMultiplier = DEFAULT_SHADOW_POTION_MULTIPLIER;
    }

    public DirectionalLight(DirectionalLight directionalLight) {
        this(
                new Vector3f(directionalLight.getColour()),
                new Vector3f(directionalLight.getDirection()),
                directionalLight.getIntensity()
        );
    }

    public Vector3f getColour() {
        return mColour;
    }

    public Vector3f getDirection() {
        return mDirection;
    }

    public float getIntensity() {
        return mIntensity;
    }

    public void setColour(Vector3f colour) {
        mColour = colour;
    }

    public void setDirection(Vector3f direction) {
        mDirection = direction;
    }

    public void setIntensity(float intensity) {
        mIntensity = intensity;
    }

    public void setShadowPositionMultiplier(float shadowPositionMultiplier) {
        mShadowPositionMultiplier = shadowPositionMultiplier;
    }

    public float getShadowPositionMultiplier() {
        return mShadowPositionMultiplier;
    }

    public OrthoCoords getOrthoCoords() {
        return mOrthoCoords;
    }

    public void setOrthoCoords(
            float left,
            float right,
            float bottom,
            float top,
            float near,
            float far
    ) {
        mOrthoCoords.setLeft(left);
        mOrthoCoords.setRight(right);
        mOrthoCoords.setBottom(bottom);
        mOrthoCoords.setTop(top);
        mOrthoCoords.setNear(near);
        mOrthoCoords.setFar(far);
    }

    public void setOrthoCoords(OrthoCoords orthoCoords) {
        setOrthoCoords(
                orthoCoords.getLeft(),
                orthoCoords.getRight(),
                orthoCoords.getBottom(),
                orthoCoords.getTop(),
                orthoCoords.getNear(),
                orthoCoords.getFar()
        );
    }
}