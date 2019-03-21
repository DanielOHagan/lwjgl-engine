package com.company.engine.graph.rendering;

import com.company.engine.graph.lighting.Attenuation;
import com.company.engine.graph.lighting.DirectionalLight;
import com.company.engine.graph.lighting.PointLight;
import com.company.engine.graph.lighting.SpotLight;
import com.company.engine.graph.material.Material;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {

    public static final int POSITION_VBO_INDEX = 0;
    public static final int TEXTURE_COORDINATES_VBO_INDEX = 1;
    public static final int NORMALS_VBO_INDEX = 2;
    public static final int WEIGHTS_VBO_INDEX = 3;
    public static final int JOINT_INDICES_VBO_INDEX = 4;

    public static final int SHADER_FALSE = 0;
    public static final int SHADER_TRUE = 1;

    private final int mProgramId;
    private final Map<String, Integer> mUniforms;

    private int mVertexShaderId;
    private int mFragmentShaderId;
    private boolean mBound;

    public ShaderProgram() throws Exception {
        mProgramId = glCreateProgram();
        if (mProgramId == 0) {
            throw new Exception("Could not create shader program");
        }
        mUniforms = new HashMap<>();
        mBound = false;
    }

    private int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = glCreateShader(shaderType);

        if (shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception(
                    "Error compiling shader code: " +
                            glGetShaderInfoLog(shaderId, 1024)
            );
        }

        glAttachShader(mProgramId, shaderId);

        return shaderId;
    }

    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = glGetUniformLocation(mProgramId, uniformName);

        if (uniformLocation < 0) {
            throw new Exception("Could not find uniform in compiled shader: " + uniformName);
        }
        mUniforms.put(uniformName, uniformLocation);
    }

    public void createUniformArray(String uniformName, int size) throws Exception {
        for (int i = 0; i < size; i++) {
            createUniform(uniformName + "[" + i + "]");
        }
    }

    public void createMaterialUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".colour");
        createUniform(uniformName + ".useTexture");
        createUniform(uniformName + ".diffuse");
        createUniform(uniformName + ".specular");
        createUniform(uniformName + ".hasNormalMap");
        createUniform(uniformName + ".reflectance");
    }

    public void createDirectionalLightUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".colour");
        createUniform(uniformName + ".direction");
        createUniform(uniformName + ".intensity");
    }

    public void createPointLightArrayUniform(String uniformName, int size) throws Exception {
        for (int i = 0; i < size; i++) {
            createPointLightUniform(uniformName + "[" + i + "]");
        }
    }

    public void createPointLightUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".colour");
        createUniform(uniformName + ".position");
        createUniform(uniformName + ".intensity");
        createUniform(uniformName + ".isActive");

        createAttenuationUniform(uniformName + ".attenuation");
    }

    public void createAttenuationUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".constant");
        createUniform(uniformName + ".linear");
        createUniform(uniformName + ".exponent");
    }

    public void createSpotLightArrayUniform(String uniformName, int size) throws Exception {
        for (int i = 0; i < size; i++) {
            createSpotLightUniform(uniformName + "[" + i + "]");
        }
    }

    public void createSpotLightUniform(String uniformName) throws Exception {
        createPointLightUniform(uniformName + ".pointLight");
        createUniform(uniformName + ".coneDir");
        createUniform(uniformName + ".cutOff");
    }

    public void createVertexShader(String shaderCode) throws Exception {
        mVertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        mFragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    public void setUniform(String uniformName, Matrix4f value) {
        if (!mBound) {
            throw new IllegalStateException("Can not create uniform when program is not bound");
        }

        //dump the matrix into a float buffer
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer floatBuffer = stack.mallocFloat(16);
            value.get(floatBuffer);
            glUniformMatrix4fv(mUniforms.get(uniformName), false, floatBuffer);
        }
    }

    public void setUniform(String uniformName, Vector4f value) {
        if (!mBound) {
            throw new IllegalStateException("Can not create uniform when program is not bound");
        }

        glUniform4f(
                mUniforms.get(uniformName),
                value.x,
                value.y,
                value.z,
                value.w
        );
    }

    public void setUniform(String uniformName, Material material) {
        setUniform(uniformName + ".useTexture", material.isUsingTexture());
        setUniform(uniformName + ".colour", material.getColour());
        setUniform(uniformName + ".diffuse", material.getDiffuseColour());
        setUniform(uniformName + ".specular", material.getSpecularColour());
        setUniform(uniformName + ".reflectance", material.getReflectance());
        setUniform(uniformName + ".hasNormalMap", material.hasNormalMap());
    }

    public void setUniform(String uniformName, PointLight[] pointLightArray) {
        int length = pointLightArray != null ? pointLightArray.length : 0;

        for (int i = 0; i < length; i++) {
            setUniform(uniformName, pointLightArray[i]);
        }
    }

    public void setUniform(String uniformName, PointLight pointLight) {
        setUniform(uniformName + ".colour", pointLight.getColour());
        setUniform(uniformName + ".position", pointLight.getPosition());
        setUniform(uniformName + ".intensity", pointLight.getIntensity());
        setUniform(uniformName + ".isActive", pointLight.isActive());

        setUniform(uniformName + ".attenuation", pointLight.getAttenuation());
    }

    public void setUniform(String uniformName, SpotLight[] spotLightArray) {
        int length = spotLightArray != null ? spotLightArray.length : 0;

        for (int i = 0; i < length; i++) {
            setUniform(uniformName, spotLightArray[i]);
        }
    }

    public void setUniform(String uniformName, SpotLight spotLight) {
        setUniform(uniformName + ".pointLight", spotLight.getPointLight());
        setUniform(uniformName + ".coneDir", spotLight.getConeDirection());
        setUniform(uniformName + ".cutOff", spotLight.getCutOff());
    }

    public void setUniform(String uniformName, SpotLight spotLight, int index) {
        setUniform(uniformName + "[" + index + "]", spotLight);
    }

    public void setUniform(String uniformName, PointLight pointLight, int index) {
        setUniform(uniformName + "[" + index + "]", pointLight);
    }

    public void setUniform(String uniformName, DirectionalLight dirLight) {
        setUniform(uniformName + ".colour", dirLight.getColour());
        setUniform(uniformName + ".direction", dirLight.getDirection());
        setUniform(uniformName + ".intensity", dirLight.getIntensity());
    }

    public void setUniform(String uniformName, Attenuation attenuation) {
        setUniform(uniformName + ".constant", attenuation.getConstant());
        setUniform(uniformName + ".linear", attenuation.getLinear());
        setUniform(uniformName + ".exponent", attenuation.getExponent());

    }

    public void setUniform(String uniformName, Vector3f vector3f) {
        if (!mBound) {
            throw new IllegalStateException("Can not create uniform when program is not bound");
        }

        glUniform3f(
                mUniforms.get(uniformName),
                vector3f.x,
                vector3f.y,
                vector3f.z
        );
    }

    public void setUniform(String uniformName, float value) {
        if (!mBound) {
            throw new IllegalStateException("Can not create uniform when program is not bound");
        }

        glUniform1f(mUniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, int value) {
        if (!mBound) {
            throw new IllegalStateException("Can not create uniform when program is not bound");
        }

        glUniform1i(mUniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, boolean value) {
        if (!mBound) {
            throw new IllegalStateException("Can not create uniform when program is not bound");
        }

        glUniform1i(mUniforms.get(uniformName), value ? SHADER_TRUE : SHADER_FALSE);
    }

    public void link() throws Exception {

        //link the program
        glLinkProgram(mProgramId);

        if (glGetProgrami(mProgramId, GL_LINK_STATUS) == 0) {
            throw new Exception(
                    "Error linking shader code: " +
                            glGetProgramInfoLog(mProgramId, 1024)
            );
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
            System.err.println(
                    "Warning validating shader code: " +
                            glGetProgramInfoLog(mProgramId, 1024)
            );
        }
    }

    //the "activation" method
    public void bind() {
        glUseProgram(mProgramId);
        mBound = true;
    }

    public void unbind() {
        glUseProgram(0);
        mBound = false;
    }

    public void cleanUp() {
        unbind();
        if (mProgramId != 0) {
            glDeleteProgram(mProgramId);
        }
    }
}