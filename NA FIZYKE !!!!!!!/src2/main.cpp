#include <raylib.h>

#include "CentralForceFields.hpp"

const int SCREEN_WIDTH = 800;
const int SCREEN_HEIGHT = 800;

int main() {
    InitWindow(SCREEN_WIDTH, SCREEN_HEIGHT, "CentralForceFields");
    SetTargetFPS(60);
  
    CentralForceFields::Render();

    CloseWindow();
    return 0;
}

