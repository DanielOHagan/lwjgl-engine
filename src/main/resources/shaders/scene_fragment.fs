#version 330

in vec2 outTexCoord;

out vec4 fragColour;

uniform sampler2D textureSampler;
uniform int useTexture;
uniform vec4 colour;

void main() {
    if (useTexture > 0) {
        fragColour = texture(textureSampler, outTexCoord);
    } else {
        fragColour = colour;
    }
}