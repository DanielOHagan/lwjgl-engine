package com.company.engine.graph;

import de.matthiasmann.twl.utils.PNGDecoder;

import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class Texture {

    private final int mId;

    public Texture(String fileName) throws Exception {
        this(Texture.class.getResourceAsStream(fileName));
    }

    public Texture(InputStream inputStream) throws Exception {
        //load texture file
        PNGDecoder decoder = new PNGDecoder(inputStream);

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
}
