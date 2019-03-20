package com.company.engine.utils;

import com.company.engine.graph.rendering.ShaderProgram;

public class ShaderUtils {

    /**
    Create 1 or more uniform(s)
     */
    public static void createShaderUniforms(ShaderProgram shaderProgram, String[] uniformNames) throws Exception {
        if (shaderProgram != null && uniformNames != null && uniformNames.length > 0) {
            for (String uniformName : uniformNames) {
                shaderProgram.createUniform(uniformName);
            }
        }
    }

    /**
    Create 1 or more Material uniform(s)
     */
    public static void createMaterialUniforms(ShaderProgram shaderProgram, String[] uniformNames) throws Exception {
        if (shaderProgram != null && uniformNames != null && uniformNames.length > 0) {
            for (String uniformName : uniformNames) {
                shaderProgram.createMaterialUniform(uniformName);
            }
        }
    }
}