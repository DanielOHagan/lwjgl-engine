#version 330

const int SHADER_TRUE = 1;
const int SHADER_FALSE = 0;

in vec2 outTextCoord;
in vec3 vertexPosition;

out vec4 fragColour;

uniform sampler2D textureSampler;
uniform vec4 colour;
uniform int useTexture;

void main() {
    if (useTexture == SHADER_TRUE) {
        fragColour = texture(textureSampler, outTextCoord);
    } else {
        fragColour = colour;
    }
}