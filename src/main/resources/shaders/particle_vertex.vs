#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;
layout (location = 2) in vec3 vertexNormal;
layout (location = 5) in mat4 modelViewMatrix;
layout (location = 13) in vec2 textOffset;
layout (location = 14) in vec4 colour;

out vec2 outTexCoord;
out vec4 outColour;

uniform mat4 projectionMatrix;
uniform int numColumns;
uniform int numRows;

void main() {
    gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0);

    //for texture atlas
    float x = (texCoord.x / numColumns + textOffset.x);
    float y = (texCoord.y / numRows + textOffset.y);

    outTexCoord = vec2(x, y);
    outColour = colour;
}