package org.example;

import org.lwjgl.fmod.FMOD;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.*;

public class Main {
    public static void main(String[] args) {

        Displaymanager.createDisplay();

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        while (!Displaymanager.isCloseRequested()){

            // Set the clear color
            glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

            Displaymanager.updateDisplay();
        }

        Displaymanager.destroyDisplay();
    }
}

