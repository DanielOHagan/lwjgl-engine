package com.company.engine.graph;

import de.matthiasmann.twl.utils.PNGDecoder;

import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL30.glTexParameterIi;

public class Texture {

    private final int mId;
    private final int mWidth;
    private final int mHeight;

    private int mNumColumns = 1;
    private int mNumRows = 1;

    public Texture(String fileName) throws Exception {
        this(Texture.class.getResourceAsStream(fileName));
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
        this(Texture.class.getResourceAsStream(fileName));
        mNumColumns = numColumns;
        mNumRows = numRows;
    }

    public Texture(InputStream inputStream) throws Exception {

        try {
            //load texture file
            PNGDecoder decoder = new PNGDecoder(inputStream);

            mWidth = decoder.getWidth();
            mHeight = decoder.getHeight();

            //load texture contents into a byte buffer
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(
                    4 * decoder.getWidth() * decoder.getHeight()
            );
            decoder.decode(byteBuffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            byteBuffer.flip();

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
                    decoder.getWidth(),
                    decoder.getHeight(),
                    0,
                    GL_RGBA,
                    GL_UNSIGNED_BYTE,
                    byteBuffer
            );
            //generate Mip Map
            glGenerateMipmap(GL_TEXTURE_2D);

            inputStream.close();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, mId);
    }

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
