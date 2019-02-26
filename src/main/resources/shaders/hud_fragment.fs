#version 330

in vec2 outTextCoord;

out vec4 fragColour;

uniform sampler2D textureSampler;
uniform vec4 colour;

void main() {
    fragColour = colour * texture(textureSampler, outTextCoord);
}