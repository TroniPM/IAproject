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
package com.pmateus.gui;

import com.pmateus.gui.util.popupmenu.PopUpMenuAtRightClickNormalEditorListener;
import com.pmateus.gui.util.xmlpack.XmlTextPane;
import com.pmateus.util.Session;
import com.pmateus.util.UtilMethods;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.formats.KRSS2DocumentFormat;
import org.semanticweb.owlapi.formats.LatexDocumentFormat;
import org.semanticweb.owlapi.formats.ManchesterSyntaxDocumentFormat;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.OWLDocumentFormat;

public class Ontology extends javax.swing.JPanel {

    private JFramePrincipal jFrameMain;
    public SwingFXWebView aWebViewer = null;

    public boolean houveAlteracao = false;
    private String pathToIndex = "\\data\\viewer\\data\\";//Com \\ no inicio e \\ no fim
    private String deleteBatFile = "delete.bat";//File name
    private String converterBatFile = "conversor.bat";//file name
    private String outputFileNameToConversor = "foaf.owl";//file name
    private ComboBoxModel outputModels = new DefaultComboBoxModel(new String[]{"RDF/XML", "KRSS2", "Latex", "Manchester OWL Syntax", "OWL/XML", "OWL Functional Syntax", "Turtle"});

    ;
    /**
     * Creates new form Compiler
     */
    public String getSelectedText() {
        if (jTextPane1.getSelectedText() != null) { // See if they selected something 
            return jTextPane1.getSelectedText();
            // Do work with String s
        }
        return "";
    }

    public void destroy() {
        jFrameMain = null;
        aWebViewer = null;
    }

    public Ontology(JFramePrincipal frameMain) {
        this.jFrameMain = frameMain;
        initComponents();

        jTextPane1.addMouseListener(new PopUpMenuAtRightClickNormalEditorListener(jTextPane1));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane1 = new XmlTextPane();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/pmateus/gui/Bundle"); // NOI18N
        jButton1.setText(bundle.getString("Ontology.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText(bundle.getString("Ontology.jButton2.text")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jTextPane1.setEditable(false);
        jTextPane1.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jScrollPane2.setViewportView(jTextPane1);

        jComboBox1.setModel(outputModels);
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        jLabel1.setText(bundle.getString("Ontology.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 174, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        initGraphViewer();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        //copy all
        String myString = jTextPane1.getText();
        UtilMethods.copyToClipboard(myString);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            //{"RDF/XML", "KRSS2", "Latex", "Manchester OWL Syntax", "OWL/XML", "OWL Functional Syntax", "Turtle"});
            int i = jComboBox1.getSelectedIndex();
            OWLDocumentFormat format = null;
            switch (i) {
//                case 0: {
//                }
//                break;
                case 1: {
                    format = (new KRSS2DocumentFormat());
                }
                break;
                case 2: {
                    format = (new LatexDocumentFormat());
                }
                break;
                case 3: {
                    format = (new ManchesterSyntaxDocumentFormat());
                }
                break;
                case 4: {
                    format = (new OWLXMLDocumentFormat());
                }
                break;
                case 5: {
                    format = (new FunctionalSyntaxDocumentFormat());
                }
                break;
                case 6: {
                    format = (new TurtleDocumentFormat());
                }
                break;
            }
            refreshOntologyTextViewer(format);
        }
    }//GEN-LAST:event_jComboBox1ItemStateChanged
    public void refreshOntologyTextViewer() {
        refreshOntologyTextViewer(null);
    }

    public void refreshOntologyTextViewer(OWLDocumentFormat format) {
        if (Session.isDebbug) {
            System.out.println(Compiler.class + " refreshOntologyTextViewer()");
        }
        try {
            jFrameMain.coreApp.owlRepository.saveOntologyToOutputStream(format);
            String aString1 = new String(jFrameMain.coreApp.owlRepository.currentOutputStreamOntology.toString()).trim().replaceAll("\n+", "\n");
            jTextPane1.setText(aString1);

        } catch (Exception e) {
        }

        jTextPane1.setCaretPosition(0);//getViewport().setViewPosition(new Point(0, 0));
    }

    public void atualizar() {
        refreshOntologyTextViewer();
    }

    public void initGraphViewer() {
        Process aProcess = null;
        //Deleting current files...
        try {
            String pathDel = System.getProperty("user.dir") + pathToIndex + deleteBatFile;
            System.out.println("PATH DELETE: " + pathDel);
            aProcess = Runtime.getRuntime().exec("cmd /c start /wait " + pathDel);
            aProcess.waitFor();
        } catch (IOException ex) {
            if (Session.isDebbug) {
                Logger.getLogger(Compiler.class.getName()).log(Level.SEVERE, null, ex);
            }

            JOptionPane.showMessageDialog(null, "We couldn't execute some scripts. Try to run this programm as Administrator:\n" + ex.getLocalizedMessage(), "Problem to execute script", JOptionPane.ERROR_MESSAGE);
        } catch (InterruptedException ex) {
            if (Session.isDebbug) {
                Logger.getLogger(Compiler.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(null, "We couldn't execute some scripts. Try to run this programm as Administrator:\n" + ex.getLocalizedMessage(), "Problem to execute script", JOptionPane.ERROR_MESSAGE);
        }

        //Creating new files e converting...
        try {
            String pathConv = System.getProperty("user.dir") + pathToIndex + outputFileNameToConversor;
            System.out.println("PATH TO SAVE: " + pathConv);
            jFrameMain.coreApp.owlRepository.saveOntologyToView(pathConv);

            String pathConverted = System.getProperty("user.dir") + pathToIndex + converterBatFile;
            System.out.println("PATH CONVERTED: " + pathConverted);
            aProcess = Runtime.getRuntime().exec("cmd /c start /wait " + pathConverted);
            aProcess.waitFor();

        } catch (IOException ex) {
            if (Session.isDebbug) {
                Logger.getLogger(Compiler.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(null, "We couldn't execute some scripts. Try to run this programm as Administrator:\n" + ex.getLocalizedMessage(), "Problem to execute script", JOptionPane.ERROR_MESSAGE);
        } catch (InterruptedException ex) {
            if (Session.isDebbug) {
                Logger.getLogger(Compiler.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(null, "We couldn't execute some scripts. Try to run this programm as Administrator:\n" + ex.getLocalizedMessage(), "Problem to execute script", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            if (Session.isDebbug) {
                Logger.getLogger(Compiler.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(null, "We couldn't execute some scripts. Try to run this programm as Administrator:\n" + ex.getLocalizedMessage(), "Problem to execute script", JOptionPane.ERROR_MESSAGE);
        }

        if (aWebViewer != null) {
            aWebViewer.myJFrame.dispatchEvent(new WindowEvent(aWebViewer.myJFrame, WindowEvent.WINDOW_CLOSING));
        }

        aWebViewer = new SwingFXWebView(this);

        aWebViewer.requestFocus();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    public javax.swing.JEditorPane jTextPane1;
    // End of variables declaration//GEN-END:variables

}
