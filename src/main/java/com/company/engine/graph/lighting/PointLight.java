package com.company.engine.graph.lighting;

import org.joml.Vector3f;

public class PointLight {

    private Vector3f mPosition;
    private Vector3f mColour;
    private Attenuation mAttenuation;
    private float mIntensity;
    private boolean mActive;

    public PointLight(Vector3f position, Vector3f colour, float intensity, boolean active) {
        mPosition = position;
        mColour = colour;
        mAttenuation = new Attenuation(1, 0, 0);
        mIntensity = intensity;
        mActive = active;
    }

    public PointLight(
            Vector3f position,
            Vector3f colour,
            float intensity,
            Attenuation attenuation,
            boolean active
    ) {
        this(position, colour, intensity, active);
        mAttenuation = attenuation;
    }



    public PointLight(PointLight pointLight) {
        this(
                pointLight.getPosition(),
                pointLight.getColour(),
                pointLight.getIntensity(),
                pointLight.getAttenuation(),
                pointLight.isActive()
        );
    }

    public Vector3f getPosition() {
        return mPosition;
    }

    public Vector3f getColour() {
        return mColour;
    }

    public Attenuation getAttenuation() {
        return mAttenuation;
    }

    public float getIntensity() {
        return mIntensity;
    }

    public void setPosition(Vector3f position) {
        mPosition = position;
    }

    public void setColour(Vector3f colour) {
        mColour = colour;
    }

    public void setAttenuation(Attenuation attenuation) {
        mAttenuation = attenuation;
    }

    public void setIntensity(float intensity) {
        mIntensity = intensity;
    }

    public void setActive(boolean active) {
        mActive = active;
    }

    public boolean isActive() {
        return mActive;
    }
}