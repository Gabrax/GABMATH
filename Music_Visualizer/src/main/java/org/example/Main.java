package org.example;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.example.GabKeys.GAB_KEY_F;
import static org.example.GabKeys.GAB_KEY_H;
import static org.lwjgl.opengl.GL11.*;

public class Main {
    public static void main(String[] args) {

        Window.Init((int) (1920 * 0.75f), (int) (1920 * 0.75));

        Camera camera = new Camera();

        // Create multiple cubes at different positions
        Cube[] cubes = {
                new Cube(new Vector3f(-4.0f, 0.0f, 0.0f)),
                new Cube(new Vector3f(-3.0f, 0.0f, 0.0f)),
                new Cube(new Vector3f(-2.0f, 0.0f, 0.0f)),
                new Cube(new Vector3f(-1.0f, 0.0f, 0.0f)),
                new Cube(new Vector3f(0.0f, 0.0f, 0.0f))
        };

        while (!Window.isCloseRequested()) {

            camera.move();
            //System.out.println(camera.getPosition().toString());

            // Get the view matrix from the camera
            Matrix4f viewMatrix = camera.getViewMatrix();

            // Set up a simple projection matrix (adjust FOV, aspect ratio, near and far planes as needed)
            Matrix4f projectionMatrix = new Matrix4f().perspective(
                    (float) Math.toRadians(45.0), // Field of view
                    (float) Window._currentWidth / Window._currentHeight, // Aspect ratio
                    0.01f, // Near plane
                    100.0f // Far plane
            );

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            glEnable(GL_DEPTH_TEST);

            // Render each cube
            for (Cube cube : cubes) {
                cube.render(viewMatrix, projectionMatrix);
            }

            if (Input.keyPressed(GAB_KEY_F)) {
                Window.ToggleFullscreen();
            }
            if (Input.keyPressed(GAB_KEY_H)) {
                Window.ToggleWireframe();
            }

            Window.updateDisplay();
        }

        // Cleanup
        for (Cube cube : cubes) {
            cube.cleanup();
        }

        Window.destroyDisplay();
    }
}
