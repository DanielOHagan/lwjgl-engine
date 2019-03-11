package com.company.engine.graph.particles;

import com.company.engine.scene.items.GameItem;

import java.util.List;

public interface IParticleEmitter {

    void cleanUp();
    Particle getBaseParticle();
    List<GameItem> getParticles();
    boolean isActive();
}