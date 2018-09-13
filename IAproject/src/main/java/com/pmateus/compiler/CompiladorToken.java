/*
 * Copyright 2018 Paulo Mateus da Silva.
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
package com.pmateus.compiler;

import com.pmateus.compiler.classes.AtomoCompilador;
import com.pmateus.util.RandomString;

/**
 *
 * @author Matt
 */
public class CompiladorToken extends AtomoCompilador {

    public String label;
    public String id;
    public boolean isNegacao = false;

    public CompiladorToken(int line, int column, String label) {
        super(line, column);
        this.label = label;

//        this.id = new RandomString(label.length(), label.replace(")", "").replace("(", "").replace(" ", "")).nextString();
        this.id = new RandomString(label.length()).nextString();
    }

    public CompiladorToken(int line, int column, String label, boolean isNegacao) {
        super(line, column);
        this.label = label;
        this.isNegacao = isNegacao;
        this.id = new RandomString(label.length()).nextString();
    }

    public CompiladorToken(int line, int column) {
        super(line, column);
    }

    @Override
    public String toString() {
        String a = "id: " + id + " | ";
        a += "   line: " + line + CRLF;
        a += "   column: " + column + CRLF;
        a += "   label: " + (isNegacao ? "NOT> " : "") + label + CRLF;
        a += "------------------------";
        return a;
    }

    public String string() {
        String a = "id: " + id + " | ";
//        a += "line: " + line + " | ";
//        a += "column: " + column + " | ";
        a += "label: " + (isNegacao ? "NOT> " : "") + label;
        return a;
    }
}
