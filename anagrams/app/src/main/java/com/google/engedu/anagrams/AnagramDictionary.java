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
    private Map<String, Set<String>> lettersToWords = new HashMap<>();

    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (wordSet.contains(word)) continue;

            String sortedWord = sortString(word);
            Set<String> sortedSet = lettersToWords.getOrDefault(sortedWord, new HashSet<String>());

            sortedSet.add(word);
            lettersToWords.put(sortedWord, sortedSet);

            wordSet.add(word);
            wordList.add(word);
        }
    }

    private String sortString(String inp) {
        // Helper method to sort strings
        char[] sortedInpArr = inp.toCharArray();
        Arrays.sort(sortedInpArr);
        return new String(sortedInpArr);
    }

//    private boolean isAnagram(String word, String base) {
//        // INCOMPLETE
//        Map<Character, Integer> counter = new HashMap<>();
//
//        // Build counter
//        for (char ch : base.toCharArray()) {
//            int prev = counter.getOrDefault(ch, 0);
//            counter.put(ch, prev + 1);
//        }
//
//        for (char ch : word.toCharArray()) {
//            int val = counter.getOrDefault(ch, -1);
//            if (val == 0) {
//                return false;
//            } else {
//                counter.put(ch, val - 1);
//            }
//            return true;
//        }
//
//        return false;
//    }

    public boolean isGoodWord(String word, String base) {
        return wordSet.contains(word) && !word.contains(base);
    }


    public List<String> getAnagrams(String targetWord) {
        String sortedTarget = sortString(targetWord);
        Set<String> anagramSet = lettersToWords.get(sortedTarget);

        // A word is considered anagram of itself, so need to exclude it
        // Preallocate space for list for potential savings
        // Emulate Set difference manually
        int LIST_SIZE = anagramSet.size();
        List<String> res = new ArrayList<>(LIST_SIZE - 1);

        for (String word : anagramSet) {
            if (!word.equals(targetWord)) res.add(word);
        }

        return res;
    }

    public List<String> getAnagramsWithOneMoreLetter(String word) {
        // generate a-z
        char[] charArr = new char[26];
        for (int i = 0; i < 26; i++) {
            charArr[i] = (char)(i + 97);
        }

        List<String> result = new ArrayList<>();
        for (char ch : charArr) {
            List anagrams = getAnagrams(word + ch);
            result.addAll(anagrams);
        }

        result.addAll(getAnagrams(word));
        return result;
    }

    public String pickGoodStarterWord() {
        int size = wordList.size();
        int randInt = random.nextInt(size);
        for (int i = randInt; i < randInt + size; i++) {
            String word = wordList.get(i % size);
            String sorted = sortString(word);
            if (lettersToWords.get(sorted).size() > MIN_NUM_ANAGRAMS) {
                return word;
            }
        }

        // If code enters here, then there are no words which satisfy MIN_NUM_ANAGRAMS
        // TODO: Throw exception instead and handle error
        throw new Error();
    }
}
