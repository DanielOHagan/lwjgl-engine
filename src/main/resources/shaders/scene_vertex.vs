#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;
layout (location = 2) in vec3 vertexNormal;
layout (location = 3) in vec4 jointWeights;
layout (location = 4) in ivec4 jointIndices;
layout (location = 5) in mat4 instancedModelViewMatrix;
//layout (location = 9) in mat4 instancedModelLightViewMatrix;

//uniform mat4 modelViewMatrix;

uniform int isInstanced;
uniform mat4 nonInstancedModelViewMatrix;
uniform mat4 projectionMatrix;

out vec2 outTexCoord;

void main() {

    vec4 positionTemp;
    vec4 normalTemp;
    mat4 modelViewMatrix;
    mat4 modelLightViewMatrix;

    if (isInstanced > 0) {
        modelViewMatrix = instancedModelViewMatrix;
        //modelLightViewMatrix = instancedModelLightViewMatrix;

        positionTemp = vec4(position, 1.0);
        normalTemp = vec4(vertexNormal, 0.0);
    } else {
        modelViewMatrix = nonInstancedModelViewMatrix;
        //lightViewMatrix = nonInstancedModelLightViewMatrix;

        int count = 0;


        if (count == 0) {
            positionTemp = vec4(position, 1.0);
            normalTemp = vec4(vertexNormal, 0.0);
        }
    }

    vec4 vertexPosition = modelViewMatrix * positionTemp;

    outTexCoord = texCoord;
    gl_Position = projectionMatrix * vertexPosition;
}