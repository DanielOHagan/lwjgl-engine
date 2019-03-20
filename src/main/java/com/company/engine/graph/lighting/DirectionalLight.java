package com.company.engine.graph.lighting;

import org.joml.Vector3f;

public class DirectionalLight {

    private Vector3f mColour;
    private Vector3f mDirection;
    private float mIntensity;

    public DirectionalLight(Vector3f colour, Vector3f direction, float intensity) {
        mColour = colour;
        mDirection = direction;
        mIntensity = intensity;
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

    public static class OrthoCoords {
        public float left;
        public float right;
        public float bottom;
        public float top;
        public float near;
        public float far;
    }
}