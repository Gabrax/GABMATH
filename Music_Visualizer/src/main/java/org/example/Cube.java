package org.example;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
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
    private Vector3f position;

    private final float[] vertices = {
            // Positions          // Colors
            -0.25f, -0.25f, -0.25f,   1.0f, 0.0f, 0.0f,  // Red
            0.25f, -0.25f, -0.25f,   0.0f, 1.0f, 0.0f,  // Green
            0.25f,  0.25f, -0.25f,   0.0f, 0.0f, 1.0f,  // Blue
            0.25f,  0.25f, -0.25f,   0.0f, 0.0f, 1.0f,  // Blue
            -0.25f,  0.25f, -0.25f,   1.0f, 1.0f, 0.0f,  // Yellow
            -0.25f, -0.25f, -0.25f,   1.0f, 0.0f, 0.0f,  // Red

            -0.25f, -0.25f,  0.25f,   1.0f, 0.0f, 0.0f,  // Red
            0.25f, -0.25f,  0.25f,   0.0f, 1.0f, 0.0f,  // Green
            0.25f,  0.25f,  0.25f,   0.0f, 0.0f, 1.0f,  // Blue
            0.25f,  0.25f,  0.25f,   0.0f, 0.0f, 1.0f,  // Blue
            -0.25f,  0.25f,  0.25f,   1.0f, 1.0f, 0.0f,  // Yellow
            -0.25f, -0.25f,  0.25f,   1.0f, 0.0f, 0.0f,  // Red

            -0.25f,  0.25f,  0.25f,   1.0f, 0.0f, 0.0f,  // Red
            -0.25f,  0.25f, -0.25f,   1.0f, 1.0f, 0.0f,  // Yellow
            -0.25f, -0.25f, -0.25f,   1.0f, 0.0f, 0.0f,  // Red
            -0.25f, -0.25f, -0.25f,   1.0f, 0.0f, 0.0f,  // Red
            -0.25f, -0.25f,  0.25f,   1.0f, 0.0f, 0.0f,  // Red
            -0.25f,  0.25f,  0.25f,   1.0f, 0.0f, 0.0f,  // Red

            0.25f,  0.25f,  0.25f,   0.0f, 0.0f, 1.0f,  // Blue
            0.25f,  0.25f, -0.25f,   0.0f, 1.0f, 0.0f,  // Green
            0.25f, -0.25f, -0.25f,   0.0f, 1.0f, 0.0f,  // Green
            0.25f, -0.25f, -0.25f,   0.0f, 1.0f, 0.0f,  // Green
            0.25f, -0.25f,  0.25f,   0.0f, 1.0f, 0.0f,  // Green
            0.25f,  0.25f,  0.25f,   0.0f, 0.0f, 1.0f,  // Blue

            -0.25f, -0.25f, -0.25f,   1.0f, 0.0f, 0.0f,  // Red
            0.25f, -0.25f, -0.25f,   0.0f, 1.0f, 0.0f,  // Green
            0.25f, -0.25f,  0.25f,   0.0f, 1.0f, 0.0f,  // Green
            0.25f, -0.25f,  0.25f,   0.0f, 1.0f, 0.0f,  // Green
            -0.25f, -0.25f,  0.25f,   1.0f, 0.0f, 0.0f,  // Red
            -0.25f, -0.25f, -0.25f,   1.0f, 0.0f, 0.0f,  // Red

            -0.25f,  0.25f, -0.25f,   1.0f, 1.0f, 0.0f,  // Yellow
            0.25f,  0.25f, -0.25f,   0.0f, 0.0f, 1.0f,  // Blue
            0.25f,  0.25f,  0.25f,   0.0f, 0.0f, 1.0f,  // Blue
            0.25f,  0.25f,  0.25f,   0.0f, 0.0f, 1.0f,  // Blue
            -0.25f,  0.25f,  0.25f,   1.0f, 0.0f, 0.0f,  // Red
            -0.25f,  0.25f, -0.25f,   1.0f, 1.0f, 0.0f   // Yellow
    };

    // Constructor to set up the cube's VAO, VBO, shaders, and position
    public Cube(Vector3f position) {
        this.position = position;
        setupShaders();
        setup();
    }

    // Set up the shader program
    private void setupShaders() {
        shader = new LoadShader("cubeV.glsl", "cubeF.glsl");
    }

    // Set up the cube VAO and VBO
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

    // Render the cube
    public void render(Matrix4f viewMatrix, Matrix4f projectionMatrix) {
        shader.use();

        // Set the model matrix (for positioning the cube)
        Matrix4f model = new Matrix4f();
        model.translate(position); // Translate to the cube's position
        float angle = (float) GLFW.glfwGetTime(); // Time-based angle for rotation
        model.rotate((float) Math.toRadians(angle * 50), 0.1f, 1.0f, 0.0f); //

        // Set the model matrix
        shader.setMat4("model", model);

        // Set the view matrix
        shader.setMat4("view", viewMatrix);

        // Set the projection matrix
        shader.setMat4("projection", projectionMatrix);

        // Render the cube
        glBindVertexArray(vaoID);
        glDrawArrays(GL_TRIANGLES, 0, 36);
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDeleteBuffers(vboID);
        glDeleteVertexArrays(vaoID);
    }
}
