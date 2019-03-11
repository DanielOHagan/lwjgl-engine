package com.company.engine.graph;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    private final Vector3f mPosition;
    private final Vector3f mRotation;

    private float mFov; //Field of view in radians
    private float mViewDistanceStart; //start rendering distance
    private float mViewDistanceEnd; //stop rendering distance
    private Matrix4f mViewMatrix; //what the camera sees

    public Camera() {
        mPosition = new Vector3f(0, 0, 0);
        mRotation = new Vector3f(0, 0, 0);
        mFov = (float) Math.toRadians(90.0f);
        mViewDistanceStart = 0.01f;
        mViewDistanceEnd = 1000.f;
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

    public float getViewDistanceStart() {
        return mViewDistanceStart;
    }

    public void setViewDistanceStart(int mZNear) {
        this.mViewDistanceStart = mZNear;
    }

    public float getViewDistanceEnd() {
        return mViewDistanceEnd;
    }

    public void setViewDistanceEnd(int mZFar) {
        this.mViewDistanceEnd = mZFar;
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