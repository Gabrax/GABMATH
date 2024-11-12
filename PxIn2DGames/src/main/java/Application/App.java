package Application;

import com.raylib.Jaylib;

import static Application.Game1.renderGame1;
import static Application.Game2.renderGame2;
import static Application.PingPong.RenderPingPong;
import static com.raylib.Jaylib.*;

public class App {

    private enum Scene {
        MENU,
        GAME1,
        GAME2,
        GAME3,
        PING_PONG_GAME,
    }

    private static Scene currentScene = Scene.MENU;
    // Menu selection index (0: PING_PONG_GAME, 1: GAME1, 2: GAME2, 3: GAME3)
    private static int menuSelection = 0;
    private static final int menuItemsCount = 4;

    public static void Run(){
        AppUtils.Window.init();

        // Load the selection cube texture (adjust path as needed)
        Texture selectionCubeTexture = LoadTexture("resources/arrow.png");

        while (!WindowShouldClose()) {

            // Update menu selection based on arrow key input
            if (currentScene == Scene.MENU) {
                if (IsKeyPressed(KEY_DOWN)) {
                    menuSelection = (menuSelection + 1) % menuItemsCount;
                }
                if (IsKeyPressed(KEY_UP)) {
                    menuSelection = (menuSelection - 1 + menuItemsCount) % menuItemsCount;
                }
                // Select menu item
                if (IsKeyPressed(KEY_ENTER)) {
                    switch (menuSelection) {
                        case 0 -> currentScene = Scene.PING_PONG_GAME;
                        case 1 -> currentScene = Scene.GAME1;
                        case 2 -> currentScene = Scene.GAME2;
                        case 3 -> currentScene = Scene.GAME3;
                    }
                }
            }

            // Return to menu with Control keys
            if (currentScene != Scene.MENU && (IsKeyPressed(KEY_LEFT_CONTROL) || IsKeyPressed(KEY_RIGHT_CONTROL))) {
                currentScene = Scene.MENU;
            }

            BeginDrawing();
            ClearBackground(BLACK);

            switch (currentScene) {
                case MENU:
                    DrawText("MAIN MENU", 350, 100, 20, WHITE);
                    DrawText("Ping Pong", 350, 150, 20, menuSelection == 0 ? YELLOW : WHITE);
                    DrawText("Game1", 350, 200, 20, menuSelection == 1 ? YELLOW : WHITE);
                    DrawText("Game2", 350, 250, 20, menuSelection == 2 ? YELLOW : WHITE);
                    DrawText("Game3", 350, 300, 20, menuSelection == 3 ? YELLOW : WHITE);

                    DrawText("Press ESCAPE to Exit", 320, 350, 20, WHITE);

                    // Draw a textured cube next to the selected menu item
                    float cubePosY = 145 + menuSelection * 50;
                    DrawTextureEx(selectionCubeTexture, new Jaylib.Vector2(300, cubePosY), 0.0f, 0.5f, WHITE);
                    break;

                case PING_PONG_GAME:
                    RenderPingPong();
                    break;
                case GAME1:
                    renderGame1();
                    break;
                case GAME2:
                    renderGame2();
                    break;
                default:
                    System.out.println("Unknown Scene");
                    break;
            }
            EndDrawing();
        }
        UnloadTexture(selectionCubeTexture);
        CloseWindow();
    }
}
