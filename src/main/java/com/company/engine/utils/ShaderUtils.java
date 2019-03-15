package com.company.engine.utils;

import com.company.engine.graph.ShaderProgram;

public class ShaderUtils {

    /**
    Create 1 or more uniform(s)
     */
    public static void setUpShaderUniforms(ShaderProgram shaderProgram, String[] uniformNames) throws Exception {
        if (shaderProgram != null && uniformNames != null && uniformNames.length > 0) {
            for (String uniformName : uniformNames) {
                shaderProgram.createUniform(uniformName);
            }
        }
    }

    /**
    Create 1 or more Material uniform(s)
     */
    public static void setUpMaterialUniforms(ShaderProgram shaderProgram, String[] uniformNames) throws Exception {
        if (shaderProgram != null && uniformNames != null && uniformNames.length > 0) {
            for (String uniformName : uniformNames) {
                shaderProgram.createMaterialUniform(uniformName);
            }
        }
    }
}
