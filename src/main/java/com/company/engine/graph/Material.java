package com.company.engine.graph;

import org.joml.Vector4f;

public class Material {

    //TODO: Add opacity support in the scene shader

    public static final Vector4f DEFAULT_COLOUR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    public static final float DEFAULT_REFLECTANCE = 0.0f;

    private Vector4f mAmbientColour;
    private Vector4f mDiffuseColour;
    private Vector4f mSpecularColour;
    private Vector4f mColour;
    private float mReflectance;
    private Texture mTexture;
    private boolean mUsingTexture;

    public Material() {
        mAmbientColour = new Vector4f(DEFAULT_COLOUR.x, DEFAULT_COLOUR.y, DEFAULT_COLOUR.z, DEFAULT_COLOUR.w);
        mDiffuseColour = new Vector4f(DEFAULT_COLOUR.x, DEFAULT_COLOUR.y, DEFAULT_COLOUR.z, DEFAULT_COLOUR.w);
        mSpecularColour = new Vector4f(DEFAULT_COLOUR.x, DEFAULT_COLOUR.y, DEFAULT_COLOUR.z, DEFAULT_COLOUR.w);
        mColour = new Vector4f(DEFAULT_COLOUR.x, DEFAULT_COLOUR.y, DEFAULT_COLOUR.z, DEFAULT_COLOUR.w);
        mTexture = null;
        mUsingTexture = false;
        mReflectance = 0.0f;
    }

    public Material(Vector4f colour, float reflectance) {
        this(
                colour,
                colour,
                colour,
                colour,
                null,
                reflectance
        );
    }

    public Material(Vector4f colour) {
        this(
                colour,
                colour,
                colour,
                colour,
                null,
                DEFAULT_REFLECTANCE
        );
    }

    public Material(Texture texture) {
        this(
                DEFAULT_COLOUR,
                DEFAULT_COLOUR,
                DEFAULT_COLOUR,
                DEFAULT_COLOUR,
                texture,
                DEFAULT_REFLECTANCE
        );
    }

    public Material(Texture texture, float reflectance) {
        this(
                DEFAULT_COLOUR,
                DEFAULT_COLOUR,
                DEFAULT_COLOUR,
                DEFAULT_COLOUR,
                texture,
                reflectance
        );
    }

    public Material(
            Vector4f ambientColour,
            Vector4f diffuseColour,
            Vector4f specularColour,
            Vector4f colour,
            Texture texture,
            float reflectance
    ) {
        mAmbientColour = new Vector4f(ambientColour.x, ambientColour.y, ambientColour.z, ambientColour.w);
        mDiffuseColour = new Vector4f(diffuseColour.x, diffuseColour.y, diffuseColour.z, diffuseColour.w);
        mSpecularColour = new Vector4f(specularColour.x, specularColour.y, specularColour.z, specularColour.w);
        mTexture = texture;
        mUsingTexture = mTexture != null;
        mColour = colour;
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
    }

    public void setColour(Vector4f colour) {
        mColour = colour;
    }

    public Vector4f getColour() {
        return mColour;
    }

    public void setUsingTexture(boolean usingTexture) {
        mUsingTexture = usingTexture;
    }

    public boolean isUsingTexture() {
        return mUsingTexture;
    }

    public void setOpacity(float opacity) {
        mColour.w = opacity;
    }

    public float getOpacity() {
        return mColour.w;
    }
}