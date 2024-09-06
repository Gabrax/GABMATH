package org.example;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static java.sql.Types.NULL;
import static org.example.testWindow.RenderMode.WIREFRAME;
import static org.example.testWindow.WindowMode.WINDOWED;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_FILL;


public class testWindow {


    private static long window;
    private static long _monitor;
    private static GLFWVidMode _mode;
    public static int _currentWidth = 0;
    public static int _currentHeight = 0;
    private static int _windowedWidth = 0;
    private static int _windowedHeight = 0;
    private static int _fullscreenWidth = 0;
    private static int _fullscreenHeight = 0;
    private static int _mouseScreenX = 0;
    private static int _mouseScreenY = 0;
    private static boolean _windowHasFocus = true;
    private static boolean _forceCloseWindow = false;
    private static int _scrollWheelYOffset = 0;
    private static WindowMode _windowMode = WINDOWED;
    private static RenderMode _renderMode = WIREFRAME;
    private static double prevTime = 0.0;
    private static double crntTime = 0.0;
    private static double timeDiff;
    private static int counter = 0;
    private static int windowPosX = (_windowedWidth - _windowedWidth) / 2;
    private static int windowPosY = (_windowedHeight - _windowedHeight) / 2;

    public enum WindowMode {
        WINDOWED, FULLSCREEN
    }

    public enum RenderMode {
        WIREFRAME, NORMAL
    }

    public static void ToggleFullscreen() {
        if (_windowMode == WINDOWED)
            SetWindowMode(WindowMode.FULLSCREEN);
        else
            SetWindowMode(WINDOWED);
    }

    public static void ToggleWireframe() {
        if (_renderMode == RenderMode.NORMAL) {
            SetRenderMode(WIREFRAME);
        } else {
            SetRenderMode(RenderMode.NORMAL);
        }
    }

    public static void CreateWindow(WindowMode windowMode) {
        if (windowMode == WINDOWED) {
            _currentWidth = _windowedWidth;
            _currentHeight = _windowedHeight;
            window = glfwCreateWindow(_windowedWidth, _windowedHeight, "Tic Tac Toe", NULL, NULL);
            if (_mode != null) {
                int xpos = (_mode.width() - _currentWidth) / 2;
                int ypos = (_mode.height() - _currentHeight) / 2;
                glfwSetWindowPos(window, xpos, ypos);
            }
        } else if (windowMode == WindowMode.FULLSCREEN) {
            _currentWidth = _fullscreenWidth;
            _currentHeight = _fullscreenHeight;
            window = glfwCreateWindow(_fullscreenWidth, _fullscreenHeight, "Tic Tac Toe", _monitor, NULL);
        }
        _windowMode = windowMode;
    }

    public static void SetWindowMode(WindowMode windowMode) {
        if (windowMode == WINDOWED) {
            _currentWidth = _windowedWidth;
            _currentHeight = _windowedHeight;
            glfwSetWindowMonitor(window, NULL, 0, 0, _windowedWidth, _windowedHeight, 0);
            if (_mode != null) {
                int xpos = (_mode.width() - _currentWidth) / 2;
                int ypos = (_mode.height() - _currentHeight) / 2;
                glfwSetWindowPos(window, xpos, ypos);
            }
        } else if (windowMode == WindowMode.FULLSCREEN) {
            _currentWidth = _fullscreenWidth;
            _currentHeight = _fullscreenHeight;
            glfwSetWindowMonitor(window, _monitor, 0, 0, _fullscreenWidth, _fullscreenHeight, _mode.refreshRate());
        }
        _windowMode = windowMode;
    }

    public static void SetRenderMode(RenderMode renderMode) {
        if (renderMode == WIREFRAME) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        } else if (renderMode == RenderMode.NORMAL) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }
        _renderMode = renderMode;
    }

    public void init(int width, int height) {

        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        // Resolution and window size
        _monitor = glfwGetPrimaryMonitor();
        _mode = glfwGetVideoMode(_monitor);
        assert _mode != null;
        glfwWindowHint(GLFW_RED_BITS, _mode.redBits());
        glfwWindowHint(GLFW_GREEN_BITS, _mode.greenBits());
        glfwWindowHint(GLFW_BLUE_BITS, _mode.blueBits());
        glfwWindowHint(GLFW_REFRESH_RATE, _mode.refreshRate());
        _fullscreenWidth = _mode.width();
        _fullscreenHeight = _mode.height();
        _windowedWidth = width;
        _windowedHeight = height;

        if (_windowedWidth > _fullscreenWidth || _windowedHeight > _fullscreenHeight) {
            _windowedWidth = (int) (_fullscreenWidth * 0.75f);
            _windowedHeight = (int) (_fullscreenHeight * 0.75f);
        }

        CreateWindow(WINDOWED);

        if (window == NULL) {
            System.out.println("Failed to create GLFW window");
            cleanup();
            return;
        }

//        window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
//        if (window == MemoryUtil.NULL) {
//            throw new RuntimeException("Failed to create the GLFW window");
//        }

        try {
            InputStream is = testWindow.class.getClassLoader().getResourceAsStream("logo.png");
            if (is == null) {
                throw new IOException("Resource not found");
            }
            BufferedImage icon = ImageIO.read(is);
            GLFWImage.Buffer imageBuffer = GLFWImage.malloc(1);

            // Convert BufferedImage to ByteBuffer
            ByteBuffer buffer = BufferedImageToByteBuffer(icon);
            GLFWImage image = GLFWImage.malloc();
            image.set(icon.getWidth(), icon.getHeight(), buffer);
            imageBuffer.put(0, image);

            // Set the window icon
            GLFW.glfwSetWindowIcon(window, imageBuffer);

            // Clean up
            imageBuffer.free();

        } catch (IOException e) {
            e.printStackTrace();
        }

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true);
        });

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1); // Enable v-sync
        GLFW.glfwShowWindow(window);

        GL.createCapabilities();

        FrameCallback();
    }

    public static long getWindow() {
        return window;
    }
    private void FrameCallback() {
        glfwSetFramebufferSizeCallback(window, (window, width, height) -> glViewport(0, 0, width, height));

        glfwSetWindowFocusCallback(window, testWindow::windowFocusCallback);
    }

    private static void windowFocusCallback(long window, boolean focused) {
        _windowHasFocus = focused;
    }

    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(window);
    }

    public void updateWindow() {
        Input.update();
        GLFW.glfwSwapBuffers(window);
        GLFW.glfwPollEvents();
    }

    public void cleanup() {
        if (window != MemoryUtil.NULL) {
            glfwFreeCallbacks(window);
            GLFW.glfwDestroyWindow(window);
        }
        GLFW.glfwTerminate();
    }

    private static ByteBuffer BufferedImageToByteBuffer(BufferedImage image) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(image.getWidth() * image.getHeight() * 4);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgba = image.getRGB(x, y);
                buffer.put((byte) ((rgba >> 16) & 0xFF)); // Red
                buffer.put((byte) ((rgba >> 8) & 0xFF));  // Green
                buffer.put((byte) (rgba & 0xFF));         // Blue
                buffer.put((byte) ((rgba >> 24) & 0xFF)); // Alpha
            }
        }
        buffer.flip();
        return buffer;
    }

    public static int getScrollWheelYOffset()
    {
        return _scrollWheelYOffset;
    }

    public static void ResetScrollWheelYOffset()
    {
        _scrollWheelYOffset = 0;
    }
}
