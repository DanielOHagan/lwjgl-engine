package com.company.engine.graph.particles;

import java.util.List;

public interface IParticleEmitter {

    void cleanUp();
    Particle getBaseParticle();
    List<Particle> getParticles();

}
