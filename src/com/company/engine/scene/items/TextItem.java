package com.company.engine.scene.items;

import com.company.engine.Utils;
import com.company.engine.graph.Material;
import com.company.engine.graph.Texture;
import com.company.engine.graph.mesh.Mesh;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class TextItem extends GameItem {

    /*
    Used to display any kind of text
     */

    private static final int VERTICES_PER_QUAD = 4;

    private final int mFontAtlasColumnCount;
    private final int mFontAtlasRowCount;

    private String mText;
    private float mZPosition;

    public TextItem(
            String text,
            String fontAtlasPath,
            int fontAtlasColumnCount,
            int fontAtlasRowCount
    ) throws Exception {
        super();
        mText = text;
        mFontAtlasColumnCount = fontAtlasColumnCount;
        mFontAtlasRowCount = fontAtlasRowCount;
        mZPosition = 0.0f;

        //set the texture to the font atlas
        Texture texture = new Texture(fontAtlasPath);
        setMesh(buildMesh(texture, mFontAtlasColumnCount, mFontAtlasRowCount));
    }

    private Mesh buildMesh(Texture texture, int columnCount, int rowCount) {
        byte[] chars = mText.getBytes(Charset.forName("ISO-8859-1"));
        int numChars = chars.length;

        List<Float> positions = new ArrayList<>();
        List<Float> textCoords = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        float tileWidth = (float) texture.getWidth() / (float) columnCount;
        float tileHeight = (float) texture.getHeight() / (float) rowCount;

        for (int i = 0; i < numChars; i++) {
            byte currentChar = chars[i];
            int col = currentChar & columnCount;
            int row = currentChar / columnCount;

            //build a tile made up of two triangles

            //top left vertex
            positions.add((float) i * tileWidth); //x
            positions.add(0.0f); //y
            positions.add(mZPosition); //z
            textCoords.add((float) col / (float) mFontAtlasColumnCount);
            textCoords.add((float) row / (float) mFontAtlasRowCount);
            indices.add(i * VERTICES_PER_QUAD);

            //bottom left vertex
            positions.add((float) i * tileWidth); //x
            positions.add(tileHeight); //y
            positions.add(mZPosition); //z
            textCoords.add((float) col / (float) mFontAtlasColumnCount);
            textCoords.add((float) (row + 1) / (float) mFontAtlasRowCount);
            indices.add(i * VERTICES_PER_QUAD + 1);

            //bottom right vertex
            positions.add((float) (i + 1) * tileWidth); //x
            positions.add(tileHeight); //y
            positions.add(mZPosition); //z
            textCoords.add((float) (col + 1) / (float) mFontAtlasColumnCount);
            textCoords.add((float) (row + 1) / (float) mFontAtlasRowCount);
            indices.add(i * VERTICES_PER_QUAD + 2);

            //top right vertex
            positions.add((float) (i + 1) * tileWidth); //x
            positions.add(0.0f); //y
            positions.add(mZPosition); //z
            textCoords.add((float) (col + 1) / (float) mFontAtlasColumnCount);
            textCoords.add((float) row / (float) mFontAtlasRowCount);
            indices.add(i * VERTICES_PER_QUAD + 3);

            //add the indices for the top left and bottom right vertices
            indices.add(i * VERTICES_PER_QUAD);
            indices.add(i * VERTICES_PER_QUAD + 2);
        }

        float[] positionArray = Utils.listToFloatArray(positions);
        float[] textCoordsArray = Utils.listToFloatArray(textCoords);
        int[] indicesArray = Utils.listToIntArray(indices);

        Mesh mesh = new Mesh(positionArray, textCoordsArray, null, indicesArray);
        mesh.setMaterial(new Material(texture));

        return mesh;
    }

    public void setText(String text) {
        mText = text;
        getMesh().deleteBuffers();
        setMesh(buildMesh(
                getMesh().getMaterial().getTexture(),
                mFontAtlasColumnCount,
                mFontAtlasRowCount
        ));
    }

    public String getText() {
        return mText;
    }
}
