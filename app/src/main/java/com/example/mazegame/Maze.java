package com.example.mazegame;

import java.util.Random;

public class Maze {

    private int[][] mazeMap;
    private int width;
    private int height;
    private int exitX;
    private int exitY;

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
                if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
                    mazeMap[y][x] = 1; // Стены
                } else {
                    mazeMap[y][x] = rand.nextDouble() < 0.75 ? 0 : 1;
                }
            }
        }

        mazeMap[1][1] = 0;

        exitX = width - 2;
        exitY = height - 2;
        mazeMap[exitY][exitX] = 2;

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
        return 1;
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

    public boolean isPlayerAtExit(double playerX, double playerY) {
        return playerX >= exitX && playerX < (exitX + 1) &&
                playerY >= exitY && playerY < (exitY + 1);
    }

    public int getExitX() {
        return exitX;
    }

    public int getExitY() {
        return exitY;
    }
}
