package org.example;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Cube {
    private int vaoID;
    private int vboID;
    private LoadShader shader;

    private final float[] vertices = {
            // Positions        // Colors
            -0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, // Bottom-left
            0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, // Bottom-right
            0.5f,  0.5f, 0.0f, 0.0f, 0.0f, 1.0f, // Top-right
            0.5f,  0.5f, 0.0f, 0.0f, 0.0f, 1.0f, // Top-right
            -0.5f,  0.5f, 0.0f, 1.0f, 1.0f, 0.0f, // Top-left
            -0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 0.0f  // Bottom-left
    };

    public Cube() {
        setupShaders();
        setup();
    }

        private void setupShaders() {
        String vertexShaderPath = "cubeV.glsl";
        String fragmentShaderPath = "cubeF.glsl";
        shader = new LoadShader(vertexShaderPath, fragmentShaderPath);
    }

    private void setup() {
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(vertices.length);
        vertexBuffer.put(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Position attribute
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // Color attribute
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glBindVertexArray(0);
        MemoryUtil.memFree(vertexBuffer);
    }

    public void render() {
        shader.use();
        glBindVertexArray(vaoID);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDeleteBuffers(vboID);
        glDeleteVertexArrays(vaoID);
    }
}
