package com.company.engine.loaders.assimp;

import org.joml.Matrix4f;

public class Bone {

    private final int mId;
    private final String mName;

    private Matrix4f mOffsetMatrix;

    public Bone(int id, String name, Matrix4f offsetMatrix) {
        mId = id;
        mName = name;
        mOffsetMatrix = offsetMatrix;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public Matrix4f getOffsetMatrix() {
        return mOffsetMatrix;
    }
}
