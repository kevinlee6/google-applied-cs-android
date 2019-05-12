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

package com.google.engedu.bstguesser;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class TreeNode {
    private static final int SIZE = 60;
    private static final int MARGIN = 20;
    private int value, height;
    protected TreeNode left, right;
    private boolean showValue;
    private int x, y;
    private int color = Color.rgb(150, 150, 250);

    public TreeNode(int value) {
        this.value = value;
        this.height = 0;
        showValue = false;
        left = null;
        right = null;
    }

    private int getHeight(TreeNode node) {
        return node == null ? 0 : node.height;
    }
    private int calcNewHeight(TreeNode node) {
        if (node == null ||
            (node.left == null && node.right == null)) {
            return 0;
        }
        int lHeight = getHeight(node.left);
        int rHeight = getHeight(node.right);
        return Math.max(lHeight, rHeight) + 1;
    }
    private int getBalanceFactor(TreeNode node) {
        return node == null ?
                0 :
                getHeight(node.left) - getHeight(node.right);
    }
    private TreeNode rotateRight(TreeNode node) {
       TreeNode l = node.left;
       TreeNode lr = l.right;

       l.right = node;
       node.left = lr;

       node.height = calcNewHeight(node);
       l.height = calcNewHeight(l);

       return l;
    }
    private TreeNode rotateLeft(TreeNode node) {
        TreeNode r = node.right;
        TreeNode rl = r.left;

        node.right = rl;
        r.left= node;

        node.height = calcNewHeight(node);
        r.height = calcNewHeight(r);

        return r;
    }
    public TreeNode insert(int valueToInsert) {
        if (getValue() > valueToInsert) {
            if (left == null) {
                left = new TreeNode(valueToInsert);
            } else {
                left = left.insert(valueToInsert);
            }
        } else {
            if (right == null) {
                right = new TreeNode(valueToInsert);
            } else {
                right = right.insert(valueToInsert);
            }
        }

        height = calcNewHeight(this);

        int balanceFactor = getBalanceFactor(this);
        if (balanceFactor > 1) {
            if (valueToInsert > left.getValue()) {
                left = rotateLeft(left);
            }
            return rotateRight(this);
        }
        if (balanceFactor < -1) {
            if (valueToInsert < right.getValue()) {
                right = rotateRight(right);
            }
            return rotateLeft(this);
        }

        return this;
    }

    public int getValue() {
        return value;
    }

    public void positionSelf(int x0, int x1, int y) {
        this.y = y;
        x = (x0 + x1) / 2;

        if(left != null) {
            left.positionSelf(x0, right == null ? x1 - 2 * MARGIN : x, y + SIZE + MARGIN);
        }
        if (right != null) {
            right.positionSelf(left == null ? x0 + 2 * MARGIN : x, x1, y + SIZE + MARGIN);
        }
    }

    public void draw(Canvas c) {
        Paint linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(3);
        linePaint.setColor(Color.GRAY);
        if (left != null)
            c.drawLine(x, y + SIZE/2, left.x, left.y + SIZE/2, linePaint);
        if (right != null)
            c.drawLine(x, y + SIZE/2, right.x, right.y + SIZE/2, linePaint);

        Paint fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(color);
        c.drawRect(x-SIZE/2, y, x+SIZE/2, y+SIZE, fillPaint);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(SIZE * 2/3);
        paint.setTextAlign(Paint.Align.CENTER);
        c.drawText(showValue ? String.valueOf(value) : "?", x, y + SIZE * 3/4, paint);

        if (height > 0) {
            Paint heightPaint = new Paint();
            heightPaint.setColor(Color.MAGENTA);
            heightPaint.setTextSize(SIZE * 2 / 3);
            heightPaint.setTextAlign(Paint.Align.LEFT);
            c.drawText(String.valueOf(height), x + SIZE / 2 + 10, y + SIZE * 3 / 4, heightPaint);
        }

        if (left != null)
            left.draw(c);
        if (right != null)
            right.draw(c);
    }

    public int click(float clickX, float clickY, int target) {
        int hit = -1;
        if (Math.abs(x - clickX) <= (SIZE / 2) && y <= clickY && clickY <= y + SIZE) {
            if (!showValue) {
                if (target != value) {
                    color = Color.RED;
                } else {
                    color = Color.GREEN;
                }
            }
            showValue = true;
            hit = value;
        }
        if (left != null && hit == -1)
            hit = left.click(clickX, clickY, target);
        if (right != null && hit == -1)
            hit = right.click(clickX, clickY, target);
        return hit;
    }

    public void invalidate() {
        color = Color.CYAN;
        showValue = true;
    }
}
