/*
 * Copyright 2016 Paulo Mateus [UFRPE-UAG] <paulomatew@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pmateus.core;

import com.pmateus.util.Session;
import java.util.ArrayList;

public class UndoRedoManager {

    private ArrayList<OntologyBundle> array = new ArrayList<OntologyBundle>();
    private int currentIndex = -1;//correção para indice

    private final int maxObjectsOnArray = 15;

    public int getCurrentSize() {
        return array.size();
    }

    public void insert(OntologyBundle bundle) {
        while (array.size() >= maxObjectsOnArray) {
            array.remove(0);
        }

        array.add(bundle);
        currentIndex = array.size() - 1;//posição passa a ser a inserida
    }

    public OntologyBundle getCurrent() {
        if (!array.isEmpty()) {
            return array.get(currentIndex);
        }
        return null;
    }

    public OntologyBundle getPrevious() {
        if (canGetPrevious()) {
            return array.get(--currentIndex);
        }
        return null;
    }

    public OntologyBundle getNext() {
        if (canGetNext()) {
            return array.get(++currentIndex);
        }
        return null;
    }

    public boolean canGetNext() {
        if (array.size() <= 1 || currentIndex + 1 > array.size() - 1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean canGetPrevious() {

        if (array.size() <= 1 || currentIndex - 1 < 0) {
            return false;
        } else {
            return true;
        }
    }

    public void printThis() {
        if (Session.isDebbug) {
            System.out.println(
                    "UndoRedoManager printThis(): array.size()=" + array.size()
                    + " | currentIndex=" + currentIndex
                    + " | left=" + canGetPrevious() + " | right=" + canGetNext());
        }
    }

}
