#version 330

in vec2 outTexCoord;

out vec4 fragColour;

struct Material {
    vec4 colour;
    int useTexture;
};

uniform Material material;
uniform sampler2D textureSampler;
uniform int useTexture;
uniform vec4 colour;

void main() {
    if (material.useTexture > 0) {
        fragColour = texture(textureSampler, outTexCoord);
    } else {
        fragColour = material.colour;
    }
}