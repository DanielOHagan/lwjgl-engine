package com.company.engine.graph.lighting;

public class OrthoCoords {

    private float mLeft;
    private float mRight;
    private float mBottom;
    private float mTop;
    private float mNear;
    private float mFar;

    public void setLeft(float left) {
        mLeft = left;
    }

    public void setRight(float right) {
        mRight = right;
    }

    public void setBottom(float bottom) {
        mBottom = bottom;
    }

    public void setTop(float top) {
        mTop = top;
    }

    public void setNear(float near) {
        mNear = near;
    }

    public void setFar(float far) {
        mFar = far;
    }

    public float getLeft() {
        return mLeft;
    }

    public float getRight() {
        return mRight;
    }

    public float getBottom() {
        return mBottom;
    }

    public float getTop() {
        return mTop;
    }

    public float getNear() {
        return mNear;
    }

    public float getFar() {
        return mFar;
    }
}
