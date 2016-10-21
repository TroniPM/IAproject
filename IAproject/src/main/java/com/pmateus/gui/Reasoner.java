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
import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class Reasoner extends javax.swing.JPanel {

    private JFramePrincipal jFrameMain;

    private DefaultStyledDocument doc;
    public javax.swing.JTextPane editor;

    public void destroy() {
        jFrameMain = null;
    }

    /**
     * Creates new form Query
     */
    public Reasoner(JFramePrincipal frameMain) {
        this.jFrameMain = frameMain;
        initEditor();
        initComponents();

        editor = jTextPane1;

        jTextPane1.addMouseListener(new PopUpMenuAtRightClickNormalEditorListener(jTextPane1));
        jTextPane2.addMouseListener(new PopUpMenuAtRightClickNormalEditorListener(jTextPane2));
    }

    private void initEditor() {

        final StyleContext cont = StyleContext.getDefaultStyleContext();
        final AttributeSet attr_blue = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.BLUE);
        //final AttributeSet attr_green = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.GREEN);
        final AttributeSet attrBlack = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.BLACK);
        final AttributeSet attr_red = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.RED);
        doc = new DefaultStyledDocument() {
            @Override
            public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
                super.insertString(offset, str, a);

                String text = getText(0, getLength());
                int before = findLastNonWordChar(text, offset);
                if (before < 0) {
                    before = 0;
                }
                int after = findFirstNonWordChar(text, offset + str.length());
                int wordL = before;
                int wordR = before;

                while (wordR <= after) {
                    if (wordR == after || String.valueOf(text.charAt(wordR)).matches("\\W")) {
                        if (text.substring(wordL, wordR).matches(
                                "(\\W)*("
                                + "DisjointWith" + "|"
                                + "SubClassOf" + "|"
                                + "NoAxiom" + "|"
                                + "EquivalentClasses" + "|"
                                + "EquivalentTo" + ")"
                        )) {
                            setCharacterAttributes(wordL, wordR - wordL, attr_blue, false);
                        } else if (text.substring(wordL, wordR).matches(
                                "(\\W)*("
                                + "IF" + "|"
                                + "THEN" + "|"
                                + "AND" + ")"
                        )) {
                            setCharacterAttributes(wordL, wordR - wordL, attr_red, false);
                        } else {
                            setCharacterAttributes(wordL, wordR - wordL, attrBlack, false);
                        }
                        wordL = wordR;
                    }
                    wordR++;
                }
            }

            @Override
            public void remove(int offs, int len) throws BadLocationException {
                super.remove(offs, len);

                String text = getText(0, getLength());
                int before = findLastNonWordChar(text, offs);
                if (before < 0) {
                    before = 0;
                }
                int after = findFirstNonWordChar(text, offs);

                if (text.substring(before, after).matches(
                        "(\\W)*("
                        + "DisjointWith" + "|"
                        + "SubClassOf" + "|"
                        + "NoAxiom" + "|"
                        + "EquivalentClasses" + "|"
                        + "EquivalentTo" + ")"
                )) {
                    setCharacterAttributes(before, after - before, attr_blue, false);
                } else if (text.substring(before, after).matches(
                        "(\\W)*("
                        + "IF" + "|"
                        + "THEN" + "|"
                        + "AND" + ")"
                )) {
                    setCharacterAttributes(before, after - before, attr_red, false);
                } else {
                    setCharacterAttributes(before, after - before, attrBlack, false);
                }
            }
        };
    }

    private int findLastNonWordChar(String text, int index) {
        while (--index >= 0) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
        }
        return index;
    }

    private int findFirstNonWordChar(String text, int index) {
        while (index < text.length()) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
            index++;
        }
        return index;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jButton1 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextPane2 = new XmlTextPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane(doc);

        setMinimumSize(new java.awt.Dimension(0, 0));

        jButton1.setText("Reason");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton3.setText("Remove Axiom");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Edit Axiom");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Save on Text File");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText("Standard");

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setSelected(true);
        jRadioButton2.setText("Advanced");

        jLabel1.setText("Ontology inferred:");

        jTextPane2.setEditable(false);
        jTextPane2.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jScrollPane3.setViewportView(jTextPane2);

        jTextPane1.setEditable(false);
        jTextPane1.setFont(new java.awt.Font("Monospaced", 0, 13)); // NOI18N
        jScrollPane2.setViewportView(jTextPane1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton5))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(18, 18, 18)
                        .addComponent(jRadioButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 347, Short.MAX_VALUE)
                        .addComponent(jButton4)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3))
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jRadioButton1)
                        .addComponent(jRadioButton2))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1)
                        .addComponent(jButton3)
                        .addComponent(jButton4)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //reason
        atualizar();
    }//GEN-LAST:event_jButton1ActionPerformed

    public void atualizar() {
        boolean isStandard = true;
        if (jRadioButton2.isSelected()) {
            isStandard = false;
        }
        doReasoning(isStandard);
    }

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        try {
            String retorno = JOptionPane.showInputDialog(this,
                    "What's the ID of the Axiom? (If blank, all the inconsistent axioms will be removed)",
                    "Remove Axiom",
                    JOptionPane.WARNING_MESSAGE);

            if (retorno != null && retorno.replace(" ", "").equals("")) {
                if (!jFrameMain.coreApp.pelletRepository.deleteAllAxiomasInconsistentes(true, 0)) {
                    JOptionPane.showMessageDialog(null, "Error while tried to remove axiom", "Erro", JOptionPane.ERROR_MESSAGE);
                }
                jFrameMain.coreApp.atualizarTelas();
            } else if (retorno == null) {
            } else {
                int id = Integer.parseInt(retorno);
                if (id <= 0) {
                    JOptionPane.showMessageDialog(null, "Insert a valid ID.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                } else {
                    if (!jFrameMain.coreApp.pelletRepository.deleteAllAxiomasInconsistentes(false, id)) {
                        JOptionPane.showMessageDialog(null, "Error while tried to remove axiom", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                    jFrameMain.coreApp.atualizarTelas();
                }
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "You have to insert a NUMBER", "Erro", JOptionPane.ERROR_MESSAGE);
        }

        boolean isStandard = true;
        if (jRadioButton2.isSelected()) {
            isStandard = false;
        }
        doReasoning(isStandard);

    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try {
            String retorno = JOptionPane.showInputDialog(this,
                    "What's the ID of the Axiom? (Cannot be empty)",
                    "Edit Axiom",
                    JOptionPane.WARNING_MESSAGE);

            if (retorno == null) {
                return;
            }

            int id = Integer.parseInt(retorno);
            if (id <= 0) {
                JOptionPane.showMessageDialog(null, "Insert a valid ID.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            } else {
                if (jFrameMain.coreApp.pelletRepository.canUseIndexDeleteAxioms(id)) {
                    retorno = JOptionPane.showInputDialog(this,
                            "Compiler...:",
                            "Insert code",
                            JOptionPane.WARNING_MESSAGE);
                    if (retorno != null) {
                        if (!jFrameMain.coreApp.onSubmitted(retorno)) {
                            JOptionPane.showMessageDialog(null, "Error while tried to edit axiom", "Erro", JOptionPane.ERROR_MESSAGE);
                        } else {
                            boolean aaa = jFrameMain.coreApp.pelletRepository.deleteAllAxiomasInconsistentes(false, id);
                            System.out.println(aaa);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Error while tried to edit axiom", "Erro", JOptionPane.ERROR_MESSAGE);
                }
                jFrameMain.coreApp.atualizarTelas();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "You have to insert a NUMBER", "Erro", JOptionPane.ERROR_MESSAGE);
        }

        boolean isStandard = true;
        if (jRadioButton2.isSelected()) {
            isStandard = false;
        }
        doReasoning(isStandard);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        System.out.println("--> " + jFrameMain.filename);
        if (jFrameMain.filename.equals("")) {
            JOptionPane.showMessageDialog(null, "You need to save your project before...", "Wait!", JOptionPane.INFORMATION_MESSAGE);
        } else {
            try {
                jFrameMain.coreApp.owlRepository.saveOntologyInferredToFile("./data/ontology/inferred/" + jFrameMain.filename);
            } catch (Exception ex) {
                if (Session.isDebbug) {
                    Logger.getLogger(JFramePrincipal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    public void doReasoning(boolean isStandard) {
        jFrameMain.coreApp.pelletRepository.getReasoningScheme(isStandard);

        atualizarPaineis();
    }

    public void atualizarPaineis() {
        jTextPane2.setText(jFrameMain.coreApp.pelletRepository.ontology_INFERRED_STRING.replaceAll("\n+", "\n"));
        jTextPane2.setCaretPosition(0);

        jTextPane1.setText(jFrameMain.coreApp.pelletRepository.inconsistence_STRING + jFrameMain.coreApp.pelletRepository.ontology_INFERRED_LOGICA_DE_DESCRICAO);
        jTextPane1.setCaretPosition(0);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextPane jTextPane1;
    public javax.swing.JEditorPane jTextPane2;
    // End of variables declaration//GEN-END:variables
}
