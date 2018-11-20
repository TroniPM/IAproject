package com.tronipm.gauufrpe.controller;

import com.tronipm.gauufrpe.model.RegraEnum;
import com.tronipm.gauufrpe.model.TokenEnum;
import com.tronipm.gauufrpe.model.Regra;
import com.tronipm.gauufrpe.model.TokenPreProcessamento;
import com.tronipm.gauufrpe.compiler.exception.LexicalAnalyzerException;
import com.tronipm.gauufrpe.compiler.exception.SintaticAnalyzerException;
import java.util.ArrayList;

public class ControllerSintatic {

    private static ControllerSintatic sintatic = null;

    private ArrayList<TokenPreProcessamento> tokens = null;
    private ArrayList<Object> stack = null;
    private String stackState = null;
    private int iteracao = 1;

    public ArrayList<TokenPreProcessamento> getTokens() {
        return this.tokens;
    }

    public static void main(String[] args) throws LexicalAnalyzerException, SintaticAnalyzerException {
        String teste = "";
        teste += "nor ( Racional or Crazy and Matus some  Racional and nor (not joao and marcos) and (joao or antonia));\n";
        teste += "Person isa not Racional;\n";
        teste += " Racional or Crazy ;\n";
        teste += "Human isa Racional and Crazy;\n";
        teste += "Human equivalent (Racional and Crazy) or (Dog and Irational);\n";
        teste += "Human isa (Racional or Crazy) and (not Dog or Irational);\n";
        teste += "Person equivalent hasPet some Dog;\n";
        teste += "Human isa hasPet only Cat;\n";
        teste += "Human equivalent Person and (hasPet some Cat);\n";
        teste += "Vet isa (hasPet some Cat) or (hasPet some Dog);\n";
        teste += "Vet equivalent Person and (hasPet some (Cat or Dog));\n";
        teste += "Doctor isa (hasPet some Dog);\n";
        teste += "Doctor equivalent (hasPet only Dog);";

        ControllerLexical.getInstance().init(teste);
        ControllerSintatic.getInstance().init(ControllerLexical.getInstance().getTokens());
    }

    private ControllerSintatic() {
    }

    public static ControllerSintatic getInstance() {
        if (sintatic == null) {
            sintatic = new ControllerSintatic();
        }

        return sintatic;
    }

    public void init(ArrayList<TokenPreProcessamento> arr) throws SintaticAnalyzerException {
        if (arr.isEmpty()) {
            throw new SintaticAnalyzerException("Unexpected empty token list. Cause: probably the source code is empty.");
        }

        this.tokens = arr;

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

    private boolean lookAhead(TokenPreProcessamento get, TokenEnum get0) {
        return get.type == get0;
    }

    private boolean lookAhead(Regra get, RegraEnum get0) {
        return get.tipo == get0;
    }

    private void printStack() {
        for (int i = stack.size() - 1; i >= 0; i--) {
            Object o = stack.get(i);
            if (o instanceof TokenPreProcessamento) {
                System.out.println(((TokenPreProcessamento) o).type.toString());
            } else if (o instanceof Regra) {
                System.out.println(((Regra) o).tipo.toString());
            }
        }
    }

    private void saveStackState(String atual) {
        stackState += (iteracao++) + ") -------------- ( " + atual + " )\n";

        //TODO inverter posição da pilha
        for (int i = stack.size() - 1; i >= 0; i--) {
            if (stack.get(i) instanceof TokenPreProcessamento) {
                stackState += ((TokenPreProcessamento) stack.get(i)).type.toString() + "\n";
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

        addToStack(new TokenPreProcessamento(TokenEnum.END));
        addToStack(new Regra(RegraEnum.DEF));

        for (int i = 0; i < tokens.size(); i++) {
            Object tokenDaPilha = getObjectFromStack();
            if (tokenDaPilha instanceof TokenPreProcessamento) {

                TokenPreProcessamento o1 = (TokenPreProcessamento) tokenDaPilha;
                saveStackState(o1.type.toString());

                if (tokens.get(i).type != o1.type) {
//                    System.out.println(stackState);
//                    System.out.println(o1.toString());
//                    System.out.println("<><><><><><><><><><><><><><><><><><><><>");
//                    System.out.println(tokensLexical.get(i).toString());

                    throw new SintaticAnalyzerException("Unexpected token '" + (tokens.get(i).lexeme)
                            + "' at line " + tokens.get(i).line + ", position " + tokens.get(i).position
                            + " (expected: '" + o1.description1 + "').");
                }

            } else if (tokenDaPilha instanceof Regra) {
                Regra o1 = (Regra) tokenDaPilha;
                saveStackState(o1.tipo.toString());
                if (o1.tipo == RegraEnum.DEF) {
                    if (tokens.get(i).type == TokenEnum.NOR) {
//                        println("if (tokensLexical.get(i).type == TokenEnum.NOR)");
                        addToStack(new Regra(RegraEnum.PONTO_VIRGULA));
                        addToStack(new Regra(RegraEnum.DEF2));
                        addToStack(new TokenPreProcessamento(TokenEnum.PARENTESE_FECHAR));
                        addToStack(new Regra(RegraEnum.DEF));
                        addToStack(new TokenPreProcessamento(TokenEnum.PARENTESE_ABRIR));
                        addToStack(new TokenPreProcessamento(TokenEnum.NOR));
                    } else if (tokens.get(i).type == TokenEnum.PARENTESE_ABRIR) {
//                        println("if (tokensLexical.get(i).type == TokenEnum.PARENTESE_ABRIR)");
                        addToStack(new Regra(RegraEnum.PONTO_VIRGULA));
                        addToStack(new Regra(RegraEnum.DEF2));
                        addToStack(new TokenPreProcessamento(TokenEnum.PARENTESE_FECHAR));
                        addToStack(new Regra(RegraEnum.DEF));
                        addToStack(new TokenPreProcessamento(TokenEnum.PARENTESE_ABRIR));
                    } else if (tokens.get(i).type == TokenEnum.IDENTIFIER
                            && (lookAhead(tokens.get(i + 1), TokenEnum.OR)
                            || lookAhead(tokens.get(i + 1), TokenEnum.ISA)
                            || lookAhead(tokens.get(i + 1), TokenEnum.EQUIVALENT)
                            || lookAhead(tokens.get(i + 1), TokenEnum.THAT)
                            || lookAhead(tokens.get(i + 1), TokenEnum.SOME)
                            || lookAhead(tokens.get(i + 1), TokenEnum.ALL)
                            || lookAhead(tokens.get(i + 1), TokenEnum.ONLY)
                            || lookAhead(tokens.get(i + 1), TokenEnum.VALUE)
                            || lookAhead(tokens.get(i + 1), TokenEnum.MIN)
                            || lookAhead(tokens.get(i + 1), TokenEnum.MAX)
                            || lookAhead(tokens.get(i + 1), TokenEnum.EXACTLY)
                            || lookAhead(tokens.get(i + 1), TokenEnum.AND))) {
//                        println("if (tokensLexical.get(i).type == TokenEnum.IDENTIFIER) + LOOKAHEAD(MODIFICADORES)");
                        addToStack(new Regra(RegraEnum.PONTO_VIRGULA));
                        addToStack(new Regra(RegraEnum.DEF));
                        addToStack(new Regra(RegraEnum.MODIFIER_ALL));
                        addToStack(new Regra(RegraEnum.CLASSE));
                    } else if (tokens.get(i).type == TokenEnum.IDENTIFIER) {
//                        println("if (tokensLexical.get(i).type == TokenEnum.IDENTIFIER)");
                        addToStack(new Regra(RegraEnum.PONTO_VIRGULA));
                        addToStack(new Regra(RegraEnum.CLASSE));
                    } else if (tokens.get(i).type == TokenEnum.NOT) {
                        if (lookAhead(tokens.get(i + 1), TokenEnum.IDENTIFIER)
                                && (lookAhead(tokens.get(i + 2), TokenEnum.OR)
                                || lookAhead(tokens.get(i + 2), TokenEnum.ISA)
                                || lookAhead(tokens.get(i + 2), TokenEnum.EQUIVALENT)
                                || lookAhead(tokens.get(i + 2), TokenEnum.THAT)
                                || lookAhead(tokens.get(i + 2), TokenEnum.SOME)
                                || lookAhead(tokens.get(i + 2), TokenEnum.ALL)
                                || lookAhead(tokens.get(i + 2), TokenEnum.ONLY)
                                || lookAhead(tokens.get(i + 2), TokenEnum.VALUE)
                                || lookAhead(tokens.get(i + 2), TokenEnum.MIN)
                                || lookAhead(tokens.get(i + 2), TokenEnum.MAX)
                                || lookAhead(tokens.get(i + 2), TokenEnum.EXACTLY)
                                || lookAhead(tokens.get(i + 2), TokenEnum.AND))) {
//                            println("TokenEnum.NOT >>>>>> if (tokensLexical.get(i).type == TokenEnum.IDENTIFIER) + LOOKAHEAD(MODIFICADORES)");
                            addToStack(new Regra(RegraEnum.PONTO_VIRGULA));
                            addToStack(new Regra(RegraEnum.DEF));
                            addToStack(new Regra(RegraEnum.MODIFIER_ALL));
                            addToStack(new Regra(RegraEnum.CLASSE));
                        } else if (lookAhead(tokens.get(i + 1), TokenEnum.IDENTIFIER)) {
//                            println("TokenEnum.NOT >>>>>> if (tokensLexical.get(i).type == TokenEnum.IDENTIFIER)");
                            addToStack(new Regra(RegraEnum.PONTO_VIRGULA));
                            addToStack(new Regra(RegraEnum.CLASSE));
                        }
                    } else {
                        println(stackState);
                        throw new SintaticAnalyzerException(tokens.get(i), "NOT, NOR, (, IDENTIFIER");
                    }
                } else if (o1.tipo == RegraEnum.CLASSE) {
                    if (tokens.get(i).type == TokenEnum.IDENTIFIER) {
                        addToStack(new TokenPreProcessamento(TokenEnum.IDENTIFIER));
                    } else if (tokens.get(i).type == TokenEnum.NOT && lookAhead(tokens.get(i + 1), TokenEnum.IDENTIFIER)) {
                        addToStack(new TokenPreProcessamento(TokenEnum.IDENTIFIER));
                        addToStack(new TokenPreProcessamento(TokenEnum.NOT));
                    } else {
                        println(stackState);
                        throw new SintaticAnalyzerException(tokens.get(i), "NOT, IDENTIFIER");
                    }
                } else if (o1.tipo == RegraEnum.DEF2) {
                    if (tokens.get(i).type == TokenEnum.AND
                            || tokens.get(i).type == TokenEnum.OR
                            || tokens.get(i).type == TokenEnum.ISA
                            || tokens.get(i).type == TokenEnum.EQUIVALENT
                            || tokens.get(i).type == TokenEnum.THAT
                            || tokens.get(i).type == TokenEnum.SOME
                            || tokens.get(i).type == TokenEnum.ALL
                            || tokens.get(i).type == TokenEnum.ONLY
                            || tokens.get(i).type == TokenEnum.VALUE
                            || tokens.get(i).type == TokenEnum.MIN
                            || tokens.get(i).type == TokenEnum.MAX
                            || tokens.get(i).type == TokenEnum.EXACTLY
                            || tokens.get(i).type == TokenEnum.AND) {
                        addToStack(new Regra(RegraEnum.DEF));
                        addToStack(new Regra(RegraEnum.MODIFIER_ALL));
                    } else {
                        //PODE GERAR VAZIO, então não printar erro
                    }

                } else if (o1.tipo == RegraEnum.PONTO_VIRGULA) {
                    if (tokens.get(i).type == TokenEnum.PONTO_VIRGULA
                            && i + 1 == tokens.size()) {
                        addToStack(new TokenPreProcessamento(TokenEnum.PONTO_VIRGULA));
                    } else if (tokens.get(i).type == TokenEnum.PONTO_VIRGULA) {
                        addToStack(new Regra(RegraEnum.DEF));
                        addToStack(new TokenPreProcessamento(TokenEnum.PONTO_VIRGULA));
                    } else {
                        //PODE GERAR VAZIO, então não printar erro
                    }

                } else if (o1.tipo == RegraEnum.MODIFIER_ALL) {
                    if (tokens.get(i).type == TokenEnum.AND) {
                        addToStack(new TokenPreProcessamento(TokenEnum.AND));
                    } else if (tokens.get(i).type == TokenEnum.OR) {
                        addToStack(new TokenPreProcessamento(TokenEnum.OR));
                    } else if (tokens.get(i).type == TokenEnum.ISA) {
                        addToStack(new TokenPreProcessamento(TokenEnum.ISA));
                    } else if (tokens.get(i).type == TokenEnum.EQUIVALENT) {
                        addToStack(new TokenPreProcessamento(TokenEnum.EQUIVALENT));
                    } else if (tokens.get(i).type == TokenEnum.THAT) {
                        addToStack(new TokenPreProcessamento(TokenEnum.THAT));
                    } else if (tokens.get(i).type == TokenEnum.SOME) {
                        addToStack(new TokenPreProcessamento(TokenEnum.SOME));
                    } else if (tokens.get(i).type == TokenEnum.ALL) {
                        addToStack(new TokenPreProcessamento(TokenEnum.ALL));
                    } else if (tokens.get(i).type == TokenEnum.ONLY) {
                        addToStack(new TokenPreProcessamento(TokenEnum.ONLY));
                    } else if (tokens.get(i).type == TokenEnum.VALUE) {
                        addToStack(new TokenPreProcessamento(TokenEnum.VALUE));
                    } else if (tokens.get(i).type == TokenEnum.MIN) {
                        addToStack(new TokenPreProcessamento(TokenEnum.MIN));
                    } else if (tokens.get(i).type == TokenEnum.MAX) {
                        addToStack(new TokenPreProcessamento(TokenEnum.MAX));
                    } else if (tokens.get(i).type == TokenEnum.EXACTLY) {
                        addToStack(new TokenPreProcessamento(TokenEnum.EXACTLY));
                    } else {
                        println(stackState);
                        throw new SintaticAnalyzerException(tokens.get(i), "AND, OR, ISA, EQUIVALENT, THAT, SOME, ALL, ONLY, VALUE, MIN, MAX, EXACTLY");
                    }
                }
                i--;
            }
        }
    }
}
