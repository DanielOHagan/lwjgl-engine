package com.company.engine.scene.items;

import com.company.engine.graph.mesh.Mesh;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class GameItem {

    private Vector3f mPosition;
    private Quaternionf mRotation;
    private float mScale;
    private Mesh[] mMeshArray; //holds all of the item's meshes (e.g. if the model's head and body are separate, this stores them both here)
    private int mTexturePos;
    private boolean mIgnoresFrustumCulling;
    private boolean mInsideFrustum;

    public GameItem() {
        mPosition = new Vector3f(0, 0, 0);
        mRotation = new Quaternionf();
        mScale = 1;
        mTexturePos = 0;
        mIgnoresFrustumCulling = false;
        mInsideFrustum = true;
    }

    public GameItem(Mesh mesh) {
        this();
        mMeshArray = new Mesh[] { mesh };
    }

    public GameItem(Mesh[] meshes) {
        this();
        mMeshArray = meshes;
    }

    public void cleanUp() {
        for (Mesh mesh : mMeshArray) {
            if (mesh != null) {
                mesh.cleanUp();
            }
        }
    }

    public void setMeshArray(Mesh[] meshArray) {
        this.mMeshArray = meshArray;
    }

    public void setPosition(float x, float y, float z) {
        mPosition.x = x;
        mPosition.y = y;
        mPosition.z = z;
    }

    /**
     *
     * @param rotationQuaternion Quaternion with 4 values, measured in radians
     */
    public void setRotation(Quaternionf rotationQuaternion) {
        mRotation.set(rotationQuaternion);
    }

    public void setScale(float mScale) {
        this.mScale = mScale;
    }

    public Vector3f getPosition() {
        return mPosition;
    }

    public Quaternionf getRotation() {
        return mRotation;
    }

    public float getScale() {
        return mScale;
    }

    public Mesh[] getMeshArray() {
        return mMeshArray;
    }

    public Mesh getMesh() {
        return mMeshArray[0];
    }

    public void setTexturePos(int texturePos) {
        mTexturePos = texturePos;
    }

    public int getTexturePos() {
        return mTexturePos;
    }

    public void setMesh(Mesh mesh) {
        if (mMeshArray == null) {
            mMeshArray = new Mesh[1];
        }
        mMeshArray[0] = mesh;
    }

    public void setIgnoresFrustumCulling(boolean ignoresFrustumCulling) {
        mIgnoresFrustumCulling = ignoresFrustumCulling;
    }

    public boolean ignoresFrustumCulling() {
        return mIgnoresFrustumCulling;
    }

    public void setInsideFrustum(boolean insideFrustum) {
        mInsideFrustum = insideFrustum;
    }

    public boolean isInsideFrustum() {
        return mInsideFrustum;
    }

    public void setUsingTexture(boolean usingTexture) {
        if (mMeshArray != null && mMeshArray.length > 0) {
            for (Mesh mesh : mMeshArray) {
                if (mesh.getMaterial() != null) {
                    mesh.getMaterial().setUsingTexture(usingTexture);
                }
            }
        }
    }
}