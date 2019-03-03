package com.company.engine.loaders;

import com.company.engine.Utils;
import com.company.engine.graph.mesh.InstancedMesh;
import com.company.engine.graph.mesh.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class ObjLoader {

    public static Mesh loadMesh(String fileName) throws Exception {
        return loadMesh(fileName, 1);
    }

    public static Mesh loadMesh(String fileName, int instances) throws Exception {
        List<String> lines = Utils.readAllLines(fileName);

        List<Vector3f> positionsList = new ArrayList<>();
        List<Vector2f> textCoordsList = new ArrayList<>();
        List<Vector3f> normalsList = new ArrayList<>();
        List<Face> facesList = new ArrayList<>();

        for (String line : lines) {
            String[] tokens = line.split("\\s+");

            switch(tokens[0]) {
                case "v":
                    //geometric vertex
                    Vector3f geoVec3f = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );
                    positionsList.add(geoVec3f);
                break;
                case "vt":
                    //texture coordinate
                    Vector2f textVec2f = new Vector2f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2])
                    );
                    textCoordsList.add(textVec2f);
                break;
                case "vn":
                    //vector normal
                    Vector3f normVec3f = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );
                    normalsList.add(normVec3f);
                break;
                case "f":
                    //face
                    Face face = new Face(
                            tokens[1],
                            tokens[2],
                            tokens[3]
                    );
                    facesList.add(face);
                break;
            }
        }

        return reorderMeshLists(positionsList, textCoordsList, normalsList, facesList, instances);
    }

    private static Mesh reorderMeshLists(
            List<Vector3f> positionsList,
            List<Vector2f> textCoordsList,
            List<Vector3f> normalsList,
            List<Face> facesList,
            int instances
    ) {
        //the lists need to be reordered because the order of definition for texture coordinates
        //and normal coordinates does not correspond to the vertices order
        List<Integer> indices = new ArrayList<>();

        //create position array in the order it has been declared
        //puts each x, y and z of each positions element into an array
        //positions.size() * 3 is used as an array initialiser because each position has an x, y, and z value that needs to be stored
        float[] positionsArr = new float[positionsList.size() * 3];
        int i = 0;
        for (Vector3f position : positionsList) {
            positionsArr[i * 3] = position.x;
            positionsArr[i * 3 + 1] = position.y;
            positionsArr[i * 3 + 2] = position.z;
            i++;
        }

        float[] textureCoordinatesArr = new float[positionsList.size() * 2]; //textures are 2D so only need an x and y
        float[] normalsArr = new float[positionsList.size() * 3]; //normals are 3D so they need an x, y and z

        for (Face face : facesList) {
            for (IdxGroup idxValue : face.getFaceVertexIndices()) {
                processFaceVertex(
                        idxValue,
                        textCoordsList,
                        normalsList,
                        indices,
                        textureCoordinatesArr,
                        normalsArr
                );
            }
        }

        int[] indicesArr = Utils.listToIntArray(indices);

        if (instances > 1) {
            return new InstancedMesh(
                    positionsArr,
                    textureCoordinatesArr,
                    normalsArr,
                    indicesArr,
                    instances
            );
        } else {
            return new Mesh(
                    positionsArr,
                    textureCoordinatesArr,
                    normalsArr,
                    indicesArr
            );
        }
    }

    private static void processFaceVertex(
            IdxGroup indices,
            List<Vector2f> textCoordsList,
            List<Vector3f> normalsList,
            List<Integer> indicesList,
            float[] textCoordsArr,
            float[] normalsArr
    ) {
        //set index for vertex coordinates
        int posIndex = indices.idxPos; //defines the geometric vertex's index
        indicesList.add(posIndex);

        //reorder texture coordinates
        if (indices.idxTextCoord >= 0) {
            Vector2f textCoord = textCoordsList.get(indices.idxTextCoord);
            textCoordsArr[posIndex * 2] = textCoord.x;
            textCoordsArr[posIndex * 2 + 1] = 1 - textCoord.y;
        }

        //reorder vector normals
        if (indices.idxVecNormal >= 0) {
            Vector3f vecNorm = normalsList.get(indices.idxVecNormal);
            normalsArr[posIndex * 3] = vecNorm.x;
            normalsArr[posIndex * 3 + 1] = vecNorm.x;
            normalsArr[posIndex * 3 + 2] = vecNorm.x;
        }
    }

    protected static class Face {

        //List of index group for a triangle face (3 vertices per face)

        private IdxGroup[] idxGroups;

        public Face (String v1, String v2, String v3) {
            idxGroups = new IdxGroup[3];

            idxGroups[0] = parseLine(v1);
            idxGroups[1] = parseLine(v2);
            idxGroups[2] = parseLine(v3);
        }

        private IdxGroup parseLine(String line) {
            //uses the "f x/x/x" from the .obj file
            //turns the string data into values and, if available, puts it into the face's IdxGroup
            IdxGroup idxGroup = new IdxGroup();
            String[] tokens = line.split("/");
            int length = tokens.length;

            idxGroup.idxPos = Integer.parseInt(tokens[0]) - 1;

            if (length > 1) {
                //it can be empty if the .obj file does not define texture coords
                String texCoord = tokens[1];
                idxGroup.idxTextCoord = texCoord.length() > 0 ?
                        Integer.parseInt(texCoord) - 1 : IdxGroup.NO_VALUE;

                if (length > 2) {
                    idxGroup.idxVecNormal = Integer.parseInt(tokens[2]) - 1;
                }
            }

            return idxGroup;
        }

        public IdxGroup[] getFaceVertexIndices() {
            return idxGroups;
        }
    }

    protected static class IdxGroup {

        //holds info on a vertex of a face

        public static final int NO_VALUE = -1;

        public int idxPos;
        public int idxTextCoord;
        public int idxVecNormal;

        public IdxGroup() {
            idxPos = NO_VALUE;
            idxTextCoord = NO_VALUE;
            idxVecNormal = NO_VALUE;
        }
    }
}
