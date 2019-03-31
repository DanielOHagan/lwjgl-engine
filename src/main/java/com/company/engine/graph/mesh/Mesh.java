package com.company.engine.graph.mesh;

import com.company.engine.IUsesResources;
import com.company.engine.graph.material.*;
import com.company.engine.graph.material.Texture;
import com.company.engine.scene.items.GameItem;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.company.engine.graph.rendering.ShaderProgram.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh implements IUsesResources {

    public static final int MAX_WEIGHTS = 4;

    protected int mVaoId;
    protected List<Integer> mVboIdList;

    private int mVertexCount;
    private Material mMaterial;

    private boolean mUsingTextCoords;
    private boolean mUsingNormals;
    private boolean mUsingWeights;
    private boolean mUsingJointIndices;

    private float mBoundingRadius;

    public Mesh(
            float[] positionArray,
            float[] textCoordArray,
            float[] normalArray,
            int[] indexArray
    ) {
        this(
                positionArray,
                textCoordArray,
                normalArray,
                indexArray,
                null,
                null
        );
    }

    public Mesh(
            float[] positionArray,
            float[] textCoordArray,
            float[] normalArray,
            int[] indexArray,
            int[] jointIndexArray,
            float[] weightArray
    ) {
        initialiseMesh(
                positionArray,
                textCoordArray,
                normalArray,
                indexArray,
                jointIndexArray,
                weightArray
        );
    }

    /**
    Store info into respective buffers for use in rendering
     */
    private void initialiseMesh(
            float[] positionArray,
            float[] textCoordArray,
            float[] normalArray,
            int[] indexArray,
            int[] jointIndexArray,
            float[] weightIndex
    ) {
        mUsingTextCoords = textCoordArray != null;
        mUsingJointIndices = jointIndexArray != null;
        mUsingWeights = weightIndex != null;

        ArrayList<FloatBuffer> floatBufferList = new ArrayList<>();
        ArrayList<IntBuffer> intBufferList = new ArrayList<>();

        FloatBuffer positionBuffer;
        FloatBuffer textCoordBuffer;
        FloatBuffer normalBuffer;
        FloatBuffer weightBuffer;
        IntBuffer indexBuffer;
        IntBuffer joinIndexBuffer;

        try {
            calculateBoundRadius(positionArray);

            mVertexCount = indexArray.length;
            mVboIdList = new ArrayList<>();

            mVaoId = glGenVertexArrays();
            glBindVertexArray(mVaoId);

            //position VBO
            int vboId = glGenBuffers();
            mVboIdList.add(vboId);
            positionBuffer = MemoryUtil.memAllocFloat(positionArray.length);
            floatBufferList.add(positionBuffer);
            positionBuffer.put(positionArray).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, positionBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(POSITION_VBO_INDEX, 3, GL_FLOAT, false, 0, 0);

            //texture coordinates VBO
            if (textCoordArray != null) {
                mUsingTextCoords = true;
                vboId = glGenBuffers();
                mVboIdList.add(vboId);
                textCoordBuffer = MemoryUtil.memAllocFloat(textCoordArray.length);
                floatBufferList.add(textCoordBuffer);
                textCoordBuffer.put(textCoordArray).flip();
                glBindBuffer(GL_ARRAY_BUFFER, vboId);
                glBufferData(GL_ARRAY_BUFFER, textCoordBuffer, GL_STATIC_DRAW);
                glVertexAttribPointer(TEXTURE_COORDINATES_VBO_INDEX, 2, GL_FLOAT, false, 0, 0);
            }

            //normals VBO
            if (normalArray != null) {
                mUsingNormals = true;
                vboId = glGenBuffers();
                mVboIdList.add(vboId);
                normalBuffer = MemoryUtil.memAllocFloat(normalArray.length);
                floatBufferList.add(normalBuffer);
                normalBuffer.put(normalArray).flip();
                glBindBuffer(GL_ARRAY_BUFFER, vboId);
                glBufferData(GL_ARRAY_BUFFER, normalBuffer, GL_STATIC_DRAW);
                glVertexAttribPointer(NORMALS_VBO_INDEX, 3, GL_FLOAT, false, 0, 0);
            }

            //weights VBO
            if (weightIndex != null) {
                mUsingWeights = true;
                vboId = glGenBuffers();
                mVboIdList.add(vboId);
                weightBuffer = MemoryUtil.memAllocFloat(weightIndex.length);
                floatBufferList.add(weightBuffer);
                glBindBuffer(GL_ARRAY_BUFFER, vboId);
                glBufferData(GL_ARRAY_BUFFER, weightBuffer, GL_STATIC_DRAW);
                glVertexAttribPointer(WEIGHTS_VBO_INDEX, 4, GL_FLOAT, false, 0, 0);
            }

            //joint indices VBO
            if (jointIndexArray != null) {
                mUsingJointIndices = true;
                vboId = glGenBuffers();
                mVboIdList.add(vboId);
                joinIndexBuffer = MemoryUtil.memAllocInt(jointIndexArray.length);
                intBufferList.add(joinIndexBuffer);
                glBindBuffer(GL_ARRAY_BUFFER, vboId);
                glBufferData(GL_ARRAY_BUFFER, joinIndexBuffer, GL_STATIC_DRAW);
                glVertexAttribPointer(JOINT_INDICES_VBO_INDEX, 4, GL_FLOAT, false, 0, 0);
            }

            //indices VBO
            vboId = glGenBuffers();
            mVboIdList.add(vboId);
            indexBuffer = MemoryUtil.memAllocInt(indexArray.length);
            intBufferList.add(indexBuffer);
            indexBuffer.put(indexArray).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

        } finally {
            for (FloatBuffer floatBuffer : floatBufferList) {
                if (floatBuffer != null) {
                    MemoryUtil.memFree(floatBuffer);
                }
            }
            for (IntBuffer intBuffer : intBufferList) {
                if (intBuffer != null) {
                    MemoryUtil.memFree(intBuffer);
                }
            }

            //unbind the buffers
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
    }

    private void calculateBoundRadius(float[] positionArray) {
        mBoundingRadius = 0;

        for (float position : positionArray) {
            mBoundingRadius = Math.max(Math.abs(position), mBoundingRadius);
        }
    }

    /**
    Renders a single Mesh instance
     */
    public void render() {
        initRender();

        glDrawElements(GL_TRIANGLES, mVertexCount, GL_UNSIGNED_INT, 0);

        endRender();
    }

    /**
    Renders a List of GameItem instances
     */
    public void renderList(
            List<GameItem> gameItems,
            Consumer<GameItem> consumer
    ) {
        initRender();

        for (GameItem gameItem : gameItems) {
            consumer.accept(gameItem);
            glDrawElements(GL_TRIANGLES, mVertexCount, GL_UNSIGNED_INT, 0);
        }

        endRender();
    }

    /**
    Prepare for rendering
     */
    protected void initRender() {
        Texture texture = mMaterial != null &&
                mMaterial.getTexture() != null ? mMaterial.getTexture() : null;

        if (texture != null) {
            //activate first texture bank
            glActiveTexture(GL_TEXTURE0);
            //bind the texture
            glBindTexture(GL_TEXTURE_2D, texture.getId());
        }

        Texture normalMap = mMaterial != null &&
                mMaterial.getNormalMap() != null ? mMaterial.getNormalMap() : null;

        if (normalMap != null) {
            //active the second texture bank
            glActiveTexture(GL_TEXTURE1);
            //bind texture
            glBindTexture(GL_TEXTURE_2D, normalMap.getId());
        }

        glBindVertexArray(mVaoId);
        glEnableVertexAttribArray(POSITION_VBO_INDEX);

        if (mUsingTextCoords) {
            glEnableVertexAttribArray(TEXTURE_COORDINATES_VBO_INDEX);
        }

        if (mUsingNormals) {
            glEnableVertexAttribArray(NORMALS_VBO_INDEX);
        }

        if (mUsingJointIndices) {
            glEnableVertexAttribArray(JOINT_INDICES_VBO_INDEX);
        }

        if (mUsingWeights) {
            glEnableVertexAttribArray(WEIGHTS_VBO_INDEX);
        }
    }

    /**
    Clean up after rendering
     */
    protected void endRender() {
        glDisableVertexAttribArray(POSITION_VBO_INDEX);

        if (mUsingTextCoords) {
            glDisableVertexAttribArray(TEXTURE_COORDINATES_VBO_INDEX);
        }

        if (mUsingNormals) {
            glDisableVertexAttribArray(NORMALS_VBO_INDEX);
        }

        if (mUsingWeights) {
            glDisableVertexAttribArray(WEIGHTS_VBO_INDEX);
        }

        if (mUsingJointIndices) {
            glDisableVertexAttribArray(JOINT_INDICES_VBO_INDEX);
        }

        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /**
    Delete all used buffers
     */
    public void deleteBuffers() {
        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : mVboIdList) {
            glDeleteBuffers(vboId);
        }

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(mVaoId);
    }

    @Override
    public void cleanUp() {
        deleteBuffers();

        // delete the material
        if (mMaterial != null) {
            mMaterial.cleanUp();
        }
    }

    public void setMaterial(Material mMaterial) {
        this.mMaterial = mMaterial;
    }

    public Material getMaterial() {
        return mMaterial;
    }

    public int getVertexCount() {
        return mVertexCount;
    }

    public void setBoundingRadius(float boundingRadius) {
        mBoundingRadius = boundingRadius;
    }

    public float getBoundingRadius() {
        return mBoundingRadius;
    }
}