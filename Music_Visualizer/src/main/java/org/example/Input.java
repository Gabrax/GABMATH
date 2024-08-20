package org.example;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

public class Input {

    private static double mouseX, mouseY;
    private static double mouseOffsetX, mouseOffsetY;
    private static boolean leftMouseDown, rightMouseDown;
    private static boolean leftMousePressed, rightMousePressed;
    private static boolean leftMouseDownLastFrame, rightMouseDownLastFrame;
    private static boolean preventRightMouseHoldTillNextClick = false;
    private static boolean mouseWheelUp, mouseWheelDown;
    private static int mouseWheelValue;
    private static boolean[] keyDown = new boolean[350];
    private static boolean[] keyDownLastFrame = new boolean[350];
    private static boolean[] keyPressed = new boolean[350];

    public static void init() {
        long window = Window._window;
        double[] x = new double[1], y = new double[1];
        GLFW.glfwGetCursorPos(window, x, y);
        mouseX = x[0];
        mouseY = y[0];
        mouseOffsetX = mouseX;
        mouseOffsetY = mouseY;
    }

    public static void update() {
        long window = Window._window;

        // Handle mouse wheel scrolling
        mouseWheelUp = false;
        mouseWheelDown = false;
        mouseWheelValue = Window.getScrollWheelYOffset();
        if (mouseWheelValue < 0) {
            mouseWheelDown = true;
        } else if (mouseWheelValue > 0) {
            mouseWheelUp = true;
        }
        Window.ResetScrollWheelYOffset();

        // Handle keyboard input
        for (int i = 32; i < 349; i++) {
            // Down
            keyDown[i] = GLFW.glfwGetKey(window, i) == GLFW.GLFW_PRESS;

            // Pressed
            keyPressed[i] = keyDown[i] && !keyDownLastFrame[i];
            keyDownLastFrame[i] = keyDown[i];
        }

        // Handle mouse position
        double[] x = new double[1], y = new double[1];
        GLFW.glfwGetCursorPos(window, x, y);
        mouseOffsetX = x[0] - mouseX;
        mouseOffsetY = y[0] - mouseY;
        mouseX = x[0];
        mouseY = y[0];

        // Left mouse button
        leftMouseDown = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        leftMousePressed = leftMouseDown && !leftMouseDownLastFrame;
        leftMouseDownLastFrame = leftMouseDown;

        // Right mouse button
        rightMouseDown = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        rightMousePressed = rightMouseDown && !rightMouseDownLastFrame;
        rightMouseDownLastFrame = rightMouseDown;

        if (rightMousePressed) {
            preventRightMouseHoldTillNextClick = false;
        }
    }

    public static boolean keyPressed(int keycode) {
        return keyPressed[keycode];
    }

    public static boolean keyDown(int keycode) {
        return keyDown[keycode];
    }

    public static float getMouseOffsetX() {
        return (float) mouseOffsetX;
    }

    public static float getMouseOffsetY() {
        return (float) mouseOffsetY;
    }

    public static boolean leftMouseDown() {
        return leftMouseDown;
    }

    public static boolean rightMouseDown() {
        return rightMouseDown && !preventRightMouseHoldTillNextClick;
    }

    public static boolean leftMousePressed() {
        return leftMousePressed;
    }

    public static boolean rightMousePressed() {
        return rightMousePressed;
    }

    public static boolean mouseWheelDown() {
        return mouseWheelDown;
    }

    public static int getMouseWheelValue() {
        return mouseWheelValue;
    }

    public static boolean mouseWheelUp() {
        return mouseWheelUp;
    }

    public static void preventRightMouseHold() {
        preventRightMouseHoldTillNextClick = true;
    }

    public static int getMouseX() {
        return (int) mouseX;
    }

    public static int getMouseY() {
        return (int) mouseY;
    }
}

