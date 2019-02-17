package com.company.engine.graph;

import org.joml.Vector4f;

public class Material {

    public static final Vector4f DEFAULT_COLOUR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    private Vector4f mAmbientColour, mDiffuseColour, mSpecularColour;
    private float mReflectance;
    private Texture mTexture;

    public Material() {
        mAmbientColour = DEFAULT_COLOUR;
        mDiffuseColour = DEFAULT_COLOUR;
        mSpecularColour = DEFAULT_COLOUR;
        mTexture = null;
        mReflectance = 0;
    }

    public Material(Vector4f colour, float reflectance) {
        this(colour, colour, colour, null, reflectance);
    }

    public Material(Texture texture) {
        this(DEFAULT_COLOUR, DEFAULT_COLOUR, DEFAULT_COLOUR, texture, 0);
    }

    public Material(Texture texture, float reflectance) {
        this(DEFAULT_COLOUR, DEFAULT_COLOUR, DEFAULT_COLOUR, texture, reflectance);
    }

    public Material(
            Vector4f ambientColour, Vector4f diffuseColour,
            Vector4f specularColour, Texture texture, float reflectance
    ) {
        mAmbientColour = ambientColour;
        mDiffuseColour = diffuseColour;
        mSpecularColour = specularColour;
        mTexture = texture;
        mReflectance = reflectance;
    }

    public boolean isTextured() {
        return mTexture != null;
    }

    public Vector4f getAmbientColour() {
        return mAmbientColour;
    }

    public Vector4f getDiffuseColour() {
        return mDiffuseColour;
    }

    public Vector4f getSpecularColour() {
        return mSpecularColour;
    }

    public float getReflectance() {
        return mReflectance;
    }

    public Texture getTexture() {
        return mTexture;
    }

    public void setAmbientColour(Vector4f mAmbientColour) {
        this.mAmbientColour = mAmbientColour;
    }

    public void setDiffuseColour(Vector4f mDiffuseColour) {
        this.mDiffuseColour = mDiffuseColour;
    }

    public void setSpecularColour(Vector4f mSpecularColour) {
        this.mSpecularColour = mSpecularColour;
    }

    public void setReflectance(float mReflectance) {
        this.mReflectance = mReflectance;
    }

    public void setTexture(Texture mTexture) {
        this.mTexture = mTexture;
    }
}
