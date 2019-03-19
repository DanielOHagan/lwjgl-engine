#version 330

in vec2 outTexCoord;

out vec4 fragColour;

struct Attenuation {
    float constant;
    float linear;
    float exponent;
};

struct DirectionalLight {
    vec3 colour;
    vec3 direction;
    float intensity;
};

struct Material {
    vec4 colour;
    int useTexture;

    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    float reflectance;
};

struct PointLight {
    /**
    position is assumed to be in model view space
    */
    vec3 colour;
    vec3 position;
    float intensity;
    Attenuation attenuation;
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