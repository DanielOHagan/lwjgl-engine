#version 330

const int MAX_POINT_LIGHTS = 5;
const int MAX_SPOT_LIGHTS = 5;

const int SHADER_FALSE = 0;
const int SHADER_TRUE = 1;

in vec2 out_texCoord;
in vec3 out_modelViewVertexNormal;
in vec3 out_modelViewVertexPosition;
in mat4 out_modelViewMatrix;
//in vec4 out_modelLightViewVertexPosition;

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
    int hasNormalMap;

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
    int isActive;
};

struct SpotLight {
    PointLight pointLight;
    vec3 coneDir;
    float cutOff;
};

uniform Material material;
uniform sampler2D textureSampler;
uniform sampler2D normalMap;
uniform sampler2D shadowMap;

uniform int isRenderingShadows;

uniform PointLight pointLightArray[MAX_POINT_LIGHTS];
uniform SpotLight spotLightArray[MAX_SPOT_LIGHTS];
uniform DirectionalLight directionalLight;

uniform vec3 ambientLight;
uniform float specularPower;

vec4 ambientComponent;
vec4 diffuseComponent;
vec4 specularComponent;


void configureColours(Material material, vec2 textCoord) {
    if (material.useTexture == SHADER_TRUE) {
        ambientComponent = texture(textureSampler, textCoord);
        diffuseComponent = ambientComponent;
        specularComponent = ambientComponent;
    } else {
        ambientComponent = material.colour;
        diffuseComponent = material.diffuse;
        specularComponent = material.specular;
    }
}

vec4 calculateLightColour(
    vec3 lightColour,
    float lightIntensity,
    vec3 position,
    vec3 lightSourceDirection,
    vec3 normal
) {
    vec4 diffuseColour = vec4(0, 0, 0, 0);
    vec4 specularColour = vec4(0, 0, 0, 0);

    //diffuse light
    float diffuseFactor = max(dot(normal, lightSourceDirection), 0.0);
    diffuseColour = diffuseComponent * vec4(lightColour, 1.0) * lightIntensity * diffuseFactor;

    //specular light
    vec3 camDirection = normalize(-position);
    vec3 fromLightSourceDirection = -lightSourceDirection;
    vec3 reflectedLight = normalize(reflect(fromLightSourceDirection, normal));
    float specularFactor = max(dot(camDirection, reflectedLight), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    specularColour = specularComponent * lightIntensity *
        specularFactor * material.reflectance * vec4(lightColour, 1.0);

    return (diffuseColour + specularColour);
}

vec4 calculatePointLight(PointLight pointLight, vec3 position, vec3 normal) {
    vec3 lightDirection = pointLight.position - position;
    vec3 lightSourceDirection = normalize(lightDirection);
    vec4 lightColour = calculateLightColour(
        pointLight.colour,
        pointLight.intensity,
        position,
        lightSourceDirection,
        normal
    );

    //attenuation
    float lightRayLength = length(lightDirection);
    float attenuationInv = pointLight.attenuation.constant + pointLight.attenuation.linear *
        lightRayLength + pointLight.attenuation.exponent * lightRayLength * lightRayLength;

    return lightColour / attenuationInv;
}

vec4 calculateSpotLight(SpotLight spotLight, vec3 position, vec3 normal) {
    vec3 lightDirection = spotLight.pointLight.position - position;
    vec3 lightSourceDirection = normalize(lightDirection);
    vec3 lightSourceDirectionInverse = -lightSourceDirection;
    float spotAngle = dot(lightSourceDirectionInverse, normalize(spotLight.coneDir));

    vec4 colour = vec4(0, 0, 0, 0);

    if (spotAngle > spotLight.cutOff) {
        colour = calculatePointLight(spotLight.pointLight, position, normal);

        //the closer to the cutOff the less intense the light
        colour *= (1.0 - (1.0 - spotAngle) / (1.0 - spotLight.cutOff));
    }

    return colour;
}

vec4 calculateDirectionalLight(
    DirectionalLight dirLight,
    vec3 position,
    vec3 normal
) {
    return calculateLightColour(
        dirLight.colour,
        dirLight.intensity,
        position,
        normalize(dirLight.direction),
        normal
    );
}

vec3 calculateNormal(
    Material material,
    vec3 normal,
    vec2 textCoord,
    mat4 modelViewMatrix
) {
    vec3 newNormal = normal;

    if (material.hasNormalMap == SHADER_TRUE) {
        newNormal = texture(normalMap, textCoord).rgb;
        newNormal = normalize(newNormal * 2 - 1);
        newNormal = normalize(modelViewMatrix * vec4(newNormal, 0.0)).xyz;
    }

    return newNormal;
}

float calculateShadow(vec4 position) {
    if (isRenderingShadows == SHADER_FALSE) {
        return 1.0;
    }

    vec3 projectionCoords = position.xyz;
    //transform from screen coords to texture coords
    projectionCoords = projectionCoords * 0.5 + 0.5;
    float bias = 0.05;
    float shadowFactor = 0.0;
    vec2 inc = 1.0 / textureSize(shadowMap, 0);

    for (int row = -1; row <= 1; ++row) {
        for (int column = -1; column <= 1; ++column) {
            float textureDepth =
                texture(shadowMap, projectionCoords.xy + vec2(row, column) * inc).r;
            shadowFactor += projectionCoords.z - bias > textureDepth ? 1.0 : 0.0;
        }
    }

    shadowFactor /= 9.0;

    if (projectionCoords.z > 1.0) {
        shadowFactor = 1.0;
    }

    return 1 - shadowFactor;
}

void main() {
    configureColours(material, out_texCoord);

    vec3 currentNormal = calculateNormal(
        material,
        out_modelViewVertexNormal,
        out_texCoord,
        out_modelViewMatrix
    );

    vec4 diffuseSpecularComponent = calculateDirectionalLight(
        directionalLight,
        out_modelViewVertexPosition,
        currentNormal
    );

    //point lights
    for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
        if (
            pointLightArray[i].isActive > 0 &&
            pointLightArray[i].intensity > 0
        ) {
            diffuseSpecularComponent += calculatePointLight(
                pointLightArray[i],
                out_modelViewVertexPosition,
                currentNormal
            );
        }
    }

    //spot lights
    for (int i = 0; i < MAX_SPOT_LIGHTS; i++) {
        if (
            spotLightArray[i].pointLight.isActive > 0 &&
            spotLightArray[i].pointLight.intensity > 0
        ) {
            diffuseSpecularComponent += calculateSpotLight(
                spotLightArray[i],
                out_modelViewVertexPosition,
                currentNormal
            );
        }
    }

//    float shadow = calculateShadow(out_modelLightViewVertexPosition);

    ambientComponent *= vec4(ambientLight, 1.0);

    fragColour = clamp(ambientComponent +
        diffuseSpecularComponent /* *shadow*/, 0, 1);
}