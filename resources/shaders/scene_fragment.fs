#version 330

in vec2 outTexCoord;

out vec4 fragColour;

uniform sampler2D textureSampler;

void main() {
    //fragColour = texture(textureSampler, outTexCoord);
    fragColour = vec4(1, 0, 0, 1);
}