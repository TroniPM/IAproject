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
package com.tronipm.orcaide.view;

import com.tronipm.orcaide.core.CoreApplication;
import com.tronipm.orcaide.util.Session;
import com.tronipm.orcaide.util.UtilMethods;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.io.IOUtils;

public class JFramePrincipal extends javax.swing.JFrame {

    private static String version = "2.0.0:0";
    public static int instancia = 0;
    public static int language = 0;
    public Compiler tabCompiler = null;
    public Ontology tabOntology = null;
    public Query tabQuery = null;
    public Reasoner tabReasoner = null;
    public Preferences tabPreferences = null;
    public String currentDocument = ""; //NOI18N
    public String filename = ""; //NOI18N
    public static ImageIcon iconCopy, iconPaste, iconCut, iconViewer, iconHome;

    public CoreApplication coreApp = null;

    private JFramePrincipal(String path, String name) {
        currentDocument = path;
        filename = name;
        coreApp = new CoreApplication(this, false, null);

        init(false);
        filename = path;
        currentDocument = path;
        setDocumentName(name);
        
        String aString = carregarArquivo(path);
        tabCompiler.editor.setText(aString);

        coreApp.atualizarTelas();
    }

    private void destroy() {
        coreApp.destroy();
        tabCompiler.destroy();
        tabCompiler = null;
        tabOntology.destroy();
        tabOntology = null;
        tabReasoner.destroy();
        tabReasoner = null;
        tabQuery.destroy();
        tabQuery = null;
        tabPreferences.destroy();
        tabPreferences = null;
        System.gc();
    }

    private void init(boolean isOpenMode) {
        instancia++;

        tabCompiler = new Compiler(this);
        tabOntology = new Ontology(this);
        tabReasoner = new Reasoner(this);
        tabQuery = new Query(this);
        tabPreferences = new Preferences(this);

        if (!isOpenMode) {
            currentDocument = "new" + instancia;
        }
        setDocumentName(currentDocument);

        initComponents();
        addIcons();
        setLocationRelativeTo(null);

        jTabbedPane1.addTab("Compiler", tabCompiler);
        jTabbedPane1.addTab("Ontology", tabOntology);
        jTabbedPane1.addTab("Reasoner", tabReasoner);
        jTabbedPane1.addTab("Query", tabQuery);

        //core.startLookingForErrors();
        try {
            coreApp.frame.verifyOntologyUndoRedoButtonEnabled();
        } catch (NullPointerException e) {

        }
    }

    public JFramePrincipal() {
        coreApp = new CoreApplication(this, false, null);

        init(false);

        coreApp.atualizarTelas();
    }

    private void addIcons() {
        //program
        iconHome = new ImageIcon("./data/imgs/home.png");
        this.setIconImage(iconHome.getImage());
        iconViewer = new ImageIcon("./data/imgs/viewer.png");

        //New
        ImageIcon newFile = new ImageIcon("./data/imgs/new.png");
        jMenuItem1.setIcon(newFile);
        ImageIcon open = new ImageIcon("./data/imgs/open.png");
        jMenuItem17.setIcon(open);
        //
        ImageIcon save = new ImageIcon("./data/imgs/save.png");
        jMenuItem2.setIcon(save);
        ImageIcon saveAs = new ImageIcon("./data/imgs/save_as.png");
        jMenuItem16.setIcon(saveAs);
        //
        ImageIcon exit = new ImageIcon("./data/imgs/quit.png");
        jMenuItem3.setIcon(exit);
        //Edit
        ImageIcon undo = new ImageIcon("./data/imgs/undo.png");
        jMenuItem4.setIcon(undo);
        ImageIcon redo = new ImageIcon("./data/imgs/redo.png");
        jMenuItem5.setIcon(redo);
        //
        iconCopy = new ImageIcon("./data/imgs/copy.gif");
        jMenuItem13.setIcon(iconCopy);
        iconPaste = new ImageIcon("./data/imgs/paste.png");
        jMenuItem14.setIcon(iconPaste);
        iconCut = new ImageIcon("./data/imgs/cut.png");
        jMenuItem15.setIcon(iconCut);
        //Ontology
        ImageIcon compiler = new ImageIcon("./data/imgs/compiler.png");
        jMenuItem20.setIcon(compiler);
        //
        ImageIcon undo_ontology = new ImageIcon("./data/imgs/undo_ontology.png");
        jMenuItem23.setIcon(undo_ontology);
        ImageIcon redo_ontology = new ImageIcon("./data/imgs/redo_ontology.png");
        jMenuItem24.setIcon(redo_ontology);
        //
        ImageIcon merge = new ImageIcon("./data/imgs/merge.png");
        jMenuItem19.setIcon(merge);
        //
        ImageIcon graphV = new ImageIcon("./data/imgs/graph.png");
        jMenuItem21.setIcon(graphV);
        ImageIcon textV = new ImageIcon("./data/imgs/text.png");
        jMenuItem22.setIcon(textV);
        //
        ImageIcon copy_ont = new ImageIcon("./data/imgs/copy_ontology.png");
        jMenuItem6.setIcon(copy_ont);
        ImageIcon clear_ont = new ImageIcon("./data/imgs/clear_ontology.png");
        jMenuItem12.setIcon(clear_ont);
        //Tools
        ImageIcon settings = new ImageIcon("./data/imgs/settings.png");
        jMenuItem7.setIcon(settings);
        ImageIcon language = new ImageIcon("./data/imgs/language.png");
        jMenu4.setIcon(language);
        //
        ImageIcon en = new ImageIcon("./data/imgs/en.png");
        jMenuItem8.setIcon(en);
        ImageIcon br = new ImageIcon("./data/imgs/br.png");
        jMenuItem9.setIcon(br);
        ImageIcon es = new ImageIcon("./data/imgs/es.png");
        jMenuItem18.setIcon(es);
        //Help
        ImageIcon help = new ImageIcon("./data/imgs/help.png");
        jMenuItem10.setIcon(help);
        ImageIcon about = new ImageIcon("./data/imgs/about.png");
        jMenuItem11.setIcon(about);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem17 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem16 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenuItem15 = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jMenuItem20 = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
        jMenuItem23 = new javax.swing.JMenuItem();
        jMenuItem24 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem19 = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        jMenuItem21 = new javax.swing.JMenuItem();
        jMenuItem22 = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem7 = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem18 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem10 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItem11 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.LEFT);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/pmateus/gui/Bundle"); // NOI18N
        jMenu1.setText(bundle.getString("JFramePrincipal.jMenu1.text_1")); // NOI18N

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText(bundle.getString("JFramePrincipal.jMenuItem1.text_1")); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem17.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem17.setText(bundle.getString("JFramePrincipal.jMenuItem17.text_1")); // NOI18N
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem17);
        jMenu1.add(jSeparator2);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText(bundle.getString("JFramePrincipal.jMenuItem2.text_1")); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem16.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem16.setText(bundle.getString("JFramePrincipal.jMenuItem16.text_1")); // NOI18N
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem16);
        jMenu1.add(jSeparator1);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem3.setText(bundle.getString("JFramePrincipal.jMenuItem3.text_1")); // NOI18N
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuBar1.add(jMenu1);

        jMenu2.setText(bundle.getString("JFramePrincipal.jMenu2.text_1")); // NOI18N

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem4.setText(bundle.getString("JFramePrincipal.jMenuItem4.text_1")); // NOI18N
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem5.setText(bundle.getString("JFramePrincipal.jMenuItem5.text_1")); // NOI18N
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);
        jMenu2.add(jSeparator5);

        jMenuItem13.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem13.setText(bundle.getString("JFramePrincipal.jMenuItem13.text_1")); // NOI18N
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem13);

        jMenuItem14.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem14.setText(bundle.getString("JFramePrincipal.jMenuItem14.text_1")); // NOI18N
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem14);

        jMenuItem15.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem15.setText(bundle.getString("JFramePrincipal.jMenuItem15.text_1")); // NOI18N
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem15ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem15);

        jMenuBar1.add(jMenu2);

        jMenu6.setText(bundle.getString("JFramePrincipal.jMenu6.text_1")); // NOI18N

        jMenuItem20.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem20.setText(bundle.getString("JFramePrincipal.jMenuItem20.text_1")); // NOI18N
        jMenuItem20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem20ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem20);
        jMenu6.add(jSeparator10);

        jMenuItem23.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem23.setText(bundle.getString("JFramePrincipal.jMenuItem23.text")); // NOI18N
        jMenuItem23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem23ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem23);

        jMenuItem24.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem24.setText(bundle.getString("JFramePrincipal.jMenuItem24.text")); // NOI18N
        jMenuItem24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem24ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem24);
        jMenu6.add(jSeparator3);

        jMenuItem19.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem19.setText(bundle.getString("JFramePrincipal.jMenuItem19.text_1")); // NOI18N
        jMenu6.add(jMenuItem19);
        jMenu6.add(jSeparator8);

        jMenuItem21.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem21.setText(bundle.getString("JFramePrincipal.jMenuItem21.text_1")); // NOI18N
        jMenuItem21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem21ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem21);

        jMenuItem22.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem22.setText(bundle.getString("JFramePrincipal.jMenuItem22.text_1")); // NOI18N
        jMenuItem22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem22ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem22);
        jMenu6.add(jSeparator9);

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem6.setText(bundle.getString("JFramePrincipal.jMenuItem6.text_1")); // NOI18N
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem6);

        jMenuItem12.setText(bundle.getString("JFramePrincipal.jMenuItem12.text_1")); // NOI18N
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem12);

        jMenuBar1.add(jMenu6);

        jMenu3.setText(bundle.getString("JFramePrincipal.jMenu3.text_1")); // NOI18N

        jMenuItem7.setText(bundle.getString("JFramePrincipal.jMenuItem7.text_1")); // NOI18N
        jMenu3.add(jMenuItem7);
        jMenu3.add(jSeparator6);

        jMenu4.setText(bundle.getString("JFramePrincipal.jMenu4.text_1")); // NOI18N

        jMenuItem8.setText(bundle.getString("JFramePrincipal.jMenuItem8.text_1")); // NOI18N
        jMenu4.add(jMenuItem8);

        jMenuItem9.setText(bundle.getString("JFramePrincipal.jMenuItem9.text_1")); // NOI18N
        jMenu4.add(jMenuItem9);

        jMenuItem18.setText(bundle.getString("JFramePrincipal.jMenuItem18.text_1")); // NOI18N
        jMenu4.add(jMenuItem18);

        jMenu3.add(jMenu4);

        jMenuBar1.add(jMenu3);

        jMenu5.setText(bundle.getString("JFramePrincipal.jMenu5.text_1")); // NOI18N

        jMenuItem10.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jMenuItem10.setText(bundle.getString("JFramePrincipal.jMenuItem10.text_1")); // NOI18N
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem10);
        jMenu5.add(jSeparator4);

        jMenuItem11.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        jMenuItem11.setText(bundle.getString("JFramePrincipal.jMenuItem11.text_1")); // NOI18N
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem11);

        jMenuBar1.add(jMenu5);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 579, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        closeScheme();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        closeScheme();
    }//GEN-LAST:event_formWindowClosing

    private void jMenuItem22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem22ActionPerformed
        //text viewer
        jTabbedPane1.setSelectedIndex(1);
    }//GEN-LAST:event_jMenuItem22ActionPerformed

    private void jMenuItem20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem20ActionPerformed
        //compiler
        jTabbedPane1.setSelectedIndex(0);
    }//GEN-LAST:event_jMenuItem20ActionPerformed

    private void jMenuItem21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem21ActionPerformed
        //graph viewer

        tabOntology.initGraphViewer();
    }//GEN-LAST:event_jMenuItem21ActionPerformed

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        //copy text
        if (jTabbedPane1.getSelectedIndex() == 0) {
            UtilMethods.copyToClipboard(tabCompiler.getSelectedText());
        } else if (jTabbedPane1.getSelectedIndex() == 1) {
            UtilMethods.copyToClipboard(tabOntology.getSelectedText());
        }
    }//GEN-LAST:event_jMenuItem13ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        //about
        String myLicense = "Apache License 2.0";
        String txt = "Developed by:\nPaulo Mateus <Federal Rural de Pernambuco University> [paulomatew@gmail.com]\n\n2016\n\nThis software is released under " + myLicense + ".\n\nWebVOWL and OWL2VOWL is under the MIT license at this time (19/05/2016).\n\nBig thanks to http://www.ldf.fi/service/owl-converter/";
        JTextArea textArea = new JTextArea(txt);
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(null, scrollPane, "About | OrCA IDE - version " + version,
                JOptionPane.YES_NO_OPTION);
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        //help
        String aux = null;
        try {
            aux = readHowToFile();
        } catch (IOException ex) {
            Logger.getLogger(Compiler.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (aux != null) {
            JTextArea textArea = new JTextArea(aux);
            JScrollPane scrollPane = new JScrollPane();
            scrollPane.setViewportView(textArea);
            textArea.setLineWrap(false);
            textArea.setWrapStyleWord(false);
            scrollPane.setPreferredSize(new Dimension(700, 500));
            JOptionPane.showMessageDialog(null, scrollPane, "Help",
                    JOptionPane.YES_NO_OPTION);
        }
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed
        //paste
        if (jTabbedPane1.getSelectedIndex() == 0) {
            String regex = tabCompiler.getSelectedText();
            String clipbString = UtilMethods.pasteFromClipboard();
            String allText = tabCompiler.editor.getText();
            if (regex.equals("")) {
                int pos = tabCompiler.editor.getCaretPosition();
                String newAllText = allText.substring(0, pos) + clipbString;
                int newPos = newAllText.length();//Adicionar cursor após a inserção
                newAllText += allText.substring(pos, allText.length());
                tabCompiler.editor.setText(newAllText);
                tabCompiler.editor.setCaretPosition(newPos);
            } else {
                tabCompiler.editor.replaceSelection(clipbString);
            }

        }
    }//GEN-LAST:event_jMenuItem14ActionPerformed

    private void jMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed
        //cut
        if (jTabbedPane1.getSelectedIndex() == 0) {
            String txtRegex = tabCompiler.getSelectedText();
            String allTxt = tabCompiler.editor.getText();
            tabCompiler.editor.setText(allTxt.replace(txtRegex, ""));

            UtilMethods.copyToClipboard(txtRegex);
        }
    }//GEN-LAST:event_jMenuItem15ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        //copy currentOntology
        UtilMethods.copyToClipboard(tabOntology.jTextPane1.getText());
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        //delete currentOntology
        if (javax.swing.JOptionPane.showConfirmDialog(this,
                "Are you sure to delete this ontology?", "Really Delete?",
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE) == javax.swing.JOptionPane.YES_OPTION) {
            coreApp.destroy();
            System.gc();
            coreApp = new CoreApplication(this, false, null);

            coreApp.atualizarTelas();
        }
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        //save normal
        boolean isNew = true;
        try {
            String a = currentDocument.replace("new", "");
            int test = Integer.valueOf(a);
        } catch (Exception e) {
            isNew = false;
        }
        if (isNew) {
            choosePathToSaveFile();
        } else {
            try {
                escreverEmArquivo(currentDocument, this.tabCompiler.editor.getText(), false);
            } catch (Exception ex) {
                if (Session.isDebbug) {
                    Logger.getLogger(JFramePrincipal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void choosePathToSaveFile() {
        JFileChooser jfc_save = new JFileChooser();
        jfc_save.setCurrentDirectory(new File(System.getProperty("user.dir") + "\\data\\ontology"));
        //fc.setVisible(true);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILE or OWL", "txt", "text", "owl");
        jfc_save.setFileFilter(filter);
        jfc_save.setApproveButtonText("Save");
        int returnVal = jfc_save.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            currentDocument = (jfc_save.getSelectedFile().getPath());
            if (!currentDocument.endsWith(".orca")) {
                currentDocument += ".orca";
            }

            try {
                if (!currentDocument.equals("")) {
                    String aString = currentDocument;

                    escreverEmArquivo(aString, this.tabCompiler.editor.getText(), false);
                    filename = jfc_save.getSelectedFile().getName();
                    setDocumentName(aString);
                }

            } catch (Exception e) {
                if (Session.isDebbug) {
                    Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
    }

    private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed
        //save as
        choosePathToSaveFile();

    }//GEN-LAST:event_jMenuItem16ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        //new

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JFramePrincipal().setVisible(true);
            }
        });
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed
        //open

        final JFileChooser jfc_save = new JFileChooser();
        jfc_save.setCurrentDirectory(new File(System.getProperty("user.dir") + "\\data\\ontology"));
        //fc.setVisible(true);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILE or OWL", "txt", "text", "owl", "orca");
        jfc_save.setFileFilter(filter);
        jfc_save.setApproveButtonText("Open");
        int returnVal = jfc_save.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new JFramePrincipal(jfc_save.getSelectedFile().getPath(),
                            jfc_save.getSelectedFile().getName()).setVisible(true);
                }
            });
        }


    }//GEN-LAST:event_jMenuItem17ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        //undo
        if (tabCompiler.undoRedoManager.canUndo()) {
            tabCompiler.undoRedoManager.undo();
        }

        verifyNormalUndoRedoButtonEnabled();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    public void verifyNormalUndoRedoButtonEnabled() {
        if (tabCompiler.undoRedoManager.canUndo()) {
            jMenuItem4.setEnabled(true);
        } else {
            jMenuItem4.setEnabled(false);
        }
        if (tabCompiler.undoRedoManager.canRedo()) {
            jMenuItem5.setEnabled(true);
        } else {
            jMenuItem5.setEnabled(false);
        }
    }

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        //redo
        if (tabCompiler.undoRedoManager.canRedo()) {
            tabCompiler.undoRedoManager.redo();
        }

        verifyNormalUndoRedoButtonEnabled();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem23ActionPerformed
        //undo currentOntology
        coreApp.owlRepository.getPrevious();
        verifyOntologyUndoRedoButtonEnabled();
    }//GEN-LAST:event_jMenuItem23ActionPerformed

    private void jMenuItem24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem24ActionPerformed
        //redo currentOntology
        coreApp.owlRepository.getNext();
        verifyOntologyUndoRedoButtonEnabled();
    }//GEN-LAST:event_jMenuItem24ActionPerformed

    public void verifyOntologyUndoRedoButtonEnabled() throws NullPointerException {
        if (coreApp.owlRepository.undoRedoOntologyManager.canGetPrevious()) {
            jMenuItem23.setEnabled(true);
        } else {
            jMenuItem23.setEnabled(false);
        }
        if (coreApp.owlRepository.undoRedoOntologyManager.canGetNext()) {
            jMenuItem24.setEnabled(true);
        } else {
            jMenuItem24.setEnabled(false);
        }
    }

    private void setDocumentName(String pathAndName) {
        setTitle("OrCA IDE | document: " + pathAndName);
    }

    private void closeScheme() {
        if (javax.swing.JOptionPane.showConfirmDialog(this,
                "Are you sure to close this window?", "Really Closing?",
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE) == javax.swing.JOptionPane.YES_OPTION) {
            if (instancia == 1) {
                System.exit(0);
            } else {
                instancia--;
                this.destroy();
                this.dispose();
                System.gc();

            }
        }
    }

    private String readHowToFile() throws FileNotFoundException, IOException {

        InputStreamReader inputStream = new InputStreamReader(new FileInputStream("./data/howto.txt"), "UTF-8");
        String everything = null;
        try {
            everything = IOUtils.toString(inputStream);
        } finally {
            inputStream.close();
        }

        return everything;
    }

    public static void escreverEmArquivo(String caminho, String content, boolean isAppend) {
        FileOutputStream fop = null;
        File file;
        try {
            file = new File(caminho);
            fop = new FileOutputStream(file, isAppend);
            //Se arquivo não existe, é criado
            if (!file.exists()) {
                file.createNewFile();
            }
            //pega o content em bytes
            byte[] contentInBytes = content.getBytes();
            fop.write(contentInBytes);
            //flush serve para garantir o envio do último lote de bytes
            fop.flush();
            fop.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String carregarArquivoRetornaString(String arquivo) {
        String linhas = null;
        try {
            FileInputStream fin = new FileInputStream(arquivo);
            byte[] a = new byte[fin.available()];
            fin.read(a);
            fin.close();
            linhas = new String(a);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return linhas;
    }

    private static String carregarArquivo(String arquivo) {
        String linhas = null;
        try {
            FileInputStream fin = new FileInputStream(arquivo);
            byte[] a = new byte[fin.available()];
            fin.read(a);
            fin.close();
            linhas = new String(a);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return linhas;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem20;
    private javax.swing.JMenuItem jMenuItem21;
    private javax.swing.JMenuItem jMenuItem22;
    private javax.swing.JMenuItem jMenuItem23;
    private javax.swing.JMenuItem jMenuItem24;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}
