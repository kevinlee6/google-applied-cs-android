/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.wordstack;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private static final int WORD_LENGTH = 5;
    public static final int LIGHT_BLUE = Color.rgb(176, 200, 255);
    public static final int LIGHT_GREEN = Color.rgb(200, 255, 200);
    private ArrayList<String> words = new ArrayList<>();
    private Random random = new Random();
    private StackedLayout stackedLayout;
    private Stack<LetterTile> placedTiles = new Stack<>();
    private String word1, word2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while((line = in.readLine()) != null) {
                String word = line.trim();
                if (word.length() == WORD_LENGTH) {
                    words.add(word);
                }
            }
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
        LinearLayout verticalLayout = (LinearLayout) findViewById(R.id.vertical_layout);
        stackedLayout = new StackedLayout(this);
        verticalLayout.addView(stackedLayout, 3);

        View word1LinearLayout = findViewById(R.id.word1);
        word1LinearLayout.setOnTouchListener(new TouchListener());
        word1LinearLayout.setOnDragListener(new DragListener());
        View word2LinearLayout = findViewById(R.id.word2);
        word2LinearLayout.setOnTouchListener(new TouchListener());
        word2LinearLayout.setOnDragListener(new DragListener());
    }

    private class TouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !stackedLayout.empty()) {
                LetterTile tile = (LetterTile) stackedLayout.peek();
                tile.moveToViewGroup((ViewGroup) v);
                if (stackedLayout.empty()) {
                    TextView messageBox = (TextView) findViewById(R.id.message_box);
                    messageBox.setText(word1 + " " + word2);
                }
                placedTiles.push(tile);
                return true;
            }
            return false;
        }
    }

    private class DragListener implements View.OnDragListener {

        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(LIGHT_GREEN);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundColor(Color.WHITE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign Tile to the target Layout
                    LetterTile tile = (LetterTile) event.getLocalState();
                    tile.moveToViewGroup((ViewGroup) v);
                    placedTiles.push(tile);

                    if (stackedLayout.empty()) {
                        TextView messageBox = (TextView) findViewById(R.id.message_box);
                        messageBox.setText(word1 + " " + word2);
                    }

                    return true;
            }
            return false;
        }
    }

    private String shuffleCombinedWord(String a, String b) {
        int totalLength = a.length() + b.length();
        StringBuilder res = new StringBuilder(totalLength);

        int pointerA = 0, pointerB = 0;
        while (pointerA < WORD_LENGTH && pointerB < WORD_LENGTH) {
            int randomInt = random.nextInt(2);
            if (randomInt == 0) {
                res.append(a.charAt(pointerA));
                pointerA++;
            } else {
                res.append(b.charAt(pointerB));
                pointerB++;
            }
        }

        res.append(a.substring(pointerA));
        res.append(b.substring(pointerB));

        return res.toString();
    }

    private int selectRandomInt(int offset, int size) {
        int randInt = random.nextInt(size);
        return (randInt + offset) % size;
    }

    private void pushToStackedLayout(String word) {
        Context context = getApplicationContext();
        for (int i = word.length()-1; i > -1; i--) {
            LetterTile tile = new LetterTile(context, word.charAt(i));
            stackedLayout.push(tile);
        }
    }

    private void cleanUpOnStartGame() {
        LinearLayout word1LinearLayout = findViewById(R.id.word1);
        LinearLayout word2LinearLayout = findViewById(R.id.word2);
        word1LinearLayout.removeAllViews();
        word2LinearLayout.removeAllViews();
        stackedLayout.clear();
        stackedLayout.removeAllViews();
        placedTiles.clear();
    }

    public boolean onStartGame(View view) {
        cleanUpOnStartGame();
        TextView messageBox = (TextView) findViewById(R.id.message_box);
        messageBox.setText("Game started");

        int randInt1 = selectRandomInt(0, words.size());
        int randInt2 = selectRandomInt(randInt1, words.size() - 1);

        word1 = words.get(randInt1);
        word2 = words.get(randInt2);

        String shuffledCombinedWord = shuffleCombinedWord(word1, word2);
        pushToStackedLayout(shuffledCombinedWord);

        messageBox.setText(shuffledCombinedWord);

        return true;
    }

    public boolean onUndo(View view) {
        if (!placedTiles.empty()) {
            LetterTile popped = placedTiles.pop();
            popped.moveToViewGroup(stackedLayout);
        }
        return true;
    }
}
