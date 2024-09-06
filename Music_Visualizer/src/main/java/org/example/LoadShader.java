package org.example;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.joml.Matrix2f;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LoadShader {
    private final int ID;

    // Constructor that reads and compiles vertex, fragment, and optionally geometry shaders
    public LoadShader(String vertexPath, String fragmentPath, String geometryPath) {
        String vertexCode = readShaderFile(vertexPath);
        String fragmentCode = readShaderFile(fragmentPath);
        String geometryCode = geometryPath != null ? readShaderFile(geometryPath) : "";

        // Compile shaders
        int vertex = compileShader(vertexCode, GL20.GL_VERTEX_SHADER);
        int fragment = compileShader(fragmentCode, GL20.GL_FRAGMENT_SHADER);
        int geometry = geometryPath != null ? compileShader(geometryCode, GL32.GL_GEOMETRY_SHADER) : 0;

        // Link shaders into a program
        ID = GL20.glCreateProgram();
        GL20.glAttachShader(ID, vertex);
        GL20.glAttachShader(ID, fragment);
        if (geometryPath != null) {
            GL20.glAttachShader(ID, geometry);
        }
        GL20.glLinkProgram(ID);
        checkCompileErrors(ID, "PROGRAM");

        // Delete shaders after linking
        GL20.glDeleteShader(vertex);
        GL20.glDeleteShader(fragment);
        if (geometryPath != null) {
            GL20.glDeleteShader(geometry);
        }
    }

    // Overloaded constructor without geometry shader
    public LoadShader(String vertexPath, String fragmentPath) {
        this(vertexPath, fragmentPath, null);
    }

    // Reads shader file from the classpath
    private String readShaderFile(String filePath) {
        StringBuilder code = new StringBuilder();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filePath)) {
            assert is != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    code.append(line).append("\n");
                }
            }
            System.out.println("ShaderLoaded");
        } catch (IOException e) {
            throw new RuntimeException("Error reading shader file: " + filePath, e);
        }
        return code.toString();
    }

    // Activates the shader program
    public void use() {
        GL20.glUseProgram(ID);
    }

    // Set uniform methods for various data types
    public void setBool(String name, boolean value) {
        GL20.glUniform1i(GL20.glGetUniformLocation(ID, name), value ? 1 : 0);
    }

    public void setInt(String name, int value) {
        GL20.glUniform1i(GL20.glGetUniformLocation(ID, name), value);
    }

    public void setFloat(String name, float value) {
        GL20.glUniform1f(GL20.glGetUniformLocation(ID, name), value);
    }

    public void setVec2(String name, Vector2f value) {
        GL20.glUniform2f(GL20.glGetUniformLocation(ID, name), value.x, value.y);
    }

    public void setVec3(String name, Vector3f value) {
        GL20.glUniform3f(GL20.glGetUniformLocation(ID, name), value.x, value.y, value.z);
    }

    public void setVec4(String name, Vector4f value) {
        GL20.glUniform4f(GL20.glGetUniformLocation(ID, name), value.x, value.y, value.z, value.w);
    }

    public void setMat2(String name, Matrix2f mat) {
        float[] matArray = new float[4];
        mat.get(matArray);
        GL20.glUniformMatrix2fv(GL20.glGetUniformLocation(ID, name), false, matArray);
    }

    public void setMat3(String name, Matrix3f mat) {
        float[] matArray = new float[9];
        mat.get(matArray);
        GL20.glUniformMatrix3fv(GL20.glGetUniformLocation(ID, name), false, matArray);
    }

    public void setMat4(String name, Matrix4f mat) {
        float[] matArray = new float[16];
        mat.get(matArray);
        GL20.glUniformMatrix4fv(GL20.glGetUniformLocation(ID, name), false, matArray);
    }

    // Compiles a shader and checks for errors
    private int compileShader(String shaderCode, int type) {
        int shader = GL20.glCreateShader(type);
        GL20.glShaderSource(shader, shaderCode);
        GL20.glCompileShader(shader);
        checkCompileErrors(shader, type == GL20.GL_VERTEX_SHADER ? "VERTEX" :
                type == GL20.GL_FRAGMENT_SHADER ? "FRAGMENT" : "GEOMETRY");
        return shader;
    }

    // Checks for compilation and linking errors
    private void checkCompileErrors(int shader, String type) {
        int success;
        String infoLog;
        if (!type.equals("PROGRAM")) {
            success = GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS);
            if (success == GL20.GL_FALSE) {
                infoLog = GL20.glGetShaderInfoLog(shader);
                System.err.println("ERROR::SHADER_COMPILATION_ERROR of type: " + type + "\n" + infoLog);
            }
        } else {
            success = GL20.glGetProgrami(shader, GL20.GL_LINK_STATUS);
            if (success == GL20.GL_FALSE) {
                infoLog = GL20.glGetProgramInfoLog(shader);
                System.err.println("ERROR::PROGRAM_LINKING_ERROR of type: " + type + "\n" + infoLog);
            }
        }
    }
}
