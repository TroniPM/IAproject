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
package com.pmateus.compiler.novo;

/**
 *
 * @author Matt
 */
public enum RegraEnum {
    DEF(1), CLASSE(2), RES_MAIN(3), RES_CLASSE(4), RES_PROP(5), PONTO_VIRGULA(6);

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
        }

        return null;
    }
}
