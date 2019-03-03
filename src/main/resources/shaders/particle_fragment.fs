#version 330

in vec2 outTexCoord;
in vec3 mVectorPos;
in vec4 outColour;

out vec4 fragColour;

uniform sampler2D textureSampler;
uniform int useTexture;

void main() {
    if (useTexture > 0) {
        fragColour = texture(textureSampler, outTexCoord);
    } else {
        fragColour = outColour;
    }
}