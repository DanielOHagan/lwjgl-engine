package com.company.engine.graph.mesh;

import com.company.engine.graph.Material;
import com.company.engine.graph.Texture;
import com.company.engine.scene.items.GameItem;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.company.engine.graph.ShaderProgram.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {

    public static final int MAX_WEIGHTS = 4;

    protected int mVaoId;
    protected List<Integer> mVboIdList;

    private int mVertexCount;
    private Material mMaterial;

    private boolean mUsingTextCoords;
    private boolean mUsingNormals;
    private boolean mUsingWeights;
    private boolean mUsingJointIndices;

    public Mesh(
            float[] positions,
            float[] textCoords,
            float[] normals,
            int[] indices
    ) {
        initialiseMesh(
                positions,
                textCoords,
                normals,
                indices,
                null,
                null
        );
    }

    public Mesh(
            float[] positions,
            float[] textCoords,
            float[] normals,
            int[] indices,
            int[] jointIndices,
            float[] weights
    ) {
        initialiseMesh(
                positions,
                textCoords,
                normals,
                indices,
                jointIndices,
                weights
        );
    }

    /*
    Store info into respective buffers for use in rendering
     */
    private void initialiseMesh(
            float[] positions,
            float[] textCoords,
            float[] normals,
            int[] indices,
            int[] jointIndices,
            float[] weights
    ) {
        mUsingTextCoords = textCoords != null;
        mUsingJointIndices = jointIndices != null;
        mUsingWeights = weights != null;

        ArrayList<FloatBuffer> floatBuffers = new ArrayList<>();
        ArrayList<IntBuffer> intBuffers = new ArrayList<>();

        FloatBuffer posBuffer;
        FloatBuffer textCoordsBuffer;
        FloatBuffer normalsBuffer;
        FloatBuffer weightsBuffer;
        IntBuffer indicesBuffer;
        IntBuffer joinIndicesBuffer;

        try {
            mVertexCount = indices.length;
            mVboIdList = new ArrayList<>();

            mVaoId = glGenVertexArrays();
            glBindVertexArray(mVaoId);

            //position VBO
            int vboId = glGenBuffers();
            mVboIdList.add(vboId);
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            floatBuffers.add(posBuffer);
            posBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(POSITION_VBO_INDEX, 3, GL_FLOAT, false, 0, 0);

            //texture coordinates VBO
            if (textCoords != null) {
                mUsingTextCoords = true;
                vboId = glGenBuffers();
                mVboIdList.add(vboId);
                textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
                floatBuffers.add(textCoordsBuffer);
                textCoordsBuffer.put(textCoords).flip();
                glBindBuffer(GL_ARRAY_BUFFER, vboId);
                glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
                glVertexAttribPointer(TEXTURE_COORDINATES_VBO_INDEX, 2, GL_FLOAT, false, 0, 0);
            }

            //normals VBO
            if (normals != null) {
                mUsingNormals = true;
                vboId = glGenBuffers();
                mVboIdList.add(vboId);
                normalsBuffer = MemoryUtil.memAllocFloat(normals.length);
                floatBuffers.add(normalsBuffer);
                normalsBuffer.put(normals).flip();
                glBindBuffer(GL_ARRAY_BUFFER, vboId);
                glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL_STATIC_DRAW);
                glVertexAttribPointer(NORMALS_VBO_INDEX, 3, GL_FLOAT, false, 0, 0);
            }

            //weights VBO
            if (weights != null) {
                mUsingWeights = true;
                vboId = glGenBuffers();
                mVboIdList.add(vboId);
                weightsBuffer = MemoryUtil.memAllocFloat(weights.length);
                floatBuffers.add(weightsBuffer);
                glBindBuffer(GL_ARRAY_BUFFER, vboId);
                glBufferData(GL_ARRAY_BUFFER, weightsBuffer, GL_STATIC_DRAW);
                glVertexAttribPointer(WEIGHTS_VBO_INDEX, 4, GL_FLOAT, false, 0, 0);
            }

            //joint indices VBO
            if (jointIndices != null) {
                mUsingJointIndices = true;
                vboId = glGenBuffers();
                mVboIdList.add(vboId);
                joinIndicesBuffer = MemoryUtil.memAllocInt(jointIndices.length);
                intBuffers.add(joinIndicesBuffer);
                glBindBuffer(GL_ARRAY_BUFFER, vboId);
                glBufferData(GL_ARRAY_BUFFER, joinIndicesBuffer, GL_STATIC_DRAW);
                glVertexAttribPointer(JOINT_INDICES_VBO_INDEX, 4, GL_FLOAT, false, 0, 0);
            }

            //indices VBO
            vboId = glGenBuffers();
            mVboIdList.add(vboId);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            intBuffers.add(indicesBuffer);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        } finally {
            for (FloatBuffer floatBuffer : floatBuffers) {
                if (floatBuffer != null) {
                    MemoryUtil.memFree(floatBuffer);
                }
            }
            for (IntBuffer intBuffer : intBuffers) {
                if (intBuffer != null) {
                    MemoryUtil.memFree(intBuffer);
                }
            }

            //unbind the buffers
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
    }

    /*
    Renders a single Mesh instance
     */
    public void render() {
        initRender();

        glDrawElements(GL_TRIANGLES, mVertexCount, GL_UNSIGNED_INT, 0);

        endRender();
    }

    /*
    Renders a List of GameItem instances
     */
    public void renderList(List<GameItem> gameItems, Consumer<GameItem> consumer) {
        initRender();

        for (GameItem gameItem : gameItems) {
            consumer.accept(gameItem);
            glDrawElements(GL_TRIANGLES, mVertexCount, GL_UNSIGNED_INT, 0);
        }

        endRender();
    }

    /*
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

        /* TODO:
        Texture normalMap = mMaterial != null &&
                mMaterial.getNormalMap() != null ? mMaterial.getNormalMap() : null;

        if (normalMap != null) {
            //active the second texture bank
            glActiveTexture(GL_TEXTURE1);
            //bind texture
            glBindTexture(GL_TEXTURE_2D, normalMap.getId());
        }
        */

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

    /*
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

    /*
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
}
