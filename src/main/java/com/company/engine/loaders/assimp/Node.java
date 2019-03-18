package com.company.engine.loaders.assimp;

import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private final List<Node> mChildrenNodeList;
    private final List<Matrix4f> mTransformationList;
    private final String mName;
    private final Node mParentNode;

    public Node(String name, Node parentNode) {
        mName = name;
        mParentNode = parentNode;
        mChildrenNodeList = new ArrayList<>();
        mTransformationList = new ArrayList<>();
    }

    public static Matrix4f getParentTransforms(Node node, int framePosition) {
        if (node == null) {
            return new Matrix4f();
        } else {
            Matrix4f parentTransform = new Matrix4f(getParentTransforms(
                    node.getParentNode(),
                    framePosition)
            );
            List<Matrix4f> transformationList = node.getTransformationList();
            Matrix4f nodeTransformMatrix;
            int transListSize = transformationList.size();

            if (framePosition < transListSize) {
                nodeTransformMatrix = transformationList.get(framePosition);
            } else if (transListSize > 0) {
                nodeTransformMatrix = transformationList.get(transListSize - 1);
            } else {
                nodeTransformMatrix = new Matrix4f();
            }

            return parentTransform.mul(nodeTransformMatrix);
        }
    }

    public void addChildNode(Node childNode) {
        mChildrenNodeList.add(childNode);
    }

    public void addTransformationMatrix(Matrix4f matrix4f) {
        mTransformationList.add(matrix4f);
    }

    public Node getNodeByName(String name) {
        Node result = null;

        if (mName.equals(name)) {
            result = this;
        } else {
            for (Node childNode : mChildrenNodeList) {
                result = childNode.getNodeByName(name);

                if (result != null) {
                    break;
                }
            }
        }

        return result;
    }

    public int getAnimationFramesCount() {
        int numFrames = mTransformationList.size();

        for (Node childNode : mChildrenNodeList) {
            int childFrame = childNode.getAnimationFramesCount();

            numFrames = Math.max(numFrames, childFrame);
        }

        return numFrames;
    }

    public List<Node> getChildrenNodeList() {
        return mChildrenNodeList;
    }

    public List<Matrix4f> getTransformationList() {
        return mTransformationList;
    }

    public String getName() {
        return mName;
    }

    public Node getParentNode() {
        return mParentNode;
    }
}
