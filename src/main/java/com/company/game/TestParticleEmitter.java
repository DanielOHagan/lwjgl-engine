package com.company.game;

import com.company.engine.graph.particles.IParticleEmitter;
import com.company.engine.graph.particles.Particle;
import com.company.engine.scene.items.GameItem;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TestParticleEmitter implements IParticleEmitter {

    private final List<GameItem> mParticles;
    private final Particle mBaseParticle;

    private int mMaxParticleCount;
    private boolean mActive;
    private boolean mRendering;
    private long mCreationPeriodMillis;
    private long mLastCreationTime;
    private float mSpeedRandomRange;
    private float mPositionRandomRange;
    private float mScaleRandomRange;
    private long mAnimRange;

    private boolean mInsideFrustum;
    private boolean mIgnoresFrustumCulling;
    private boolean mFrustumCullingParticles;

    public TestParticleEmitter(Particle baseParticle, int maxParticleCount, long creationPeriodMillis) {
        mParticles = new ArrayList<>();
        mBaseParticle = baseParticle;
        mMaxParticleCount = maxParticleCount;
        mActive = true;
        mLastCreationTime = 0;
        mCreationPeriodMillis = creationPeriodMillis;
        mInsideFrustum = false;
        mIgnoresFrustumCulling = false;
        mFrustumCullingParticles = true;
        mRendering = true;
    }

    @Override
    public void cleanUp() {
        mBaseParticle.cleanUp();
        for (GameItem gameItem : mParticles) {
            gameItem.cleanUp();
        }
    }

    @Override
    public Particle getBaseParticle() {
        return mBaseParticle;
    }

    @Override
    public List<GameItem> getParticleList() {
        return mParticles;
    }

    @Override
    public boolean isActive() {
        return mActive;
    }

    @Override
    public boolean isRendering() {
        return mRendering;
    }

    @Override
    public boolean isInsideFrustum() {
        return mInsideFrustum;
    }

    @Override
    public boolean ignoresFrustumCulling() {
        return mIgnoresFrustumCulling;
    }

    @Override
    public boolean isFrustumCullingParticles() {
        return mFrustumCullingParticles;
    }

    @Override
    public int getMaxParticleCount() {
        return mMaxParticleCount;
    }

    @Override
    public void setIgnoresFrustumCulling(boolean ignoresFrustumCulling) {
        mIgnoresFrustumCulling = ignoresFrustumCulling;
    }

    @Override
    public void setInsideFrustum(boolean insideFrustum) {
        mInsideFrustum = insideFrustum;
    }

    @Override
    public void setActive(boolean active) {
        mActive = active;
    }

    @Override
    public void setRendering(boolean rendering) {
        mRendering = rendering;
    }

    @Override
    public void setFrustumCullingParticles(boolean frustumCullingParticles) {
        mFrustumCullingParticles = frustumCullingParticles;
    }

    @Override
    public void setMaxParticleCount(int maxParticleCount) {
        mMaxParticleCount = maxParticleCount;
    }

    public void update(long elapsedTime) {
        long currentTime = System.currentTimeMillis();

        if (mLastCreationTime == 0) {
            mLastCreationTime = currentTime;
        }

        Iterator<? extends GameItem> iterator = mParticles.iterator();
        while (iterator.hasNext()) {
            Particle particle = (Particle) iterator.next();
            if (particle.updateParticleLifeSpan(elapsedTime) < 0) {
                iterator.remove();
            } else {
                updatePosition(particle, elapsedTime);
            }
        }

        int length = mParticles.size();
        if (currentTime - mLastCreationTime >= mCreationPeriodMillis &&
                length < mMaxParticleCount
        ) {
            createParticle();
            mLastCreationTime = currentTime;
        }
    }

    private void createParticle() {
        Particle particle = new Particle(mBaseParticle);

        //randomise the particle attributes
        float sign = Math.random() > 0.5d ? -1.0f : 1.0f;
        float speedInc = sign * (float) Math.random() * mSpeedRandomRange;
        float posInc = sign * (float) Math.random() * mPositionRandomRange;
        float scaleInc = sign * (float) Math.random() * mScaleRandomRange;

        if (particle.isAnimated()) {
            long updateAnimInc = (long) sign * (long) (Math.random() * (float) mAnimRange);

            if (particle.getMesh().getMaterial().isUsingTexture()) {
                particle.setUpdateTextureTime(particle.getUpdateTextureTime() + updateAnimInc);
            }
        } else {
            particle.setParticleColour(new Vector4f(
                    Math.random() > 0.5f ? 1 : 0, //red
                    Math.random() > 0.5f ? 1 : 0, //green
                    Math.random() > 0.5f ? 1 : 0, //blue
                    Math.random() > 0.5f ? 1 : 0.5f //alpha
            ));
        }
        particle.getPosition().add(posInc, posInc, posInc);
        particle.getSpeed().add(speedInc, speedInc, speedInc);
        particle.setScale(particle.getScale() + scaleInc);
        mParticles.add(particle);
    }

    public void updatePosition(Particle particle, long elapsedTime) {
        Vector3f speed = particle.getSpeed();
        float delta = elapsedTime / 1000.0f;
        float dx = speed.x * delta;
        float dy = speed.y * delta;
        float dz = speed.z * delta;
        Vector3f pos = particle.getPosition();
        particle.setPosition(pos.x + dx, pos.y + dy, pos.z + dz);
    }

    public void setAnimRange(long mAnimRange) {
        this.mAnimRange = mAnimRange;
    }

    public void setPositionRandomRange(float mPositionRandomRange) {
        this.mPositionRandomRange = mPositionRandomRange;
    }

    public void setSpeedRandomRange(float mSpeedRandomRange) {
        this.mSpeedRandomRange = mSpeedRandomRange;
    }

    public void setScaleRandomRange(float mScaleRandomRange) {
        this.mScaleRandomRange = mScaleRandomRange;
    }
}