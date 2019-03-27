package com.company.engine.graph.particles;

import com.company.engine.scene.items.GameItem;

import java.util.List;

public interface IParticleEmitter {

    void cleanUp();

    Particle getBaseParticle();
    List<GameItem> getParticleList();

    boolean isActive();
    boolean isInsideFrustum();
    boolean ignoresFrustumCulling();
    boolean isFrustumCullingParticles();
    int getMaxParticleCount();

    void setIgnoresFrustumCulling(boolean ignoresFrustumCulling);
    void setInsideFrustum(boolean insideFrustum);
    void setActive(boolean active);
    void setFrustumCullingParticles(boolean frustumCullingParticles);
    void setMaxParticleCount(int maxParticleCount);
}