package com.example.scarnesdice;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private Button rollButton;
    private Button holdButton;
    private Button resetButton;
    private Button[] buttons;
    private ImageView diceFace;

    private TextView humanTotalScoreTV;
    private TextView cpuTotalScoreTV;
    private TextView turnScoreTV;

    private int userTotalScore;
    private int userTurnScore;
    private int cpuTotalScore;
    private int cpuTurnScore;
    private boolean isPlayerTurn = true;
    private boolean gameOver = false;
    private final int CPU_HOLD_SCORE = 15;
    private final Random random = new Random();
    private final Handler handler = new Handler();
    private final Runnable r = new Runnable() {
        @Override
        public void run() {
            int diceRes = rollDice();

            if (diceRes == 1) {
                isPlayerTurn = true;
                turnScoreTV.setText("CPU turn over.\nPlayer's turn");
            } else if (cpuTurnScore >= CPU_HOLD_SCORE) {
                turnScoreTV.setText("CPU held for " + cpuTurnScore + " points.\nPlayer's turn");
                holdScore();
                isPlayerTurn = true;
            }

            if (gameOver) { return; }

            if (isPlayerTurn) {
                enableButtons();
            } else {
                handler.postDelayed(this, 1000);
            }
        }
    };

    private void setDiceFace(int num) {
        int id = getResources().getIdentifier(
                    "dice" + num,
                    "drawable",
                    getPackageName()
            );

        diceFace.setImageResource(id);
    }

    private int rollDice() {
        int randInt = random.nextInt(6) + 1;
        setDiceFace(randInt);

        boolean isRollOne = randInt == 1;

        if (isPlayerTurn) {
            userTurnScore = isRollOne ? 0 : userTurnScore + randInt;
            turnScoreTV.setText("User Turn Score: " + userTurnScore);
        } else {
            cpuTurnScore = isRollOne ? 0 : cpuTurnScore + randInt;
            turnScoreTV.setText("CPU Turn Score: " + cpuTurnScore);
        }

        return randInt;
    }

    private void disableButtons() {
        disableButtons(true);
    }

    private void disableButtons(boolean includeReset) {
        for (Button button : buttons) {
            button.setEnabled(false);
        }
        if (!includeReset) {
            resetButton.setEnabled(true);
        }
    }

    private void enableButtons() {
        for (Button button : buttons) {
            button.setEnabled(true);
        }
    }

    private void checkWin() {
        if (cpuTotalScore >= 100 || userTotalScore >= 100) {
            if (cpuTotalScore >= 100) {
                turnScoreTV.setText("CPU won");
            } else {
                turnScoreTV.setText("You won!");
            }

            gameOver = true;
            disableButtons(false);
        }
    }

    private void holdScore() {
        if (isPlayerTurn) {
            userTotalScore += userTurnScore;
            userTurnScore = 0;
            humanTotalScoreTV.setText(userTotalScore + "");
        } else {
            cpuTotalScore += cpuTurnScore;
            cpuTurnScore = 0;
            cpuTotalScoreTV.setText(cpuTotalScore + "");
        }

        checkWin();
    }

    private void resetGame() {
        userTotalScore = userTotalScore = cpuTurnScore = cpuTotalScore = 0;
        isPlayerTurn = true;
        gameOver = false;

        humanTotalScoreTV.setText("0");
        cpuTotalScoreTV.setText("0");
        turnScoreTV.setText("");

        enableButtons();
    }

    private void transitionToCpu() {
        holdScore();
        if (!gameOver) {
            isPlayerTurn = false;
            disableButtons();
            handler.post(r);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rollButton = findViewById(R.id.rollButton);
        holdButton = findViewById(R.id.holdButton);
        resetButton = findViewById(R.id.resetButton);
        buttons = new Button[]{rollButton, holdButton, resetButton};
        diceFace = findViewById(R.id.diceImageView);

        humanTotalScoreTV = findViewById(R.id.humanScoreTV);
        cpuTotalScoreTV = findViewById(R.id.cpuScoreTV);
        turnScoreTV = findViewById(R.id.turnScoreTV);

        rollButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int res = rollDice();
                if (res == 1) {
                    userTurnScore = 0;
                    turnScoreTV.setText("LOST TURN. You rolled a 1.");
                    disableButtons();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            transitionToCpu();
                        }
                    }, 1000);
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });

        holdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transitionToCpu();
            }
        });

    }
}
