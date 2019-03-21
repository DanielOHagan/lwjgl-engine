package com.company.engine.graph.anim;

import java.util.List;

public class Animation {

    private int mCurrentFrame;
    private List<AnimatedFrame> mAnimatedFrameList;
    private String mName;
    private double mDuration;

    public Animation(
            String name,
            List<AnimatedFrame> animatedFrameList,
            double duration
    ) {
        mName = name;
        mAnimatedFrameList = animatedFrameList;
        mDuration = duration;
        mCurrentFrame = 0;
    }

    public void nextFrame() {
        int nextFrame = mCurrentFrame + 1;

        if (nextFrame > mAnimatedFrameList.size() - 1) {
            mCurrentFrame = 0;
        } else {
            mCurrentFrame = nextFrame;
        }
    }

    public AnimatedFrame getCurrentFrame() {
        return mAnimatedFrameList.get(mCurrentFrame);
    }

    public AnimatedFrame getNextFrame() {
        nextFrame();
        return getCurrentFrame();
    }

    public List<AnimatedFrame> getAnimatedFrameList() {
        return mAnimatedFrameList;
    }

    public String getName() {
        return mName;
    }

    public double getDuration() {
        return mDuration;
    }
}