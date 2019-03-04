#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;
layout (location = 2) in vec3 vertexNormal;
layout (location = 5) in mat4 instancedModelViewMatrix;
layout (location = 13) in vec2 textOffset;

out vec2 outTexCoord;

uniform int isInstanced;
uniform mat4 projectionMatrix;
uniform int numColumns;
uniform int numRows;

uniform mat4 nonInstancedModelViewMatrix;
uniform float nonInstancedTextOffsetX;
uniform float nonInstancedTextOffsetY;

void main() {

    float x;
    float y;
    mat4 modelViewMatrixTemp;

    if (isInstanced > 0) {
        modelViewMatrixTemp = instancedModelViewMatrix;

        //for texture atlas
        x = (texCoord.x / numColumns + textOffset.x);
        y = (texCoord.y / numRows + textOffset.y);

    } else {
        modelViewMatrixTemp = nonInstancedModelViewMatrix;

        //for texture atlas
        x = (texCoord.x / numColumns + nonInstancedTextOffsetX);
        y = (texCoord.y / numRows + nonInstancedTextOffsetY);
    }

    gl_Position = projectionMatrix * modelViewMatrixTemp * vec4(position, 1.0);
    outTexCoord = vec2(x, y);
}