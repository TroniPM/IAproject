package com.pmateus.compiler.novo;

import com.pmateus.compiler.exception.LexicalAnalyzerException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by Paulo Mateus on 26/06/2017. For project Compiladores
 * (https://github.com/TroniPM/Compilador) Contact: <paulomatew@gmail.com>
 */
public class Lexical {

    private static Lexical lexical = null;

    private String sourceCode = null;
    public ArrayList<Token> tokenArray = null;

    public static void main(String[] args) throws LexicalAnalyzerException {
        String teste = "";
        teste += "Person isa not Racional;\n";
        teste += "Human equivalent not(Racional or Crazy);\n";
        teste += "Human isa Racional and Crazy;\n";
        teste += "Human equivalent (Racional and Crazy) or (Dog and Irational);\n";
        teste += "Human isa (Racional or Crazy) and (Dog or Irational);\n";
        teste += "Person equivalent hasPet some Dog;\n";
        teste += "Human isa hasPet only Cat;\n";
        teste += "Human equivalent Person and (hasPet some Cat);\n";
        teste += "Vet isa (hasPet some Cat) or (hasPet some Dog);\n";
        teste += "Vet equivalent Person and (hasPet some (Cat or Dog));\n";
        teste += "Doctor isa (hasPet some Dog);\n";
        teste += "Doctor equivalent (hasPet only Dog);";

        Lexical.getInstance().init(teste);
    }

    private Lexical() {
    }

    public static Lexical getInstance() {
        if (lexical == null) {
            lexical = new Lexical();
        }

        return lexical;
    }

    public ArrayList<Token> init(String sourceCode) throws LexicalAnalyzerException {
        if (sourceCode == null || sourceCode.isEmpty()) {
            throw new LexicalAnalyzerException("Nenhum código fonte informado.");
        }

        this.sourceCode = formatSourceCode(sourceCode);

        tokenArray = parser();

        return tokenArray;
    }

    private String formatSourceCode(String msg) {
        msg = msg.replace("(", " ( ")
                .replace(")", " ) ")
                .replace("{", " { ")
                .replace("}", " } ")
                .replace(";", " ; ")
                .replace("[", " [ ")
                .replace("]", " ] ");

        return msg;
    }

    private ArrayList<Token> parser() throws LexicalAnalyzerException {
        ArrayList<Token> arr = new ArrayList<>();

        String[] sourcePorLinha = sourceCode.replace("\r\n", "\n").replace("\r", "\n").split("\n");

        for (int j1 = 0; j1 < sourcePorLinha.length; j1++) {
            String linha = sourcePorLinha[j1];

            //ignorar comentários
            if (linha.trim().startsWith("#")) {
                continue;
            }

            StringTokenizer st = new StringTokenizer(linha);

            int pos = 0;
            outer:
            while (st.hasMoreTokens()) {
                pos++;
                String s = st.nextToken();

//                boolean ctrl = false;
//                for (int i = 0; i < InsertionAnalyser.commands_list.length; i++) {
//                    if (s.equals(InsertionAnalyser.commands_list[i])) {
//
//                        Token l = new Token();
//                        l.type = TokenEnum.parse(s.toUpperCase());
//                        l.lexeme = s;
//                        l.line = j1 + 1;
//                        l.position = pos;
//                        arr.add(l);
//
//                        ctrl = true;
//
//                        if (l.type == null) {
//                            throw new LexicalAnalyzerException("Unknow token '" + l.lexeme + "' at line " + l.line);
//                        }
//                    }
//                }
//                if (ctrl) {
//                    continue;
//                }
                Token l = new Token();
                l.type = TokenEnum.parse(s.toUpperCase());
                l.lexeme = s;
                l.line = j1 + 1;
                l.position = pos;
                arr.add(l);

                if (l.type == null) {
                    throw new LexicalAnalyzerException(l);
                }
            }
        }
        return arr;
    }
}
