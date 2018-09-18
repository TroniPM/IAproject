/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pmateus.gui;

import com.sun.javafx.application.PlatformImpl;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
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
    private String pathToIndex2 = "./data/viewer/index.html";//Com \\ no inicio

    public SwingFXWebView() {

    }

    public SwingFXWebView(Ontology aThis) {
        System.out.println("SwingFXWebView(Ontology aThis)");
        this.ontologyJPanel = aThis;

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
                stage = null;
                browser = null;
                jfxPanel = null;
                webEngine = null;
                ontologyJPanel = null;
                myJFrame = null;
            }
        });

        myJFrame.setIconImage(JFramePrincipal.iconViewer.getImage());
        initComponents();
    }

    public void reloadURL() {
        webEngine.reload();
    }

    private void initComponents() {
        System.out.println("initComponents()");

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
        System.out.println("createScene()");
        PlatformImpl.runLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("run()");

                stage = new Stage();
                stage.setResizable(true);

                Group root = new Group();
                Scene scene = new Scene(root, 80, 20);
                stage.setScene(scene);

                // Set up the embedded browser:
                browser = new WebView();
                webEngine = browser.getEngine();
                String absPath = new File(pathToIndex2).getAbsolutePath();
                String fileToOpen = "file:///" + absPath;
                System.out.println("FILE WERBVIEW: " + fileToOpen);
                webEngine.load(fileToOpen);

                ObservableList<Node> children = root.getChildren();
                children.add(browser);

                jfxPanel.setScene(scene);
            }
        });
    }
}
