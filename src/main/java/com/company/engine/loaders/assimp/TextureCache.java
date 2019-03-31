package com.company.engine.loaders.assimp;

import com.company.engine.IUsesResources;
import com.company.engine.graph.material.Texture;

import java.util.HashMap;
import java.util.Map;

public class TextureCache implements IUsesResources {

    private static TextureCache INSTANCE;

    private Map<String, Texture> mTextureMap;

    private TextureCache() {
        mTextureMap = new HashMap<>();
    }

    public static synchronized TextureCache getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TextureCache();
        }

        return INSTANCE;
    }

    public Texture getTexture(String filePath) throws Exception {
        Texture texture = mTextureMap.get(filePath);

        //store texture if not already instanced
        if (texture == null) {
            texture = new Texture(filePath);
            mTextureMap.put(filePath, texture);
        }

        return texture;
    }

    @Override
    public void cleanUp() {
        for (String string : mTextureMap.keySet()) {
            Texture texture = mTextureMap.get(string);

            if (texture != null) {
                texture.cleanUp();
            }

            mTextureMap.remove(string);
        }

        mTextureMap.clear();

        INSTANCE = null;
    }
}