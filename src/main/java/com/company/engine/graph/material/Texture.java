package com.company.engine.graph.material;

import com.company.engine.IUsesResources;
import com.company.engine.utils.FileUtils;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

public class Texture implements IUsesResources {

    private final int mId;
    private final int mWidth;
    private final int mHeight;

    private int mNumColumns = 1;
    private int mNumRows = 1;

    public Texture(String fileName) throws Exception {
        this(FileUtils.ioResourceToByteBuffer(fileName, 1024));
    }

    public Texture(int width, int height, int pixelFormat) {
        mId = glGenTextures();
        mWidth = width;
        mHeight = height;

        glBindTexture(GL_TEXTURE_2D, mId);
        glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_DEPTH_COMPONENT,
                mWidth,
                mHeight,
                0,
                pixelFormat,
                GL_FLOAT,
                (ByteBuffer) null
        );
        glTexParameterIi(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterIi(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameterIi(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameterIi(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    }

    public Texture(String fileName, int numColumns, int numRows) throws Exception {
        this(fileName);
        mNumColumns = numColumns;
        mNumRows = numRows;
    }

    public Texture(ByteBuffer imageData) throws Exception {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer avChannels = stack.mallocInt(1);

            //turn image into byte buffer
            ByteBuffer decodedImage = stbi_load_from_memory(
                    imageData,
                    width,
                    height,
                    avChannels,
                    4
            );

            mWidth = width.get();
            mHeight = height.get();

            //create new OpenGL texture
            mId = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, mId);

            //tell OpenGL how to unpack the RGBA bytes. Each component is 1 byte in size
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            //upload the texture data
            glTexImage2D(
                    GL_TEXTURE_2D,
                    0,
                    GL_RGBA,
                    mWidth,
                    mHeight,
                    0,
                    GL_RGBA,
                    GL_UNSIGNED_BYTE,
                    decodedImage
            );
            //generate Mip Map
            glGenerateMipmap(GL_TEXTURE_2D);
        }
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, mId);
    }

    @Override
    public void cleanUp() {
        glDeleteTextures(mId);
    }

    public int getId() {
        return mId;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getNumColumns() {
        return mNumColumns;
    }

    public int getNumRows() {
        return mNumRows;
    }
}