package com.ajs;

import com.ajs.bibliothek.BibliothekDialog;

import javax.swing.*;
import java.awt.*;

public class Fenetre extends JFrame {
    Container container;
    public Fenetre(String title){
        super(title);
        Dimension dim = Scene.getInstance().getPreferredSize();
        setSize((int)dim.getWidth()+20,(int)dim.getHeight()+45);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        new BibliothekDialog(this,"Bibliothèque",true);

        container = getContentPane();
        container.setLayout(new FlowLayout());
        container.add(Scene.getInstance());
        Scene.getInstance().build();
        setVisible(true);
    }
}
