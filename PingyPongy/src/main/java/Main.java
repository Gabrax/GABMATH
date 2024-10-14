import com.raylib.Raylib;

import static com.raylib.Jaylib.*;
import static Game.Game.*;

public class Main {
    public static void main(String[] args) {

        Window.init();
        MusicPlayer.Init();

        while (!WindowShouldClose()) {

            MusicPlayer.Update();
            Resize();

            if(IsKeyPressed(KEY_SPACE)) Window.pause = !Window.pause;
            if(!Window.pause){

                Ball.letBounce();
                Player1.movePlayer();
                Player2.movePlayer();
            }
            if(IsKeyPressed(KEY_R)) Reset();
            ScoreBoard.UpdateBoard();

            if(IsKeyPressed(KEY_H)){
                Ball.velocityX += 50.0f;
                Ball.velocityY += 50.0f;
            }else if(IsKeyPressed(KEY_J)){
                Ball.velocityX -= 50.0f;
                Ball.velocityY -= 50.0f;
            }

            BeginDrawing();
                ClearBackground(BLACK);

                    DrawRectangleRoundedLines(Player1.getPlayer1(),2.0f,4,2.0f,RED);
                    DrawRectangleRoundedLines(Player2.getPlayer2(),2.0f,4,2.0f,BLUE);
                    Ball.draw();

                DrawText("SPACE to Pause/Unpause", 10, GetScreenHeight() - 25, 20, LIGHTGRAY);
                DrawText(DEBUG("Ball velocity",Ball.velocityX,Ball.velocityY),0,0,20,LIGHTGRAY);
                DrawText(DEBUG("", ScoreBoard.p1, ScoreBoard.p2), ScoreBoard.x, ScoreBoard.y, 40, LIGHTGRAY);
                if (Window.pause) DrawText("PAUSED", 350, 200, 30, GRAY);
            EndDrawing();
        }
        CloseWindow();
    }
}