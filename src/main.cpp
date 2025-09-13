#include "Audio.h"
#include "raylib.h"
#define RAYGUI_IMPLEMENTATION
#include "raygui.h"

int main(void)
{
  Audio audio;
  audio.SetSR(Sample_Rate::SR_44100);

  const int screenWidth = 800, screenHeight = 450;

  InitWindow(screenWidth, screenHeight, "raylib [core] example - input mouse");
  SetTargetFPS(60);               

  bool showMessageBox = false;

  while (!WindowShouldClose())    
  {
    BeginDrawing();

    ClearBackground(GetColor(GuiGetStyle(DEFAULT, BACKGROUND_COLOR)));

    Rectangle buttonRec = { 24.0f, 24.0f, 120.0f, 30.0f };
    if (GuiButton(buttonRec, "#191#Show Message")) 
    {
        showMessageBox = true;
    }

    if (showMessageBox)
    {
        Rectangle msgBoxRec = { 85.0f, 70.0f, 250.0f, 100.0f };
        int result = GuiMessageBox(msgBoxRec, 
                                   "#191#Message Box", 
                                   "Hi! This is a message!", 
                                   "Nice;Cool");

        if (result >= 0) 
        {
            showMessageBox = false;
        }
    }

    if (IsKeyPressed(KEY_ENTER)) audio.Toggle();

    EndDrawing();
  }

  CloseWindow();        
}
