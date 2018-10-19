package com.pmateus.compiler.novo;

import com.pmateus.compiler.exception.LexicalAnalyzerException;
import com.pmateus.compiler.exception.SintaticAnalyzerException;
import java.util.ArrayList;

public class Sintatic {

    public ArrayList<Token> tokensLexical = null;
    private ArrayList<Object> stack = null;
    public String stackState = null;
    private int iteracao = 1;

    public static void main(String[] args) throws LexicalAnalyzerException, SintaticAnalyzerException {
        String teste = "";
//        teste += "Person isa not Racional;\n";
//        teste += "Human equivalent not(Racional or Crazy);\n";
//        teste += " Racional or Crazy ;\n";
        teste += "nOr ( Racional or Crazy and not Matus some Racional);\n";
//        teste += "Human isa Racional and Crazy;\n";
//        teste += "Human equivalent (Racional and Crazy) or (Dog and Irational);\n";
//        teste += "Human isa (Racional or Crazy) and (Dog or Irational);\n";
//        teste += "Person equivalent hasPet some Dog;\n";
//        teste += "Human isa hasPet only Cat;\n";
//        teste += "Human equivalent Person and (hasPet some Cat);\n";
//        teste += "Vet isa (hasPet some Cat) or (hasPet some Dog);\n";
//        teste += "Vet equivalent Person and (hasPet some (Cat or Dog));\n";
//        teste += "Doctor isa (hasPet some Dog);\n";
//        teste += "Doctor equivalent (hasPet only Dog);";

        ArrayList<Token> list = new Lexical().init(teste);
        new Sintatic().init(list);
    }

    public void init(ArrayList<Token> arr) throws SintaticAnalyzerException {
        if (arr.isEmpty()) {
            throw new SintaticAnalyzerException("Unexpected empty token list. Cause: probably the source code is empty.");
        }

        this.tokensLexical = arr;

        parserNew();
    }

    private void addToStack(Object token) {
        stack.add(token);
    }

    private Object getObjectFromStack() {
        try {
            Object t = stack.get(stack.size() - 1);
            stack.remove(t);
//            if (t instanceof Token) {
//                stack.remove(t);
//            } else {
//                stack.remove(t);
//            }

            return t;
        } catch (Exception ex) {
            return null;
        }

    }

    private boolean lookAhead(Token get, TokenEnum get0) {
        return get.type == get0;
    }

    private boolean lookAhead(Regra get, RegraEnum get0) {
        return get.tipo == get0;
    }

    private void printStack() {
        for (int i = stack.size() - 1; i >= 0; i--) {
            Object o = stack.get(i);
            if (o instanceof Token) {
                System.out.println(((Token) o).type.toString());
            } else if (o instanceof Regra) {
                System.out.println(((Regra) o).tipo.toString());
            }
        }
    }

    private void saveStackState(String atual) {
        stackState += (iteracao++) + ") -------------- ( " + atual + " )\n";

        //TODO inverter posição da pilha
        for (int i = stack.size() - 1; i >= 0; i--) {
            if (stack.get(i) instanceof Token) {
                stackState += ((Token) stack.get(i)).type.toString() + "\n";
            } else if (stack.get(i) instanceof Regra) {
                stackState += ((Regra) stack.get(i)).tipo.toString() + "\n";
            } else {
                stackState += "FIM DA PILHA\n";
            }
        }
        /*for (Object in : pilha) {
            if (in instanceof LexicalToken) {
                estadoDaPilha += ((LexicalToken) in).lexeme + "\n";
            } else {
                estadoDaPilha += "<" + ((RegraProducao) in).method + ">\n";
            }
        }*/
        stackState += "------------------------------\n";
    }

    private void println(String aa) {
        if (true) {
            System.out.println(aa);
        }
    }

    private void parserNew() throws SintaticAnalyzerException {
        stack = new ArrayList<>();

        stackState = "";
        iteracao = 1;

//        addToStack(new Token(-1, "$", "FIM DO PROGRAMA ($), <declarar_func>"));
        addToStack(new Token(TokenEnum.END));
        addToStack(new Regra(RegraEnum.DEF));
//        saveStackState("DEF");

        for (int i = 0; i < tokensLexical.size(); i++) {
            Object tokenDaPilha = getObjectFromStack();
            if (tokenDaPilha instanceof Token) {

                Token o1 = (Token) tokenDaPilha;
                saveStackState(o1.type.toString());

                if (tokensLexical.get(i).type != o1.type) {
                    System.out.println(stackState);
                    System.out.println(o1.toString());
                    System.out.println("<><><><><><><><><><><><><><><><><><><><>");
                    System.out.println(tokensLexical.get(i).toString());

                    throw new SintaticAnalyzerException("Unexpected token '" + (tokensLexical.get(i).lexeme)
                            + "' at line " + tokensLexical.get(i).line + ", position " + tokensLexical.get(i).position
                            + " (expected: '" + o1.description1 + "').");
                }

            } else if (tokenDaPilha instanceof Regra) {
                Regra o1 = (Regra) tokenDaPilha;
                saveStackState(o1.tipo.toString());
                if (o1.tipo == RegraEnum.DEF) {
                    if (tokensLexical.get(i).type == TokenEnum.NOR) {
                        println("if (tokensLexical.get(i).type == TokenEnum.NOR)");
                        addToStack(new Regra(RegraEnum.PONTO_VIRGULA));
                        addToStack(new Token(TokenEnum.PARENTESE_FECHAR));
                        addToStack(new Regra(RegraEnum.DEF));
                        addToStack(new Token(TokenEnum.PARENTESE_ABRIR));
                        addToStack(new Token(TokenEnum.NOR));
                    } else if (tokensLexical.get(i).type == TokenEnum.PARENTESE_ABRIR) {
                        println("if (tokensLexical.get(i).type == TokenEnum.PARENTESE_ABRIR)");
                        addToStack(new Regra(RegraEnum.PONTO_VIRGULA));
                        addToStack(new Token(TokenEnum.PARENTESE_FECHAR));
                        addToStack(new Regra(RegraEnum.DEF));
                        addToStack(new Token(TokenEnum.PARENTESE_ABRIR));
                    } else if (tokensLexical.get(i).type == TokenEnum.IDENTIFIER
                            && (lookAhead(tokensLexical.get(i + 1), TokenEnum.OR)
                            || lookAhead(tokensLexical.get(i + 1), TokenEnum.ISA)
                            || lookAhead(tokensLexical.get(i + 1), TokenEnum.EQUIVALENT)
                            || lookAhead(tokensLexical.get(i + 1), TokenEnum.THAT)
                            || lookAhead(tokensLexical.get(i + 1), TokenEnum.SOME)
                            || lookAhead(tokensLexical.get(i + 1), TokenEnum.ALL)
                            || lookAhead(tokensLexical.get(i + 1), TokenEnum.ONLY)
                            || lookAhead(tokensLexical.get(i + 1), TokenEnum.VALUE)
                            || lookAhead(tokensLexical.get(i + 1), TokenEnum.MIN)
                            || lookAhead(tokensLexical.get(i + 1), TokenEnum.MAX)
                            || lookAhead(tokensLexical.get(i + 1), TokenEnum.EXACTLY)
                            || lookAhead(tokensLexical.get(i + 1), TokenEnum.AND)) //                            || tokensLexical.get(i).type == TokenEnum.NOT
                            //                            && lookAhead(tokensLexical.get(i + 1), TokenEnum.IDENTIFIER)
                            ) {
                        println("if (tokensLexical.get(i).type == TokenEnum.IDENTIFIER) + LOOKAHEAD(MODIFICADORES)");
                        addToStack(new Regra(RegraEnum.PONTO_VIRGULA));
                        addToStack(new Regra(RegraEnum.DEF));
                        addToStack(new Regra(RegraEnum.MODIFIER_ALL));
                        addToStack(new Regra(RegraEnum.CLASSE));
                    } else if (tokensLexical.get(i).type == TokenEnum.IDENTIFIER) {
                        println("if (tokensLexical.get(i).type == TokenEnum.IDENTIFIER)");
                        addToStack(new Regra(RegraEnum.PONTO_VIRGULA));
                        addToStack(new Regra(RegraEnum.CLASSE));
                    } else {
                        println(stackState);
                        throw new SintaticAnalyzerException(tokensLexical.get(i), "NOR, (, IDENTIFIER");
                    }
                } else if (o1.tipo == RegraEnum.CLASSE) {
                    if (tokensLexical.get(i).type == TokenEnum.IDENTIFIER) {
                        addToStack(new Token(TokenEnum.IDENTIFIER));
                    } else if (tokensLexical.get(i).type == TokenEnum.NOT && lookAhead(tokensLexical.get(i + 1), TokenEnum.IDENTIFIER)) {
                        addToStack(new Token(TokenEnum.IDENTIFIER));
                        addToStack(new Token(TokenEnum.NOT));
                    } else {
                        println(stackState);
                        throw new SintaticAnalyzerException(tokensLexical.get(i), "NOT, IDENTIFIER");
                    }
                } else if (o1.tipo == RegraEnum.PONTO_VIRGULA) {
                    if (tokensLexical.get(i).type == TokenEnum.PONTO_VIRGULA) {
                        addToStack(new Token(TokenEnum.PONTO_VIRGULA));
                    } else {
                        //PODE GERAR VAZIO, então não printar erro
                    }

                } else if (o1.tipo == RegraEnum.MODIFIER_ALL) {
                    if (tokensLexical.get(i).type == TokenEnum.AND) {
                        addToStack(new Token(TokenEnum.AND));
                    } else if (tokensLexical.get(i).type == TokenEnum.OR) {
                        addToStack(new Token(TokenEnum.OR));
                    } else if (tokensLexical.get(i).type == TokenEnum.ISA) {
                        addToStack(new Token(TokenEnum.ISA));
                    } else if (tokensLexical.get(i).type == TokenEnum.EQUIVALENT) {
                        addToStack(new Token(TokenEnum.EQUIVALENT));
                    } else if (tokensLexical.get(i).type == TokenEnum.THAT) {
                        addToStack(new Token(TokenEnum.THAT));
                    } else if (tokensLexical.get(i).type == TokenEnum.SOME) {
                        addToStack(new Token(TokenEnum.SOME));
                    } else if (tokensLexical.get(i).type == TokenEnum.ALL) {
                        addToStack(new Token(TokenEnum.ALL));
                    } else if (tokensLexical.get(i).type == TokenEnum.ONLY) {
                        addToStack(new Token(TokenEnum.ONLY));
                    } else if (tokensLexical.get(i).type == TokenEnum.VALUE) {
                        addToStack(new Token(TokenEnum.VALUE));
                    } else if (tokensLexical.get(i).type == TokenEnum.MIN) {
                        addToStack(new Token(TokenEnum.MIN));
                    } else if (tokensLexical.get(i).type == TokenEnum.MAX) {
                        addToStack(new Token(TokenEnum.MAX));
                    } else if (tokensLexical.get(i).type == TokenEnum.EXACTLY) {
                        addToStack(new Token(TokenEnum.EXACTLY));
                    } else {
                        println(stackState);
                        throw new SintaticAnalyzerException(tokensLexical.get(i), "), AND, OR, ISA, EQUIVALENT, THAT, SOME, ALL, ONLY, VALUE, MIN, MAX, EXACTLY");
                    }
                }
                i--;
            }
//            else if (tokenDaPilha instanceof RegraProducao) {
//                RegraProducao o1 = (RegraProducao) tokenDaPilha;
//
//                saveStackState(o1.method);
//
//                if (o1.method.equals("programa")) {
//                    if (programa(tokensLexical.get(i))) {
//                        int label = (labels_chaves.get(labels_chaves.size() - 1) + 1);
//
//                        labels_chaves.add(label);
//                        addToStack(new RegraProducao("declarar_func"));
//                        addToStack(new Token(7, "}", "}", "C" + String.valueOf(label)));
//                        addToStack(new RegraProducao("escopo"));
//                        addToStack(new Token(6, "{", "{", "C" + String.valueOf(label)));
//
//                        label = (labels_parentese.get(labels_parentese.size() - 1) + 1);
//                        labels_parentese.add(label);
//
//                        addToStack(new Token(5, ")", ")", "P" + String.valueOf(label)));
//                        addToStack(new Token(4, "(", "(", "P" + String.valueOf(label)));
//                        addToStack(new Token(3, "main", "main"));
//
//                    } else {
//                        throw new SintaticAnalyzerException("Unexpected token '" + (tokensLexical.get(i).lexeme)
//                                + "' at line " + tokensLexical.get(i).line + " (expected: 'main').");
//                    }
//                } else if (o1.method.equals("escopo")) {
//                    String spe = "int, boolean, identificador, print, call, if, while, break, continue, }";
//                    if (escopo(tokensLexical.get(i))) {
//                        //<escopo> sempre tem q ser a primeira chamada (pra ser a ultima na pila)
//                        if (tokensLexical.get(i).type == 16) {//DECLARAÇÃO INT
//                            tokensLexical.get(i + 1).regra = "int";
//                            addToStack(new RegraProducao("escopo"));
//                            addToStack(new Token(8, ";", ";"));
//                            addToStack(new Token(1, "<identificador>", "identificador"));
//                            addToStack(new Token(16, "int", spe));
//                        } else if (tokensLexical.get(i).type == 17) {//DECLARAÇÃO BOOLEAN
//                            tokensLexical.get(i + 1).regra = "boolean";
//                            addToStack(new RegraProducao("escopo"));
//                            addToStack(new Token(8, ";", ";"));
//                            addToStack(new Token(1, "<identificador>", "identificador"));
//                            addToStack(new Token(17, "boolean", spe));
//                        } else if (tokensLexical.get(i).type == 1) {//Atribuição
//                            tokensLexical.get(i + 1).regra = "atrib";
//                            addToStack(new RegraProducao("escopo"));
//                            addToStack(new Token(8, ";", ";"));
//                            addToStack(new RegraProducao("atrib"));
//                            addToStack(new Token(10, "=", "="));
//                            addToStack(new Token(1, "<identificador>", spe));
//
//                            //throw new SintaticAnalyzerException("FAZER REGRAS DE <atrib>");
//                        } else if (tokensLexical.get(i).type == 27) {//Print
//                            int label = (labels_parentese.get(labels_parentese.size() - 1) + 1);
//                            //, "P" + String.valueOf(label)
//                            labels_parentese.add(label);
//                            addToStack(new RegraProducao("escopo"));
//                            addToStack(new Token(8, ";", ";"));
//                            addToStack(new Token(5, ")", "false, true, identificador, constante, )", "P" + String.valueOf(label)));
//                            addToStack(new RegraProducao("printar_sec"));
//                            addToStack(new Token(4, "(", "(", "P" + String.valueOf(label)));
//                            addToStack(new Token(27, "print", spe));
//                        } else if (tokensLexical.get(i).type == 21) {//IF
//                            int label = (labels_chaves.get(labels_chaves.size() - 1) + 1);
//                            //, "C" + String.valueOf(label)
//                            labels_chaves.add(label);
//                            addToStack(new RegraProducao("escopo"));
//                            addToStack(new RegraProducao("bloco_else"));
//                            addToStack(new Token(7, "}", "}", "C" + String.valueOf(label)));
//                            addToStack(new RegraProducao("escopo"));
//                            addToStack(new Token(6, "{", "{", "C" + String.valueOf(label)));
//
//                            label = (labels_parentese.get(labels_parentese.size() - 1) + 1);
//                            //, "P" + String.valueOf(label)
//                            labels_parentese.add(label);
//
//                            addToStack(new Token(5, ")", ")", "P" + String.valueOf(label)));
//                            addToStack(new RegraProducao("exp_logic"));
//                            addToStack(new Token(4, "(", "(", "P" + String.valueOf(label)));
//
//                            label = (labels_if.get(labels_if.size() - 1) + 1);
//                            //, "I" + String.valueOf(label)
//                            labels_if.add(label);
//                            labels_if_atual.add(label);
//
//                            addToStack(new Token(21, "if", spe, "I" + String.valueOf(label)));
//                        } else if (tokensLexical.get(i).type == 23) {//WHILE
//                            int label = (labels_chaves.get(labels_chaves.size() - 1) + 1);
//                            //, "C" + String.valueOf(label)
//                            labels_chaves.add(label);
//                            addToStack(new RegraProducao("escopo"));
//                            addToStack(new Token(7, "}", "}", "C" + String.valueOf(label)));
//                            addToStack(new RegraProducao("escopo"));
//                            addToStack(new Token(6, "{", "{", "C" + String.valueOf(label)));
//
//                            label = (labels_parentese.get(labels_parentese.size() - 1) + 1);
//                            //, "P" + String.valueOf(label)
//                            labels_parentese.add(label);
//
//                            addToStack(new Token(5, ")", ")", "P" + String.valueOf(label)));
//                            addToStack(new RegraProducao("exp_logic"));
//                            addToStack(new Token(4, "(", "(", "P" + String.valueOf(label)));
//                            addToStack(new Token(23, "while", spe));
//                        } else if (tokensLexical.get(i).type == 36) {//CALL
//                            addToStack(new RegraProducao("escopo"));
//                            addToStack(new Token(8, ";", ";"));
//                            addToStack(new RegraProducao("chamar_func"));
//
//                        } else if (tokensLexical.get(i).type == 18) {//BREAK
//                            addToStack(new RegraProducao("escopo"));
//                            addToStack(new Token(8, ";", ";"));
//                            addToStack(new Token(18, "break", spe));
//                        } else if (tokensLexical.get(i).type == 19) {//CONTINUE
//                            addToStack(new RegraProducao("escopo"));
//                            addToStack(new Token(8, ";", ";"));
//                            addToStack(new Token(19, "continue", spe));
//                        }
//
//                    } else {
//                        //throw new SintaticAnalyzerException("NÃO É <escopo>");
//                        throw new SintaticAnalyzerException("Unexpected token '" + (tokensLexical.get(i).lexeme)
//                                + "' at line " + tokensLexical.get(i).line + " (expected: '" + spe + "').");
//                    }
//                } else if (o1.method.equals("atrib")) {
//                    //UTILIZANDO LOOK AHEAD
//                    if (atrib(tokensLexical.get(i))) {
//                        if (tokensLexical.get(i).type == 1//IDENTIFICADOR e PONTOVIRGULA
//                                && lookAhead(tokensLexical.get(i + 1), new Token(8, ";"))) {
//                            tokensLexical.get(i).regra = "exp_arit";
//                            addToStack(new Token(1, "<identificador>", "identificador"));
//                        } else if (tokensLexical.get(i).type == 0//COSNTANTE e PONTOVIRGULA
//                                && lookAhead(tokensLexical.get(i + 1), new Token(8, ";"))) {
//                            tokensLexical.get(i).regra = "exp_arit_int";
//                            addToStack(new Token(0, "<numero>", "cosntante"));
//                        } else if (tokensLexical.get(i).type == 25 //TRUE
//                                && lookAhead(tokensLexical.get(i + 1), new Token(8, ";"))) {
//                            tokensLexical.get(i).regra = "exp_logic_boolean";
//                            addToStack(new Token(25, "true", "true"));
//                        } else if (tokensLexical.get(i).type == 26 //FALSE
//                                && lookAhead(tokensLexical.get(i + 1), new Token(8, ";"))) {
//                            tokensLexical.get(i).regra = "exp_logic_boolean";
//                            addToStack(new Token(26, "false", "false"));
//                        } else if (tokensLexical.get(i).type == 36) {//CALL
//                            addToStack(new RegraProducao("chamar_func"));
//                        } else if (tokensLexical.get(i).type == 37) { //[ exp_arit
//                            tokensLexical.get(i).regra = "exp_arit";
//                            addToStack(new Token(38, "]", "]"));
//                            addToStack(new RegraProducao("exp_arit"));
//                            addToStack(new Token(37, "[", "["));
//                        } else {
//                            //tokensLexical.get(i).print();
//                            tokensLexical.get(i - 1).regra += "_" + "exp_logic";
//                            addToStack(new RegraProducao("exp_logic"));
//                        }
//                    } else {
//                        throw new SintaticAnalyzerException("NÃO É <atrib>");
//                    }
//                } else if (o1.method.equals("chamar_func")) {
//                    if (chamar_func(tokensLexical.get(i))) {
//                        int label = (labels_parentese.get(labels_parentese.size() - 1) + 1);
//                        //, "P" + String.valueOf(label)
//                        labels_parentese.add(label);
//
//                        tokensLexical.get(i).regra = "call_func";
//                        tokensLexical.get(i + 1).regra = "func_iden";
//                        addToStack(new Token(5, ")", ")", "P" + String.valueOf(label)));
//                        addToStack(new RegraProducao("lista_arg"));
//                        addToStack(new Token(4, "(", "(", "P" + String.valueOf(label)));
//                        addToStack(new Token(1, "<identificador>", "identificador"));
//                        addToStack(new Token(36, "call", "call"));
//                    } else {
//                        throw new SintaticAnalyzerException("Unexpected token '" + (tokensLexical.get(i).lexeme)
//                                + "' at line " + tokensLexical.get(i).line + " (expected: 'call').");
//                    }
//                } else if (o1.method.equals("exp_arit")) {
//                    if (exp_arit(tokensLexical.get(i))) {
//                        if (tokensLexical.get(i).type == 1) {
//                            tokensLexical.get(i).regra = "exp_arit_int";
//                            addToStack(new RegraProducao("oper_arit"));
//                            addToStack(new Token(1, "<identificador>", "identificador"));
//                        } else if (tokensLexical.get(i).type == 0) {
//                            tokensLexical.get(i).regra = "exp_arit_int";
//                            addToStack(new RegraProducao("oper_arit"));
//                            addToStack(new Token(0, "<numero>", "constante"));
//                        } else if (tokensLexical.get(i).type == 4) {//(
//                            int label = (labels_parentese.get(labels_parentese.size() - 1) + 1);
//                            //, "C" + String.valueOf(label)
//                            labels_parentese.add(label);
//
//                            //tokensLexical.get(i).regra = ;
//                            addToStack(new RegraProducao("oper_arit"));
//                            addToStack(new Token(5, ")", ")", "P" + String.valueOf(label)));
//                            addToStack(new RegraProducao("exp_arit"));
//                            addToStack(new Token(4, "(", "(", "P" + String.valueOf(label)));
//                        }
//                    } else if (o1.dontPrintException) {
//                        System.out.println("exp_arit >> o1.dontPrintException");
//                    } else {
//                        throw new SintaticAnalyzerException("Unexpected token '" + (tokensLexical.get(i).lexeme)
//                                + "' at line " + tokensLexical.get(i).line + " (<exp_arit>).");
//                    }
//                } else if (o1.method.equals("oper_arit")) {
//                    //PODE GERAR VAZIO
//                    if (oper_arit(tokensLexical.get(i))) {
//                        if (tokensLexical.get(i).type == 11) {
//                            tokensLexical.get(i).regra = "exp_arit";
//                            addToStack(new RegraProducao("exp_arit"));
//                            addToStack(new Token(11, "+", "+"));
//                        } else if (tokensLexical.get(i).type == 12) {
//                            tokensLexical.get(i).regra = "exp_arit";
//                            addToStack(new RegraProducao("exp_arit"));
//                            addToStack(new Token(12, "-", "-"));
//                        } else if (tokensLexical.get(i).type == 13) {
//                            tokensLexical.get(i).regra = "exp_arit";
//                            addToStack(new RegraProducao("exp_arit"));
//                            addToStack(new Token(13, "*", "*"));
//                        } else if (tokensLexical.get(i).type == 14) {
//                            tokensLexical.get(i).regra = "exp_arit";
//                            addToStack(new RegraProducao("exp_arit"));
//                            addToStack(new Token(14, "/", "/"));
//                        }
//
//                    }
//                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
//                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
//                } else if (o1.method.equals("exp_logic")) {
//                    if (exp_logic(tokensLexical.get(i))) {
//                        if (tokensLexical.get(i).type == 1) {
//                            tokensLexical.get(i).regra = "exp_logic";
//                            addToStack(new RegraProducao("oper_logic"));
//                            addToStack(new Token(1, "<identificador>", "identificador"));
//                        } else if (tokensLexical.get(i).type == 0) {
//                            tokensLexical.get(i).regra = "exp_logic_int";
//                            addToStack(new RegraProducao("oper_logic"));
//                            addToStack(new Token(0, "<numero>", "constante"));
//                        } else if (tokensLexical.get(i).type == 25) {
//                            tokensLexical.get(i).regra = "exp_logic_boolean";
//                            addToStack(new RegraProducao("oper_logic"));
//                            addToStack(new Token(25, "true", "true"));
//                        } else if (tokensLexical.get(i).type == 26) {
//                            tokensLexical.get(i).regra = "exp_logic_boolean";
//                            addToStack(new RegraProducao("oper_logic"));
//                            addToStack(new Token(26, "false", "false"));
//                        } else if (tokensLexical.get(i).type == 4) {//(
//                            int label = (labels_parentese.get(labels_parentese.size() - 1) + 1);
//                            //, String.valueOf(label)
//                            labels_parentese.add(label);
//                            tokensLexical.get(i).regra = "exp_logic";
//                            addToStack(new RegraProducao("exp_logic_cont"));
//                            addToStack(new RegraProducao("oper_logic"));
//                            addToStack(new Token(5, ")", ")", "P" + String.valueOf(label)));
//                            addToStack(new RegraProducao("exp_logic"));
//                            addToStack(new Token(4, "(", "(", "P" + String.valueOf(label)));
//                        } else {
//                            //Evitar que seja passada uma condição vazia.
//                            throw new SintaticAnalyzerException("Unexpected token '" + (tokensLexical.get(i).lexeme)
//                                    + "' at line " + tokensLexical.get(i).line + " (<exp_logic>).");
//                        }
//
//                    } else if (o1.dontPrintException) {
//                        System.out.println("exp_logic >> o1.dontPrintException");
//
//                    } else {
//                        //throw new SintaticAnalyzerException("NÃO É <exp_logic>");
//
//                        throw new SintaticAnalyzerException("Unexpected token '" + (tokensLexical.get(i).lexeme)
//                                + "' at line " + tokensLexical.get(i).line + " (<exp_logic>).");
//                    }
//
//                } else if (o1.method.equals("oper_logic")) {
//                    //PODE GERAR VAZIO
//                    if (oper_logic(tokensLexical.get(i))) {
//                        if (tokensLexical.get(i).type == 28) {
//                            tokensLexical.get(i).regra = "exp_logic";
//                            addToStack(new RegraProducao("exp_logic_cont"));
//                            addToStack(new RegraProducao("exp_logic"));
//                            addToStack(new Token(28, "<", "<"));
//                        } else if (tokensLexical.get(i).type == 29) {
//                            tokensLexical.get(i).regra = "exp_logic";
//                            addToStack(new RegraProducao("exp_logic_cont"));
//                            addToStack(new RegraProducao("exp_logic"));
//                            addToStack(new Token(29, ">", ">"));
//                        } else if (tokensLexical.get(i).type == 30) {
//                            tokensLexical.get(i).regra = "exp_logic";
//                            addToStack(new RegraProducao("exp_logic_cont"));
//                            addToStack(new RegraProducao("exp_logic"));
//                            addToStack(new Token(30, "<=", "<="));
//                        } else if (tokensLexical.get(i).type == 31) {
//                            tokensLexical.get(i).regra = "exp_logic";
//                            addToStack(new RegraProducao("exp_logic_cont"));
//                            addToStack(new RegraProducao("exp_logic"));
//                            addToStack(new Token(31, ">=", ">="));
//                        } else if (tokensLexical.get(i).type == 32) {
//                            tokensLexical.get(i).regra = "exp_logic";
//                            addToStack(new RegraProducao("exp_logic_cont"));
//                            addToStack(new RegraProducao("exp_logic"));
//                            addToStack(new Token(32, "==", "=="));
//                        } else if (tokensLexical.get(i).type == 33) {
//                            tokensLexical.get(i).regra = "exp_logic";
//                            addToStack(new RegraProducao("exp_logic_cont"));
//                            addToStack(new RegraProducao("exp_logic"));
//                            addToStack(new Token(33, "!=", "!="));
//                        }
//
//                    }
//                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
//                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
//                } else if (o1.method.equals("exp_logic_cont")) {
//                    //PODE GERAR VAZIO
//                    if (oper_logic_cont(tokensLexical.get(i))) {
//                        if (tokensLexical.get(i).type == 34) {
//                            tokensLexical.get(i).regra = "exp_logic";
//                            addToStack(new RegraProducao("exp_logic"));
//                            addToStack(new Token(34, "&&", "&&"));
//                        } else if (tokensLexical.get(i).type == 35) {
//                            tokensLexical.get(i).regra = "exp_logic";
//                            addToStack(new RegraProducao("exp_logic"));
//                            addToStack(new Token(35, "||", "||"));
//                        }
//                    }
//                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
//                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
//                } else if (o1.method.equals("printar_sec")) {
//                    //PODE GERAR VAZIO
//                    if (printar_sec(tokensLexical.get(i))) {
//                        String esp = "false, true, identificador, constante";
//                        if (tokensLexical.get(i).type == 1) {//IDENTIFICADOR
//                            addToStack(new Token(1, "<identificador>", esp));
//                        } else if (tokensLexical.get(i).type == 0) {//CONSTANTE
//                            addToStack(new Token(0, "<numero>", esp));
//                        } else if (tokensLexical.get(i).type == 25) {//TRUE
//                            addToStack(new Token(25, "true", esp));
//                        } else if (tokensLexical.get(i).type == 26) {//FALSE
//                            addToStack(new Token(26, "false", esp));
//                        }
//                    }
//                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
//                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
//
//                } else if (o1.method.equals("bloco_else")) {
//                    //PODE GERAR VAZIO
//                    if (bloco_else(tokensLexical.get(i))) {
//                        int label = (labels_chaves.get(labels_chaves.size() - 1) + 1);
//                        //, "C" + String.valueOf(label)
//                        labels_chaves.add(label);
//                        addToStack(new Token(7, "}", "}", "C" + String.valueOf(label)));
//                        addToStack(new RegraProducao("escopo"));
//                        addToStack(new Token(6, "{", "{", "C" + String.valueOf(label)));
//
//                        label = labels_if_atual.get(labels_if_atual.size() - 1);
//                        labels_if_atual.remove(labels_if_atual.size() - 1);
//                        addToStack(new Token(22, "else", "else", "I" + String.valueOf(label)));
//
//                    } else {
//                        //quando não tiver bloco if, removo o if de ifs atuais
//                        labels_if_atual.remove(labels_if_atual.size() - 1);
//                    }
//                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
//                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
//                } else if (o1.method.equals("lista_arg")) {
//                    //PODE GERAR VAZIO
//                    if (lista_arg(tokensLexical.get(i))) {
//                        if (tokensLexical.get(i).type == 0) {//CONSTANTE
//                            tokensLexical.get(i).regra = "arg_int";
//                            addToStack(new RegraProducao("lista_arg_sec"));
//                            addToStack(new Token(0, "<numero>", "constante"));
//                        } else if (tokensLexical.get(i).type == 1) {//IDENTIFICADOR
//                            tokensLexical.get(i).regra = "argument";
//                            addToStack(new RegraProducao("lista_arg_sec"));
//                            addToStack(new Token(1, "<identificador>", "identificador"));
//                        } else if (tokensLexical.get(i).type == 25) {//TRUE
//                            tokensLexical.get(i).regra = "arg_boolean";
//                            addToStack(new RegraProducao("lista_arg_sec"));
//                            addToStack(new Token(25, "true", "true"));
//                        } else if (tokensLexical.get(i).type == 26) {//FALSE
//                            tokensLexical.get(i).regra = "arg_boolean";
//                            addToStack(new RegraProducao("lista_arg_sec"));
//                            addToStack(new Token(26, "false", "false"));
//                        }
//                    }
//                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
//                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
//                } else if (o1.method.equals("lista_arg_sec")) {
//                    if (lista_arg_sec(tokensLexical.get(i))) {
//                        if (tokensLexical.get(i).type == 9) {//VIRGULA
//                            addToStack(new RegraProducao("lista_arg_sec"));
//                            addToStack(new RegraProducao("lista_arg_ter"));
//                            addToStack(new Token(9, ",", ","));
//                        }
//                    }
//                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
//                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
//                } else if (o1.method.equals("lista_arg_ter")) {
//                    String esp = "false, true, identificador, constante";
//                    if (lista_arg_ter(tokensLexical.get(i))) {
//                        if (tokensLexical.get(i).type == 0) {//CONSTANTE
//                            tokensLexical.get(i).regra = "arg_int";
//                            addToStack(new Token(0, "<numero>", esp));
//                        } else if (tokensLexical.get(i).type == 1) {//IDENTIFICADOR
//                            tokensLexical.get(i).regra = "argument";
//                            addToStack(new Token(1, "<identificador>", esp));
//                        } else if (tokensLexical.get(i).type == 25) {//TRUE
//                            tokensLexical.get(i).regra = "arg_boolean";
//                            addToStack(new Token(25, "true", esp));
//                        } else if (tokensLexical.get(i).type == 26) {//FALSE
//                            tokensLexical.get(i).regra = "arg_boolean";
//                            addToStack(new Token(26, "false", esp));
//                        }
//                    } else {
//                        throw new SintaticAnalyzerException("Unexpected token '" + (tokensLexical.get(i).lexeme)
//                                + "' at line " + tokensLexical.get(i).line + " (expected: '" + esp + "').");
//                    }
//                } else if (o1.method.equals("declarar_func")) {
//                    //PODE GERAR VAZIO
//                    if (declarar_func(tokensLexical.get(i))) {
//                        int label = (labels_chaves.get(labels_chaves.size() - 1) + 1);
//                        //, "C" + String.valueOf(label)
//                        labels_chaves.add(label);
//
//                        addToStack(new RegraProducao("declarar_func"));
//                        addToStack(new Token(7, "}", "}", "C" + String.valueOf(label)));
//                        addToStack(new RegraProducao("retorno_func"));
//                        addToStack(new RegraProducao("escopo"));
//                        addToStack(new Token(6, "{", "{", "C" + String.valueOf(label)));
//
//                        label = (labels_parentese.get(labels_parentese.size() - 1) + 1);
//                        //, "P" + String.valueOf(label)
//                        labels_parentese.add(label);
//
//                        addToStack(new Token(5, ")", ")", "P" + String.valueOf(label)));
//                        addToStack(new RegraProducao("lista_param"));
//                        addToStack(new Token(4, "(", "(", "P" + String.valueOf(label)));
//                        addToStack(new Token(1, "<identificador>", "identificador"));
//                        addToStack(new RegraProducao("func_tipo"));
//                        addToStack(new Token(24, "function", "function"));
//
//                    }
//                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
//                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
//                } else if (o1.method.equals("func_tipo")) {
//                    String esp = "void, int, boolean";
//                    if (func_tipo(tokensLexical.get(i))) {
//                        if (tokensLexical.get(i).type == 15) {//VOID
//                            tokensLexical.get(i).regra = "func_void";
//                            tokensLexical.get(i + 1).regra = "void";
//                            addToStack(new Token(15, "void", esp));
//                        } else if (tokensLexical.get(i).type == 16) {//INT
//                            tokensLexical.get(i).regra = "func_int";
//                            tokensLexical.get(i + 1).regra = "int";
//                            addToStack(new Token(16, "int", esp));
//                        } else if (tokensLexical.get(i).type == 17) {//BOOELAN
//                            tokensLexical.get(i).regra = "func_bool";
//                            tokensLexical.get(i + 1).regra = "boolean";
//                            addToStack(new Token(17, "boolean", esp));
//                        }
//                    } else {
//                        throw new SintaticAnalyzerException("Unexpected token '" + (tokensLexical.get(i).lexeme)
//                                + "' at line " + tokensLexical.get(i).line + " (expected: '" + esp + "').");
//                    }
//                } else if (o1.method.equals("lista_param")) {
//                    //PODE GERAR VAZIO
//                    String esp = "int, boolean";
//                    if (lista_param(tokensLexical.get(i))) {
//                        if (tokensLexical.get(i).type == 16) {//INT
//                            tokensLexical.get(i).regra = "param_type_int";
//                            tokensLexical.get(i + 1).regra = "param_int";
//                            addToStack(new RegraProducao("lista_param_sec"));
//                            addToStack(new Token(1, "<identificador>", "identificador"));
//                            addToStack(new Token(16, "int", esp));
//                        } else if (tokensLexical.get(i).type == 17) {//BOOELAN
//                            tokensLexical.get(i).regra = "param_type_boolean";
//                            tokensLexical.get(i + 1).regra = "param_boolean";
//                            addToStack(new RegraProducao("lista_param_sec"));
//                            addToStack(new Token(1, "<identificador>", "identificador"));
//                            addToStack(new Token(17, "boolean", esp));
//                        }
//                    }
//                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
//                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
//                } else if (o1.method.equals("lista_param_sec")) {
//                    //PODE GERAR VAZIO
//                    if (lista_param_sec(tokensLexical.get(i))) {
//                        //tokenListFromLexical.get(i + 2).regra = "param_iden";
//                        addToStack(new RegraProducao("lista_param_sec"));
//                        addToStack(new Token(1, "<identificador>", "identificador"));
//                        //System.out.println(">>>>>>>>>>>>>>>>>> " + tokenListFromLexical.get(i).type);
//                        if (tokensLexical.get(i).type == 9//VIRGULA e INTEIRO
//                                && lookAhead(tokensLexical.get(i + 1), new Token(16, "int", "int, boolean"))) {
//                            tokensLexical.get(i + 1).regra = "param_type_int";
//                            tokensLexical.get(i + 2).regra = "param_int";
//                            addToStack(new Token(16, "int", "int, boolean"));
//                        } else if (tokensLexical.get(i).type == 9//VIRGULA e BOLEAN
//                                && lookAhead(tokensLexical.get(i + 1), new Token(17, "boolean", "int, boolean"))) {
//                            tokensLexical.get(i + 1).regra = "param_type_boolean";
//                            tokensLexical.get(i + 2).regra = "param_boolean";
//                            addToStack(new Token(17, "int", "int, boolean"));
//                        }
//
//                        addToStack(new Token(9, ",", ","));
//
//                    }
//                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
//                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
//                } else if (o1.method.equals("retorno_func")) {
//                    //PODE GERAR VAZIO
//                    if (retorno_func(tokensLexical.get(i))) {
//                        addToStack(new Token(8, ";", ";"));
//                        addToStack(new RegraProducao("retorno_func_sec"));
//                        addToStack(new Token(20, "return", "return"));
//                    }
//                    /*QUANDO PALAVRA GERA VAZIO NÃO PODE GERAR EXCEPTION 
//                        (PQ VAI CHAMAR O PRÓXIMO DA PILHA*/
//                } else if (o1.method.equals("retorno_func_sec")) {
//                    //É o mesmo esquema da atribuição
//                    if (retorno_func_sec(tokensLexical.get(i))) {
//                        if (tokensLexical.get(i).type == 1//IDENTIFICADOR e PONTOVIRGULA
//                                && lookAhead(tokensLexical.get(i + 1), new Token(8, ";"))) {
//                            tokensLexical.get(i).regra = "ident";
//                            addToStack(new Token(1, "<identificador>", "identificador"));
//                        } else if (tokensLexical.get(i).type == 0//COSNTANTE e PONTOVIRGULA
//                                && lookAhead(tokensLexical.get(i + 1), new Token(8, ";"))) {
//                            tokensLexical.get(i).regra = "exp_arit_int";
//                            addToStack(new Token(0, "<numero>", "constante"));
//                        } else if (tokensLexical.get(i).type == 25 //TRUE
//                                && lookAhead(tokensLexical.get(i + 1), new Token(8, ";"))) {
//                            tokensLexical.get(i).regra = "exp_logic_boolean";
//                            addToStack(new Token(25, "true", "true"));
//                        } else if (tokensLexical.get(i).type == 26 //FALSE
//                                && lookAhead(tokensLexical.get(i + 1), new Token(8, ";"))) {
//                            tokensLexical.get(i).regra = "exp_logic_boolean";
//                            addToStack(new Token(26, "false", "false"));
//                        } else if (tokensLexical.get(i).type == 36) {//CALL
//                            addToStack(new RegraProducao("chamar_func"));
//                        } else if (tokensLexical.get(i).type == 37) { //[ exp_arit
//                            tokensLexical.get(i).regra = "exp_arit";
//                            addToStack(new Token(38, "]", "]"));
//                            addToStack(new RegraProducao("exp_arit"));
//                            addToStack(new Token(37, "[", "["));
//                        } else {
//                            addToStack(new RegraProducao("exp_logic"));
//                        }
//                    } else {
//                        //throw new SintaticAnalyzerException("NÃO É <retorno_func_sec>");
//                        throw new SintaticAnalyzerException("Unexpected token '" + (tokensLexical.get(i).lexeme)
//                                + "' at line " + tokensLexical.get(i).line + " (expected: 'identificador, constante, true, false, <chamar_func>, [<exp_arit>], <exp_logic>').");
//                    }
//                }
//                i--;
//            }

            /**
             * Quando chega ao final, verificar se DECLARAR_FUNC é vazio
             */
            if (stack.get(stack.size() - 1) == null) {

                if (tokensLexical.get(i) instanceof Token) {
                    throw new SintaticAnalyzerException("Unexpected token '" + (tokensLexical.get(i).lexeme)
                            + "' at line " + tokensLexical.get(i).line + " (expected: '" + tokensLexical.get(i).description1 + "').");
                } else {
                    throw new SintaticAnalyzerException("Pilha foi lida mas o código fonte ainda não acabou.");
                }
            }
//            if (i + 1 == tokensLexical.size() && stack.size() > 0) {
//                //RegraProducao o1 = (RegraProducao) getNaPilha();
//
//                Object o = getObjectFromStack();
//                if (o instanceof RegraProducao && ((RegraProducao) o).method.equals("declarar_func")) {
//                    //dummy if
//                } else {
//                    /*for (Object in : pilha) {
//                        if (in instanceof LexicalToken) {
//                            System.out.println(((LexicalToken) in).lexeme);
//                        } else {
//                            System.out.println(((RegraProducao) in).method);
//                        }
//                    }*/
//                    //System.out.println(arr.get(i+1));
//                    String in = "";
//
//                    String esp = "";
//                    for (int x1 = 0; x1 < stack.size(); x1++) {
//                        Object out = getObjectFromStack();
//                        if (out instanceof Token) {
//                            in += ((Token) out).lexeme + ", ";
//
//                            if (x1 == 0) {
//                                esp = ((Token) out).lexeme;
//                            }
//                        } else if (out instanceof RegraProducao) {
//                            in += (((RegraProducao) out).method) + ", ";
//
//                            if (x1 == 0) {
//                                esp = ((RegraProducao) out).method;
//                            }
//                        }
//                    }
//                    throw new SintaticAnalyzerException("All source code was readed, but stack is not EMPTY (size: " + stack.size() + ", tokens: " + in + "). Expected: '" + esp + "'");
//                }
//            }
        }
    }

    /**
     * Aqui embaixo vai rolar a magica
     *
     */
//    private boolean retorno_func_sec(Token token) {
//        return token.type == 1 //identificador
//                || token.type == 0 //constante
//                || token.type == 25 //true
//                || token.type == 26 //false
//                || token.type == 36 //call
//                || token.type == 37 //[
//                || token.type == 38 //]
//                || token.type == 4 // (
//                ;
//    }
//
//    private boolean exp_logic(Token token) {
//        return token.type == 0
//                || token.type == 1
//                || token.type == 4
//                || token.type == 5
//                || token.type == 25
//                || token.type == 26;
//
//    }
//
//    private boolean oper_arit(Token token) {
//        return token.type == 11 || token.type == 12
//                || token.type == 13 || token.type == 14;
//    }
//
//    private boolean oper_logic(Token token) {
//        return token.type == 28
//                || token.type == 29
//                || token.type == 30
//                || token.type == 31
//                || token.type == 32
//                || token.type == 33;
//    }
//
//    private boolean exp_arit(Token token) {
//        return token.type == 0
//                || token.type == 1
//                || token.type == 4
//                || token.type == 5;
//
//    }
//
//    private boolean chamar_func(Token token) {
//        return token.type == 36;
//    }
//
//    private boolean atrib(Token token) {
//        return token.type == 1 //identificador
//                || token.type == 0 //constante
//                || token.type == 25 //true
//                || token.type == 26 //false
//                || token.type == 36 //call
//                || token.type == 37 //[
//                || token.type == 38 //]
//                || token.type == 4 // (
//                ;
//    }
//
//    private boolean retorno_func(Token token) {
//        return token.type == 20;
//
//    }
//
//    private boolean lista_param_sec(Token token) {
//        return token.type == 9;
//    }
//
//    private boolean lista_param(Token token) {
//        return token.type == 16 || token.type == 17;
//    }
//
//    private boolean func_tipo(Token token) {
//        return token.type == 15 || token.type == 16 || token.type == 17;
//    }
//
//    private boolean declarar_func(Token token) {
//        return token.type == 24;
//    }
//
//    private boolean lista_arg_ter(Token token) {
//        return token.type == 1 || token.type == 0
//                || token.type == 25 || token.type == 26;
//    }
//
//    private boolean lista_arg_sec(Token token) {
//        return token.type == 9;
//    }
//
//    private boolean lista_arg(Token token) {
//        return token.type == 1 || token.type == 0
//                || token.type == 25 || token.type == 26;
//    }
//
//    private boolean bloco_else(Token token) {
//        return token.type == 22;
//    }
//
//    private boolean printar_sec(Token token) {
//        return token.type == 1 || token.type == 0
//                || token.type == 25 || token.type == 26;
//    }
//
//    private boolean printar(Token token) {
//        return token.type == 27;
//    }
//
//    private boolean escopo(Token token) {
//        return token.type == 16 || token.type == 17
//                || token.type == 18 || token.type == 19
//                || token.type == 1 || token.type == 27
//                || token.type == 36 || token.type == 21
//                || token.type == 23 || token.type == 7
//                || token.type == 20;
//    }
//
//    private boolean programa(Token token) {
//        return token.type == 3;
//    }
//
//    private boolean oper_logic_cont(Token token) {
//        return token.type == 34 || token.type == 35;
//    }
}
