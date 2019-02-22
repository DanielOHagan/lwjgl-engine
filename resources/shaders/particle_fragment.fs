#version 330

in vec2 outTexCoord;
in vec3 mVectorPos;

out vec4 fragColour;

uniform sampler2D textureSampler;

void main() {
    fragColour = texture(textureSampler, outTexCoord);
}