package com.company.engine.loaders.assimp;

import com.company.engine.graph.Material;
import com.company.engine.graph.anim.AnimatedFrame;
import com.company.engine.graph.anim.Animation;
import com.company.engine.graph.mesh.Mesh;
import com.company.engine.scene.items.AnimGameItem;
import com.company.engine.utils.ArrayUtils;
import com.company.engine.utils.AssImpUtils;
import com.company.engine.utils.MeshUtils;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.assimp.Assimp.*;

public class AnimMeshesLoader extends StaticMeshesLoader {

    public static AnimGameItem loadAnimGameItem(
            String filePath,
            String texturesDirectory
    ) throws Exception {
        return loadAnimGameItem(
                filePath,
                texturesDirectory,
                aiProcess_GenSmoothNormals |
                        aiProcess_JoinIdenticalVertices |
                        aiProcess_Triangulate |
                        aiProcess_FixInfacingNormals |
                        aiProcess_LimitBoneWeights
        );
    }

    public static AnimGameItem loadAnimGameItem(
            String filePath,
            String texturesDirectory,
            int flags
    ) throws Exception {
        AIScene aiScene = aiImportFile(filePath, flags);

        if (aiScene == null) {
            throw new Exception("Error loading model");
        }

        int numMaterials = aiScene.mNumMaterials();
        PointerBuffer aiMaterials = aiScene.mMaterials();
        List<Material> materialList = new ArrayList<>();

        for (int i = 0; i < numMaterials; i++) {
            AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
            processMaterial(aiMaterial, materialList, texturesDirectory);
        }

        List<Bone> boneList = new ArrayList<>();
        int numMeshes = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        Mesh[] mesheArray = new Mesh[numMeshes];

        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            Mesh mesh = processMesh(aiMesh, materialList, boneList);
            mesheArray[i] = mesh;
        }

        AINode aiRootNode = aiScene.mRootNode();
        Matrix4f rootTransformation = AssImpUtils.toJomlMatrix4f(
                aiRootNode.mTransformation()
        );
        Node rootNode = processNodesHierarchy(aiRootNode, null);
        Map<String, Animation> animationMap = processAnimations(
                aiScene,
                boneList,
                rootNode,
                rootTransformation
        );

        return new AnimGameItem(mesheArray, animationMap);
    }

    private static Mesh processMesh(
            AIMesh aiMesh,
            List<Material> materialList,
            List<Bone> boneList
    ) {
        List<Float> vertexList = new ArrayList<>();
        List<Float> textCoordList = new ArrayList<>();
        List<Float> normalList = new ArrayList<>();
        List<Float> weightList = new ArrayList<>();
        List<Integer> indexList = new ArrayList<>();
        List<Integer> boneIdList = new ArrayList<>();

        processVertexList(aiMesh.mVertices(), vertexList);
        processNormalList(aiMesh.mNormals(), normalList);
        processTextCoordList(aiMesh.mTextureCoords(0), textCoordList);
        processIndexList(aiMesh.mNumFaces(), aiMesh.mFaces(), indexList);
        processBoneList(aiMesh, boneList, boneIdList, weightList);

//        Mesh mesh = MeshUtils.createMeshByType(
//                ArrayUtils.listToFloatArray(vertexList),
//                ArrayUtils.listToFloatArray(textCoordList),
//                ArrayUtils.listToFloatArray(normalList),
//                ArrayUtils.listToIntArray(indexList),
//                ArrayUtils.listToIntArray(boneIdList),
//                ArrayUtils.listToFloatArray(weightList)
//        );

        Mesh mesh = new Mesh(
                ArrayUtils.listToFloatArray(vertexList),
                ArrayUtils.listToFloatArray(textCoordList),
                ArrayUtils.listToFloatArray(normalList),
                ArrayUtils.listToIntArray(indexList),
                ArrayUtils.listToIntArray(boneIdList),
                ArrayUtils.listToFloatArray(weightList)
        );

        attachMaterial(mesh, aiMesh, materialList);

        return mesh;
    }

    private static void processBoneList(
            AIMesh aiMesh,
            List<Bone> boneList,
            List<Integer> boneIdList,
            List<Float> weightList
    ) {
        Map<Integer, List<VertexWeight>> weightSet = new HashMap<>();
        int numBones = aiMesh.mNumBones();
        PointerBuffer aiBones = aiMesh.mBones();

        for (int i = 0; i < numBones; i++) {
            AIBone aiBone = AIBone.create(aiBones.get(i));
            int id = boneIdList.size();
            Bone bone = new Bone(
                    id,
                    aiBone.mName().dataString(),
                    AssImpUtils.toJomlMatrix4f(aiBone.mOffsetMatrix())
            );

            boneList.add(bone);

            int numWeights = aiBone.mNumWeights();
            AIVertexWeight.Buffer aiWeights = aiBone.mWeights();

            for (int j = 0; j < numWeights; j++) {
                AIVertexWeight aiVertexWeight = aiWeights.get(j);
                VertexWeight vertexWeight = new VertexWeight(
                        bone.getId(),
                        aiVertexWeight.mVertexId(),
                        aiVertexWeight.mWeight()
                );
                List<VertexWeight> vertexWeightList = weightSet.get(vertexWeight.getVertexId());

                if (vertexWeightList == null) {
                    vertexWeightList = new ArrayList<>();
                    weightSet.put(vertexWeight.getVertexId(), vertexWeightList);
                }
                vertexWeightList.add(vertexWeight);
            }
        }

        int numVertices = aiMesh.mNumVertices();

        for (int i = 0; i < numVertices; i++) {
            List<VertexWeight> vertexWeightList = weightSet.get(i);
            int size = vertexWeightList != null ? vertexWeightList.size() : 0;

            for (int j = 0; j < Mesh.MAX_WEIGHTS; j++) {
                if (j < size) {
                    VertexWeight vertexWeight = vertexWeightList.get(j);
                    weightList.add(vertexWeight.getWeight());
                    boneIdList.add(vertexWeight.getBoneId());
                } else {
                    weightList.add(0.0f);
                    boneIdList.add(0);
                }
            }
        }
    }


    private static Node processNodesHierarchy(AINode aiNode, Node parentNode) {
        String nodeName = aiNode.mName().dataString();
        Node node = new Node(nodeName, parentNode);

        int numChildren = aiNode.mNumChildren();
        PointerBuffer aiChildren = aiNode.mChildren();

        for (int i = 0; i < numChildren; i++) {
            AINode aiChildNode = AINode.create(aiChildren.get(i));
            Node childNode = processNodesHierarchy(aiChildNode, node);
            node.addChildNode(childNode);
        }

        return node;
    }

    private static Map<String, Animation> processAnimations(
            AIScene aiScene,
            List<Bone> boneList,
            Node rootNode,
            Matrix4f rootTransformation
    ) {
        Map<String, Animation> animationMap = new HashMap<>();

        //process all animations
        int numAnimations = aiScene.mNumAnimations();
        PointerBuffer aiAnimations = aiScene.mAnimations();

        for (int i = 0; i < numAnimations; i++) {
            AIAnimation aiAnimation = AIAnimation.create(aiAnimations.get(i));

            //calculate transformation matrix for each node
            int numChannels = aiAnimation.mNumChannels();
            PointerBuffer aiChannels = aiAnimation.mChannels();

            for (int j = 0; j < numChannels; j++) {
                AINodeAnim aiNodeAnim = AINodeAnim.create(aiChannels.get(j));
                String nodeName = aiNodeAnim.mNodeName().dataString();
                Node node = rootNode.getNodeByName(nodeName);
                buildTransformationMatrices(aiNodeAnim, node);
            }

            List<AnimatedFrame> frameList = generateAnimatedFrameList(
                    boneList,
                    rootNode,
                    rootTransformation
            );
            Animation animation = new Animation(
                    aiAnimation.mName().dataString(),
                    frameList,
                    aiAnimation.mDuration()
            );
            animationMap.put(animation.getName(), animation);
        }

        return animationMap;
    }

    private static List<AnimatedFrame> generateAnimatedFrameList(
            List<Bone> boneList,
            Node rootNode,
            Matrix4f rootTransformation
    ) {
        int numFrames = rootNode.getAnimationFramesCount();
        List<AnimatedFrame> frameList = new ArrayList<>();

        for (int i = 0; i < numFrames; i++) {
            AnimatedFrame animatedFrame = new AnimatedFrame();

            frameList.add(animatedFrame);

            int numBones = boneList.size();

            for (int j = 0; j < numBones; j++) {
                Bone bone = boneList.get(j);
                Node node = rootNode.getNodeByName(bone.getName());
                Matrix4f boneMatrix = Node.getParentTransforms(node, i);

                boneMatrix.mul(bone.getOffsetMatrix());
                boneMatrix = new Matrix4f(rootTransformation).mul(boneMatrix);
                animatedFrame.setJointMatrix(j, boneMatrix);
            }
        }

        return frameList;
    }

    private static void buildTransformationMatrices(AINodeAnim aiNodeAnim, Node node) {
        int numFrames = aiNodeAnim.mNumPositionKeys();
        AIVectorKey.Buffer positionKeys = aiNodeAnim.mPositionKeys();
        AIVectorKey.Buffer scalingKeys = aiNodeAnim.mScalingKeys();
        AIQuatKey.Buffer rotationKeys = aiNodeAnim.mRotationKeys();

        for (int i = 0; i < numFrames; i++) {
            AIVectorKey aiVectorKey = positionKeys.get(i);
            AIVector3D vector3D = aiVectorKey.mValue();
            Matrix4f transformationMatrix =
                    new Matrix4f().translate(vector3D.x(), vector3D.y(), vector3D.z());
            AIQuatKey quatKey = rotationKeys.get(i);
            AIQuaternion aiQuaternion = quatKey.mValue();
            Quaternionf quaternion = new Quaternionf(
                    aiQuaternion.x(),
                    aiQuaternion.y(),
                    aiQuaternion.z(),
                    aiQuaternion.w()
            );

            if (i < aiNodeAnim.mNumScalingKeys()) {
                aiVectorKey = scalingKeys.get(i);
                vector3D = aiVectorKey.mValue();
                transformationMatrix.scaleLocal(vector3D.x(), vector3D.y(), vector3D.z());
            }

            node.addTransformationMatrix(transformationMatrix);
        }
    }
}