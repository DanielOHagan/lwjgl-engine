package com.company.engine.scene.items;

import com.company.engine.graph.mesh.Mesh;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class GameItem {

    private Vector3f mPosition;
    private Quaternionf mRotation;
    private float mScale;
    private Mesh[] mMeshes; //holds all of the item's meshes (e.g. if the model's head and body are separate, this stores them both here)
    private int mTexturePos;

    public GameItem() {
        mPosition = new Vector3f(0, 0, 0);
        mRotation = new Quaternionf();
        mScale = 1;
        mTexturePos = 0;
    }

    public GameItem(Mesh mesh) {
        this();
        mMeshes = new Mesh[] { mesh };
    }

    public GameItem(Mesh[] meshes) {
        this();
        mMeshes = meshes;
    }

    public void cleanUp() {
        for (Mesh mesh : mMeshes) {
            if (mesh != null) {
                mesh.cleanUp();
            }
        }
    }

    public void setMeshes(Mesh[] mMeshes) {
        this.mMeshes = mMeshes;
    }

    public void setPosition(float x, float y, float z) {
        mPosition.x = x;
        mPosition.y = y;
        mPosition.z = z;
    }

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

    public Mesh[] getMeshes() {
        return mMeshes;
    }

    public Mesh getMesh() {
        return mMeshes[0];
    }

    public void setTexturePos(int texturePos) {
        mTexturePos = texturePos;
    }

    public int getTexturePos() {
        return mTexturePos;
    }

    public void setMesh(Mesh mesh) {
        mMeshes[0] = mesh;
    }
}
