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

            BeginDrawing();
                ClearBackground(BLACK);

                    DrawRectangleRoundedLines(Player1.getPlayer1(),2.0f,4,2.0f,RED);
                    DrawRectangleRoundedLines(Player2.getPlayer2(),2.0f,4,2.0f,BLUE);
                    Sun.draw();
                    Ball.draw();

                DrawText("SPACE to Pause/Unpause", 10, GetScreenHeight() - 25, 20, LIGHTGRAY);
                DrawText(DEBUG("Ball velocity",Ball.speed()),0,0,20,LIGHTGRAY);
                DrawText(DEBUG("", ScoreBoard.p1, ScoreBoard.p2), ScoreBoard.x, ScoreBoard.y, 40, LIGHTGRAY);
                DrawText(DEBUG("Air", Ball.airResistance), 0, 30, 20, LIGHTGRAY);
                DrawText(DEBUG("Magnus", Ball.magnus), 0, 50, 20, LIGHTGRAY);
                DrawText(DEBUG("Gravity", Ball.isGravityEnabled), 0, 70, 20, LIGHTGRAY);
                DrawText(DEBUG("SunPull", Ball.isSunEnabled), 0, 90, 20, LIGHTGRAY);

                if (Window.pause) DrawText("PAUSED", 350, 200, 30, GRAY);
            EndDrawing();
        }
        CloseWindow();
    }
}