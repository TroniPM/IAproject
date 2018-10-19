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
public class RegraProducao {

    public RegraProducaoEnum tipo = null;
    public boolean dontPrintException = false;

//    public String method = null;
//    public RegraProducao(String method) {
//        this.method = method;
//    }
//    public void print() {
//        System.out.println("-----------------");
//        System.out.println("Regra: <" + method + ">");
//        System.out.println("-----------------");
//    }
//    public RegraProducao(String method, int tipo, boolean dontPrintException) {
//        this.method = method;
//        this.tipo = Regras.parse(tipo);
//        this.dontPrintException = dontPrintException;
//    }
    public RegraProducao(int tipo) {
        this.tipo = RegraProducaoEnum.parse(tipo);;
    }

    public RegraProducao(RegraProducaoEnum regra) {
        this.tipo = regra;
    }

    public RegraProducao(int tipo, boolean dontPrintException) {
        this.tipo = RegraProducaoEnum.parse(tipo);
        this.dontPrintException = dontPrintException;
    }

    public void print() {
        System.out.println("-----------------");
        System.out.println("Regra: <" + tipo.toString() + ">");
        System.out.println("-----------------");
    }
}
