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
package com.tronipm.orcaide.model;

/**
 *
 * @author Matt
 */
public enum TokenEnum {
    END(100),
    NUMBER(99),
    IDENTIFIER(98),
    //negacao
    //	{"not"};
    NOT(1),
    NOR(14),
    //classe
    //	{"and", "or" "isa", "equivalent", "that"};
    AND(2), OR(3), ISA(4), EQUIVALENT(5), THAT(6), DISJOINT(15),
    //property
    //	{"some", "all", "only"};
    SOME(7), ALL(8), ONLY(9),
    //individuo/quantidade
    //	{"value", "min", "max", "exactly"};
    VALUE(10), MIN(11), MAX(12), EXACTLY(13),
    //UTILS
    PARENTESE_ABRIR(51), PARENTESE_FECHAR(52),
    CHAVES_ABRIR(53), CHAVES_FECHAR(54),
    COLCHETES_ABRIR(55), COLCHETES_FECHAR(56),
    PONTO_VIRGULA(50);

    private final int valor;

    TokenEnum(int valorOpcao) {
        valor = valorOpcao;
    }

    public int getValor() {
        return valor;
    }

    public static TokenEnum parse(Integer id) {
        TokenEnum job = null;

        if (null != id) {
            switch (id) {
                case 98:
                    job = TokenEnum.IDENTIFIER;
                    break;
                case 99:
                    job = TokenEnum.NUMBER;
                    break;
                case 100:
                    job = TokenEnum.END;
                    break;
                case 1:
                    job = TokenEnum.NOT;
                    break;
                case 2:
                    job = TokenEnum.AND;
                    break;
                case 3:
                    job = TokenEnum.OR;
                    break;
                case 4:
                    job = TokenEnum.ISA;
                    break;
                case 5:
                    job = TokenEnum.EQUIVALENT;
                    break;
                case 6:
                    job = TokenEnum.THAT;
                    break;
                case 7:
                    job = TokenEnum.SOME;
                    break;
                case 8:
                    job = TokenEnum.ALL;
                    break;
                case 9:
                    job = TokenEnum.ONLY;
                    break;
                case 10:
                    job = TokenEnum.VALUE;
                    break;
                case 11:
                    job = TokenEnum.MIN;
                    break;
                case 12:
                    job = TokenEnum.MAX;
                    break;
                case 13:
                    job = TokenEnum.EXACTLY;
                    break;
                case 14:
                    job = TokenEnum.NOR;
                    break;
                case 15:
                    job = TokenEnum.DISJOINT;
                    break;
                case 50:
                    job = TokenEnum.PONTO_VIRGULA;
                    break;
                case 51:
                    job = TokenEnum.PARENTESE_ABRIR;
                    break;
                case 52:
                    job = TokenEnum.PARENTESE_FECHAR;
                    break;
                case 53:
                    job = TokenEnum.CHAVES_ABRIR;
                    break;
                case 54:
                    job = TokenEnum.CHAVES_FECHAR;
                    break;
                case 55:
                    job = TokenEnum.COLCHETES_ABRIR;
                    break;
                case 56:
                    job = TokenEnum.COLCHETES_FECHAR;
                    break;
                default:
                    break;
            }
        }
        return job;
    }

    /**
     *
     * @param id is UPPERCASE
     * @return
     */
    public static TokenEnum parse(String id) {
        TokenEnum job = null;

        if (null != id) {
            switch (id) {
                //Desabilito aqui para não permitir a entrada dessa palavra no codigo fonte
//                case "END":
//                    job = TokenEnum.END;
//                    break;
                case "NOT":
                    job = TokenEnum.NOT;
                    break;
                case "NOR":
                    job = TokenEnum.NOR;
                    break;
                case "AND":
                    job = TokenEnum.AND;
                    break;
                case "OR":
                    job = TokenEnum.OR;
                    break;
                case "ISA":
                    job = TokenEnum.ISA;
                    break;
                case "EQUIVALENT":
                    job = TokenEnum.EQUIVALENT;
                    break;
                case "THAT":
                    job = TokenEnum.THAT;
                    break;
                case "SOME":
                    job = TokenEnum.SOME;
                    break;
                case "ALL":
                    job = TokenEnum.ALL;
                    break;
                case "DISJOINT":
                    job = TokenEnum.DISJOINT;
                    break;
                case "ONLY":
                    job = TokenEnum.ONLY;
                    break;
                case "VALUE":
                    job = TokenEnum.VALUE;
                    break;
                case "MIN":
                    job = TokenEnum.MIN;
                    break;
                case "MAX":
                    job = TokenEnum.MAX;
                    break;
                case "EXACTLY":
                    job = TokenEnum.EXACTLY;
                    break;
                case ";":
                    job = TokenEnum.PONTO_VIRGULA;
                    break;
                case "(":
                    job = TokenEnum.PARENTESE_ABRIR;
                    break;
                case ")":
                    job = TokenEnum.PARENTESE_FECHAR;
                    break;
                case "{":
                    job = TokenEnum.CHAVES_ABRIR;
                    break;
                case "}":
                    job = TokenEnum.CHAVES_FECHAR;
                    break;
                case "[":
                    job = TokenEnum.COLCHETES_ABRIR;
                    break;
                case "]":
                    job = TokenEnum.COLCHETES_FECHAR;
                    break;
                default:
                    if (Character.isDigit(id.charAt(0))) {
                        //Se começa com digito mas não tem LETRA depois
                        if (!id.matches(".*[a-zA-Z]+.*")) {
                            job = TokenEnum.NUMBER;
                        }
                    } else {
                        job = TokenEnum.IDENTIFIER;
                    }
                    break;
            }
        }
        return job;
    }

    @Override
    public String toString() {
        if (valor == TokenEnum.NOT.getValor()) {
            return "NOT";
        } else if (valor == TokenEnum.NOR.getValor()) {
            return "NOR";
        } else if (valor == TokenEnum.AND.getValor()) {
            return "AND";
        } else if (valor == TokenEnum.OR.getValor()) {
            return "OR";
        } else if (valor == TokenEnum.ISA.getValor()) {
            return "ISA";
        } else if (valor == TokenEnum.EQUIVALENT.getValor()) {
            return "EQUIVALENT";
        } else if (valor == TokenEnum.THAT.getValor()) {
            return "THAT";
        } else if (valor == TokenEnum.SOME.getValor()) {
            return "SOME";
        } else if (valor == TokenEnum.ALL.getValor()) {
            return "ALL";
        } else if (valor == TokenEnum.DISJOINT.getValor()) {
            return "DISJOINT";
        } else if (valor == TokenEnum.ONLY.getValor()) {
            return "ONLY";
        } else if (valor == TokenEnum.VALUE.getValor()) {
            return "VALUE";
        } else if (valor == TokenEnum.MIN.getValor()) {
            return "MIN";
        } else if (valor == TokenEnum.MAX.getValor()) {
            return "MAX";
        } else if (valor == TokenEnum.EXACTLY.getValor()) {
            return "EXACTLY";
        } else if (valor == TokenEnum.NUMBER.getValor()) {
            return "NUMBER";
        } else if (valor == TokenEnum.IDENTIFIER.getValor()) {
            return "IDENTIFIER";
        } else if (valor == TokenEnum.PONTO_VIRGULA.getValor()) {
            return "PONTO_VIRGULA";
        } else if (valor == TokenEnum.PARENTESE_ABRIR.getValor()) {
            return "PARENTESE_ABRIR";
        } else if (valor == TokenEnum.PARENTESE_FECHAR.getValor()) {
            return "PARENTESE_FECHAR";
        } else if (valor == TokenEnum.CHAVES_ABRIR.getValor()) {
            return "CHAVES_ABRIR";
        } else if (valor == TokenEnum.CHAVES_FECHAR.getValor()) {
            return "CHAVES_FECHAR";
        } else if (valor == TokenEnum.COLCHETES_ABRIR.getValor()) {
            return "COLCHETES_ABRIR";
        } else if (valor == TokenEnum.COLCHETES_FECHAR.getValor()) {
            return "COLCHETES_FECHAR";
        } else if (valor == TokenEnum.END.getValor()) {
            return "END";
        }
        return null;
    }
}
