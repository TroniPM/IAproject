/*
 * Copyright 2016 Paulo Mateus [UFRPE-UAG] <paulomatew@gmail.com>
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
package com.pmateus.core;

import com.pmateus.util.Session;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class InsertionAnalyser {

    /**
     * Alterar Compiler.class initEditor() caso adicionar mais palavras
     * resevadas.
     */
    public static String command_declare_entity = "ent";
    public static String command_declare_property = "pro";
    public static String command_intersection = "and";
    public static String command_union = "or";
    public static String command_subclass = "subc";
    public static String command_equiv = "equiv";
    public static String command_not = "not";
    public static String command_nor = "nor";
    public static String command_relationship = "rs";
    public static String command_qtf_existencial = "+";
    public static String command_qtf_universal = "*";
    public static String command_NEW_qtf_existencial = "some";
    public static String command_NEW_qtf_universal = "all";

    /*
    * TODO alterar isso aqui caso adicione palavras reservadas novas
     */
    public static String[] commands_list = new String[]{"some", "all", "value",
        "min", "max", "exactly", "that", "not", "and", "or", "only", "isa", "equivalent", "nor"};

    public String[] all_commands = null;

    public ArrayList<String> declaration_commands = new ArrayList<String>();
    public String erroString = "";

    public int commandsCount = 1;
    private CoreApplication coreApp;

    public void destroy() {
        coreApp = null;
        declaration_commands = null;
        all_commands = null;
    }

    public InsertionAnalyser() {

    }

    public InsertionAnalyser(CoreApplication aThis) {
        this.coreApp = aThis;
    }

    public boolean onSubmitCode(String submitted) {
        if (Session.isDebbug) {
            System.out.println(InsertionAnalyser.class + " onSubmitCode()");
        }
        /*TODO fazer a copia de atributos aqui, atualizar indice atual, e adicionar novo Object no array*/
        erroString += "--<font color=\"#696969\" face=\"\" /><b> Start</b>: Submit " + commandsCount + "</font> --<br/>";

        all_commands = submitted.split(";");

        boolean itsOk = verifySintaxeProccess();
        if (itsOk) {
            for (String command : all_commands) {

                if (command.startsWith("#")) {
                    continue;
                }

                executeCommand(command.trim());
            }

            try {
                coreApp.owlRepository.saveState();
            } catch (OWLOntologyCreationException ex) {
                Logger.getLogger(InsertionAnalyser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (itsOk) {
            erroString += "[<font color=\"#006400\" face=\"\" /><b>Successfully submitted</b></font>]<br/>";
        } else {
            /*TODO removo a copia de ATRIBUTOS feita, já que houve algum erro...*/
        }

        erroString += "--<font color=\"#696969\" face=\"\" /><b>End</b>: Submit " + commandsCount + "</font> --<br/>";
        commandsCount++;

        coreApp.atualizarTelas();

        return itsOk;
    }

    /**
     *
     * @return true if can continue
     */
    public boolean verifySintaxeProccess() {
        if (Session.isDebbug) {
            System.out.println(InsertionAnalyser.class + " verifySintaxeDeclaration()");
        }
        String erro1 = "Declaração contem palavra reserdada";
        String erro2 = "Comando não possui =";
        String erro3 = "Comando não possui palavra reservada no Inicio";
        String erro4 = "Comando possui uma sintaxe incorreta";
        String erro5 = "Comando não possui \";\"";
        String erroN1 = "Comando está com uso errado de {} ou ()";
        String erroN2 = "Comando não possui uma propriedade apos o {";
        String erroN3 = "Comando não possui ações esperadas and, or ou not";
        String erroN4 = "Comando não possui fim do comando )}";
        String erroN5 = "Comando não possui quantificador '" + command_NEW_qtf_existencial + "' ou '" + command_NEW_qtf_universal + "'";
        String erro_linha_singular = "linha";
        String erro_linha_plural = "linhas";
        int i = 1;

        String lastString = "";
        for (String in : all_commands) {
            if (in.startsWith("#")) {
                continue;
            }
            //Verifico se comando possui caracter IGUAL
            if (!in.contains("=")) {
                erroString += "[<font color=\"#ff0000\" face=\"\" /><b>ERRO:</b> " + erro2 + "</font> (" + erro_linha_singular + " " + i + "):<br/>" + in + "]<br/>";
                return false;
            }

            boolean check = checkSemiColonSitaxe(in);
            if (check) {
                erroString += "[<font color=\"#ff0000\" face=\"\" /><b>ERRO:</b> " + erro5 + "</font> (" + erro_linha_plural + " " + i + ", " + (i + 1) + "):<br/>" + in + "]<br/>";
                return false;
            }

            if (checkIfIsTheNewSintaxeScheme(in)) {
                /**
                 * AQUIIIIIIIIIIIIIIIII
                 */
                int id = checkNEWSintaxeScheme(in);

                switch (id) {
                    case 1:
                        erroString += "[<font color=\"#ff0000\" face=\"\" /><b>ERRO:</b> " + erroN1 + "</font> (" + erro_linha_singular + " " + i + "):<br/>" + in + "]<br/>";
                        return false;
                    case 2:
                        erroString += "[<font color=\"#ff0000\" face=\"\" /><b>ERRO:</b> " + erroN2 + "</font> (" + erro_linha_singular + " " + i + "):<br/>" + in + "]<br/>";
                        return false;
                    case 3:
                        erroString += "[<font color=\"#ff0000\" face=\"\" /><b>ERRO:</b> " + erroN3 + "</font> (" + erro_linha_singular + " " + i + "):<br/>" + in + "]<br/>";
                        return false;
                    case 4:
                        erroString += "[<font color=\"#ff0000\" face=\"\" /><b>ERRO:</b> " + erroN4 + "</font> (" + erro_linha_singular + " " + i + "):<br/>" + in + "]<br/>";
                        return false;
                    case 5:
                        erroString += "[<font color=\"#ff0000\" face=\"\" /><b>ERRO:</b> " + erroN5 + "</font> (" + erro_linha_singular + " " + i + "):<br/>" + in + "]<br/>";
                        return false;
                }
                i++;
                lastString = in;
                continue;//pulo essa verificação se for nova sintaxe
            }

            //Verifico se o comando possui uma palavra reservada no inicio
            if (!in.split("=")[0].toLowerCase().trim().equals(command_declare_entity)
                    && !in.split("=")[0].toLowerCase().trim().equals(command_declare_property)
                    && !in.split("=")[0].toLowerCase().trim().equals(command_intersection)
                    && !in.split("=")[0].toLowerCase().trim().equals(command_union)
                    && !in.split("=")[0].toLowerCase().trim().equals(command_not)
                    && !in.split("=")[0].toLowerCase().trim().equals(command_equiv)
                    && !in.split("=")[0].toLowerCase().trim().equals(command_subclass)
                    && !in.split("=")[0].toLowerCase().trim().equals(command_relationship)) {

                erroString += "[<font color=\"#ff0000\" face=\"\" /><b>ERRO:</b> " + erro3 + "</font> (" + erro_linha_singular + " " + i + "):<br/>" + in + "]<br/>";
                return false;

            }
            //Verifico se o comando possui palavra reserdada no corpo.
            check = checkStringByReservedWord(in);
            if (check) {
                erroString += "[<font color=\"#ff0000\" face=\"\" /><b>ERRO:</b> " + erro1 + "</font> (" + erro_linha_singular + " " + i + "):<br/>" + in + "]<br/>";
                return false;
            }

            check = checkStringSitaxe(in);
            if (check) {
                erroString += "[<font color=\"#ff0000\" face=\"\" /><b>ERRO:</b> " + erro4 + "</font> (" + erro_linha_singular + " " + i + "):<br/>" + in + "]<br/>";
                return false;
            }

            i++;
            lastString = in;
        }

        erroString += "[<font color=\"#0000ff\" face=\"\" /><b>Sintaxe OK</b></font>]<br/>";
        return true;
    }

    /**
     *
     * @param regex
     * @return true if regex contain reserved word. false if regex it's ok.
     */
    public boolean checkStringByReservedWord(String regex) {
        if (Session.isDebbug) {
            System.out.println(InsertionAnalyser.class + " checkStringByReservedWord()");
        }
        String[] aux = regex.split("=");
        if (aux.length > 1) {
            if (aux[1].contains(",")) {
                String[] oneByOne = aux[1].split(",");

                for (String in : oneByOne) {
                    if (checkStringByReservedWord(in)) {
                        return true;
                    }
                }
            }

        } else if (aux.length == 1) {
            if (regex.toLowerCase().trim().equals(command_declare_entity)) {
                return true;
            } else if (regex.toLowerCase().trim().equals(command_declare_property)) {
                return true;
            } else if (regex.toLowerCase().trim().equals(command_intersection)) {
                return true;
            } else if (regex.toLowerCase().trim().equals(command_union)) {
                return true;
            } else if (regex.toLowerCase().trim().equals(command_not)) {
                return true;
            } else if (regex.toLowerCase().trim().equals(command_equiv)) {
                return true;
            } else if (regex.toLowerCase().trim().equals(command_subclass)) {
                return true;
            } else if (regex.toLowerCase().trim().equals(command_relationship)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param regex
     * @return true if regex contain reserved word. false if regex it's ok.
     */
    public boolean checkStringSitaxe(String regex) {
        if (Session.isDebbug) {
            System.out.println(InsertionAnalyser.class + " checkStringSitaxe()");
        }
        String[] aux = regex.split("=");
        if (aux[0].equals(command_intersection)
                || aux[0].equals(command_union)
                || aux[0].equals(command_not)
                || aux[0].equals(command_equiv)
                || aux[0].equals(command_subclass)) {
            if (!aux[1].contains(">")) {
                return true;
            }
            String[] baux = aux[1].split(">");
            //prefixo de > é NADA
            if (baux[0].length() == 0) {
                return true;
            }
            //sufixo de < é NADA
            if (baux[1].length() == 0) {
                return true;
            }
        } else if (aux[0].trim().equals(command_declare_entity)
                || aux[0].trim().equals(command_declare_property)) {
            if (aux.length == 1) {
                return true;
            }
            if (aux[1].trim().length() == 0) {
                return true;
            }
        } else if (aux[0].trim().equals(command_relationship)) {
            if (aux[1].length() - aux[1].replace(">", "").length() < 2) {
                return true;
            }
            String[] baux = aux[1].split(">");
            //prefixo de > é NADA
            if (baux[0].length() == 0) {
                return true;
            } else if (baux[1].length() == 0) {
                return true;
            } else if (baux[2].length() == 0) {
                return true;
            }

            if (!baux[1].contains(command_qtf_existencial)
                    && !baux[1].contains(command_qtf_universal)) {
                return true;
            }
        }
        return false;
    }

    public void executeCommand(String fullLine) {
        if (Session.isDebbug) {
            System.out.println(InsertionAnalyser.class + " executeCommand(): " + fullLine);
        }

        if (fullLine.startsWith(command_declare_entity)) {
            addEntityInterface(fullLine.split("=")[1]);
        } else if (fullLine.startsWith(command_not)) {
            addDisjointInterface(fullLine.split("=")[1]);
        } else if (fullLine.startsWith(command_declare_property)) {
            addPropertyInterface(fullLine.split("=")[1]);
        } else if (fullLine.startsWith(command_intersection)) {
            addIntersectionInterface(fullLine.split("=")[1]);
        } else if (fullLine.startsWith(command_equiv)) {
            addEquivalenceInterface(fullLine.split("=")[1]);
        } else if (fullLine.startsWith(command_union)) {
            addUnionInterface(fullLine.split("=")[1]);
        } else if (fullLine.startsWith(command_subclass)) {
            addSubclassInterface(fullLine.split("=")[1]);
        } else if (fullLine.startsWith(command_relationship)) {
            addRelationshipInterface(fullLine.split("=")[1]);
        } else {
            addCommandNewSintaxe(fullLine);
        }

    }

    public void addEntityInterface(String fullCommand) {
        if (Session.isDebbug) {
            System.out.println(InsertionAnalyser.class + " addEntityInterface(): " + fullCommand);
        }
        coreApp.owlRepository.addEntity(fullCommand);
    }

    public void addPropertyInterface(String fullCommand) {
        if (Session.isDebbug) {
            System.out.println(InsertionAnalyser.class + " addPropertyInterface(): " + fullCommand);
        }
        coreApp.owlRepository.addProperty(fullCommand);
    }

    public void addIntersectionInterface(String fullCommand) {
        if (Session.isDebbug) {
            System.out.println(InsertionAnalyser.class + " addIntersectionInterface(): " + fullCommand);
        }
        coreApp.owlRepository.addIntersection(fullCommand);
    }

    public void addUnionInterface(String fullCommand) {
        if (Session.isDebbug) {
            System.out.println(InsertionAnalyser.class + " addUnionInterface(): " + fullCommand);
        }
        coreApp.owlRepository.addUnion(fullCommand);
    }

    public void addSubclassInterface(String fullCommand) {
        if (Session.isDebbug) {
            System.out.println(InsertionAnalyser.class + " addSubclassInterface(): " + fullCommand);
        }
        coreApp.owlRepository.addSubclass(fullCommand);
    }

    public void addRelationshipInterface(String fullCommand) {
        if (Session.isDebbug) {
            System.out.println(InsertionAnalyser.class + " addRelationshipInterface(): " + fullCommand);
        }
        coreApp.owlRepository.addRelationship(fullCommand);
    }

    private boolean checkSemiColonSitaxe(String in) {
        int qntd = in.length() - in.replace("=", "").length();

        if (qntd > 1) {
            return true;
        }
        return false;
    }

    private void addDisjointInterface(String fullCommand) {
        coreApp.owlRepository.addDisjointClass(fullCommand);
    }

    private void addEquivalenceInterface(String fullCommand) {
        coreApp.owlRepository.addEquivalencesClass(fullCommand);

    }

    private boolean checkIfIsTheNewSintaxeScheme(String comando) {
        comando = comando.replace(" ", "");

        String auxEsquerda = comando.split("=")[0];
        String auxDireita = comando.split("=")[1];

        if (!auxEsquerda.equals(command_declare_property)
                || !auxEsquerda.equals(command_intersection)
                || !auxEsquerda.equals(command_union)
                || !auxEsquerda.equals(command_not)
                || !auxEsquerda.equals(command_equiv)
                || !auxEsquerda.equals(command_subclass)
                || !auxEsquerda.equals(command_relationship)) {
            if (auxDireita.startsWith(command_NEW_qtf_existencial) || auxDireita.startsWith(command_NEW_qtf_universal)) {

                return true;
            }
        }

        return false;

    }

    private int checkNEWSintaxeScheme(String command) {
        if (command.startsWith("#")) {
            return 0;
        }

        int qntd1 = command.length() - command.replace("{", "").length();
        int qntd2 = command.length() - command.replace("}", "").length();

        if (qntd1 != qntd2) {
            return 1;//Erro de } ou )
        }

        qntd1 = command.length() - command.replace("(", "").length();
        qntd2 = command.length() - command.replace(")", "").length();

        if (qntd1 != qntd2) {
            return 1;
        }

        String cmdSemEspaco = command.replace(" ", "");
        String prop = cmdSemEspaco.split("\\{")[1].split(",")[0];
        if (prop.startsWith(command_intersection) || prop.startsWith(command_union)/* || prop.startsWith(command_not)*/) {
            return 2;//Erro de não possui propriedade
        }

        if (!cmdSemEspaco.contains("," + command_intersection + "(")
                && !cmdSemEspaco.contains("," + command_union + "(") /*&& !cmdSemEspaco.contains("," + command_not + "(")*/) {
            return 3;//Erro de não contém ações esperadas, AND OR e NOT

        }

        if (!cmdSemEspaco.contains(")}")) {
            return 4; //Não está sendo finalizado o comando
        }

        String quantificador = cmdSemEspaco.split("=")[1].split("\\{")[0];
        if (!quantificador.equals(command_NEW_qtf_existencial)
                && !quantificador.equals(command_NEW_qtf_universal)) {
            return 5; //Não está sendo finalizado o comando
        }

        return 0;
    }

    private void addCommandNewSintaxe(String fullLine) {
        //coreApp.owlRepository.addEquivalencesClass(fullCommand);
        fullLine = fullLine.replace(" ", "");

        String mainClasse = fullLine.split("=")[0];
        String quantificador = fullLine.split("=")[1].split("\\{")[0].replace(" ", "");//some/all
        if (quantificador.equals(command_NEW_qtf_existencial)) {
            quantificador = "+";
        } else if (quantificador.equals(command_NEW_qtf_universal)) {
            quantificador = "*";
        }
        String propriedade = fullLine.split("\\{")[1].split(",")[0];//some/all

        String comandAnd = "";
        String comandAnd2 = "";
        String comandOr = "";
        String comandOr2 = "";
        String comandNot = "";
        String comandNot2 = "";

        if (fullLine.contains(command_union + "(")) {
            String comando = fullLine.split(command_union + "\\(")[1].split("\\)")[0];
            String classes[] = comando.split(",");
            for (String in : classes) {
                comandOr += "," + in;
                comandOr2 += "," + in;
            }

            comandOr = comandOr.replaceFirst(",", "");
            comandOr2 = comandOr2.replaceFirst(",", "");

            comandOr = mainClasse + ">" + comandOr;
            comandOr2 = mainClasse + ">" + quantificador + propriedade + ">" + comandOr2;
            addUnionInterfaceNew(comandOr, comandOr2);
        }
        if (fullLine.contains(command_intersection + "(")) {
            String comando = fullLine.split(command_intersection + "\\(")[1].split("\\)")[0];
            String classes[] = comando.split(",");
            for (String in : classes) {
                comandAnd += "," + in;
                comandAnd2 += "," + in;
            }

            comandAnd = comandAnd.replaceFirst(",", "");
            comandAnd2 = comandAnd2.replaceFirst(",", "");

            comandAnd = mainClasse + ">" + comandAnd;
            comandAnd2 = mainClasse + ">" + quantificador + propriedade + ">" + comandAnd2;
            addIntersectionInterfaceNew(comandAnd, comandAnd2);
        }
        /*if (fullLine.contains(command_not + "(")) {
         String comando = fullLine.split(command_not + "\\(")[1].split("\\)")[0];
         String classes[] = comando.split(",");
         for (String in : classes) {
         comandNot += "," + in;
         comandNot2 += "," + in;
         }

         comandNot = comandNot.replaceFirst(",", "");
         comandNot2 = comandNot2.replaceFirst(",", "");

         comandNot = mainClasse + ">" + comandNot;
         comandNot2 = mainClasse + ">" + quantificador + propriedade + ">" + comandNot2;
         addDisjointInterfaceNew(comandNot, comandNot2);
         }*/

    }

    private void addUnionInterfaceNew(String fullCommand, String fullCommand2) {
        coreApp.owlRepository.addUnion(fullCommand);
        coreApp.owlRepository.addRelationship(fullCommand2);

    }

    private void addIntersectionInterfaceNew(String fullCommand, String fullCommand2) {
        coreApp.owlRepository.addIntersection(fullCommand);
        coreApp.owlRepository.addRelationship(fullCommand2);
    }

    private void addDisjointInterfaceNew(String fullCommand, String fullCommand2) {
        coreApp.owlRepository.addDisjointClass(fullCommand);
        //coreApp.owlRepository.addRelationship(fullCommand2);
    }
}
