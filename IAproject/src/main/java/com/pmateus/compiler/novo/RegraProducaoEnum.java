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
public enum RegraProducaoEnum {
    DEF(1), CLASSE(2), RES_MAIN(3), RES_CLASSE(4), RES_PROP(5);

    private final int valor;

    RegraProducaoEnum(int valorOpcao) {
        valor = valorOpcao;
    }

    public int getValor() {
        return valor;
    }

    public static RegraProducaoEnum parse(Integer id) {
        RegraProducaoEnum job = null;

        if (null != id) {
            switch (id) {
                case 1:
                    job = RegraProducaoEnum.DEF;
                    break;
                case 2:
                    job = RegraProducaoEnum.CLASSE;
                    break;
                case 3:
                    job = RegraProducaoEnum.RES_MAIN;
                    break;
                case 4:
                    job = RegraProducaoEnum.RES_CLASSE;
                    break;
                case 5:
                    job = RegraProducaoEnum.RES_PROP;
                    break;
                default:
                    break;
            }
        }

        return job;
    }

    @Override
    public String toString() {
        if (valor == RegraProducaoEnum.DEF.getValor()) {
            return "DEF";
        } else if (valor == RegraProducaoEnum.CLASSE.getValor()) {
            return "CLASSE";
        } else if (valor == RegraProducaoEnum.RES_MAIN.getValor()) {
            return "RES_MAIN";
        } else if (valor == RegraProducaoEnum.RES_CLASSE.getValor()) {
            return "RES_CLASSE";
        } else if (valor == RegraProducaoEnum.RES_PROP.getValor()) {
            return "RES_PROP";
        }

        return null;
    }
}
