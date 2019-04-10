package com.company.engine.utils;

import com.company.engine.graph.mesh.InstancedMesh;
import com.company.engine.graph.mesh.Mesh;
import com.company.engine.graph.mesh.MeshType;
import org.joml.Vector4f;

import java.util.List;

import static com.company.engine.utils.ArrayUtils.listToFloatArray;
import static com.company.engine.utils.ArrayUtils.listToIntArray;

public class MeshUtils {

    /**
     *
     * @param vertexList
     * @param textCoordList
     * @param normalList
     * @param indexList
     * @param instances
     * @param meshType
     * @return Mesh instance that is created using the arguments
     */
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

    /**
     * Converts the Lists into arrays.
     * Create a Mesh instance using the arguments.
     *
     * @param vertexList a Float List of vertex coordinates
     * @param textCoordList a Float List of texture coordinates
     * @param normalList a Float List of normal coordinates
     * @param indexList an Integer List of vertex indices
     * @param instances number of instances to be created
     * @param meshType the type of Mesh to be created
     * @return the Mesh object that is created using the arguments
     */
    public static Mesh createMeshByType(
            List<Float> vertexList,
            List<Float> textCoordList,
            List<Float> normalList,
            List<Integer> indexList,
            int instances,
            MeshType meshType
    ) {
        return createMeshByType(
                listToFloatArray(vertexList),
                listToFloatArray(textCoordList),
                listToFloatArray(normalList),
                listToIntArray(indexList),
                instances,
                meshType
        );
    }

    /**
     * Set the bounding radius of all mesh instances
     * @param meshArray an array of Mesh that will be the target
     * @param boundingRadius the bounding radius value to be set for each Mesh instance
     */
    public static void setBoundingRadius(
            Mesh[] meshArray,
            float boundingRadius
    ) {
        for(Mesh mesh : meshArray) {
            mesh.setBoundingRadius(boundingRadius);
        }
    }

    /**
     * Sets the colour of the mesh if a material already exists. Does Not create a
     * material if null.
     * @param meshArray an array of Mesh that will be the target
     * @param colour the colour in RGBA with a range between 0 - 1.0
     */
    public static void setColour(Mesh[] meshArray, Vector4f colour) {
        for (Mesh mesh : meshArray) {
            if (mesh.getMaterial() != null) {
                mesh.getMaterial().setColour(colour);
            }
        }
    }

    public static void setCullingFaces(Mesh[] meshArray, boolean cullingFaces) {
        for (Mesh mesh : meshArray) {
            if (mesh != null) {
                mesh.setCullingFaces(cullingFaces);
            }
        }
    }
}