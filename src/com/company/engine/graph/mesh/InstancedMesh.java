package com.company.engine.graph.mesh;

import com.company.engine.Utils;
import com.company.engine.graph.Texture;
import com.company.engine.graph.Transformation;
import com.company.engine.scene.items.GameItem;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

public class InstancedMesh extends Mesh {

    private static final int FLOAT_SIZE_BYTES = 4; //number of bytes used to store a float in the instanced buffer
    private static final int VECTOR4F_SIZE_BYTES = 4 * FLOAT_SIZE_BYTES; //number of bytes used to store a Vector4f in the instanced buffer
    private static final int MATRIX_SIZE_FLOATS = 4 * 4; //number of floats in a 4x4 matrix
    private static final int MATRIX_SIZE_BYTES = MATRIX_SIZE_FLOATS * FLOAT_SIZE_BYTES; //number of bytes used to store a 4x4 float matrix in the instance buffer
    private static final int INSTANCE_SIZE_BYTES = MATRIX_SIZE_BYTES * 2 + FLOAT_SIZE_BYTES * 2; //number of bytes the instance buffer uses (2 4x4 float matrices and 2 floats)
    private static final int INSTANCE_SIZE_FLOATS = MATRIX_SIZE_FLOATS * 2 + 2; //number of floats the instance buffer uses (2 4x4 float matrices and 2 floats)

    private final int mNumberOfInstances;

    private int mInstanceDataVboId;
    private FloatBuffer mInstanceDataBuffer;

    public InstancedMesh(
            float[] positions,
            float[] textCoords,
            float[] normals,
            int[] indices,
            int numberOfInstances
    ) {
        super(
                positions,
                textCoords,
                normals,
                indices,
                Utils.createEmptyIntArray(MAX_WEIGHTS * positions.length / 3, 0),
                Utils.createEmptyFloatArray(MAX_WEIGHTS * positions.length / 3, 0)

        );

        mNumberOfInstances = numberOfInstances;

        initialiseInstancedMesh();
    }

    @Override
    protected void initRender() {
        super.initRender();

        int start = 5;
        int numberOfElements = 4 * 2;

        for (int i = 0; i < numberOfElements; i++) {
            glEnableVertexAttribArray(start + i);
        }
    }

    @Override
    protected void endRender() {
        int start = 5;
        int numberOfElements = 4 * 2;

        for (int i = 0; i < numberOfElements; i++) {
            glDisableVertexAttribArray(start + i);
        }

        super.endRender();
    }

    private void initialiseInstancedMesh() {
        glBindVertexArray(mVaoId);
        int start = 5; //maybe switch this to mVboIdList.size();
        int strideStart = 0;

        //model view matrix
        mInstanceDataVboId = glGenBuffers();
        mVboIdList.add(mInstanceDataVboId);
        mInstanceDataBuffer = MemoryUtil.memAllocFloat(mNumberOfInstances * INSTANCE_SIZE_FLOATS);
        glBindBuffer(GL_ARRAY_BUFFER, mInstanceDataVboId);

        //store the matrix as 4 vectors that store 4 values each
        for (int i = 0; i < 4; i++) {
            glVertexAttribPointer(
                    start,
                    4,
                    GL_FLOAT,
                    false,
                    INSTANCE_SIZE_BYTES,
                    strideStart
            );
            glVertexAttribDivisor(start, 1);
            start++;
            strideStart += VECTOR4F_SIZE_BYTES;
        }

        //light view matrix
        //store the matrix as 4 vectors that store 4 values each
//        for (int i = 0; i < 4; i++) {
//            glVertexAttribPointer(
//                    start,
//                    4,
//                    GL_FLOAT,
//                    false,
//                    INSTANCE_SIZE_BYTES,
//                    strideStart
//            );
//            glVertexAttribDivisor(start, 1);
//            start++;
//            strideStart += VECTOR4F_SIZE_BYTES;
//        }

        //texture offsets
        glVertexAttribPointer(
                start,
                2,
                GL_FLOAT,
                false,
                INSTANCE_SIZE_BYTES,
                strideStart
        );
        glVertexAttribDivisor(start, 1);

        //unbind
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void renderInstancedList(
            List<GameItem> gameItemList,
            Transformation transformation,
            Matrix4f viewMatrix,
            Matrix4f lightViewMatrix
    ) {
        renderInstancedList(
                gameItemList,
                false,
                transformation,
                viewMatrix,
                lightViewMatrix
        );
    }

    public void renderInstancedList(
            List<GameItem> gameItems,
            boolean billboard,
            Transformation transformation,
            Matrix4f viewMatrix,
            Matrix4f lightViewMatrix
    ) {
        //splits the list of game items into chunks to be rendered
        initRender();

        int chunkSize = mNumberOfInstances;
        int length = gameItems.size();

        for (int i = 0; i < length; i += chunkSize) {
            int end = Math.min(length, i + chunkSize);
            List<GameItem> subList = gameItems.subList(i, end);
            renderInstancedChunkList(
                    subList,
                    billboard,
                    transformation,
                    viewMatrix,
                    lightViewMatrix
            );
        }

        endRender();
    }

    private void renderInstancedChunkList(
            List<GameItem> gameItemList,
            boolean billboard,
            Transformation transformation,
            Matrix4f viewMatrix,
            Matrix4f lightViewMatrix
    ) {
        mInstanceDataBuffer.clear();

        int i = 0;

        Texture texture = getMaterial() != null &&
                getMaterial().getTexture() != null ? getMaterial().getTexture() : null;

        for (GameItem gameItem : gameItemList) {
            Matrix4f modelMatrix = transformation.generateModelMatrix(gameItem);
            int bufferPosition = INSTANCE_SIZE_FLOATS * i + MATRIX_SIZE_FLOATS;

            if (viewMatrix != null && billboard) {
                viewMatrix.transpose3x3(modelMatrix);
            }

            modelMatrix.get(INSTANCE_SIZE_FLOATS * i, mInstanceDataBuffer);

            //texture offsets
            if (texture != null) {
                int column = gameItem.getTexturePos() % texture.getNumColumns();
                int row = gameItem.getTexturePos() / texture.getNumColumns();
                float textOffsetX = (float) column / texture.getNumColumns();
                float textOffsetY = (float) row / texture.getNumRows();

                mInstanceDataBuffer.put(bufferPosition, textOffsetX);
                mInstanceDataBuffer.put(bufferPosition + 1, textOffsetY);
            }



            i++;
        }

        glBindBuffer(GL_ARRAY_BUFFER, mInstanceDataVboId);
        glBufferData(GL_ARRAY_BUFFER, mInstanceDataBuffer, GL_DYNAMIC_DRAW);

        //drawn instance
        glDrawElementsInstanced(
                GL_TRIANGLES,
                getVertexCount(),
                GL_UNSIGNED_INT,
                0,
                gameItemList.size()
        );

        //unbind
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}