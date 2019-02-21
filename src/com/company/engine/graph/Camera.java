package com.company.engine.graph;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    private final Vector3f mPosition;
    private final Vector3f mRotation;

    private float mFov; //Field of view in radians
    private float mZNear; //start rendering distance
    private float mZFar; //stop rendering distance
    private Matrix4f mViewMatrix; //what the camera sees

    public Camera() {
        mPosition = new Vector3f(0, 0, 0);
        mRotation = new Vector3f(0, 0, 0);
        mFov = (float) Math.toRadians(90.0f);
        mZNear = 0.01f;
        mZFar = 1000.f;
        mViewMatrix = new Matrix4f();
    }

    public void updateViewMatrix() {
        Transformation.updateGenericViewMatrix(mPosition, mRotation, mViewMatrix);
    }

    public float getFov() {
        return mFov;
    }

    public void setFov(int mFov) {
        this.mFov = mFov;
    }

    public float getZNear() {
        return mZNear;
    }

    public void setZNear(int mZNear) {
        this.mZNear = mZNear;
    }

    public float getZFar() {
        return mZFar;
    }

    public void setZFar(int mZFar) {
        this.mZFar = mZFar;
    }

    public Matrix4f getViewMatrix() {
        return mViewMatrix;
    }

    public void setViewMatrix(Matrix4f viewMatrix) {
        mViewMatrix = viewMatrix;
    }

    public Vector3f getPosition() {
        return mPosition;
    }

    public Vector3f getRotation() {
        return mRotation;
    }
}
