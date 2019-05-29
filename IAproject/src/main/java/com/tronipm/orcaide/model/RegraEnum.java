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
public enum RegraEnum {
    DEF(1), CLASSE(2), RES_MAIN(3), RES_CLASSE(4), RES_PROP(5), PONTO_VIRGULA(6), DEF2(7),
    MODIFIER_ALL(50),//MEANS TOKEN IS ONE OF: {"and", "or" "isa", "equivalent", "that", "some", "all", "only", "value", "min", "max", "exactly"}
    MODIFIER_CLASS(51),//MEANS TOKEN IS ONE OF: {"and", "or" "isa", "equivalent", "that"}
    MODIFIER_PROPERTY(52),//MEANS TOKEN IS ONE OF: {"some", "all", "only"};
    MODIFIER_INDIVIDUAL(53)//MEANS TOKEN IS ONE OF: {"value", "min", "max", "exactly"};
    ;

    private final int valor;

    RegraEnum(int valorOpcao) {
        valor = valorOpcao;
    }

    public int getValor() {
        return valor;
    }

    public static RegraEnum parse(Integer id) {
        RegraEnum job = null;

        if (null != id) {
            switch (id) {
                case 1:
                    job = RegraEnum.DEF;
                    break;
                case 2:
                    job = RegraEnum.CLASSE;
                    break;
                case 3:
                    job = RegraEnum.RES_MAIN;
                    break;
                case 4:
                    job = RegraEnum.RES_CLASSE;
                    break;
                case 5:
                    job = RegraEnum.RES_PROP;
                    break;
                case 6:
                    job = RegraEnum.PONTO_VIRGULA;
                    break;
                case 7:
                    job = RegraEnum.DEF2;
                    break;
                case 50:
                    job = RegraEnum.MODIFIER_ALL;
                    break;
                case 51:
                    job = RegraEnum.MODIFIER_CLASS;
                    break;
                case 52:
                    job = RegraEnum.MODIFIER_PROPERTY;
                    break;
                case 53:
                    job = RegraEnum.MODIFIER_INDIVIDUAL;
                    break;
                default:
                    break;
            }
        }

        return job;
    }

    @Override
    public String toString() {
        if (valor == RegraEnum.DEF.getValor()) {
            return "DEF";
        } else if (valor == RegraEnum.DEF2.getValor()) {
            return "DEF2";
        } else if (valor == RegraEnum.CLASSE.getValor()) {
            return "CLASSE";
        } else if (valor == RegraEnum.RES_MAIN.getValor()) {
            return "RES_MAIN";
        } else if (valor == RegraEnum.RES_CLASSE.getValor()) {
            return "RES_CLASSE";
        } else if (valor == RegraEnum.RES_PROP.getValor()) {
            return "RES_PROP";
        } else if (valor == RegraEnum.PONTO_VIRGULA.getValor()) {
            return "PONTO_VIRGULA";
        } else if (valor == RegraEnum.MODIFIER_ALL.getValor()) {
            return "MODIFIER_ALL";
        } else if (valor == RegraEnum.MODIFIER_CLASS.getValor()) {
            return "MODIFIER_CLASS";
        } else if (valor == RegraEnum.MODIFIER_PROPERTY.getValor()) {
            return "MODIFIER_PROPERTY";
        } else if (valor == RegraEnum.MODIFIER_INDIVIDUAL.getValor()) {
            return "MODIFIER_INDIVIDUAL";
        }
        return null;
    }
}
