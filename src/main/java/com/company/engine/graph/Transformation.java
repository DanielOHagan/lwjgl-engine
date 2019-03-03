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
    private final Matrix4f mOrthographic2dMatrix;
    private final Matrix4f mOrthographicModelMatrix;

    public Transformation() {
        mModelMatrix = new Matrix4f();
        mModelViewMatrix = new Matrix4f();
        mModelLightViewMatrix = new Matrix4f();
        mLightViewMatrix = new Matrix4f();
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


    /* Getters and Setters */

    public Matrix4f getLightViewMatrix() {
        return mLightViewMatrix;
    }
}
