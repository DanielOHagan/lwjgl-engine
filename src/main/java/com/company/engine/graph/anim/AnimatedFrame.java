package com.company.engine.graph.anim;

import org.joml.Matrix4f;

import java.util.Arrays;

public class AnimatedFrame {

    private static final Matrix4f IDENTITY_MATRIX = new Matrix4f();

    public static final int MAX_JOINTS = 150;

    private final Matrix4f[] mJointMatrixArray;

    public AnimatedFrame() {
        mJointMatrixArray = new Matrix4f[MAX_JOINTS];
        Arrays.fill(mJointMatrixArray, IDENTITY_MATRIX);
    }

    public Matrix4f[] getJointMatrixArray() {
        return mJointMatrixArray;
    }

    public void setJointMatrix(int index, Matrix4f jointMatrix) {
        mJointMatrixArray[index] = jointMatrix;
    }
}