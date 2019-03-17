package com.company.engine.utils;

import com.company.engine.graph.mesh.InstancedMesh;
import com.company.engine.graph.mesh.Mesh;
import com.company.engine.graph.mesh.MeshType;

import java.util.List;

import static com.company.engine.utils.ArrayUtils.listToFloatArray;
import static com.company.engine.utils.ArrayUtils.listToIntArray;

public class MeshUtils {

    public static Mesh createMeshByType(
            float[] vertexList,
            float[] textCoordList,
            float[] normalList,
            int[] indexList,
            int instances,
            MeshType meshType
    ) {
        switch (meshType) {
            case STANDARD:
                return new Mesh(
                    vertexList,
                    textCoordList,
                    normalList,
                    indexList
            );
            case INSTANCED:
                return new InstancedMesh(
                        vertexList,
                        textCoordList,
                        normalList,
                        indexList,
                        instances
                );
            default:
                return new Mesh(
                        vertexList,
                        textCoordList,
                        normalList,
                        indexList
                );
        }
    }

    public static Mesh createMeshByType(
            List<Float> vertexList,
            List<Float> textCoordList,
            List<Float> normalList,
            List<Integer> indexList,
            int instances,
            MeshType meshType
    ) {
        switch (meshType) {
            case STANDARD:
                return new Mesh(
                        listToFloatArray(vertexList),
                        listToFloatArray(textCoordList),
                        listToFloatArray(normalList),
                        listToIntArray(indexList)
                );
            case INSTANCED:
                return new InstancedMesh(
                        listToFloatArray(vertexList),
                        listToFloatArray(textCoordList),
                        listToFloatArray(normalList),
                        listToIntArray(indexList),
                        instances
                );
            default:
                return new Mesh(
                        listToFloatArray(vertexList),
                        listToFloatArray(textCoordList),
                        listToFloatArray(normalList),
                        listToIntArray(indexList)
                );
        }
    }

    public static void setBoundingRadius(Mesh[] meshArray, float boundingRadius) {
        for(Mesh mesh : meshArray) {
            mesh.setBoundingRadius(boundingRadius);
        }
    }
}