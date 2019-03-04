package com.company.engine.graph.particles;

import com.company.engine.graph.Texture;
import com.company.engine.graph.mesh.Mesh;
import com.company.engine.scene.items.GameItem;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Particle extends GameItem {

    private static final int NO_VALUE = -1;
    private static final Vector4f DEFAULT_COLOUR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    private long mParticleLifeSpan;
    private Vector3f mSpeed;
    private boolean mTextured;
    private boolean mAnimated; //if the particle cycles through a texture atlas
    private int mAnimatedFrameCount;
    private long mUpdateTextureTime;
    private long mCurrentAnimationTime;

    public Particle(
            Mesh mesh,
            Vector3f speed,
            long particleLifeSpan,
            boolean animated
    ) {
        this(
                mesh,
                DEFAULT_COLOUR,
                speed,
                particleLifeSpan,
                NO_VALUE,
                animated,
                mesh.getMaterial() != null && mesh.getMaterial().getTexture() != null
        );
    }

    public Particle(
            Mesh mesh,
            Vector3f speed,
            long particleLifeSpan,
            long updateTextureTime
    ) {
        this(
                mesh,
                DEFAULT_COLOUR,
                speed,
                particleLifeSpan,
                updateTextureTime,
                true,
                mesh.getMaterial() != null && mesh.getMaterial().getTexture() != null
        );
    }

    public Particle(
            Mesh mesh,
            Vector4f colour,
            Vector3f speed,
            long particleLifeSpan,
            boolean animated
    ) {
        this(
                mesh,
                colour,
                speed,
                particleLifeSpan,
                NO_VALUE,
                animated,
                mesh.getMaterial() != null && mesh.getMaterial().getTexture() != null
        );
    }

    public Particle(
            Mesh mesh,
            Vector4f colour,
            Vector3f speed,
            long particleLifeSpan,
            long updateTextureTime,
            boolean animated,
            boolean textured
    ) {
        super(mesh);
        getMesh().getMaterial().setColour(new Vector4f(colour));
        mSpeed = speed;
        mParticleLifeSpan = particleLifeSpan;
        mUpdateTextureTime = updateTextureTime;
        mAnimated = animated;
        mTextured = textured;
        if (mTextured) {
            Texture texture = getMesh().getMaterial().getTexture();
            mAnimatedFrameCount = texture.getNumColumns() * texture.getNumRows();
        } else {
            mAnimatedFrameCount = NO_VALUE;
        }
        mCurrentAnimationTime = 0;
    }

    public Particle(Particle baseParticle) {
        super(baseParticle.getMesh());

        Vector3f particlePos = baseParticle.getPosition();
        setPosition(particlePos.x, particlePos.y, particlePos.z);
        setRotation(baseParticle.getRotation());
        setScale(baseParticle.getScale());

        mSpeed = new Vector3f(baseParticle.getSpeed());
        getMesh().getMaterial().setColour(new Vector4f(baseParticle.getMesh().getMaterial().getColour()));
        mTextured = baseParticle.isTextured();
        mParticleLifeSpan = baseParticle.getParticleLifeSpan();
        mAnimated = baseParticle.isAnimated();
        mAnimatedFrameCount =  baseParticle.getAnimatedFrameCount();
        mCurrentAnimationTime = 0;
        mUpdateTextureTime = baseParticle.getUpdateTextureTime();
    }

    public long updateParticleLifeSpan(long elapsedTime) {
        mParticleLifeSpan -= elapsedTime;

        if (mAnimated && getMesh().getMaterial().isUsingTexture()) {
            mCurrentAnimationTime += elapsedTime;
            if (mCurrentAnimationTime >= mUpdateTextureTime && mAnimatedFrameCount > 0) {
                mCurrentAnimationTime = 0;
                //change to next texture
                int textPos = getTexturePos();
                textPos++;
                if (textPos < mAnimatedFrameCount) {
                    setTexturePos(textPos);
                } else {
                    setTexturePos(0);
                }
            }
        }

        return mParticleLifeSpan;
    }

    public long getParticleLifeSpan() {
        return mParticleLifeSpan;
    }

    public void setParticleLifeSpan(long particleLifeSpan) {
        mParticleLifeSpan = particleLifeSpan;
    }

    public Vector3f getSpeed() {
        return mSpeed;
    }

    public void setSpeed(Vector3f speed) {
        mSpeed = speed;
    }

    public boolean isAnimated() {
        return mAnimated;
    }

    public void setAnimated(boolean animated) {
        mAnimated = animated;
    }

    public int getAnimatedFrameCount() {
        return mAnimatedFrameCount;
    }

    public void setAnimatedFrameCount(int animatedFrameCount) {
        mAnimatedFrameCount = animatedFrameCount;
    }

    public long getUpdateTextureTime() {
        return mUpdateTextureTime;
    }

    public void setUpdateTextureTime(long updateTextureTime) {
        mUpdateTextureTime = updateTextureTime;
    }

    public long getCurrentAnimationTime() {
        return mCurrentAnimationTime;
    }

    public void setmCurrentAnimationTime(long currentAnimationTime) {
        mCurrentAnimationTime = currentAnimationTime;
    }

    public void setIsTextured(boolean isTextured) {
        mTextured = isTextured;
    }

    public boolean isTextured() {
        return mTextured;
    }
}
