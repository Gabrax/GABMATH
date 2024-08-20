package org.example;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

import static java.awt.SystemColor.window;
import static java.sql.Types.NULL;
import static org.example.Window.RenderMode.NORMAL;
import static org.example.Window.RenderMode.WIREFRAME;
import static org.example.Window.WindowMode.FULLSCREEN;
import static org.example.Window.WindowMode.WINDOWED;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Window {

    public static long _window;
    public static long _monitor;
    public static GLFWVidMode _mode;
    public static int _currentWidth = 0;
    public static int _currentHeight = 0;
    public static int _windowedWidth = 0;
    public static int _windowedHeight = 0;
    public static int _fullscreenWidth = 0;
    public static int _fullscreenHeight = 0;
    public static int _mouseScreenX = 0;
    public static int _mouseScreenY = 0;
    public static boolean _windowHasFocus = true;
    public static boolean _forceCloseWindow = false;
    public static int _scrollWheelYOffset = 0;
    public static WindowMode _windowMode = WINDOWED;
    public static RenderMode _renderMode = WIREFRAME;
    public static double prevTime = 0.0;
    public static double crntTime = 0.0;
    public static double timeDiff;
    public static int counter = 0;
    public static int windowPosX = (_windowedWidth - _windowedWidth) / 2;
    public static int windowPosY = (_windowedHeight - _windowedHeight) / 2;


    public enum WindowMode {
        WINDOWED, FULLSCREEN
    }

    public enum RenderMode {
        WIREFRAME, NORMAL
    }

    public static void ToggleFullscreen() {
        if (_windowMode == WindowMode.WINDOWED)
            SetWindowMode(WindowMode.FULLSCREEN);
        else
            SetWindowMode(WindowMode.WINDOWED);
    }

    public static void ToggleWireframe() {
        if (_renderMode == RenderMode.NORMAL) {
            SetRenderMode(RenderMode.WIREFRAME);
        } else {
            SetRenderMode(RenderMode.NORMAL);
        }
    }

    public static void CreateWindow(WindowMode windowMode) {
        if (windowMode == WindowMode.WINDOWED) {
            _currentWidth = _windowedWidth;
            _currentHeight = _windowedHeight;
            _window = glfwCreateWindow(_windowedWidth, _windowedHeight, "Tic Tac Toe", NULL, NULL);
            if (_mode != null) {
                int xpos = (_mode.width() - _currentWidth) / 2;
                int ypos = (_mode.height() - _currentHeight) / 2;
                glfwSetWindowPos(_window, xpos, ypos);
            }
        } else if (windowMode == WindowMode.FULLSCREEN) {
            _currentWidth = _fullscreenWidth;
            _currentHeight = _fullscreenHeight;
            _window = glfwCreateWindow(_fullscreenWidth, _fullscreenHeight, "Tic Tac Toe", _monitor, NULL);
        }
        _windowMode = windowMode;
    }

    public static void SetWindowMode(WindowMode windowMode) {
        if (windowMode == WindowMode.WINDOWED) {
            _currentWidth = _windowedWidth;
            _currentHeight = _windowedHeight;
            glfwSetWindowMonitor(_window, NULL, 0, 0, _windowedWidth, _windowedHeight, 0);
            if (_mode != null) {
                int xpos = (_mode.width() - _currentWidth) / 2;
                int ypos = (_mode.height() - _currentHeight) / 2;
                glfwSetWindowPos(_window, xpos, ypos);
            }
        } else if (windowMode == WindowMode.FULLSCREEN) {
            _currentWidth = _fullscreenWidth;
            _currentHeight = _fullscreenHeight;
            glfwSetWindowMonitor(_window, _monitor, 0, 0, _fullscreenWidth, _fullscreenHeight, _mode.refreshRate());
        }
        _windowMode = windowMode;
    }

    public static void SetRenderMode(RenderMode renderMode) {
        if (renderMode == RenderMode.WIREFRAME) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        } else if (renderMode == RenderMode.NORMAL) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }
        _renderMode = renderMode;
    }

    public static void Init(int width, int height){

        GLFWErrorCallback.createPrint(System.err).set();


        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");


        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);


        // Resolution and window size
        // Setup error callback
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

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

        if (_window == NULL) {
            System.out.println("Failed to create GLFW window");
            cleanUp();
            return;
        }

        try {
            InputStream is = Window.class.getClassLoader().getResourceAsStream("logo.png");
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
            GLFW.glfwSetWindowIcon(_window, imageBuffer);

            // Clean up
            imageBuffer.free();

        } catch (IOException e) {
            e.printStackTrace();
        }


        glfwSetKeyCallback(_window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true);
        });


        glfwMakeContextCurrent(_window);
        GL.createCapabilities();
        glfwSwapInterval(0); // Enable v-sync

        glfwShowWindow(_window);

        // Register callbacks for framebuffer size and window size
        FrameCallback();
    }

    public static void updateDisplay() {
        Input.update();

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glfwSwapBuffers(_window);
        glfwPollEvents();
    }

    public static void destroyDisplay() {
        cleanUp();
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    private static void cleanUp() {
        glfwFreeCallbacks(_window);
        glfwDestroyWindow(_window);
    }

    private static void FrameCallback() {
        glfwSetFramebufferSizeCallback(_window, (window, width, height) -> {
            glViewport(0, 0, width, height);
        });

        glfwSetWindowFocusCallback(_window, Window::windowFocusCallback);
    }

    private static void windowFocusCallback(long window, boolean focused) {
        _windowHasFocus = focused;
    }

    public static boolean isCloseRequested() {
        return glfwWindowShouldClose(_window);
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
