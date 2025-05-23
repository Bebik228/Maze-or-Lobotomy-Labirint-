package com.example.mazegame;

public class Player {
    public double angle;
    public double x;
    public double y;

    public Player(double x2, double y2, double angle2) {
        this.x = x2;
        this.y = y2;
        this.angle = angle2;
    }

    public void move(double distance, Maze maze) {
        double newX = this.x + (Math.cos(this.angle) * distance);
        double newY = this.y + (Math.sin(this.angle) * distance);
        int gridX = (int) newX;
        int gridY = (int) newY;
        if (gridX >= 0 && gridX < maze.getWidth() && gridY >= 0 && gridY < maze.getHeight() && maze.getCell(gridX, gridY) == 0) {
            this.x = newX;
            this.y = newY;
        }
    }

    public void rotate(double deltaAngle) {
        this.angle += deltaAngle;
    }
}




//package com.example.mazegame;
//
//public class Player {
//    public double x;
//    public double y;
//    public double angle;
//
//    public Player(double x, double y, double angle) {
//        this.x = x;
//        this.y = y;
//        this.angle = angle;
//    }
//
//    public void move(double distance, Maze maze) {
//        double newX = x + Math.cos(angle) * distance;
//        double newY = y + Math.sin(angle) * distance;
//        int gridX = (int) newX;
//        int gridY = (int) newY;
//
//        // Add a check for maze boundaries
//        if (maze.getCell(gridX, gridY) == 0 && gridX >= 0 && gridX < maze.getWidth() && gridY >= 0 && gridY < maze.getHeight()) {
//            x = newX;
//            y = newY;
//        }
//    }
//
//    public void rotate(double deltaAngle) {
//        this.angle += deltaAngle;
//    }
//}

