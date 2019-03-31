package com.company.engine.graph.lighting;

import com.company.engine.IUsesResources;
import com.company.engine.graph.material.Texture;

import static org.lwjgl.opengl.GL30.*;

public class ShadowMap implements IUsesResources {

    public static final int SHADOW_MAP_DEFAULT_WIDTH = 1024;
    public static final int SHADOW_MAP_DEFAULT_HEIGHT = 1024;

    private static final float SHADOW_MAP_MAX_SIZE_MULTIPLIER = 8;
    private static final float SHADOW_MAP_MIN_SIZE_MULTIPLIER = 0.1f;

    private final int mDepthMapFbo;
    private final Texture mDepthMapTexture;

    /**
     * Create a shadow map as a Texture, then store it for use in shaders
     * @param shadowMapSizeMultiplier The multiplier used to determine the shadow map's size
     * @throws Exception Thrown if FBO failed to create
     */
    public ShadowMap(float shadowMapSizeMultiplier) throws Exception {

        if (shadowMapSizeMultiplier > SHADOW_MAP_MAX_SIZE_MULTIPLIER) {
            shadowMapSizeMultiplier = SHADOW_MAP_MIN_SIZE_MULTIPLIER;
        } else if (shadowMapSizeMultiplier < SHADOW_MAP_MIN_SIZE_MULTIPLIER) {
            shadowMapSizeMultiplier = SHADOW_MAP_MIN_SIZE_MULTIPLIER;
        }

        //create FBO
        mDepthMapFbo = glGenFramebuffers();

        int depthMapWidth = (int) Math.floor(SHADOW_MAP_DEFAULT_WIDTH * shadowMapSizeMultiplier);
        int depthMapHeight = (int) Math.floor(SHADOW_MAP_DEFAULT_HEIGHT * shadowMapSizeMultiplier);

        //create depth map texture
        mDepthMapTexture = new Texture(
                depthMapWidth,
                depthMapHeight,
                GL_DEPTH_COMPONENT
        );

        //attach depth map texture to FBO
        glBindFramebuffer(GL_FRAMEBUFFER, mDepthMapFbo);
        glFramebufferTexture2D(
                GL_FRAMEBUFFER,
                GL_DEPTH_ATTACHMENT,
                GL_TEXTURE_2D,
                mDepthMapTexture.getId(),
                0
        );

        //set only to be used for depth mapping
        //an FBO requires a colour buffer, however,
        //we don't so we make it un-writable and un-readable
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new Exception("Failed to create FrameBuffer");
        }

        //unbind
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public Texture getDepthMapTexture() {
        return mDepthMapTexture;
    }

    public int getDepthMapFbo() {
        return mDepthMapFbo;
    }

    @Override
    public void cleanUp() {
        glDeleteFramebuffers(mDepthMapFbo);
        mDepthMapTexture.cleanUp();
    }
}