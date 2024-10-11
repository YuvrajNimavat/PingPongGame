package com.example.mypingpong;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import java.util.Random;

public class PongGame extends View {
    private int screenWidth;
    private int screenHeight;
    private int paddleWidth;
    private int paddleHeight;
    private float userPaddleX;
    private float userPaddleY;
    private float computerPaddleX;
    private float computerPaddleY;
    private float ballX;
    private float ballY;
    private float ballSpeedX;
    private float ballSpeedY;
    private int ballSize;
    private Paint paint;
    private boolean gameStarted;
    private long gameStartTime;
    private float computerPaddleSpeed;
    private Bitmap userPaddleImage;
    private Bitmap computerPaddleImage;
    private int backgroundColor;

    public PongGame(Context context) {
        super(context);
        paint = new Paint();
        gameStarted = false;

        // Load paddle images
        userPaddleImage = BitmapFactory.decodeResource(getResources(), R.drawable.paddle1);
        computerPaddleImage = BitmapFactory.decodeResource(getResources(), R.drawable.paddle2);

        // Get the primary color from the theme
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

        // Resize and rotate paddle images
        userPaddleImage = resizeAndRotateBitmap(userPaddleImage, paddleWidth, paddleHeight);
        computerPaddleImage = resizeAndRotateBitmap(computerPaddleImage, paddleWidth, paddleHeight);

        userPaddleX = screenWidth / 2f - paddleWidth / 2f;
        userPaddleY = screenHeight - paddleHeight * 2f;
        computerPaddleX = screenWidth / 2f - paddleWidth / 2f;
        computerPaddleY = paddleHeight;

        computerPaddleSpeed = screenWidth / 150f; // Adjust this value to change AI difficulty

        resetBall();
        gameStartTime = System.currentTimeMillis();
    }

    private Bitmap resizeAndRotateBitmap(Bitmap original, int newWidth, int newHeight) {
        // Resize
        Bitmap resized = Bitmap.createScaledBitmap(original, newHeight, newWidth, true);

        // Rotate
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(resized, 0, 0, resized.getWidth(), resized.getHeight(), matrix, true);
    }

    private void resetBall() {
        ballX = screenWidth / 2f;
        ballY = screenHeight / 2f;
        ballSpeedX = (new Random().nextFloat() - 0.5f) * screenWidth / 100f;
        ballSpeedY = screenHeight / 100f;

        // Ensure the ball always starts moving downwards
        if (ballSpeedY < 0) {
            ballSpeedY = -ballSpeedY;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw background with primary color
        canvas.drawColor(backgroundColor);

        // Draw paddles using rotated images
        canvas.drawBitmap(userPaddleImage, userPaddleX, userPaddleY, paint);
        canvas.drawBitmap(computerPaddleImage, computerPaddleX, computerPaddleY, paint);

        // Draw ball
        paint.setColor(Color.WHITE);
        canvas.drawCircle(ballX, ballY, ballSize, paint);

        if (!gameStarted) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - gameStartTime >= 3000) {
                gameStarted = true;
            }
        } else {
            // Move ball
            ballX += ballSpeedX;
            ballY += ballSpeedY;

            // Check for collisions with walls
            if (ballX - ballSize < 0 || ballX + ballSize > screenWidth) {
                ballSpeedX = -ballSpeedX;
            }

            // Check for collisions with paddles
            if (ballY + ballSize > userPaddleY && ballY - ballSize < userPaddleY + paddleHeight &&
                    ballX > userPaddleX && ballX < userPaddleX + paddleWidth) {
                ballSpeedY = -Math.abs(ballSpeedY); // Ensure the ball bounces upward
                ballSpeedX += (ballX - (userPaddleX + paddleWidth / 2)) / (paddleWidth / 2) * (screenWidth / 200f);
            }
            if (ballY - ballSize < computerPaddleY + paddleHeight && ballY + ballSize > computerPaddleY &&
                    ballX > computerPaddleX && ballX < computerPaddleX + paddleWidth) {
                ballSpeedY = Math.abs(ballSpeedY); // Ensure the ball bounces downward
                ballSpeedX += (ballX - (computerPaddleX + paddleWidth / 2)) / (paddleWidth / 2) * (screenWidth / 200f);
            }

            // Move computer paddle
            float targetX = ballX - paddleWidth / 2f;
            if (computerPaddleX < targetX) {
                computerPaddleX += Math.min(computerPaddleSpeed, targetX - computerPaddleX);
            } else if (computerPaddleX > targetX) {
                computerPaddleX -= Math.min(computerPaddleSpeed, computerPaddleX - targetX);
            }
            if (computerPaddleX < 0) computerPaddleX = 0;
            if (computerPaddleX > screenWidth - paddleWidth) computerPaddleX = screenWidth - paddleWidth;

            // Check for game over
            if (ballY > screenHeight || ballY < 0) {
                gameStarted = false;
                resetBall();
                gameStartTime = System.currentTimeMillis();
            }
        }

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            userPaddleX = event.getX() - paddleWidth / 2f;
            if (userPaddleX < 0) userPaddleX = 0;
            if (userPaddleX > screenWidth - paddleWidth) userPaddleX = screenWidth - paddleWidth;
        }
        return true;
    }
}