#version 330

layout (location = 0) vec3 position;
layout (location = 1) vec2 texCoord;
/*
uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;
*/
out outTexCoord;

void main() {
    outTexCoord = texCoord;
    gl_Position = /*projectionMatrix * modelViewMatrix **/ vec4(position, 1.0);
}