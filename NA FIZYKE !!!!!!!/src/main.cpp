#include "raylib.h"
#include "raymath.h"
#include <cstdio>
#include <vector>
#include <cmath>
#include <algorithm>
#include <stdexcept>

struct Circle {
    Vector2 position;
    Vector2 velocity;
    float radius;
};

struct Polygon {
    std::vector<Vector2> vertices;
    std::vector<Color> edgeColors; 
    std::vector<float> edgeWidths; 
    float rotation; 
};

std::vector<float> GenerateEdgeWidths(int sides, float minWidth, float maxWidth) {
    if (minWidth > maxWidth) {
        throw std::invalid_argument("minWidth cannot be greater than maxWidth.");
    }

    std::vector<float> edgeWidths;
    for (int i = 0; i < sides; i++) {
        float randomWidth = minWidth + (GetRandomValue(0, 100) / 100.0f) * (maxWidth - minWidth);
        edgeWidths.push_back(randomWidth);
    }
    return edgeWidths;
}

Polygon GeneratePolygon(int sides, const std::vector<float>& edgeWidths, Vector2 center) {

    if (sides != edgeWidths.size()) {
        throw std::invalid_argument("Number of sides must match the number of edge widths.");
    }

    Polygon polygon;
    float angleStep = 2 * PI / sides;

    for (int i = 0; i < sides; i++) {
        float angle = i * angleStep;
        float radius = edgeWidths[i]; 
        polygon.vertices.push_back({
            center.x + radius * cos(angle),
            center.y + radius * sin(angle)
        });
        polygon.edgeColors.push_back(BLACK); 
    }
    polygon.edgeWidths = edgeWidths;
    polygon.rotation = 0.0f;
    return polygon;
}

void RotatePolygon(Polygon& polygon, float angle) {
    Vector2 center = {0, 0};
    for (const auto& vertex : polygon.vertices) {
        center = Vector2Add(center, vertex);
    }
    center = Vector2Scale(center, 1.0f / polygon.vertices.size());

    for (auto& vertex : polygon.vertices) {
        Vector2 relative = Vector2Subtract(vertex, center);
        float rotatedX = relative.x * cos(angle) - relative.y * sin(angle);
        float rotatedY = relative.x * sin(angle) + relative.y * cos(angle);
        vertex = Vector2Add(center, {rotatedX, rotatedY});
    }
    polygon.rotation += angle;
}

void HandleCollision(Circle& circle, Polygon& polygon) {
    for (size_t i = 0; i < polygon.vertices.size(); i++) {
        Vector2 start = polygon.vertices[i];
        Vector2 end = polygon.vertices[(i + 1) % polygon.vertices.size()];

        Vector2 edge = Vector2Subtract(end, start);
        Vector2 normal = {-edge.y, edge.x};
        float length = sqrt(normal.x * normal.x + normal.y * normal.y);
        normal = Vector2Scale(normal, 1.0f / length);

        Vector2 toCircle = Vector2Subtract(circle.position, start);

        float dist = Vector2DotProduct(normal, toCircle);
        if (fabs(dist) <= circle.radius) {
            circle.velocity = Vector2Subtract(circle.velocity, Vector2Scale(normal, 2 * Vector2DotProduct(circle.velocity, normal)));

            circle.position = Vector2Add(circle.position, Vector2Scale(normal, circle.radius - dist));

            polygon.edgeColors[i] = RED;         }
    }
}

int main() {
    constexpr int screenWidth = 800;
    constexpr int screenHeight = 600;
    InitWindow(screenWidth, screenHeight, "8BALL");

    Circle circle = {{400, 300}, {15, -3}, 10}; 

    const int sides = 7;
    std::vector<float> edgeWidths = GenerateEdgeWidths(sides, 150.0f, 250.0f);
    Polygon polygon = GeneratePolygon(sides, edgeWidths, {400, 300});

    float timer = 0.0f;
    bool allColored = false;

    std::vector<Vector2> tracePath;

    SetTargetFPS(60);

    while (!WindowShouldClose()) {
        if (!allColored) {
            timer += GetFrameTime();
            tracePath.push_back(circle.position); 
        }

        circle.position.x += circle.velocity.x;
        circle.position.y += circle.velocity.y;

        HandleCollision(circle, polygon);

        allColored = std::all_of(polygon.edgeColors.begin(), polygon.edgeColors.end(), [](Color color) {
            return color.r != 0 || color.g != 0 || color.b != 0;
        });

        if (IsKeyDown(KEY_LEFT)) RotatePolygon(polygon, -0.05f);
        if (IsKeyDown(KEY_RIGHT)) RotatePolygon(polygon, 0.05f);

        if (allColored && IsKeyPressed(KEY_R)) {
            edgeWidths = GenerateEdgeWidths(sides, 150.0f, 250.0f); 
            polygon = GeneratePolygon(sides, edgeWidths, {400, 300}); 
            timer = 0.0f;
            allColored = false;
            tracePath.clear(); 
        }

        BeginDrawing();
        ClearBackground(GRAY);

        if (!tracePath.empty()) {
            DrawLineStrip(tracePath.data(), tracePath.size(), YELLOW);
        }

        for (size_t i = 0; i < polygon.vertices.size(); i++) {
            Vector2 start = polygon.vertices[i];
            Vector2 end = polygon.vertices[(i + 1) % polygon.vertices.size()];
            DrawLineV(start, end, polygon.edgeColors[i]);
        }

        DrawCircleV(circle.position, circle.radius, RED);

        DrawText(TextFormat("Time: %.2f seconds", timer), 10, 10, 20, WHITE);

        if (allColored) {
            DrawText("All walls are colored!", screenWidth / 2 - 100, screenHeight / 2, 20, GREEN);
            DrawText("Press 'R' to reset!", screenWidth / 2 - 100, screenHeight / 2 + 30, 20, YELLOW);
        }

        EndDrawing();
    }

    CloseWindow();

    return 0;
}
