package com.ajs.fileChooser;

import javax.swing.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class FilePreviewer extends JComponent implements PropertyChangeListener {
    // pour les images
    ImageIcon image = null;
    File f = null;
    JFrame lAppli;
    // pour les *.txt et les *.html
    JEditorPane txt = new JEditorPane();
    // pour les *.au, et les *.wav

    // pour les autres fichiers
    JLabel message1 = new JLabel();
    JLabel message2 = new JLabel();

    public FilePreviewer(JFileChooser fc, JFrame ja) {
        lAppli = ja;
        setPreferredSize(new Dimension(200, 200));
        fc.addPropertyChangeListener(this);
        txt.setBounds(0, 0, 200, 200);
        Font f = new Font("Comic sans ms", Font.BOLD, 14);
        message1.setBounds(0, 70, 200, 30);
        message1.setFont(f);
        message1.setForeground(Color.RED);
        message1.setHorizontalAlignment(SwingConstants.CENTER);
        message2.setBounds(0, 100, 200, 30);
        message2.setFont(f);
        message2.setForeground(Color.RED);
        message2.setHorizontalAlignment(SwingConstants.CENTER);
    }

    public void loadImage() {
        if (f != null) {
            ImageIcon tmpIcon = new ImageIcon(f.getPath());
            if (tmpIcon.getIconWidth() > 190) {
                image = new ImageIcon(tmpIcon.getImage().getScaledInstance(190,
                        -1, Image.SCALE_DEFAULT));
            } else image = tmpIcon;
        }
    }

    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();
        if (prop == JFileChooser.SELECTED_FILE_CHANGED_PROPERTY) {
            f = (File) e.getNewValue();
            if (f == null) return;
            if (isShowing()) {
                loadImage();
                removeAll();
                if (image == null) {
                    loadImage();
                }
                if (image != null) {
                    if (f == null) return;
                    // fichiers *.txt ou *.html
                    if (image.getIconWidth() == -1 && (f.getName().endsWith(".txt")
                            || f.getName().endsWith(".html"))) {
                        try {
                            txt.setPage("file:///" + f.getAbsolutePath());
                            add(txt);
                        } catch (IOException ioe) {
                            System.out.println(ioe);
                        }
                    }
                }
                repaint();
            }
        }
    }

    public void paint(Graphics g) {
        super.paint(g);
        loadImage();
        if (image != null) {
            if (image.getIconWidth() != -1) {
                // les fichiers images
                int x = getWidth() / 2 - image.getIconWidth() / 2;
                int y = getHeight() / 2 - image.getIconHeight() / 2;
                if (y < 0) y = 0;
                if (x < 5) x = 5;
                image.paintIcon(this, g, x, y);
            }
        }
    }
}

