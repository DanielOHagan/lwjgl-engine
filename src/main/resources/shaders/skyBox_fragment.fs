#version 330

in vec2 outTextCoord;
in vec3 vertexPosition;

out vec4 fragColour;

uniform sampler2D textureSampler;
uniform vec4 colour;
uniform int useTexture;

void main() {
    if (useTexture > 0) {
        fragColour = texture(textureSampler, outTextCoord);
    } else {
        fragColour = colour;
    }
}