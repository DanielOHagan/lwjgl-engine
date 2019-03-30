#version 330

const int MAX_WEIGHTS = 4;
const int MAX_JOINTS = 150;

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;
layout (location = 2) in vec3 vertexNormal;
layout (location = 3) in vec4 jointWeights;
layout (location = 4) in ivec4 jointIndices;
layout (location = 5) in mat4 instancedModelViewMatrix;
layout (location = 9) in vec2 textOffset;

uniform int isInstanced;

uniform mat4 nonInstancedModelViewMatrix;
uniform mat4 projectionMatrix;
//uniform mat4 orthoProjectionMatrix;
//uniform mat4 lightViewMatrix;

uniform int numColumns;
uniform int numRows;

out vec2 out_texCoord;
out vec3 out_modelViewVertexNormal;
out vec3 out_modelViewVertexPosition;
out mat4 out_modelViewMatrix;
//out vec4 out_modelLightViewVertexPosition;

void main() {

    vec4 positionTemp;
    vec4 normalTemp;
    mat4 modelViewMatrix;
//    mat4 modelLightViewMatrix;

    if (isInstanced > 0) {
        modelViewMatrix = instancedModelViewMatrix;
//        modelLightViewMatrix = instancedModelLightViewMatrix;

        positionTemp = vec4(position, 1.0);
        normalTemp = vec4(vertexNormal, 0.0);
    } else {
        modelViewMatrix = nonInstancedModelViewMatrix;
//        lightViewMatrix = nonInstancedModelLightViewMatrix;

        int count = 0;
        //cycle through joints and apply matrix transformations
        //TODO: animate joints
        if (count == 0) {
            positionTemp = vec4(position, 1.0);
            normalTemp = vec4(vertexNormal, 0.0);
        }
    }

    vec4 vertexPosition = modelViewMatrix * positionTemp;

    out_texCoord = texCoord;
    out_modelViewMatrix = modelViewMatrix;
    out_modelViewVertexNormal = normalize(modelViewMatrix * normalTemp).xyz;
    out_modelViewVertexPosition = vertexPosition.xyz;
//    out_modelLightViewVertexPosition = orthoProjectionMatrix * lightViewMatrix * positionTemp;

    gl_Position = projectionMatrix * vertexPosition;
}