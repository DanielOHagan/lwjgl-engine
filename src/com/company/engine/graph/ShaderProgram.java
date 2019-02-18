package com.company.engine.graph;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {

    private final int mProgramId;
    private final Map<String, Integer> mUniforms;

    private int mVertexShaderId;
    private int mFragmentShaderId;

    public ShaderProgram() throws Exception {
        mProgramId = glCreateProgram();
        if (mProgramId == 0) {
            throw new Exception("Could not create shader program");
        }
        mUniforms = new HashMap<>();
    }

    private int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = glCreateShader(shaderType);

        if (shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(mProgramId, shaderId);

        return shaderId;
    }

    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = glGetUniformLocation(mProgramId, uniformName);

        if (uniformLocation < 0) {
            throw new Exception("Could not find uniform: " + uniformName);
        }
        mUniforms.put(uniformName, uniformLocation);
    }

    public void createVertexShader(String shaderCode) throws Exception {
        mVertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        mFragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    public void setUniform(String uniformName, Matrix4f value) {
        //dump the matrix into a float buffer
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer floatBuffer = stack.mallocFloat(16);
            value.get(floatBuffer);
            glUniformMatrix4fv(mUniforms.get(uniformName), false, floatBuffer);
        }
    }

    public void setUniform(String uniformName, Vector4f value) {
        glUniform4f(mUniforms.get(uniformName), value.x, value.y, value.z, value.w);
    }

    public void setUniform(String uniformName, int value) {
        glUniform1i(mUniforms.get(uniformName), value);
    }

    public void link() throws Exception {

        //link the program
        glLinkProgram(mProgramId);

        if (glGetProgrami(mProgramId, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking shader code: " + glGetProgramInfoLog(mProgramId, 1024));
        }

        //detach the shaders after the program has been linked
        if (mVertexShaderId != 0) {
            glDetachShader(mProgramId, mVertexShaderId);
        }
        if (mFragmentShaderId != 0) {
            glDetachShader(mProgramId, mFragmentShaderId);
        }

        glValidateProgram(mProgramId);
        if (glGetProgrami(mProgramId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating shader code: " + glGetProgramInfoLog(mProgramId, 1024));
        }
    }

    //the "activation" method
    public void bind() {
        glUseProgram(mProgramId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanUp() {
        unbind();
        if (mProgramId != 0) {
            glDeleteProgram(mProgramId);
        }
    }
}
