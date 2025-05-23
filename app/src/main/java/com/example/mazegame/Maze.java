// Maze.java
package com.example.mazegame;

import java.util.Random;

public class Maze {

    private int[][] mazeMap;
    private int width;
    private int height;
    private int exitX; // X-координата выхода
    private int exitY; // Y-координата выхода

    public Maze(int width, int height) {
        this.width = width;
        this.height = height;
        this.mazeMap = new int[height][width];
        generateMaze();
    }

    private void generateMaze() {
        Random rand = new Random();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Внешние стены
                if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
                    mazeMap[y][x] = 1; // Стены
                } else {
                    // Случайное размещение пустых ячеек (0) или стен (1)
                    mazeMap[y][x] = rand.nextDouble() < 0.75 ? 0 : 1;
                }
            }
        }

        // Убедитесь, что начальная точка пуста
        mazeMap[1][1] = 0;

        // Разместите выходную дверь
        // Мы разместим ее в правом нижнем углу, убедившись, что это не стена
        // и что она доступна.
        exitX = width - 2;
        exitY = height - 2;
        mazeMap[exitY][exitX] = 2; // Присвоить '2' для ячейки выхода

        // Убедитесь, что путь к выходу свободен (простой подход: очистить соседние ячейки)
        // Это может не создать идеальный путь, но гарантирует, что выход не изолирован.
        if (exitX > 0) mazeMap[exitY][exitX - 1] = 0;
        if (exitY > 0) mazeMap[exitY - 1][exitX] = 0;
        if (exitX < width - 1) mazeMap[exitY][exitX + 1] = 0;
        if (exitY < height - 1) mazeMap[exitY + 1][exitX] = 0;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getCell(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return mazeMap[y][x];
        }
        return 1; // Вернуть стену, если за пределами
    }

    public void regenerateMaze(int newWidth, int newHeight) {
        this.width = newWidth;
        this.height = newHeight;
        this.mazeMap = new int[height][width];
        generateMaze();
    }

    public int[][] getMazeMap() {
        return mazeMap;
    }

    public void setMazeMap(int[][] mazeMap) {
        this.mazeMap = mazeMap;
    }

    // ИЗМЕНЕНО: Новый метод для проверки, находится ли игрок в ячейке выхода
    public boolean isPlayerAtExit(double playerX, double playerY) {
        // Проверяем, находится ли игрок в пределах ячейки выхода
        // Игрок находится в ячейке, если его координаты (playerX, playerY)
        // попадают в квадрат, определяемый (exitX, exitY) до (exitX + 1, exitY + 1)
        return playerX >= exitX && playerX < (exitX + 1) &&
                playerY >= exitY && playerY < (exitY + 1);
    }

    // Новые геттеры для координат выхода
    public int getExitX() {
        return exitX;
    }

    public int getExitY() {
        return exitY;
    }
}
