package org.example;

import static org.example.GabKeys.GAB_KEY_F;
import static org.example.GabKeys.GAB_KEY_H;
import static org.lwjgl.opengl.GL11.*;

public class Main {
    public static void main(String[] args) {

        Window.Init((int) (1920 * 0.75f), (int) (1920 * 0.75));

        Cube Cube = new Cube();

        while (!Window.isCloseRequested()){
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

            Cube.render();

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

