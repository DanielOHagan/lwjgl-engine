package com.company.engine.loaders.assimp;

public class VertexWeight {

    private int mBoneId;
    private int mVertexId;
    private float mWeight;

    public VertexWeight(int boneId, int vertexId, float weight) {
        mBoneId = boneId;
        mVertexId = vertexId;
        mWeight = weight;
    }

    public int getBoneId() {
        return mBoneId;
    }

    public int getVertexId() {
        return mVertexId;
    }

    public float getWeight() {
        return mWeight;
    }

    public void setBoneId(int boneId) {
        mBoneId = boneId;
    }

    public void setVertexId(int vertexId) {
        mVertexId = vertexId;
    }

    public void setWeight(float weight) {
        mWeight = weight;
    }
}
