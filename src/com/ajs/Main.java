package com.ajs;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.
                            UIManager.getInstalledLookAndFeels()) {
                        System.out.println(info.getName());
                        if ("Nimbus".equals(info.getName())) {
                            javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        }
                    }
                } catch (ClassNotFoundException | InstantiationException |
                        IllegalAccessException |
                        javax.swing.UnsupportedLookAndFeelException ex) {
                    java.util.logging.Logger.getLogger(Main.class.getName()).
                            log(java.util.logging.Level.SEVERE, null, ex);
                }
                // Display the frame and it's contents
                Fenetre fen = new Fenetre("Puzzle New");
            }
        });

    }
}
