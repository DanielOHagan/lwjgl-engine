package com.company.engine.graph.lighting;

public class Attenuation {

    private float mConstant;
    private float mLinear;
    private float mExponent;

    public Attenuation(float constant, float linear, float exponent) {
        mConstant = constant;
        mLinear = linear;
        mExponent = exponent;
    }

    public float getConstant() {
        return mConstant;
    }

    public float getLinear() {
        return mLinear;
    }

    public float getExponent() {
        return mExponent;
    }

    public void setConstant(float constant) {
        mConstant = constant;
    }

    public void setLinear(float linear) {
        mLinear = linear;
    }

    public void setExponent(float exponent) {
        mExponent = exponent;
    }
}
