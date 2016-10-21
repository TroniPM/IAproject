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

import com.pmateus.gui.JFramePrincipal;
import com.pmateus.util.Errors;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.apache.commons.io.IOUtils;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class CoreApplication {

    public int lastErro_OWLREPOSITORY = 0;
    public int lastErro_ADD = 0;
    public int lastErro_PREFERENCE = 0;
    public int lastErro_QUERY = 0;
    public boolean canShowMsgDialog = false;

    public JFramePrincipal frame;
    public InsertionAnalyser iAnalyser;
    public OWLRepository owlRepository;
    public PelletRepository pelletRepository;

    private Timer lookingErrors;
    public TimerTask tarefa;

    public CoreApplication(JFramePrincipal frame, boolean canReadFile, String path) {
        this.frame = frame;
        this.iAnalyser = new InsertionAnalyser(this);
        this.owlRepository = new OWLRepository(this);
        this.pelletRepository = new PelletRepository(this);
        lookingErrors = new Timer();
        tarefa = new TimerTask() {

            @Override
            public void run() {
                if (lastErro_OWLREPOSITORY != owlRepository.currentErro) {
                    lastErro_OWLREPOSITORY = currentErro + 0;

                    if (lastErro_OWLREPOSITORY == 1) {
                        JOptionPane.showMessageDialog(null, Errors.erro_owlrepository_1);
                    }
                }
                if (lastErro_PREFERENCE != owlRepository.currentErro) {
                    lastErro_PREFERENCE = currentErro + 0;

                    if (lastErro_PREFERENCE == 1) {
                        JOptionPane.showMessageDialog(null, Errors.erro_pref_1);
                    }
                }
            }
        };

        try {
            readConfigFile();
        } catch (IOException ex) {
            //Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
            currentErro = 1;
        }

        if (canReadFile) {
            owlRepository.init(path);
        } else {
            owlRepository.init(ontologyPath);
        }
        try {
            owlRepository.saveState();
        } catch (OWLOntologyCreationException ex) {
            Logger.getLogger(CoreApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void destroy() {
        frame = null;
        iAnalyser.destroy();
        iAnalyser = null;
        owlRepository.destroy();
        owlRepository = null;

        lookingErrors.cancel();
        lookingErrors = null;
        tarefa.cancel();
        tarefa = null;
    }

    public void startLookingForErrors() {
        lookingErrors.scheduleAtFixedRate(tarefa, 0, 500);
    }

    public void stopLookingForErrors() {
        if (lookingErrors != null) {
            lookingErrors.cancel();
        }
    }

    public boolean onSubmitted(String submitted) {
        boolean aux = iAnalyser.onSubmitCode(submitted);

        //Automatizar reasoning
        //frame.tabPellet.doReasoning();
        return aux;
    }

    public void atualizarTelas() {
        
        frame.tabOntology.atualizar();
        frame.tabReasoner.atualizar();
    }

    /**
     * A PARTIR DAQUI É PREFERENCES SCREEN
     *
     * @throws IOException
     */
    public String ontologyPath = null;
    public String ontologySavePath = null;

    public int currentErro = 0;

    public void readConfigFile() throws IOException {

        InputStreamReader inputStream = new InputStreamReader(new FileInputStream("./data/config.txt"), "UTF-8");
        String everything = null;
        try {
            everything = IOUtils.toString(inputStream);
        } finally {
            inputStream.close();
        }

        if (everything != null) {
            String[] config = everything.split("\n");

            for (int i = 0; i < config.length; i++) {
                /*if (!config[i].startsWith("##") && config[i].split("=")[0].contains("ontology_to_load_at_start_patch")) {
                 ontologyPath = config[i].split("=")[1].trim();
                 }
                 if (!config[i].startsWith("##") && config[i].split("=")[0].contains("save_current_ontology_path")) {
                 ontologySavePath = config[i].split("=")[1].trim();
                 }*/
            }
        } else {
            //ontologyPath = "";
            //ontologySavePath = "";
        }
    }

    public void saveConfigFile() throws FileNotFoundException, IOException {
        String configuracao = "current_path = " + ontologyPath;
        File file = new File("./data/config.txt");
        FileOutputStream fop = new FileOutputStream(file);

        // if file doesnt exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }

        // get the content in bytes
        byte[] contentInBytes = configuracao.getBytes();

        fop.write(contentInBytes);
        fop.flush();
        fop.close();
    }
}
