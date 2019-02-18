package com.company.engine.graph;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class Material {

    //TODO: Add opacity support in the scene shader

    public static final Vector3f DEFAULT_COLOUR = new Vector3f(1.0f, 1.0f, 1.0f);

    private Vector4f mAmbientColour;
    private Vector4f mDiffuseColour;
    private Vector4f mSpecularColour;
    private Vector4f mColour;
    private float mReflectance;
    private Texture mTexture;
    private boolean mTextured;
    private boolean mUseTexture;
    private float mOpacity;

    public Material() {
        mOpacity = 1.0f;
        mAmbientColour = new Vector4f(DEFAULT_COLOUR.x, DEFAULT_COLOUR.y, DEFAULT_COLOUR.z, mOpacity);
        mDiffuseColour = new Vector4f(DEFAULT_COLOUR.x, DEFAULT_COLOUR.y, DEFAULT_COLOUR.z, mOpacity);
        mSpecularColour = new Vector4f(DEFAULT_COLOUR.x, DEFAULT_COLOUR.y, DEFAULT_COLOUR.z, mOpacity);
        mColour = new Vector4f(DEFAULT_COLOUR.x, DEFAULT_COLOUR.y, DEFAULT_COLOUR.z, mOpacity);
        mTexture = null;
        mTextured = false;
        mUseTexture = false;
        mReflectance = 0.0f;
    }

    public Material(Vector3f colour, float reflectance) {
        this(colour, colour, colour, null, reflectance, 1.0f);
    }

    public Material(Texture texture) {
        this(DEFAULT_COLOUR, DEFAULT_COLOUR, DEFAULT_COLOUR, texture, 0.0f, 1.0f);
    }

    public Material(Texture texture, float reflectance, float opacity) {
        this(DEFAULT_COLOUR, DEFAULT_COLOUR, DEFAULT_COLOUR, texture, reflectance, opacity);
    }

    public Material(Texture texture, float reflectance) {
        this(DEFAULT_COLOUR, DEFAULT_COLOUR, DEFAULT_COLOUR, texture, reflectance, 1.0f);
    }

    public Material(
            Vector3f ambientColour, Vector3f diffuseColour,
            Vector3f specularColour, Texture texture, float reflectance, float opacity
    ) {
        mOpacity = opacity;
        mAmbientColour = new Vector4f(ambientColour.x, ambientColour.y, ambientColour.z, mOpacity);
        mDiffuseColour = new Vector4f(diffuseColour.x, diffuseColour.y, diffuseColour.z, mOpacity);
        mSpecularColour = new Vector4f(specularColour.x, specularColour.y, specularColour.z, mOpacity);
        mTexture = texture;
        mTextured = mTexture != null;
        mUseTexture = mTexture != null;
        mColour = new Vector4f(DEFAULT_COLOUR.x, DEFAULT_COLOUR.y, DEFAULT_COLOUR.z, mOpacity);
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

    public void setUseTexture(boolean useTexture) {
        mUseTexture = useTexture;
    }

    public boolean useTexture() {
        return mUseTexture;
    }

    public void setOpacity(float opacity) {
        mOpacity = opacity;
    }

    public float getOpacity() {
        return mOpacity;
    }
}
