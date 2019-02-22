#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;
layout (location = 2) in vec3 vertexNormal;

out vec2 outTexCoord;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;
uniform int numColumns;
uniform int numRows;
uniform float textOffsetX;
uniform float textOffsetY;

void main() {
    gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0);

    //for texture atlas
    float x = (texCoord.x / numColumns + textOffsetX);
    float y = (texCoord.y / numRows + textOffsetY);

    outTexCoord = vec2(x, y);
}