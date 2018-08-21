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

import com.pmateus.compiler.classes.CompiladorToken;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Matt
 */
public class Lexical {

    private static Lexical lexical = null;

    public static Lexical getInstance() {
        if (lexical == null) {
            lexical = new Lexical();
        }

        return lexical;
    }

    public static void main(String[] args) {
        String source = "Person AND hasChild SOME (Person AND (hasChild ONLY Man) AND (hasChild SOME Person));;";

        try {
            boolean aa = Lexical.getInstance().init(source);
            Sintatic.getInstance().init(source);
            System.out.println("Lexical: " + aa);
        } catch (Exception ex) {
            Logger.getLogger(Lexical.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean init(String source) throws Exception {
        String newSource = spaces(source);
        String[] cmd = newSource.split(";");

        for (int linha = 0; linha < cmd.length; linha++) {
            if (cmd[linha].trim().isEmpty()) {
                continue;
            }
            StringTokenizer st = new StringTokenizer(cmd[linha]);
            while (st.hasMoreTokens()) {
                String s = st.nextToken();

                if (Character.isDigit(s.charAt(0))) {
                    //Começa com digito mas tem LETRA (1EEEE)
                    if (s.matches(".*[a-zA-Z]+.*")) {
                        throw new Exception("Unknow token '" + s + "' at command '" + cmd[linha].trim() + "', starting at line " + (linha + 1) + ".");
                    }
                }
            }

        }

//        System.out.println(Arrays.toString(cmd));
        return true;
    }

    private String spaces(String source) {
        String a = source.replace("\r\n", " ")
                .replace("\r", " ")
                .replace("\n", " ")
                .replace(")", " ) ")
                .replace("(", " ( ")
                .replace(";", " ; ")
                .trim().replaceAll(" +", " ");

        //Todas as palavras reservadas para lower case
        for (String in : a.split(" ")) {
            for (String out : Util.commands_list) {
                if (in.equalsIgnoreCase(out)) {
                    a = a.replace(" " + in + " ", " " + out + " ");
                }
            }
        }

        return a;
    }
}
