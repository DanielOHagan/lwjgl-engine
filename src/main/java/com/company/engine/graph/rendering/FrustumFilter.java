package com.company.engine.graph.rendering;

import com.company.engine.graph.mesh.Mesh;
import com.company.engine.graph.particles.IParticleEmitter;
import com.company.engine.scene.items.GameItem;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;

public class FrustumFilter {

    private final Matrix4f mProjectionViewMatrix;

    private FrustumIntersection mFrustumIntersection;

    public FrustumFilter() {
        mProjectionViewMatrix = new Matrix4f();
        mFrustumIntersection = new FrustumIntersection();
    }

    public void updateFrustum(
            Matrix4f projectionMatrix,
            Matrix4f viewMatrix
    ) {
        //calculate projection view matrix
        mProjectionViewMatrix.set(projectionMatrix);
        mProjectionViewMatrix.mul(viewMatrix);

        //update frustum intersection
        mFrustumIntersection.set(mProjectionViewMatrix);
    }

    public void filter(
            List<GameItem> gameItemList,
            float meshBoundingRadius
    ) {
        float boundingRadius;
        Vector3f position;

        for (GameItem gameItem : gameItemList) {
            if (!gameItem.ignoresFrustumCulling()) {
                boundingRadius = gameItem.getScale() * meshBoundingRadius;
                position = gameItem.getPosition();
                gameItem.setInsideFrustum(calculateIfInsideFrustum(
                        position.x,
                        position.y,
                        position.z,
                        boundingRadius
                ));
            }
        }
    }

    public void filter(Map<? extends Mesh, List<GameItem>> sceneMeshMap) {
        for (
                Map.Entry<? extends Mesh, List<GameItem>> entry
                : sceneMeshMap.entrySet()
        ) {
            List<GameItem> gameItemList = entry.getValue();
            filter(gameItemList, entry.getKey().getBoundingRadius());
        }
    }

    public void filterParticleEmitter(IParticleEmitter emitter) {
        Vector3f position = emitter.getBaseParticle().getPosition();
        float boundingRadius = emitter.getBaseParticle().getMesh().getBoundingRadius();

        if (emitter.isFrustumCullingParticles()) {
            filter(emitter.getParticleList(), boundingRadius);
        }

        emitter.setInsideFrustum(calculateIfInsideFrustum(
                position.x,
                position.y,
                position.z,
                boundingRadius
        ));
    }

    public void filterParticleEmitters(IParticleEmitter[] emitters) {
        for (IParticleEmitter emitter : emitters) {
            if (emitter != null && !emitter.ignoresFrustumCulling()) {
                filterParticleEmitter(emitter);
            }
        }
    }

    public boolean calculateIfInsideFrustum(
            float x0,
            float y0,
            float z0,
            float boundingRadius
    ) {
        return mFrustumIntersection.testSphere(
                x0,
                y0,
                z0,
                boundingRadius
        );
    }

    /**
     * Fill a target List with pass a filter
     * @param gameItemList the unfiltered List
     * @param filteredGameItemList the target List
     */
    public void populateFilteredList(
            List<GameItem> gameItemList,
            List<GameItem> filteredGameItemList
    ) {
        filteredGameItemList.clear();
        for (GameItem gameItem : gameItemList) {
            if (gameItem.isInsideFrustum()) {
                filteredGameItemList.add(gameItem);
            }
        }
    }

    /**
     * Fill a target List with pass a filter
     * @param emitterArray the unfiltered List
     * @param filteredEmitterList the target List
     */
    public void populateFilteredList(
            IParticleEmitter[] emitterArray,
            List<IParticleEmitter> filteredEmitterList
    ) {
        filteredEmitterList.clear();
        for (IParticleEmitter emitter : emitterArray) {
            if (
                    emitter != null &&
                    (emitter.isInsideFrustum() || emitter.isFrustumCullingParticles())
            ) {
                filteredEmitterList.add(emitter);
            }
        }
    }

    /**
     * Fill a target List with pass a filter
     * @param emitter the unfiltered List
     * @param filteredEmitterParticleList the target List
     */
    public void populateFilteredList(
            IParticleEmitter emitter,
            List<GameItem> filteredEmitterParticleList
    ) {
        filteredEmitterParticleList.clear();
        if (emitter.isFrustumCullingParticles()) {
            for (GameItem gameItem : emitter.getParticleList()) {
                if (gameItem.isInsideFrustum()) {
                    filteredEmitterParticleList.add(gameItem);
                }
            }
        }
    }
}