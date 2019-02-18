package com.company.engine.graph;

import org.joml.Vector4f;

public class Material {

    public static final Vector4f DEFAULT_COLOUR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    private Vector4f mAmbientColour;
    private Vector4f mDiffuseColour;
    private Vector4f mSpecularColour;
    private Vector4f mColour;
    private float mReflectance;
    private Texture mTexture;
    private boolean mTextured;

    public Material() {
        mAmbientColour = DEFAULT_COLOUR;
        mDiffuseColour = DEFAULT_COLOUR;
        mSpecularColour = DEFAULT_COLOUR;
        mColour = new Vector4f(DEFAULT_COLOUR.x, DEFAULT_COLOUR.y, DEFAULT_COLOUR.z, DEFAULT_COLOUR.w);
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
        mTextured = mTexture != null;
        mColour = new Vector4f(DEFAULT_COLOUR.x, DEFAULT_COLOUR.y, DEFAULT_COLOUR.z, DEFAULT_COLOUR.w);
        mReflectance = reflectance;
    }

    public void cleanUp() {
        if (mTexture != null) {
            mTexture.cleanUp();
        }
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
        mTextured = mTexture != null;
    }

    public void setColour(float red, float green, float blue, float alpha) {
        mColour.x = red;
        mColour.y = green;
        mColour.z = blue;
        mColour.w = alpha;
    }

    public Vector4f getColour() {
        return mColour;
    }
}
