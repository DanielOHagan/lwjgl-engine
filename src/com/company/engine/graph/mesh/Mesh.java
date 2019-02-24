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

import static org.lwjgl.opengl.GL30.*;

public class Mesh {

    private static int POSITION_VBO_INDEX = 0;
    private static int TEXTURE_COORDINATES_VBO_INDEX = 1;
    private static int NORMALS_VBO_INDEX = 2;

    private int mVaoId;
    private List<Integer> mVboIdList;
    private int mVertexCount;
    private Material mMaterial;
    private boolean mUsingTextCoords;
    private boolean mUsingNormals;

    public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices) {
        mUsingNormals = normals != null;
        mUsingTextCoords = textCoords != null;
        initialiseMesh(positions, textCoords, normals, indices);
    }

    private void initialiseMesh(
            float[] positions,
            float[] textCoords,
            float[] normals,
            int[] indices
    ) {
        ArrayList<FloatBuffer> floatBuffers = new ArrayList<>();
        ArrayList<IntBuffer> intBuffers = new ArrayList<>();

        FloatBuffer posBuffer;
        FloatBuffer textCoordsBuffer;
        FloatBuffer normalsBuffer;
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
            if (textCoords != null) {
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
                vboId = glGenBuffers();
                mVboIdList.add(vboId);
                normalsBuffer = MemoryUtil.memAllocFloat(normals.length);
                floatBuffers.add(normalsBuffer);
                normalsBuffer.put(normals).flip();
                glBindBuffer(GL_ARRAY_BUFFER, vboId);
                glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW);
                glVertexAttribPointer(NORMALS_VBO_INDEX, 3, GL_FLOAT, false, 0, 0);
            }

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
        /* Used to render a single item */

        initRender();

        glDrawElements(GL_TRIANGLES, mVertexCount, GL_UNSIGNED_INT, 0);

        endRender();
    }

    public void renderList(List<GameItem> gameItems, Consumer<GameItem> consumer) {
        /* Used to render a list of GameItem that have the same Mesh */

        initRender();

        for (GameItem gameItem : gameItems) {
            consumer.accept(gameItem);
            glDrawElements(GL_TRIANGLES, mVertexCount, GL_UNSIGNED_INT, 0);
        }

        endRender();
    }

    protected void initRender() {
        Texture texture = mMaterial != null && mMaterial.getTexture() != null ? mMaterial.getTexture() : null;

        if (texture != null) {
            //activate first texture bank
            glActiveTexture(GL_TEXTURE0);
            //bind the texture
            glBindTexture(GL_TEXTURE_2D, texture.getId());
        }

        glBindVertexArray(mVaoId);
        glEnableVertexAttribArray(POSITION_VBO_INDEX);
        if (mUsingTextCoords) {
            glEnableVertexAttribArray(TEXTURE_COORDINATES_VBO_INDEX);
        }
        if (mUsingNormals) {
            glEnableVertexAttribArray(NORMALS_VBO_INDEX);
        }
    }

    private void endRender() {
        glDisableVertexAttribArray(POSITION_VBO_INDEX);
        if (mUsingTextCoords) {
            glDisableVertexAttribArray(TEXTURE_COORDINATES_VBO_INDEX);
        }
        if (mUsingNormals) {
            glDisableVertexAttribArray(NORMALS_VBO_INDEX);
        }
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
