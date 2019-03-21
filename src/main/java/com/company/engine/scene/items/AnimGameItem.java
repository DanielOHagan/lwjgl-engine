package com.company.engine.scene.items;

import com.company.engine.graph.anim.Animation;
import com.company.engine.graph.mesh.Mesh;

import java.util.Map;
import java.util.Optional;

public class AnimGameItem extends GameItem {

    private Map<String, Animation> mAnimationMap;
    private Animation mCurrentAnimation;

    public AnimGameItem(Mesh[] meshArray, Map<String, Animation> animationMap) {
        super(meshArray);

        mAnimationMap = animationMap;

        //get first animation and assign it to mCurrentAnimation
        Optional<Map.Entry<String, Animation>> entry =
                animationMap.entrySet().stream().findFirst();

        mCurrentAnimation = entry.isPresent() ? entry.get().getValue() : null;
    }

    public Animation getAnimation(String name) {
        return mAnimationMap.get(name);
    }

    public Animation getCurrentAnimation() {
        return mCurrentAnimation;
    }

    public void setCurrentAnimation(Animation currentAnimation) {
        mCurrentAnimation = currentAnimation;
    }
}