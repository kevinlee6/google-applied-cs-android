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

package com.google.engedu.anagrams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private Random random = new Random();
    private List<String> wordList = new ArrayList<>();
    private Set<String> wordSet = new HashSet<>();
    private Map<String, ArrayList<String>> lettersToWords = new HashMap<>();

    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            String sortedWord = sortString(word);
            if (lettersToWords.get(sortedWord) != null) {
                lettersToWords.get(sortedWord).add(word);
            } else {
                ArrayList<String> arr = new ArrayList<String>();
                arr.add(word);
                lettersToWords.put(sortedWord, arr);
            }
            wordSet.add(word);
            wordList.add(word);
        }
    }

    private boolean isAnagram(String word, String base) {
        // INCOMPLETE
        Map<Character, Integer> counter = new HashMap<>();

        // Build counter
        for (char ch : base.toCharArray()) {
            int prev = counter.getOrDefault(ch, 0);
            counter.put(ch, prev + 1);
        }

        for (char ch : word.toCharArray()) {
            int val = counter.getOrDefault(ch, -1);
            if (val == 0) {
                return false;
            } else {
                counter.put(ch, val - 1);
            }
            return true;
        }

        return false;
    }

    public boolean isGoodWord(String word, String base) {
        // Early return if not valid word to begin with
        return wordSet.contains(word) && isAnagram(word, base) && !word.contains(base);
    }

    private String sortString(String inp) {
        // Helper method to sort strings
        char[] sortedInpArr = inp.toCharArray();
        Arrays.sort(sortedInpArr);
        return new String(sortedInpArr);
    }

    public List<String> getAnagrams(String targetWord) {
        String sortedTarget = sortString(targetWord);
        // TODO: Need to account for targetWord being anagram of itself
        if (lettersToWords.get(sortedTarget) != null) {
            return lettersToWords.get(sortedTarget);
        }

        ArrayList<String> result = new ArrayList<String>();

        for (String word : wordSet) {
            String sortedWord = sortString(word);
            if (isGoodWord(word, targetWord) && sortedWord.equals(sortedTarget)) {
                result.add(word);
            }
        }

        return result;
    }

    public List<String> getAnagramsWithOneMoreLetter(String word) {
        // generate a-z
        char[] charArr = new char[26];
        for (int i = 0; i < 26; i++) {
            charArr[i] = (char)(i + 97);
        }

        List<String> result = new ArrayList<String>();
        for (char ch : charArr) {
            List anagrams = getAnagrams(word + ch);
            result.addAll(anagrams);
        }

        result.addAll(getAnagrams(word));
        return result;
    }

    public String pickGoodStarterWord() {
        int size = wordList.size();
        int randomInt = (int)(Math.random() * size);
        for (int i = randomInt; i < randomInt + size; i++) {
            String word = wordList.get(i % size);
            String sorted = sortString(word);
            if (lettersToWords.get(sorted).size() >= MIN_NUM_ANAGRAMS) {
                return word;
            }
        }
        throw new Error();
    }
}
