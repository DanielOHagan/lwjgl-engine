package com.company.engine.graph;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transformation {

    public static Matrix4f updateGenericViewMatrix(
            Vector3f position,
            Vector3f rotation,
            Matrix4f matrix
    ) {

        return matrix.rotationX((float) Math.toRadians(rotation.x))
                .rotateY((float) Math.toRadians(rotation.y))
                .translate(-position.x, position.y, position.z);
    }
}
