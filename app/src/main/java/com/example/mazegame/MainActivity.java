package com.example.mazegame;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.SurfaceView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.view.View;
import android.app.AlertDialog; // Импорт для AlertDialog
import android.content.DialogInterface; // Импорт для DialogInterface

import com.example.maze.R;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer walkSound;
    private GameView gameView;
    private SurfaceView gameSurface;
    private Button btnForward;
    private Button btnLeft;
    private Button btnRight;
    private Button btnBackward;
    private RelativeLayout gameLayout;
    private int mazeWidth = 16;
    private int mazeHeight = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameLayout = findViewById(R.id.gameLayout);
        gameSurface = findViewById(R.id.gameSurface);
        gameView = new GameView(this, this);
        gameView.getHolder().addCallback(gameView);
        gameView.setZOrderOnTop(true);
        ((ViewGroup) gameSurface.getParent()).removeView(gameSurface);
        gameLayout.addView(gameView, 0);

        walkSound = MediaPlayer.create(this, R.raw.walk);
        gameView.setWalkSound(walkSound);

        btnForward = findViewById(R.id.btnForward);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);
        btnBackward = findViewById(R.id.btnBackward);

        btnForward.setOnClickListener(v -> {
            gameView.getPlayer().move(0.2, gameView.getMaze());
            playWalkSound();
        });

        btnBackward.setOnClickListener(v -> {
            gameView.getPlayer().move(-0.2, gameView.getMaze());
            playWalkSound();
        });

        btnLeft.setOnClickListener(v -> gameView.getPlayer().rotate(-0.1));
        btnRight.setOnClickListener(v -> gameView.getPlayer().rotate(0.1));

        findViewById(R.id.btnRotate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Изменяем ориентацию экрана
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        });

        if (savedInstanceState != null) {
            mazeWidth = savedInstanceState.getInt("mazeWidth");
            mazeHeight = savedInstanceState.getInt("mazeHeight");
        }
    }

    private void playWalkSound() {
        if (walkSound != null) {
            walkSound.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (walkSound != null) {
            walkSound.release();
            walkSound = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (gameView != null) {
            Parcelable gameViewState = gameView.onSaveInstanceState();
            outState.putParcelable("gameViewState", gameViewState);
        }
        outState.putInt("mazeWidth", mazeWidth);
        outState.putInt("mazeHeight", mazeHeight);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            Parcelable gameViewState = savedInstanceState.getParcelable("gameViewState");
            if (gameViewState != null && gameView != null) {
                gameView.onRestoreInstanceState(gameViewState);
            }
            mazeWidth = savedInstanceState.getInt("mazeWidth");
            mazeHeight = savedInstanceState.getInt("mazeHeight");
        }
    }

    /**
     * Отображает диалоговое окно "Вы сбежали!".
     * Этот метод вызывается из GameView, когда игрок достигает выхода.
     */
    public void showEscapeDialog() {
        // Убедитесь, что диалог отображается в основном потоке UI
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Вы сбежали!") // Сообщение, которое будет показано
                    .setCancelable(false) // Предотвращает закрытие диалога кнопкой "Назад"
                    .setPositiveButton("OK", (dialog, id) -> {
                        // Действие при нажатии кнопки "OK", например, закрытие активности
                        finish(); // Закрывает текущую активность, завершая игру
                    });
            AlertDialog alert = builder.create();
            alert.show(); // Отображает диалоговое окно
        });
    }
}


//package com.example.mazegame;
//
//import android.content.pm.ActivityInfo;
//import android.media.MediaPlayer;
//import android.os.Bundle;
//import android.os.Parcelable;
//import android.view.SurfaceView;
//import android.widget.Button;
//import androidx.appcompat.app.AppCompatActivity;
//import android.view.ViewGroup;
//import android.widget.RelativeLayout;
//import android.view.View;
//
//import com.example.maze.R;
//
//public class MainActivity extends AppCompatActivity {
//
//    private MediaPlayer walkSound;
//    private GameView gameView;
//    private SurfaceView gameSurface;
//    private Button btnForward;
//    private Button btnLeft;
//    private Button btnRight;
//    private Button btnBackward;
//    private RelativeLayout gameLayout;
//    private int mazeWidth = 16;
//    private int mazeHeight = 16;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        gameLayout = findViewById(R.id.gameLayout);
//        gameSurface = findViewById(R.id.gameSurface);
//        gameView = new GameView(this, this);
//        gameView.getHolder().addCallback(gameView);
//        gameView.setZOrderOnTop(true);
//        ((ViewGroup) gameSurface.getParent()).removeView(gameSurface);
//        gameLayout.addView(gameView, 0);
//
//        walkSound = MediaPlayer.create(this, R.raw.walk);
//        gameView.setWalkSound(walkSound);
//
//        btnForward = findViewById(R.id.btnForward);
//        btnLeft = findViewById(R.id.btnLeft);
//        btnRight = findViewById(R.id.btnRight);
//        btnBackward = findViewById(R.id.btnBackward);
//
//        btnForward.setOnClickListener(v -> {
//            gameView.getPlayer().move(0.2, gameView.getMaze());
//            playWalkSound();
//        });
//
//        btnBackward.setOnClickListener(v -> {
//            gameView.getPlayer().move(-0.2, gameView.getMaze());
//            playWalkSound();
//        });
//
//        btnLeft.setOnClickListener(v -> gameView.getPlayer().rotate(-0.1));
//        btnRight.setOnClickListener(v -> gameView.getPlayer().rotate(0.1));
//
//        findViewById(R.id.btnRotate).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Изменяем ориентацию экрана
//                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                } else {
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                }
//            }
//        });
//
//        if (savedInstanceState != null) {
//            mazeWidth = savedInstanceState.getInt("mazeWidth");
//            mazeHeight = savedInstanceState.getInt("mazeHeight");
//        }
//    }
//
//    private void playWalkSound() {
//        if (walkSound != null) {
//            walkSound.start();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (walkSound != null) {
//            walkSound.release();
//            walkSound = null;
//        }
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        if (gameView != null) {
//            Parcelable gameViewState = gameView.onSaveInstanceState();
//            outState.putParcelable("gameViewState", gameViewState);
//        }
//        outState.putInt("mazeWidth", mazeWidth);
//        outState.putInt("mazeHeight", mazeHeight);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        if (savedInstanceState != null) {
//            Parcelable gameViewState = savedInstanceState.getParcelable("gameViewState");
//            if (gameViewState != null && gameView != null) {
//                gameView.onRestoreInstanceState(gameViewState);
//            }
//            mazeWidth = savedInstanceState.getInt("mazeWidth");
//            mazeHeight = savedInstanceState.getInt("mazeHeight");
//        }
//    }
//}


//package com.example.mazegame;
//
//import android.media.MediaPlayer;
//import android.os.Bundle;
//import android.view.SurfaceView;
//import android.widget.Button;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.maze.R;
//
//public class MainActivity extends AppCompatActivity {
//    private MediaPlayer walkSound;
//    private GameView gameView;
//    private SurfaceView gameSurface;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(com.example.maze.R.layout.activity_main);
//
//        gameSurface = findViewById(R.id.gameSurface);
//        gameView = new GameView(this, this);
//        gameView.getHolder().addCallback(gameView);
//        gameView.setZOrderOnTop(true);
//        ((android.view.ViewGroup) gameSurface.getParent()).removeView(gameSurface);
//        ((android.view.ViewGroup) findViewById(R.id.gameLayout)).addView(gameView, 0);
//
//        walkSound = MediaPlayer.create(this, R.raw.walk);
//        gameView.setWalkSound(walkSound);
//
//        Button btnForward = findViewById(R.id.btnForward);
//        Button btnLeft = findViewById(R.id.btnLeft);
//        Button btnRight = findViewById(R.id.btnRight);
//        Button btnBackward = findViewById(R.id.btnBackward);
//
//        btnForward.setOnClickListener(v -> {
//            gameView.getPlayer().move(0.2, gameView.getMaze());
//            playWalkSound();
//        });
//
//        btnBackward.setOnClickListener(v -> {
//            gameView.getPlayer().move(-0.2, gameView.getMaze());
//            playWalkSound();
//        });
//
//        btnLeft.setOnClickListener(v -> gameView.getPlayer().rotate(-0.1));
//        btnRight.setOnClickListener(v -> gameView.getPlayer().rotate(0.1));
//    }
//
//    private void playWalkSound() {
//        if (walkSound != null) {
//            walkSound.start();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (walkSound != null) {
//            walkSound.release();
//            walkSound = null;
//        }
//    }
//}

