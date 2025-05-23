package com.example.mazegame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import java.lang.reflect.Array;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private Thread gameThread;
    private SurfaceHolder holder = getHolder();
    private boolean isRunning;
    private MainActivity mainActivity;
    private Maze maze;
    private int mazeHeight = 16;
    private int mazeWidth = 16;
    private Paint paint;
    private Player player;
    /* access modifiers changed from: private */
    public int screenHeight;
    /* access modifiers changed from: private */
    public int screenWidth;
    private MediaPlayer walkSound;

    public GameView(Context context, MainActivity activity) {
        super(context);
        this.holder.addCallback(this);
        this.mainActivity = activity;
        this.maze = new Maze(this.mazeWidth, this.mazeHeight);
        this.player = new Player(1.5d, 1.5d, 0.0d);
        this.paint = new Paint();
        setFocusable(true);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                GameView.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                GameView.this.screenWidth = GameView.this.getWidth();
                GameView.this.screenHeight = GameView.this.getHeight();
            }
        });
    }

    public void setWalkSound(MediaPlayer sound) {
        this.walkSound = sound;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Maze getMaze() {
        return this.maze;
    }

    public void surfaceCreated(SurfaceHolder holder2) {
        Log.d("GameView", "Surface created");
        this.isRunning = true;
        this.gameThread = new Thread(this);
        this.gameThread.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder2) {
        this.isRunning = false;
        try {
            this.gameThread.join();
        } catch (InterruptedException e) {
            Log.e("GameView", "Thread join error", e);
        }
    }

    public void surfaceChanged(SurfaceHolder holder2, int format, int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
    }

    public void run() {
        Log.d("GameView", "Game thread started");
        while (this.isRunning) {
            if (this.holder.getSurface().isValid()) {
                Canvas canvas = this.holder.lockCanvas();
                if (canvas != null) {
                    drawGame(canvas);
                    this.holder.unlockCanvasAndPost(canvas);
                }
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    Log.e("GameView", "Sleep interrupted", e);
                }
            }

            // ИЗМЕНЕНО: Проверяем, достиг ли игрок выхода, используя новый метод
            if (maze.isPlayerAtExit(player.x, player.y)) {
                isRunning = false; // Останавливаем игровой цикл
                mainActivity.showEscapeDialog(); // Вызываем метод для отображения диалога
            }
        }
        Log.d("GameView", "Game thread ended");
    }

    private void drawGame(Canvas canvas) {
        canvas.drawColor(-7829368);
        castRays(canvas);
    }

    private void castRays(Canvas canvas) {
        if (this.screenWidth != 0) {
            int rayCount = this.screenWidth;
            for (int i = 0; i < rayCount; i++) {
                // Измененный вызов castSingleRay для получения цвета
                int wallColor = castSingleRay(this.player.x, this.player.y, normalizeAngle((this.player.angle - (1.0471975511965976d / 2.0d)) + ((((double) i) * 1.0471975511965976d) / ((double) rayCount))), i, canvas);
                if (wallColor != 0) { // Если луч столкнулся с чем-то
                    int wallHeight = (int) (((double) this.screenHeight) / (castSingleRay(this.player.x, this.player.y, normalizeAngle((this.player.angle - (1.0471975511965976d / 2.0d)) + ((((double) i) * 1.0471975511965976d) / ((double) rayCount))), true))); // Передаем true для получения только расстояния
                    int wallTop = (this.screenHeight / 2) - (wallHeight / 2);
                    this.paint.setColor(wallColor);
                    canvas.drawLine((float) i, (float) wallTop, (float) i, (float) (wallTop + wallHeight), this.paint);
                }
            }
        }
    }

    // Измененный метод castSingleRay для возврата цвета
    private int castSingleRay(double startX, double startY, double angle, int screenX, Canvas canvas) {
        double dx = Math.cos(angle);
        double dy = Math.sin(angle);
        double rayX = startX;
        double rayY = startY;
        for (int i = 0; i < 100; i++) {
            rayX += dx * 0.1d;
            rayY += 0.1d * dy;
            int gridX = (int) rayX;
            int gridY = (int) rayY;

            if (gridX >= 0 && gridX < maze.getWidth() && gridY >= 0 && gridY < maze.getHeight()) {
                if (this.maze.getCell(gridX, gridY) == 1) { // Это стена
                    double distance = Math.sqrt(Math.pow(rayX - startX, 2.0d) + Math.pow(rayY - startY, 2.0d));
                    int brightness = (int) Math.min(255.0d, 1000.0d / distance);
                    return Color.rgb(brightness, brightness, brightness);
                } else if (this.maze.isPlayerAtExit(rayX, rayY)) { // ИЗМЕНЕНО: Используем isPlayerAtExit
                    // Если это выход, вернуть зеленый цвет
                    return Color.GREEN;
                }
            }
        }
        return 0; // Ничего не найдено, не рисовать
    }

    // Добавленный метод castSingleRay для получения только расстояния
    private double castSingleRay(double startX, double startY, double angle, boolean returnDistanceOnly) {
        double dx = Math.cos(angle);
        double dy = Math.sin(angle);
        double rayX = startX;
        double rayY = startY;
        for (int i = 0; i < 100; i++) {
            rayX += dx * 0.1d;
            rayY += 0.1d * dy;
            int gridX = (int) rayX;
            int gridY = (int) rayY;

            if (gridX >= 0 && gridX < maze.getWidth() && gridY >= 0 && gridY < maze.getHeight()) {
                if (this.maze.getCell(gridX, gridY) == 1 || this.maze.isPlayerAtExit(rayX, rayY)) { // ИЗМЕНЕНО: Используем isPlayerAtExit
                    return Math.sqrt(Math.pow(rayX - startX, 2.0d) + Math.pow(rayY - startY, 2.0d));
                }
            }
        }
        return Double.MAX_VALUE;
    }

    private double normalizeAngle(double angle) {
        while (angle < 0.0d) {
            angle += 6.283185307179586d;
        }
        while (angle >= 6.283185307179586d) {
            angle -= 6.283185307179586d;
        }
        return angle;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != 0) {
            return true;
        }
        float x = event.getX();
        float center = ((float) getWidth()) / 2.0f;
        if (x < center - 200.0f) {
            this.player.rotate(-0.1d);
            return true;
        } else if (x > 200.0f + center) {
            this.player.rotate(0.1d);
            return true;
        } else {
            // ИЗМЕНЕНО: Проверяем, что игрок не движется в стену или за пределы лабиринта
            double newX = this.player.x + (Math.cos(this.player.angle) * 0.2d);
            double newY = this.player.y + (Math.sin(this.player.angle) * 0.2d);
            int gridX = (int) newX;
            int gridY = (int) newY;

            if (gridX >= 0 && gridX < maze.getWidth() && gridY >= 0 && gridY < maze.getHeight() && maze.getCell(gridX, gridY) != 1) {
                this.player.move(0.2d, this.maze);
                if (this.walkSound != null) {
                    this.walkSound.start();
                }
            }
            return true;
        }
    }

    public Parcelable onSaveInstanceState() {
        SavedState ss = new SavedState(super.onSaveInstanceState());
        ss.mazeData = this.maze.getMazeMap();
        ss.playerX = this.player.x;
        ss.playerY = this.player.y;
        ss.playerAngle = this.player.angle;
        ss.mazeWidth = this.maze.getWidth();
        ss.mazeHeight = this.maze.getHeight();
        return ss;
    }

    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.mazeWidth = ss.mazeWidth;
        this.mazeHeight = ss.mazeHeight;
        this.maze = new Maze(this.mazeWidth, this.mazeHeight);
        this.maze.setMazeMap(ss.mazeData);
        this.player.x = ss.playerX;
        this.player.y = ss.playerY;
        this.player.angle = ss.playerAngle;
    }

    static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        int[][] mazeData;
        int mazeHeight;
        int mazeWidth;
        double playerAngle;
        double playerX;
        double playerY;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.mazeWidth = in.readInt();
            this.mazeHeight = in.readInt();
            int readInt = in.readInt();
            int i = this.mazeHeight;
            int[] iArr = new int[2];
            iArr[1] = this.mazeWidth;
            iArr[0] = i;
            this.mazeData = (int[][]) Array.newInstance(Integer.TYPE, iArr);
            for (int i2 = 0; i2 < this.mazeHeight; i2++) {
                in.readIntArray(this.mazeData[i2]);
            }
            this.playerX = in.readDouble();
            this.playerY = in.readDouble();
            this.playerAngle = in.readDouble();
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.mazeWidth);
            out.writeInt(this.mazeHeight);
            out.writeInt(this.mazeData.length);
            for (int[] row : this.mazeData) {
                out.writeIntArray(row);
            }
            out.writeDouble(this.playerX);
            out.writeDouble(this.playerY);
            out.writeDouble(this.playerAngle);
        }
    }
}
