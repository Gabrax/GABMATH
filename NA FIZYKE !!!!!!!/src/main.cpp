#include "ColoringRandomPolygon.hpp"
#include "CentralForceFields.hpp"
int main()
{
    InitWindow(screenWidth, screenHeight, "8BALL");

    SetTargetFPS(60);

    ColoringRandomPolygon::Render();

    /*CentralForceFields::Render();*/
    
    CloseWindow();

    return 0;
}
