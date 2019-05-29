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
package com.tronipm.orcaide.util;

import java.util.ArrayList;

/**
 *
 * @author Matt
 */
public class Teste {

    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>();
        String a = "aa", b = "bb", c = "cc";
        list.add(a);
        list.add(b);
        list.add(c);

        System.out.println(list.contains(" cc aacc ".trim()));
//        System.out.println(list.contains(c));
//        System.out.println(list.contains("dd"));
//        System.out.println(list.indexOf(" cc ".trim()));
//        System.out.println(list.indexOf(c));
//        System.out.println(list.);
    }
}
