package com.example.mypingpong;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import java.util.Random;

public class PongGame1v1 extends View {
    private int screenWidth;
    private int screenHeight;
    private int paddleWidth;
    private int paddleHeight;
    private float player1PaddleX;
    private float player1PaddleY;
    private float player2PaddleX;
    private float player2PaddleY;
    private float ballX;
    private float ballY;
    private float ballSpeedX;
    private float ballSpeedY;
    private int ballSize;
    private Paint paint;
    private boolean gameStarted;
    private long gameStartTime;
    private Bitmap player1PaddleImage;
    private Bitmap player2PaddleImage;
    private int backgroundColor;
    private int player1Score;
    private int player2Score;
    private RectF resetButton;
    private boolean gameOver;
    private long pointScoredTime;
    private boolean waitingForNewBall;

    public PongGame1v1(Context context) {
        super(context);
        paint = new Paint();
        gameStarted = false;
        gameOver = false;
        waitingForNewBall = false;
        player1Score = 0;
        player2Score = 0;

        player1PaddleImage = BitmapFactory.decodeResource(getResources(), R.drawable.paddle1);
        player2PaddleImage = BitmapFactory.decodeResource(getResources(), R.drawable.paddle2);

        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
        backgroundColor = typedValue.data;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;
        paddleWidth = screenWidth / 8;
        paddleHeight = screenHeight / 20;
        ballSize = screenWidth / 30;

        player1PaddleImage = resizeAndRotateBitmap(player1PaddleImage, paddleWidth, paddleHeight);
        player2PaddleImage = resizeAndRotateBitmap(player2PaddleImage, paddleWidth, paddleHeight);

        player1PaddleX = screenWidth / 2f - paddleWidth / 2f;
        player1PaddleY = screenHeight - paddleHeight * 2f;
        player2PaddleX = screenWidth / 2f - paddleWidth / 2f;
        player2PaddleY = paddleHeight;

        // Center the reset button
        int buttonWidth = screenWidth / 4;
        int buttonHeight = screenHeight / 15;
        resetButton = new RectF(
                screenWidth / 2f - buttonWidth / 2f,
                screenHeight / 2f - buttonHeight / 2f,
                screenWidth / 2f + buttonWidth / 2f,
                screenHeight / 2f + buttonHeight / 2f
        );

        resetGame();
    }

    private Bitmap resizeAndRotateBitmap(Bitmap original, int newWidth, int newHeight) {
        Bitmap resized = Bitmap.createScaledBitmap(original, newHeight, newWidth, true);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(resized, 0, 0, resized.getWidth(), resized.getHeight(), matrix, true);
    }

    private void resetBall() {
        ballX = screenWidth / 2f;
        ballY = screenHeight / 2f;
        ballSpeedX = (new Random().nextFloat() - 0.5f) * screenWidth / 100f;
        ballSpeedY = screenHeight / 100f;
        if (new Random().nextBoolean()) {
            ballSpeedY = -ballSpeedY;
        }
        waitingForNewBall = true;
        pointScoredTime = System.currentTimeMillis();
    }

    private void resetGame() {
        player1Score = 0;
        player2Score = 0;
        gameOver = false;
        resetBall();
        gameStartTime = System.currentTimeMillis();
        gameStarted = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(backgroundColor);

        canvas.drawBitmap(player1PaddleImage, player1PaddleX, player1PaddleY, paint);
        canvas.drawBitmap(player2PaddleImage, player2PaddleX, player2PaddleY, paint);

        paint.setColor(Color.WHITE);
        canvas.drawCircle(ballX, ballY, ballSize, paint);

        // Draw scores on the left side
        paint.setColor(Color.WHITE);
        paint.setTextSize(screenHeight / 30f);
        canvas.drawText(String.valueOf(player2Score), 40, screenHeight / 3f, paint);
        canvas.drawText(String.valueOf(player1Score), 40, 2 * screenHeight / 3f, paint);

        if (gameOver) {
            // Draw reset button
            paint.setColor(Color.LTGRAY);
            canvas.drawRoundRect(resetButton, 15, 15, paint);
            paint.setColor(Color.BLACK);
            paint.setTextSize(screenHeight / 30f);
            float textWidth = paint.measureText("Reset");
            float x = resetButton.left + (resetButton.width() - textWidth) / 2;
            float y = resetButton.top + resetButton.height() * 0.65f;
            canvas.drawText("Reset", x, y, paint);

            String winner = (player1Score > player2Score) ? "Player 1 Wins!" : "Player 2 Wins!";
            paint.setColor(Color.YELLOW);
            paint.setTextSize(screenHeight / 25f);
            textWidth = paint.measureText(winner);
            canvas.drawText(winner, (screenWidth - textWidth) / 2, screenHeight / 3f, paint);
        } else if (!gameStarted || waitingForNewBall) {
            long currentTime = System.currentTimeMillis();
            long relevantTime = waitingForNewBall ? pointScoredTime : gameStartTime;
            if (currentTime - relevantTime >= 3000) {
                gameStarted = true;
                waitingForNewBall = false;
            } else {
                String countdown = "Starting in " + (3 - (currentTime - relevantTime) / 1000);
                paint.setColor(Color.YELLOW);
                paint.setTextSize(screenHeight / 25f);
                float textWidth = paint.measureText(countdown);
                canvas.drawText(countdown, (screenWidth - textWidth) / 2, screenHeight / 2f, paint);
            }
        } else {
            ballX += ballSpeedX;
            ballY += ballSpeedY;

            if (ballX - ballSize < 0 || ballX + ballSize > screenWidth) {
                ballSpeedX = -ballSpeedX;
            }

            if (ballY + ballSize > player1PaddleY && ballY - ballSize < player1PaddleY + paddleHeight &&
                    ballX > player1PaddleX && ballX < player1PaddleX + paddleWidth) {
                ballSpeedY = -Math.abs(ballSpeedY);
                ballSpeedX += (ballX - (player1PaddleX + paddleWidth / 2)) / (paddleWidth / 2) * (screenWidth / 200f);
            }
            if (ballY - ballSize < player2PaddleY + paddleHeight && ballY + ballSize > player2PaddleY &&
                    ballX > player2PaddleX && ballX < player2PaddleX + paddleWidth) {
                ballSpeedY = Math.abs(ballSpeedY);
                ballSpeedX += (ballX - (player2PaddleX + paddleWidth / 2)) / (paddleWidth / 2) * (screenWidth / 200f);
            }

            if (ballY > screenHeight) {
                player2Score++;
                checkGameOver();
                if (!gameOver) resetBall();
            } else if (ballY < 0) {
                player1Score++;
                checkGameOver();
                if (!gameOver) resetBall();
            }
        }

        invalidate();
    }

    private void checkGameOver() {
        if (player1Score >= 3 || player2Score >= 3) {
            gameOver = true;
            gameStarted = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_MOVE:
                for (int i = 0; i < event.getPointerCount(); i++) {
                    float touchX = event.getX(i);
                    float touchY = event.getY(i);

                    if (gameOver && resetButton.contains(touchX, touchY)) {
                        resetGame();
                        return true;
                    }

                    if (touchY > screenHeight / 2) {
                        player1PaddleX = touchX - paddleWidth / 2f;
                        if (player1PaddleX < 0) player1PaddleX = 0;
                        if (player1PaddleX > screenWidth - paddleWidth) player1PaddleX = screenWidth - paddleWidth;
                    } else {
                        player2PaddleX = touchX - paddleWidth / 2f;
                        if (player2PaddleX < 0) player2PaddleX = 0;
                        if (player2PaddleX > screenWidth - paddleWidth) player2PaddleX = screenWidth - paddleWidth;
                    }
                }
                break;
        }
        return true;
    }
}