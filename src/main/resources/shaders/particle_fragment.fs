#version 330

in vec2 outTexCoord;
in vec3 mVectorPos;

out vec4 fragColour;

uniform sampler2D textureSampler;

uniform int useTexture;
uniform vec4 nonInstancedParticleColour;

void main() {
    if (useTexture > 0) {
        fragColour = texture(textureSampler, outTexCoord);
    } else {
        fragColour = nonInstancedParticleColour;
    }
}