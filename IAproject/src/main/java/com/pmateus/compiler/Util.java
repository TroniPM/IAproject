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

/**
 *
 * @author Matt
 */
public abstract class Util {

    public static final String SOME = "some";
    public static final String ALL = "all";
    public static final String VALUE = "value";
    public static final String MIN = "min";
    public static final String MAX = "max";
    public static final String EXACTLY = "exactly";
    public static final String THAT = "that";
    public static final String NOT = "not";
    public static final String NOR = "nor";
    public static final String AND = "and";
    public static final String OR = "or";
    public static final String ONLY = "only";
    public static final String ISA = "isa";
    public static final String EQUIVALENT = "equivalent";
    public static final String[] COMMANDS_LIST = new String[]{SOME, ALL, VALUE,
        MIN, MAX, EXACTLY, THAT, NOT, AND, OR, ONLY, ISA, EQUIVALENT, NOR};

}
