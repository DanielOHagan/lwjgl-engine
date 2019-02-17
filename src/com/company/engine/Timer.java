package com.company.engine;

public class Timer {

    private double mLastLoopTime;

    public void init() {
        mLastLoopTime = getTime();
    }

    public double getTime() {
        //get current nano seconds and convert to seconds
        return System.nanoTime() / 1_000_000_000.0d;
    }

    public float getElapsedTime() {
        double time = getTime();
        float elapsedTime = (float) (time - mLastLoopTime);

        //store the time in which the previous loop cycle finished
        mLastLoopTime = time;
        return elapsedTime;
    }

    public double getLastLoopTime() {
        return mLastLoopTime;
    }
}
