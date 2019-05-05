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

package com.google.engedu.touringmusician;


import android.graphics.Point;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CircularLinkedList implements Iterable<Point> {

    private class Node {
        Point point;
        Node prev, next;

        Node(Point p) {
            point = p;
            prev = this;
            next = this;
        }
    }

    Node head;

    private void insertNode(Node toInsert, Node node) {
        Node tail = node.prev;

        toInsert.next = node;
        toInsert.prev = tail;

        tail.next = toInsert;
        node.prev = toInsert;
    }

    public void insertBeginning(Point p) {
        Node node = new Node(p);

        if (head != null) {
            insertNode(node, head);
        }

        head = node;
    }

    private float distanceBetween(Point from, Point to) {
        return (float) Math.sqrt(Math.pow(from.y-to.y, 2) + Math.pow(from.x-to.x, 2));
    }

    public float totalDistance() {
        float total = 0;
        Node temp = head.next;

        while (head != temp) {
            total += distanceBetween(temp.point, temp.next.point);
            temp = temp.next;
        }

        return total;
    }

    public void insertNearest(Point p) {
        Node toInsert = new Node(p);
        if (head == null) {
            head = toInsert;
            return;
        }

        float minDistance = Float.MAX_VALUE;
        Node nearest = head;
        Node temp = head.next;

        while (temp != head) {
            float distance = distanceBetween(p, temp.point);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = temp;
            }
            temp = temp.next;
        }

        insertNode(toInsert, nearest);
    }

    public void insertSmallest(Point p) {
        // find 2 closest nodes
        Node toInsert = new Node(p);

        if (head == null) {
            head = toInsert;
            return;
        }

        if (head.next == head) {
            insertNode(toInsert, head);
            return;
        }
    }

    public void reset() {
        head = null;
    }

    private class CircularLinkedListIterator implements Iterator<Point> {

        Node current;

        public CircularLinkedListIterator() {
            current = head;
        }

        @Override
        public boolean hasNext() {
            return (current != null);
        }

        @Override
        public Point next() {
            Point toReturn = current.point;
            current = current.next;
            if (current == head) {
                current = null;
            }
            return toReturn;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Iterator<Point> iterator() {
        return new CircularLinkedListIterator();
    }


}
