package com.company.engine.graph;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class Mesh {

    private static int POSITION_VBO_INDEX = 0;
    private static int TEXTURE_COORDINATES_VBO_INDEX = 1;
    private static int NORMALS_VBO_INDEX = 2;

    private final int mVaoId;
    private final List<Integer> mVboIdList;
    private final int mVertexCount;

    private Material mMaterial;

    public Mesh(float[] positions, float[] texCoords, int[] indices) {
        ArrayList<FloatBuffer> floatBuffers = new ArrayList<>();
        ArrayList<IntBuffer> intBuffers = new ArrayList<>();
        FloatBuffer posBuffer;
        FloatBuffer textCoordsBuffer;
        IntBuffer indicesBuffer;

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
            vboId = glGenBuffers();
            mVboIdList.add(vboId);
            textCoordsBuffer = MemoryUtil.memAllocFloat(texCoords.length);
            floatBuffers.add(textCoordsBuffer);
            textCoordsBuffer.put(texCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(TEXTURE_COORDINATES_VBO_INDEX, 2, GL_FLOAT, false, 0, 0);

            //TODO: normals VBO

            //indices VBO
            vboId = glGenBuffers();
            mVboIdList.add(vboId);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            intBuffers.add(indicesBuffer);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            //unbind the buffers
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
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
        }
    }

    public void render() {
        initRender();

        glDrawElements(GL_TRIANGLES, mVertexCount, GL_UNSIGNED_INT, 0);

        endRender();
    }

    protected void initRender() {
        Texture texture = mMaterial != null ? mMaterial.getTexture() : null;

        if (texture != null) {
            //activate first texture bank
            glActiveTexture(GL_TEXTURE0);
            //bind the texture
            glBindTexture(GL_TEXTURE_2D, texture.getId());
        }

        //draw the mesh
        glBindVertexArray(mVaoId);
        glEnableVertexAttribArray(POSITION_VBO_INDEX);
        glEnableVertexAttribArray(TEXTURE_COORDINATES_VBO_INDEX);

    }

    private void endRender() {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

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
        glDisableVertexAttribArray(0);

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
}
