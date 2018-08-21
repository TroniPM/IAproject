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
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Matt
 */
public class Sintatic {

    private static Sintatic sintatic = null;
    public ArrayList<CompiladorToken> tokens = new ArrayList<>();

    public static Sintatic getInstance() {
        if (sintatic == null) {
            sintatic = new Sintatic();
        }

        return sintatic;
    }

    public static void main(String[] args) {
//        String source = "Person AND not (hasChild SOME (Person AND (hasChild ONLY Man) AND (hasChild SOME Person)));;";
//        String source = " Lens and (hasMinEffectiveFocalLength value 35) and (hasMaxEffectiveFocalLength value 120)";
        String source = "Professor IsA not (hasPet some Dog) or not (hasPet only Cat) and not (hasPet some GoldFish) and (hasPet only (not Bird))";
        try {
            System.out.println(source);
            boolean bb = Sintatic.getInstance().init(source);
            System.out.println("Sintatic: " + bb);
        } catch (Exception ex) {
            Logger.getLogger(Sintatic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean init(String source) throws Exception {
        String newSource = spaces(source);

        int open = source.length() - newSource.replace("(", "").length();
        int close = source.length() - newSource.replace(")", "").length();
        if (open != close) {
            if (open > close) {
                throw new Exception("Source code has more '(' than ')'. ");
            } else {
                throw new Exception("Source code has more ')' than '('. ");
            }
        }

        String[] cmd = newSource.split(";");

        for (int linha = 0; linha < cmd.length; linha++) {
            if (cmd[linha].trim().isEmpty()) {
                continue;
            }
            StringTokenizer st = new StringTokenizer(cmd[linha]);
            int isNotParenteses = 0;
            while (st.hasMoreTokens()) {
                String s = st.nextToken();
                if (s.equals("(") || s.equals(")")) {
                    isNotParenteses = 0;
                } else {
                    if (isNotParenteses == 3) {
                        throw new Exception("There is no parentheses before '" + s + "' at command '" + cmd[linha].trim() + "', starting at line " + (linha + 1) + ".");
                    }
                    isNotParenteses++;
                }
            }
        }

        for (int linha = 0; linha < cmd.length; linha++) {
            char[] itens = cmd[linha].toCharArray();
            for (int i = 0; i < itens.length; i++) {
                boolean flag = false;
                if (itens[i] == ')') {
                    inner:
                    for (int j = i - 1; j >= 0; j--) {
                        if (itens[j] == '(') {
                            //(...) encontrado
                            String particula = cmd[linha].substring(j, i + 1);
                            CompiladorToken token = new CompiladorToken(0, 0, particula);
                            tokens.add(token);
                            System.out.println("VAI TROCAR: " + token.label + " >> " + token.id);
                            cmd[linha] = cmd[linha].replace(token.label, token.id);
                            flag = true;
                            break inner;
                        }
                    }
                    System.out.println("ATUAL > " + cmd[linha]);
                    if (flag) {
                        itens = cmd[linha].toCharArray();
                        i = -1;//RESETANDO
                    }
                }
            }
        }

        cmd = substituir2(cmd, Util.NOT);

        /**
         * TODO ordem de resolução
         */
        cmd = substituir3(cmd, Util.AND);
        cmd = substituir3(cmd, Util.OR);
        System.out.println(Arrays.toString(cmd));
        System.out.println("*******************************************");
        StringBuilder sb = new StringBuilder();
        for (CompiladorToken s : tokens) {
            sb.append(s.string());
            sb.append(" | ");
            sb.append(s.id);
            sb.append("\r\n");
        }
        System.out.println(sb.toString());

        return true;
    }

    private String[] substituir2(String[] cmd, String PATTERN) {
        for (int linha = 0; linha < cmd.length; linha++) {
            if (cmd[linha].trim().isEmpty()) {
                continue;
            }
            StringTokenizer st = new StringTokenizer(cmd[linha]);

            String last = null;
            boolean operador = false;
            while (st.hasMoreTokens()) {
                String s = st.nextToken();
                if (s.equals(PATTERN)) {
                    operador = true;
                    continue;
                }

                if (operador) {
                    String particula = PATTERN + " " + s;
                    CompiladorToken token = new CompiladorToken(0, 0, particula);
                    tokens.add(token);
                    System.out.println("VAI TROCAR: " + token.label + " >> " + token.id);
                    cmd[linha] = cmd[linha].replace(token.label, token.id);

                    operador = false;
                }
            }
        }

        return cmd;
    }

    private String[] substituir3(String[] cmd, String PATTERN) {
        for (int linha = 0; linha < cmd.length; linha++) {
            if (cmd[linha].trim().isEmpty()) {
                continue;
            }
            StringTokenizer st = new StringTokenizer(cmd[linha]);

            String last = null;
            boolean operador = false;
            while (st.hasMoreTokens()) {
                String s = st.nextToken();
                boolean flag = false;
                if (s.equals(PATTERN)) {
                    flag = true;
                    operador = true;
                }

                if (operador && !flag) {
                    String particula = last + " " + PATTERN + " " + s;
                    CompiladorToken token = new CompiladorToken(0, 0, particula);
                    tokens.add(token);
                    System.out.println("VAI TROCAR: " + token.label + " >> " + token.id);
                    cmd[linha] = cmd[linha].replace(token.label, token.id);
                    return substituir3(cmd, PATTERN);
                }

                if (!flag) {
                    operador = false;
                    last = s;
                }
            }
        }

        return cmd;
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
