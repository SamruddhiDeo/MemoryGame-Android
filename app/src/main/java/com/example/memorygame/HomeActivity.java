package com.example.memorygame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class HomeActivity extends AppCompatActivity {
    //Initializations
    TextView moves, time, highestMoves, highestTime;
    ImageView pauseBtn;
    AppCompatButton btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn10, btn11, btn12, openBtn1, openBtn2;
    String[] arrBtnImages = new String[12];    //contains images of cards in order
    String[] openCards = new String[2];
    ArrayList<String> cardsDisableListeners = new ArrayList<>();
    boolean stopCounting = false;
    private int count = 0, movesCount = 0, seconds = 0, minutes = 0, noOfMatches = 0, secondsToShow = 0;
    private int openBtnId1, openBtnId2;
    private int highestMovesStored = 0, highestTimeStored = 0;
    private boolean isHighScoreBroke;
    private int highestMinutesStored = 0, highestSecondsStoredToShow;
    Boolean removeListener = false;
    Intent iMain, iHome;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Declarations
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn7 = findViewById(R.id.btn7);
        btn8 = findViewById(R.id.btn8);
        btn9 = findViewById(R.id.btn9);
        btn10 = findViewById(R.id.btn10);
        btn11 = findViewById(R.id.btn11);
        btn12 = findViewById(R.id.btn12);
        pauseBtn = findViewById(R.id.pauseBtn);
        moves = findViewById(R.id.moves);
        time = findViewById(R.id.time);
        highestMoves = findViewById(R.id.highestMoves);
        highestTime = findViewById(R.id.highestTime);

        //Intent to go to home
        iMain = new Intent(HomeActivity.this, MainActivity.class);
        iHome = new Intent(getIntent());
        iMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        iHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);

        //to set highest score
        setHighestScore();

        //set background images of cards randomly
        ArrayList<String> images = new ArrayList<>(Arrays.asList("img1", "img1", "img2", "img2", "img3", "img3", "img4", "img4", "img5", "img5", "img6", "img6"));
        int min = 0, max = 11, random;
        for (int i = 0; i < arrBtnImages.length; i++) {
            random = (int) (Math.random() * ((max - min) + 1));
            arrBtnImages[i] = images.get(random);
            images.remove(random);
            max--;
        }

        //function for pause button
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseBtn.setBackgroundResource(R.drawable.baseline_play_circle_24);
                stopCounting = true;
                openPauseDialog();
            }
        });
    }

    //overriding on back pressed method
    @Override
    public void onBackPressed() {
        openPauseDialog();
    }

    //function for card click
    public void btnClick(View v) {
        if (count == 0) {
            startTimer();  //count 0 means clicked first time
        }
        if ((!removeListener) && (!cardsDisableListeners.contains(v.getTag()))) {
            AppCompatButton btn = (AppCompatButton) v;
            //change background
            btn.setText("");
            String btnId = (String) v.getTag();
            int idIndexSubstring = Integer.parseInt(String.valueOf(btnId).substring(3));
            int idIndex = idIndexSubstring - 1;
            int resId = getResources().getIdentifier(arrBtnImages[idIndex], "drawable", getPackageName());
            btn.setBackground(getResources().getDrawable(resId, null));

            //check match
            count++;
            if (count % 2 != 0) {
                openCards[0] = btnId;
            } else {
                if (openCards[0] == btnId) {
                    count--;
                } else {
                    openCards[1] = btnId;
                    removeListener = true;
                    incrementMoves();
                    checkCardMatch();
                }
            }
        }
    }

    //function to check card match or not
    private void checkCardMatch() {
        if (!(arrBtnImages[Integer.parseInt(openCards[0].substring(3)) - 1].equals(arrBtnImages[Integer.parseInt(openCards[1].substring(3)) - 1]))) {
            //to set background back to pink if not matched
            openBtnId1 = getResources().getIdentifier(openCards[0], "id", getPackageName());
            openBtnId2 = getResources().getIdentifier(openCards[1], "id", getPackageName());
            openBtn1 = findViewById(openBtnId1);
            openBtn2 = findViewById(openBtnId2);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openBtn1.setBackgroundResource(R.drawable.custom_btn);
                    openBtn2.setBackgroundResource(R.drawable.custom_btn);
                    removeListener = false;
                }
            }, 1300);
        } else {
            cardsDisableListeners.add(openCards[0]);
            cardsDisableListeners.add(openCards[1]);
            noOfMatches++;   //if match
            removeListener = false;
        }

        //check win or not
        if (noOfMatches == 6) {
            removeListener = true;
            isHighScoreBroke = updateHighestScore();
            stopCounting = true;
            openWinDialog();
        }
    }

    //function to increment moves count
    private void incrementMoves() {
        if (!stopCounting) {
            movesCount++;
            moves.setText("Moves : " + movesCount);
        }
    }

    //function for time increasing
    private void startTimer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!stopCounting) {
                    seconds++;
                }
                updateTimer();
                startTimer();
            }
        }, 1000);
    }

    private void updateTimer() {
        if (seconds >= 60) {
            minutes = seconds / 60;
            secondsToShow = seconds % 60;
            if ((seconds > minutes * 60) && (seconds < minutes * 60 + 10) || (seconds % 60 == 0)) {
                time.setText("Time : " + minutes + ":0" + secondsToShow);
            } else {
                time.setText("Time : " + minutes + ":" + secondsToShow);
            }
        } else {
            if (seconds < 10) {
                time.setText("Time : " + minutes + ":0" + seconds);
            } else {
                time.setText("Time : " + minutes + ":" + seconds);
            }
        }
    }

    private void openWinDialog() {
        Dialog winDialog = new Dialog(this);
        winDialog.setContentView(R.layout.custom_win_dialog);
        winDialog.setCanceledOnTouchOutside(false);

        Window window = winDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        TextView winDialogMoves = winDialog.findViewById(R.id.winDialogMoves);
        TextView winDialogTime = winDialog.findViewById(R.id.winDialogTime);
        ImageView winDialogRestart = winDialog.findViewById(R.id.winDialogRestart);
        ImageView winDialogHome = winDialog.findViewById(R.id.winDialogHome);
        TextView newHighScore = winDialog.findViewById(R.id.newHighScore);

        if (!isHighScoreBroke) {
            newHighScore.setVisibility(View.INVISIBLE);
        } else {
            newHighScore.setVisibility(View.VISIBLE);
        }

        winDialogMoves.setText(moves.getText());
        winDialogTime.setText(time.getText());

        winDialogRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                winDialog.dismiss();
                startActivity(iHome);
                restartGame();
            }
        });

        winDialogHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(iMain);
                restartGame();
            }
        });

        winDialog.show();
    }


    private void openPauseDialog() {
        Dialog pauseDialog = new Dialog(this);
        pauseDialog.setContentView(R.layout.custom_pause_dialog);
        pauseDialog.setCanceledOnTouchOutside(false);

        Window window = pauseDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        AppCompatButton resumeBtn = pauseDialog.findViewById(R.id.resumeBtn);
        AppCompatButton restartBtn = pauseDialog.findViewById(R.id.restartBtn);
        AppCompatButton quitBtn = pauseDialog.findViewById(R.id.quitBtn);

        resumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseDialog.dismiss();
                pauseBtn.setBackgroundResource(R.drawable.baseline_pause_circle_24);
                stopCounting = false;
            }
        });

        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(iHome);
                restartGame();
            }
        });

        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(iMain);
                restartGame();
            }
        });

        pauseDialog.show();
    }

    private void restartGame() {
        isHighScoreBroke = false;
        count = 0;
        noOfMatches = 0;
        seconds = 0;
        minutes = 0;
        movesCount = 0;
        removeListener = false;
        cardsDisableListeners.clear();
    }

    private void setHighestScore() {
        highestSecondsStoredToShow = 0;
        preferences = getSharedPreferences("HighestScore", MODE_PRIVATE);

        highestMovesStored = preferences.getInt("highest moves", 0);
        highestTimeStored = preferences.getInt("highest time", 0);
        highestMinutesStored = highestTimeStored / 60;
        highestSecondsStoredToShow = highestTimeStored % 60;
        highestMoves.setText("Moves : " + highestMovesStored);
        if ((highestTimeStored > highestMinutesStored * 60) && (highestTimeStored < highestMinutesStored * 60 + 10) || highestTimeStored % 60 == 0) {
            highestTime.setText("Time : " + highestMinutesStored + ":0" + highestSecondsStoredToShow);
        } else {
            highestTime.setText("Time : " + highestMinutesStored + ":" + highestSecondsStoredToShow);
        }
    }

    private boolean updateHighestScore() {
        preferences = getSharedPreferences("HighestScore", MODE_PRIVATE);
        if ((movesCount <= highestMovesStored) || (seconds <= highestTimeStored) || ((highestTimeStored == 0) && (highestMovesStored == 0))) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("highest moves", movesCount);
            editor.putInt("highest time", seconds);
            editor.apply();
            return true;
        }
        return false;
    }

}