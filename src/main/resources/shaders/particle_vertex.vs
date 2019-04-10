#version 330

const int SHADER_TRUE = 1;
const int SHADER_FALSE = 0;

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;
layout (location = 2) in vec3 vertexNormal;
layout (location = 5) in mat4 instancedModelViewMatrix;
layout (location = 13) in vec2 textOffset;

out vec2 outTexCoord;

uniform int isInstanced;
uniform mat4 projectionMatrix;
uniform int textureColumnCount;
uniform int textureRowCount;

uniform mat4 nonInstancedModelViewMatrix;
uniform float nonInstancedTextOffsetX;
uniform float nonInstancedTextOffsetY;

void main() {

    float x;
    float y;
    mat4 modelViewMatrixTemp;

    if (isInstanced == SHADER_TRUE) {
        modelViewMatrixTemp = instancedModelViewMatrix;

        //for texture atlas
        x = (texCoord.x / textureColumnCount + textOffset.x);
        y = (texCoord.y / textureRowCount + textOffset.y);

    } else { //is not instanced
        modelViewMatrixTemp = nonInstancedModelViewMatrix;

        //for texture atlas
        x = (texCoord.x / textureColumnCount + nonInstancedTextOffsetX);
        y = (texCoord.y / textureRowCount + nonInstancedTextOffsetY);
    }

    gl_Position = projectionMatrix * modelViewMatrixTemp * vec4(position, 1.0);
    outTexCoord = vec2(x, y);
}