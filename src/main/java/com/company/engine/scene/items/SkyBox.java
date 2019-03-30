package com.company.engine.scene.items;

import com.company.engine.graph.material.Material;
import com.company.engine.graph.mesh.Mesh;
import com.company.engine.graph.mesh.MeshType;
import com.company.engine.loaders.assimp.StaticMeshesLoader;
import org.joml.Vector4f;

public class SkyBox extends GameItem {

    private boolean mInFixedPosition;

    public SkyBox(
            String filePath,
            String textureFilePathDirectory
    ) throws Exception {
        super();
        Mesh[] meshArray = StaticMeshesLoader.loadMeshes(
                filePath,
                textureFilePathDirectory,
                1,
                MeshType.STANDARD
        );

        setMeshArray(meshArray);
        setPosition(0, 0, 0);
        setInFixedPosition(true);
    }

    public SkyBox(String filePath, Material material) throws Exception {
        super();

        Mesh[] meshArray = StaticMeshesLoader.loadMeshes(
                filePath,
                material,
                1,
                MeshType.STANDARD
        );

        setMeshArray(meshArray);
        setPosition(0, 0, 0);
        setInFixedPosition(true);
    }

    public boolean isInFixedPosition() {
        return mInFixedPosition;
    }

    public void setInFixedPosition(boolean inFixedPosition) {
        mInFixedPosition = inFixedPosition;
    }
}