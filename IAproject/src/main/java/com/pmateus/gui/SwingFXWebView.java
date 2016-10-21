/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pmateus.gui;

import com.sun.javafx.application.PlatformImpl;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * SwingFXWebView
 */
public class SwingFXWebView extends JPanel {

    public JFrame myJFrame;

    private Stage stage;
    private WebView browser;
    private JFXPanel jfxPanel;
    private WebEngine webEngine;
    private Ontology ontologyJPanel;
    private String title = "Graph Viewer";
    private String pathToIndex = "\\data\\viewer\\index.html";//Com \\ no inicio

    public SwingFXWebView() {

    }

    SwingFXWebView(Ontology aThis) {
        this.ontologyJPanel = aThis;
        initComponents();

        myJFrame = new JFrame();
        myJFrame.setTitle(title);
        //myJFrame.getContentPane().add(this);
        myJFrame.setLayout(new BorderLayout());
        myJFrame.add(this, BorderLayout.CENTER);
        myJFrame.setSize(new Dimension(800, 625));
        //frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        myJFrame.setVisible(true);
        myJFrame.setResizable(false);

        myJFrame.setLocationRelativeTo(null);

        myJFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                ontologyJPanel.aWebViewer = null;
                webEngine = null;
            }
        });

        myJFrame.setIconImage(JFramePrincipal.iconViewer.getImage());
    }

    public void reloadURL() {
        webEngine.reload();
    }

    private void initComponents() {

        jfxPanel = new JFXPanel();
        createScene();

        setLayout(new BorderLayout());
        add(jfxPanel, BorderLayout.CENTER);

    }

    /**
     * createScene
     *
     * Note: Key is that Scene needs to be created and run on "FX user thread"
     * NOT on the AWT-EventQueue Thread
     *
     */
    private void createScene() {
        PlatformImpl.startup(new Runnable() {
            @Override
            public void run() {

                stage = new Stage();
                stage.setResizable(true);

                Group root = new Group();
                Scene scene = new Scene(root, 80, 20);
                stage.setScene(scene);

                // Set up the embedded browser:
                browser = new WebView();
                webEngine = browser.getEngine();
                //D:\Documentos\NetBeansProjects\IAProject\viewer\data\foaf.json
                webEngine.load("file:///" + System.getProperty("user.dir") + pathToIndex);
                //System.out.println("file:///" + System.getProperty("user.dir") + "\\viewer\\index.html");

                ObservableList<Node> children = root.getChildren();
                children.add(browser);

                jfxPanel.setScene(scene);
            }
        });
    }
}
