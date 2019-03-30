#version 330

const int MAX_WEIGHTS = 4;
const int MAX_JOINTS = 150;

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textCoord;
layout (location = 2) in vec3 vertexNormal;
layout (location = 3) in vec4 jointWeights;
layout (location = 4) in ivec4 jointIndices;
layout (location = 5) in mat4 instancedModelViewMatrix;
layout (location = 9) in mat4 instancedModelLightViewMatrix;

uniform int isInstanced;
uniform mat4 nonInstancedModelLightViewMatrix;
uniform mat4 orthoProjectionMatrix;
uniform mat4 jointsMatrix[MAX_JOINTS];

void main() {
    vec4 tempPosition = vec4(0, 0, 0, 0);
    mat4 modelLightViewMatrix;

    if (isInstanced > 0) {
        modelLightViewMatrix = instancedModelLightViewMatrix;

        int count = 0;

        //TODO: animation support

        if (count == 0) {
            tempPosition - vec4(position, 1.0);
        }
    } else {
        modelLightViewMatrix = nonInstancedModelLightViewMatrix;
        tempPosition = vec4(position, 1.0);
    }

    gl_Position = orthoProjectionMatrix * modelLightViewMatrix * tempPosition;
}