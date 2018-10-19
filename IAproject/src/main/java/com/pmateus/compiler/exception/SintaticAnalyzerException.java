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
package com.pmateus.compiler.exception;

import com.pmateus.compiler.novo.Token;

/**
 *
 * @author Matt
 */
public class SintaticAnalyzerException extends Exception {

    public SintaticAnalyzerException(String message) {
        super(message);
    }

    public SintaticAnalyzerException(Token l) {
        super("Unexpected token '" + (l.lexeme) + "' at line " + l.line + " and position " + l.position + ".");
    }

    public SintaticAnalyzerException(Token l, String expected) {
        super("Unexpected token '" + (l.lexeme)
                + "' at line " + l.line + " and position " + l.position + " (expected: '" + expected + "').");
    }
}
