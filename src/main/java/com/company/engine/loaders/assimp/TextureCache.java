package com.company.engine.loaders.assimp;

import com.company.engine.graph.Texture;

import java.util.HashMap;
import java.util.Map;

public class TextureCache {

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
}
