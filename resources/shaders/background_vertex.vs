#verion 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;
layout (location = 2) in vec3 vertexNormal;

uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;

out vec2 outTexCoord;

void main() {
    outTexCoord = texCoord;
    gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0);
}