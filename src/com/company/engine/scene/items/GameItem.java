package com.company.engine.scene.items;

import com.company.engine.graph.Mesh;
import org.joml.Vector3f;

public class GameItem {

    private Vector3f mPosition;
    private Vector3f mRotation;
    private float mScale;
    private Mesh[] mMeshes; //holds all of the item's meshes (e.g. if the model's head and body are separate, this stores them both here)

    public GameItem() {
        mPosition = new Vector3f(0, 0, 0);
        mRotation = new Vector3f(0, 0, 0);
        mScale = 1;
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

    public void setRotation(float x, float y, float z) {
        mRotation.x = x;
        mRotation.y = y;
        mRotation.z = z;
    }

    public void setScale(float mScale) {
        this.mScale = mScale;
    }

    public Vector3f getPosition() {
        return mPosition;
    }

    public Vector3f getRotation() {
        return mRotation;
    }

    public float getScale() {
        return mScale;
    }

    public Mesh[] getMeshes() {
        return mMeshes;
    }
}
