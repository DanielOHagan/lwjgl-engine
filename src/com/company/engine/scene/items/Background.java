package com.company.engine.scene.items;

import com.company.engine.graph.Camera;
import com.company.engine.graph.Material;
import com.company.engine.graph.Texture;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Background {

    public static final Vector4f BACKGROUND_DEFAULT_COLOUR = new Vector4f(0, 1, 0, 1);
    private static int POSITION_VBO_INDEX = 0;
    private static int TEXTURE_COORDINATES_VBO_INDEX = 1;

    private int mVaoId;
    private List<Integer> mVboIdList;
    private int mVertexCount;
    private Material mMaterial;
    private boolean mUsingTextCoords;

    //background options
    private boolean mFillScreenSize;
    private boolean mScrolling;
    private int mScrollingX;
    private int mScrollStride;
    private int mScrollingPosition;

    public Background(
            float[] positions,
            float[] textCoords,
            int[] indices
    ) {
        mMaterial = new Material(BACKGROUND_DEFAULT_COLOUR);
        initialiseBackground(positions, textCoords, indices);
    }

    public Background(
            Vector4f colour
    ) {
        mMaterial = new Material(colour);
    }

    private void initialiseBackground(
            float[] positions,
            float[] textCoords,
            int[] indices
    ) {
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

        if(mFillScreenSize) {

        }

        if (mScrolling) {
            for (int i = mScrollingPosition; i < mScrollingPosition + 4; i++) {

            }
        }

        glDrawElements(GL_TRIANGLES, mVertexCount, GL_UNSIGNED_INT, 0);

        endRender();
    }

    private void initRender() {

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
    }

    private void endRender() {
        glDisableVertexAttribArray(POSITION_VBO_INDEX);
        if (mUsingTextCoords) {
            glDisableVertexAttribArray(TEXTURE_COORDINATES_VBO_INDEX);
            glBindTexture(GL_TEXTURE_2D, 0);
        }
        glBindVertexArray(0);
        //remove texture from texture bank
        if (mUsingTextCoords) {
            glBindTexture(GL_TEXTURE_2D, 0);
        }
    }

    public void update() {



        //update position of a scrolling background
        if (mScrolling) {
            mScrollingX -= mScrollStride;
            if (-mScrollingX % 300 == 0) {
                mScrollingPosition += mScrollStride;
            }
        }
    }

    public void cleanUp() {
        deleteBuffers();

        // delete the material
        if (mMaterial != null) {
            mMaterial.cleanUp();
        }
    }

    private void deleteBuffers() {
        for (int vboId : mVboIdList) {
            glDeleteBuffers(vboId);
        }

        glDisableVertexAttribArray(0);
    }

    public Material getMaterial() {
        return mMaterial;
    }

    public void setMaterial(Material material) {
        mMaterial = material;
    }
}
