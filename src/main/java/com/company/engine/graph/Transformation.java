package com.company.engine.graph;

import com.company.engine.scene.items.GameItem;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transformation {

    private final Matrix4f mModelMatrix;
    private final Matrix4f mModelViewMatrix;
    private final Matrix4f mModelLightViewMatrix;
    private final Matrix4f mLightViewMatrix;
    private final Matrix4f mOrthographicProjectionMatrix;
    private final Matrix4f mOrthographic2dMatrix;
    private final Matrix4f mOrthographicModelMatrix;

    public Transformation() {
        mModelMatrix = new Matrix4f();
        mModelViewMatrix = new Matrix4f();
        mModelLightViewMatrix = new Matrix4f();
        mLightViewMatrix = new Matrix4f();
        mOrthographicProjectionMatrix = new Matrix4f();
        mOrthographic2dMatrix = new Matrix4f();
        mOrthographicModelMatrix = new Matrix4f();
    }

    public static Matrix4f updateGenericViewMatrix(
            Vector3f position,
            Vector3f rotation,
            Matrix4f matrix
    ) {
        return matrix.rotationX((float) Math.toRadians(rotation.x))
                .rotateY((float) Math.toRadians(rotation.y))
                .translate(-position.x, position.y, position.z);
    }

    public void updateOrthographicProjectionMatrix(
            float left,
            float right,
            float bottom,
            float top,
            float near,
            float far
    ) {
        mOrthographicProjectionMatrix.setOrtho(left, right, bottom, top, near, far);
    }

    public void updateLightViewMatrix(Vector3f position, Vector3f rotation) {
        updateGenericViewMatrix(position, rotation, mLightViewMatrix);
    }

    public Matrix4f generateModelMatrix(GameItem gameItem) {
        Quaternionf rotation = gameItem.getRotation();
        return mModelMatrix.translationRotateScale(
                gameItem.getPosition().x, gameItem.getPosition().y, gameItem.getPosition().z,
                rotation.x, rotation.y, rotation.z, rotation.z,
                gameItem.getScale(), gameItem.getScale(), gameItem.getScale()
        );
    }

    public Matrix4f generateModelViewMatrix(GameItem gameItem, Matrix4f viewMatrix) {
        return generateModelViewMatrix(generateModelMatrix(gameItem), viewMatrix);
    }

    public Matrix4f generateModelViewMatrix(Matrix4f modelMatrix, Matrix4f viewMatrix) {
        return viewMatrix.mulAffine(modelMatrix, mModelViewMatrix);
    }

    public Matrix4f generateOrtho2DProjectionMatrix(float left, float right, float bottom, float top) {
        return mOrthographic2dMatrix.setOrtho2D(left, right, bottom, top);
    }

    public Matrix4f generateOrthoProjectionModelMatrix(GameItem gameItem, Matrix4f orthoMatrix) {
        return orthoMatrix.mulAffine(generateModelMatrix(gameItem), mOrthographicModelMatrix);
    }

    public Matrix4f generateModelLightViewMatrix(Matrix4f modelMatrix, Matrix4f lightViewMatrix) {
        return lightViewMatrix.mulAffine(modelMatrix, mModelViewMatrix);
    }


    /* Getters and Setters */

    public Matrix4f getLightViewMatrix() {
        return mLightViewMatrix;
    }

    public Matrix4f getOrthographicProjectionMatrix() {
        return mOrthographicProjectionMatrix;
    }
}