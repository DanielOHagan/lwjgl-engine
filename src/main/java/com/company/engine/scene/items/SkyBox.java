package com.company.engine.scene.items;

import com.company.engine.graph.Material;
import com.company.engine.graph.Texture;
import com.company.engine.graph.mesh.Mesh;
import com.company.engine.graph.mesh.MeshType;
import com.company.engine.loaders.assimp.StaticMeshesLoader;
import org.joml.Vector4f;

public class SkyBox extends GameItem {

    private boolean mInFixedPosition;

    /*
    Create a SkyBox instance using an obj file and a texture
     */
    public SkyBox(String objModelFilePath, String textureFilePath) throws Exception {
        super();
//        Mesh mesh = ObjLoader.loadMesh(objModelFilePath, MeshType.STANDARD);
        Mesh mesh = StaticMeshesLoader.loadMeshes(objModelFilePath, "")[0];
        Texture texture = new Texture(textureFilePath);

        mesh.setMaterial(new Material(texture, 0.0f));
        setMesh(mesh);
        setPosition(0, 0, 0);
        setInFixedPosition(true);
    }


    /*
    Create a SkyBox instance using an obj file and a specified colour
     */
    public SkyBox(String objModelFilePath, Vector4f colour) throws Exception {
        super();
        Mesh mesh = StaticMeshesLoader.loadMeshes(objModelFilePath, "")[0];
        Material material = new Material(colour, 0);

        mesh.setMaterial(material);
        setMesh(mesh);
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