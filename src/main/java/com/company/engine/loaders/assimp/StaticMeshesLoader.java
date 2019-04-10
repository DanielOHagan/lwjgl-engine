package com.company.engine.loaders.assimp;

import com.company.engine.graph.material.Material;
import com.company.engine.graph.material.Texture;
import com.company.engine.graph.mesh.*;
import com.company.engine.utils.MeshUtils;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.company.engine.utils.ArrayUtils.*;
import static org.lwjgl.assimp.Assimp.*;

public class StaticMeshesLoader {

    public static Mesh[] loadMeshes(
            String filePath,
            String texturesDirectory,
            int instances,
            MeshType meshType
    ) throws Exception {
        return loadMeshes(
                filePath,
                texturesDirectory,
                aiProcess_JoinIdenticalVertices |
                        aiProcess_Triangulate |
                        aiProcess_FixInfacingNormals,
                instances,
                meshType
        );
    }

    public static Mesh[] loadMeshes(
            String filePath,
            Material material,
            int instances,
            MeshType meshType
    ) throws Exception {
        return loadMeshes(
                filePath,
                material,
                aiProcess_JoinIdenticalVertices |
                        aiProcess_Triangulate |
                        aiProcess_FixInfacingNormals,
                instances,
                meshType
        );
    }

    public static Mesh[] loadMeshes(
            String filePath,
            String texturesDirectory,
            int flags,
            int instances,
            MeshType meshType
    ) throws Exception {
        AIScene aiScene = aiImportFile(filePath, flags);
        ModelFileType modelFileType = determineFileType(filePath);

        checkForErrors(aiScene, instances, meshType);

        int numMaterials = aiScene.mNumMaterials();
        PointerBuffer aiMaterials = aiScene.mMaterials();
        List<Material> materialList = new ArrayList<>();

        if (aiMaterials != null) {
            for (int i = 0; i < numMaterials; i++) {
                AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
                processMaterial(aiMaterial, materialList, texturesDirectory, modelFileType);
            }
        }

        return generateMeshArray(aiScene, materialList, instances, meshType);
    }

    private static ModelFileType determineFileType(String filePath) {
        for (ModelFileType modelFileType : ModelFileType.values()) {
            if (filePath.contains(modelFileType.getFileExtension())) {
                return modelFileType;
            }
        }

        return ModelFileType.UNKNOWN;
    }

    public static Mesh[] loadMeshes(
            String filePath,
            Material material,
            int flags,
            int instances,
            MeshType meshType
    ) throws Exception {
        AIScene aiScene = aiImportFile(filePath, flags);

        checkForErrors(aiScene, instances, meshType);

        if (instances > 1 && meshType != MeshType.INSTANCED) {
            throw new Exception("Mesh type must be instanced if instance count is greater that 1");
        }

        return generateMeshArray(aiScene, material, instances, meshType);
    }

    private static void processMaterial(
            AIMaterial aiMaterial,
            List<Material> materialList,
            String texturesDirectory,
            ModelFileType modelFileType
    ) throws Exception {
        AIColor4D colour = AIColor4D.create();
        AIString aiStringTexturePath = AIString.calloc();
        AIString aiStringNormalPath = AIString.calloc();

        loadTexturePath(aiMaterial, aiTextureType_DIFFUSE, aiStringTexturePath);
        /*
          CAUTION:
            AssImp loads many Normal maps thinking they are Height Maps,
            therefore this uses aiTextureType_HEIGHT

          NOTE:
            This engine only supports Object-Space normals.
            Tangent Space Normal Maps MUST be converted before loaded here
         */
        loadTexturePath(aiMaterial, aiTextureType_HEIGHT, aiStringNormalPath);

        Texture texture = loadTexture(aiStringTexturePath, texturesDirectory, modelFileType);
        Texture normalMap = loadTexture(aiStringNormalPath, texturesDirectory, modelFileType);

        Vector4f ambient = Material.DEFAULT_COLOUR;
        loadMeshLightValue(aiMaterial, ambient, AI_MATKEY_COLOR_AMBIENT, colour);

        Vector4f specular = Material.DEFAULT_COLOUR;
        loadMeshLightValue(aiMaterial, specular, AI_MATKEY_COLOR_SPECULAR, colour);

        Vector4f diffuse = Material.DEFAULT_COLOUR;
        loadMeshLightValue(aiMaterial, diffuse, AI_MATKEY_COLOR_DIFFUSE, colour);

        Material material = new Material(
                ambient,
                diffuse,
                specular,
                Material.DEFAULT_COLOUR,
                texture,
                normalMap,
                Material.DEFAULT_REFLECTANCE
        );

        materialList.add(material);
    }

    private static void loadTexturePath(AIMaterial aiMaterial, int aiTextureType, AIString target) {
        Assimp.aiGetMaterialTexture(
                aiMaterial,
                aiTextureType,
                0,
                target,
                (IntBuffer) null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private static Texture loadTexture(
            AIString aiString,
            String texturesDirectory,
            ModelFileType modelFileType
    ) throws Exception{
        String texturePath = aiString.dataString();

        if (texturePath != null && texturePath.length() > 0) {

            texturePath = cleanTextureFilePath(texturePath, modelFileType);

            if (texturePath == null) {
                return null;
            }

            TextureCache textureCache = TextureCache.getInstance();

            return textureCache.getTexture(texturesDirectory + "/" + texturePath);
        }

        return null;
    }

    private static String cleanTextureFilePath(
            String texturePath,
            ModelFileType modelFileType
    ) {

        texturePath = texturePath.replace("..\\", "");
        texturePath = texturePath.replace("//", "/");

        switch (modelFileType) {
            case OBJ:
                //remove make texture null if it contains texture map statement arguments
                if (texturePath.contains(" -")) {
                    return null;
                }
                break;
            case ANY:
                break;
            case UNKNOWN:

                break;
        }

        return texturePath;
    }

    private static void loadMeshLightValue(
            AIMaterial aiMaterial,
            Vector4f light,
            String lightType,
            AIColor4D colour
    ) {
        int result = aiGetMaterialColor(
                aiMaterial,
                lightType,
                aiTextureType_NONE,
                0,
                colour
        );

        if (result == 0) {
            light = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
        }
    }

    private static Mesh processMesh(
            AIMesh aiMesh,
            List<Material> materialList,
            int instances,
            MeshType meshType
    ) {
        List<Float> vertexList = new ArrayList<>();
        List<Float> textCoordList = new ArrayList<>();
        List<Float> normalList = new ArrayList<>();
        List<Integer> indexList = new ArrayList<>();

        populateMeshLists(aiMesh, vertexList, textCoordList, normalList, indexList);

        Mesh mesh;

        switch (meshType) {
            case STANDARD:
                mesh = new Mesh(
                        listToFloatArray(vertexList),
                        listToFloatArray(textCoordList),
                        listToFloatArray(normalList),
                        listToIntArray(indexList)
                );
                break;
            case INSTANCED:
                mesh = new InstancedMesh(
                        listToFloatArray(vertexList),
                        listToFloatArray(textCoordList),
                        listToFloatArray(normalList),
                        listToIntArray(indexList),
                        instances
                );
                break;
            default:
                mesh = new Mesh(
                        listToFloatArray(vertexList),
                        listToFloatArray(textCoordList),
                        listToFloatArray(normalList),
                        listToIntArray(indexList)
                );
                break;
        }

        Material material;
        int materialIndex = aiMesh.mMaterialIndex();

        if (materialIndex >= 0 && materialIndex < materialList.size()) {
            material = materialList.get(materialIndex);
        } else {
            material = new Material();
        }

        mesh.setMaterial(material);

        return mesh;
    }

    private static Mesh processMesh(
            AIMesh aiMesh,
            Material material,
            int instances,
            MeshType meshType
    ) {
        List<Float> vertexList = new ArrayList<>();
        List<Float> textCoordList = new ArrayList<>();
        List<Float> normalList = new ArrayList<>();
        List<Integer> indexList = new ArrayList<>();

        populateMeshLists(aiMesh, vertexList, textCoordList, normalList, indexList);

        Mesh mesh = MeshUtils.createMeshByType(
                vertexList,
                textCoordList,
                normalList,
                indexList,
                instances,
                meshType
        );

        if (material.getTexture() != null) {
            material.setUsingTexture(true);
        }
        mesh.setMaterial(material);

        return mesh;
    }

    private static void populateMeshLists(
            AIMesh aiMesh,
            List<Float> vertexList,
            List<Float> textCoordList,
            List<Float> normalList,
            List<Integer> indexList
    ) {
        processVertexList(
                aiMesh.mVertices(),
                vertexList
        );
        processTextCoordList(
                aiMesh.mTextureCoords(0),
                textCoordList
        );
        processNormalList(
                aiMesh.mNormals(),
                normalList
        );
        processIndexList(
                aiMesh.mNumFaces(),
                aiMesh.mFaces(),
                indexList
        );
    }

    private static void processVertexList(
            AIVector3D.Buffer aiVertices,
            List<Float> vertexList
    ) {

        while (aiVertices.remaining() > 0) {
            AIVector3D aiVertex = aiVertices.get();

            vertexList.add(aiVertex.x());
            vertexList.add(aiVertex.y());
            vertexList.add(aiVertex.z());
        }
    }

    private static void processTextCoordList(
            AIVector3D.Buffer aiTextCoords,
            List<Float> textCoordList
    ) {
        int textCoordCount = aiTextCoords != null ? aiTextCoords.remaining() : 0;

        for (int i = 0; i < textCoordCount; i++) {
            AIVector3D textCoord = aiTextCoords.get();
            textCoordList.add(textCoord.x());
            textCoordList.add(1 - textCoord.y());
        }
    }

    private static void processNormalList(
            AIVector3D.Buffer aiNormals,
            List<Float> normalList
    ) {
        while (aiNormals != null && aiNormals.remaining() > 0) {
            AIVector3D aiNormal = aiNormals.get();

            normalList.add(aiNormal.x());
            normalList.add(aiNormal.y());
            normalList.add(aiNormal.z());
        }
    }

    private static void processIndexList(
            int faceCount,
            AIFace.Buffer aiFaces,
            List<Integer> indexList
    ) {
        for (int i = 0; i < faceCount; i++) {
            AIFace aiFace = aiFaces.get(i);
            IntBuffer intBuffer = aiFace.mIndices();

            while (intBuffer.remaining() > 0) {
                indexList.add(intBuffer.get());
            }
        }
    }

    private static void checkForErrors(
            AIScene aiScene,
            int instances,
            MeshType meshType
    ) throws Exception {
        if (aiScene == null) {
            throw new Exception("Error loading model");
        }

        if (instances < 1) {
            throw new Exception("Must have at least one instance");
        }

        if (instances > 1 && meshType != MeshType.INSTANCED) {
            throw new Exception("Mesh type must be instanced if instance count is greater that 1");
        }
    }

    private static Mesh[] generateMeshArray(
            AIScene aiScene,
            List<Material> materialList,
            int instances,
            MeshType meshType
    ) throws Exception {
        int numMeshes = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        Mesh[] meshArray = new Mesh[numMeshes];

        if (aiMeshes != null) {
            for (int i = 0; i < numMeshes; i++) {
                AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
                Mesh mesh = processMesh(aiMesh, materialList, instances, meshType);
                meshArray[i] = mesh;
            }
        } else {
            throw new Exception("No Meshes were loaded.");
        }

        return meshArray;
    }

    private static Mesh[] generateMeshArray(
            AIScene aiScene,
            Material material,
            int instances,
            MeshType meshType
    ) throws Exception {
        int numMeshes = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        Mesh[] meshArray = new Mesh[numMeshes];

        if (aiMeshes != null) {
            for (int i = 0; i < numMeshes; i++) {
                AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
                Mesh mesh = processMesh(aiMesh, material, instances, meshType);
                meshArray[i] = mesh;
            }
        } else {
            throw new Exception("No Meshes were loaded.");
        }

        return meshArray;
    }
}