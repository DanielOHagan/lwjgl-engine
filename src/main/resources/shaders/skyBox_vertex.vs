#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textCoord;

out vec2 outTextCoord;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;

void main() {
    gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0);
    outTextCoord = textCoord;
}