package org.example;

import org.lwjgl.opengl.GL;

import static org.example.GabKeys.GAB_KEY_F;
import static org.example.GabKeys.GAB_KEY_H;
import static org.lwjgl.opengl.GL11.*;

public class Main {
    public static void main(String[] args) {

        Window.Init((int) (1920 * 0.75f), (int) (1920 * 0.75));

        GL.createCapabilities();

        while (!Window.isCloseRequested()){

            // Set the clear color
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

            if(Input.keyPressed(GAB_KEY_F)){
                Window.ToggleFullscreen();
            }
            if(Input.keyPressed(GAB_KEY_H)){
                Window.ToggleWireframe();
            }


            Window.updateDisplay();
        }

        Window.destroyDisplay();
    }
}

