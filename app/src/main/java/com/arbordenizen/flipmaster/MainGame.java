package com.arbordenizen.flipmaster;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class MainGame extends AppCompatActivity {

    //Class used to store word pairs.
    public class WordPair {
        private String frontWord;
        private String backWord;
    }

    public ViewGroup relLayout;

    //ArrayList of WordPairs used to store words read from .flip file
    private ArrayList<WordPair> words = new ArrayList<WordPair> ();
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_main);

        relLayout = (ViewGroup) findViewById(R.id.game_main);

        readFlipFile();

        if (!words.isEmpty()) {
            //Randomize the 'pack'
            Collections.shuffle(words);
            //Set up the cards and start the game
            setCardWords();
        } else {
            TextView gameInfoText = (TextView) findViewById(R.id.gameInfoTxt);
            gameInfoText.setVisibility(View.VISIBLE);
            gameInfoText.setText("Couldn't find any correctly formatted word pairs in .flip file");
        }
    }

    //Read selected file from preferences, build list of words
    private void readFlipFile() {
        SharedPreferences settings = getSharedPreferences(FileOperations.PREFS_NAME, 0);
        String selectedFile = settings.getString("selectedFile", null);

        File flipFile = new File(FileOperations.flipMasterDir.getAbsolutePath() + "/" + selectedFile);

        if (flipFile.exists()) {
            try {
                // Open the file
                FileInputStream fstream = null;
                fstream = new FileInputStream(flipFile);
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

                String strLine;

                //Read File Line By Line
                while ((strLine = br.readLine()) != null)   {
                    // "Hello/Hola"
                    if (strLine.matches(".+/.+")) {
                        WordPair wordPair = new WordPair();
                        wordPair.frontWord = strLine.split("/")[0];
                        wordPair.backWord = strLine.split("/")[1];

                        words.add(wordPair);
                    }
                }

                //Close the input stream
                br.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //on card front clicked
    public void flipCard(final View view) {
        Animation flip = AnimationUtils.loadAnimation(this, R.anim.flip_in);
        flip.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
                view.setEnabled(false);
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            public void onAnimationEnd(Animation arg0) {
                view.setVisibility(View.INVISIBLE);
                reverseCard();
            }
        });
        view.startAnimation(flip);
    }

    public void reverseCard() {
        final View card_back = findViewById(R.id.flipCardBack);
        Animation flip = AnimationUtils.loadAnimation(this, R.anim.flip_out);
        flip.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
                card_back.setEnabled(false);
                card_back.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            public void onAnimationEnd(Animation arg0) {
                card_back.setEnabled(true);

            }
        });
        card_back.startAnimation(flip);

    }

    //on card back clicked
    public void newCard(View view) {
        updateScore();

        if (score >= words.size()) {
            //No more cards
            Button card_back = (Button) view;
            Button card_front = (Button) findViewById(R.id.flipCard);

            //Hide both card buttons
            card_back.setVisibility(View.INVISIBLE);
            card_back.setEnabled(false);

            card_front.setEnabled(false);
            card_front.setVisibility(View.INVISIBLE);

            //Display reset button and text
            final Button resetBtn = (Button) findViewById(R.id.resetBtn);
            final TextView resetTxt = (TextView) findViewById(R.id.gameInfoTxt);

            resetBtn.setVisibility(View.VISIBLE);
            resetBtn.setEnabled(true);
            resetTxt.setVisibility(View.VISIBLE);

            resetBtn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //On reset button clicked hide reset button and text and reset game
                    resetBtn.setVisibility(View.INVISIBLE);
                    resetBtn.setEnabled(false);
                    resetTxt.setVisibility(View.INVISIBLE);
                    resetGame();
                }
            });
        } else {
            //display a new card
            setCardWords();
        }

    }

    private void resetGame() {
        score = 0;
        setScoreText();

        Collections.shuffle(words);
        setCardWords();
    }

    public void updateScore() {
        score++;
        setScoreText();
    }

    private void setScoreText() {
        TextView score_counter = (TextView) findViewById(R.id.scoreCounter);
        score_counter.setText("SCORE : " + score);
    }

    public void setCardWords() {
        Button card_back = (Button) findViewById(R.id.flipCardBack);
        Button card_front = (Button) findViewById(R.id.flipCard);

        //Get new word pair from the list
        WordPair newWordPair = words.get(score);

        card_front.setText(newWordPair.frontWord);
        card_back.setText(newWordPair.backWord);

        card_back.setVisibility(View.INVISIBLE);
        card_back.setEnabled(false);

        card_front.setEnabled(true);
        card_front.setVisibility(View.VISIBLE);

    }
}
