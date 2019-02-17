package com.company.engine.graph;

import org.joml.Matrix4f;

public class Camera {

    private float mFov;
    private float mZNear;
    private float mZFar;
    private Matrix4f mViewMatrix;

    public Camera() {
        mFov = (float) Math.toRadians(60.0f);
        mZNear = 0.01f;
        mZFar = 1000.f;
        mViewMatrix = new Matrix4f();
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
}
