package com.ajs.bibliothek;

import com.ajs.Scene;
import com.ajs.fileChooser.FilePreviewer;
import com.ajs.fileChooser.MonFilter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BibliothekDialog extends JDialog {
    private static BibliothekDialog instance = null;
    private Container container;
    private JPanel contentPane;
    private JButton btnOk;
    private JButton btnChoose;
    private JButton btnDelete;
    private Path imagesDirectory = Paths.get(Paths.get("").toAbsolutePath() + "/images");
    private final Color color1 = new Color(32, 150, 250);
    private final Color color2 = new Color(5, 250, 153);

    public static final int OK_OPTION = 0;
    public static final int OK_CANCEL = -1;
    private static int rep;

    private final JFileChooser fileChooser;
    private String[] suffixesImages = {"jpeg", "jpg", "png"};

    private String title;

    public BibliothekDialog(JFrame parent, String title, boolean isModal) {
        super(parent, title, isModal);
        this.title = title;
        instance = this;
        Dimension dim = Scene.getInstance().getPreferredSize();
        setSize(new Dimension(600, (int) dim.getHeight()));
        setLocationRelativeTo(parent);
        setResizable(false);

        if (Files.notExists(imagesDirectory)) {
            try {
                Files.createDirectory(imagesDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setAcceptAllFileFilterUsed(false);
        MonFilter mfi = new MonFilter(suffixesImages, "les fichiers image (*.png, *.jpeg");
        FilePreviewer previewer = new FilePreviewer(fileChooser, null);
        fileChooser.setAccessory(previewer);
        fileChooser.addChoosableFileFilter(mfi);

        container = getContentPane();

        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, color2, 0, getHeight() / 2, color1, false);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
            }
        };
        contentPane.setPreferredSize(new Dimension(500, 400));
        JScrollPane scrollPane = new JScrollPane(contentPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(30);
        container.add(scrollPane, BorderLayout.CENTER);

        JPanel panelBtns = new JPanel();
        panelBtns.setBackground(color1);
        container.add(panelBtns, BorderLayout.SOUTH);

        int btnWidth = 180;
        int btnHeight = 30;
        btnOk = new JButton("OK");
        btnOk.setPreferredSize(new Dimension(btnWidth, btnHeight));
        btnOk.addActionListener(e -> {
            if (BiblioImage.getCurrent() != null) {
                rep = BibliothekDialog.OK_OPTION;
                setVisible(false);
            }
        });
        panelBtns.add(btnOk);

        btnChoose = new JButton("Ajouter d'autres images");
        btnChoose.setPreferredSize(new Dimension(btnWidth, btnHeight));
        btnChoose.addActionListener(e -> {
            int rep = fileChooser.showOpenDialog(null);
            if (rep == JFileChooser.APPROVE_OPTION) {
                try {
                    this.setTitle(title);
                    Path newImagePath;
                    BufferedImage img;
                    for (File file : fileChooser.getSelectedFiles()) {
                        img = ImageIO.read(file.getAbsoluteFile());
                        newImagePath = Paths.get(imagesDirectory + "/" + file.getName());
                        File fileImage = new File(String.valueOf(newImagePath));
                        ImageIO.write(img, "jpg", fileImage);
                        if (!BiblioImage.getPathList().contains(newImagePath)) {
                            contentPane.add(new BiblioImage(img, newImagePath, this));
                        }
                    }
                    updateContentPaneHeight();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        panelBtns.add(btnChoose);

        btnDelete = new JButton("Supprimer");
        btnDelete.setPreferredSize(new Dimension(btnWidth, btnHeight));
        btnDelete.addActionListener(e -> {
            if (BiblioImage.getCurrent() != null) {
                if (BiblioImage.getCurrentPathImage().compareTo(BiblioImage.getCurrent().getPath()) != 0) {
                    contentPane.remove(BiblioImage.getCurrent());
                    contentPane.repaint();
                    BiblioImage.deleteCurrent();
                    updateContentPaneHeight();
                } else {
                    JOptionPane.showMessageDialog(null, "Cette image ne peut être supprimée.\nElle est actuellement utilisée.");
                }
            }
        });
        panelBtns.add(btnDelete);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowDeactivated(WindowEvent e) {
                rep = BibliothekDialog.OK_CANCEL;
            }
        });
    }

    public int showDialog() {
        rep = BibliothekDialog.OK_CANCEL;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(imagesDirectory, "*.{jpg,jpeg,png}")) {
            for (Path p : stream) {
                if (!BiblioImage.getPathList().contains(p)) {
                    contentPane.add(new BiblioImage(new ImageIcon(String.valueOf(p)).getImage(), p, this));
                }
            }
            updateContentPaneHeight();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BiblioImage.clean();
        instance.setTitle(title);
        setVisible(true);
        return rep;
    }

    private void updateContentPaneHeight() {
        int nbColumn = 3;
        int marging = 5;
        int nbLine = (int) Math.ceil((double) BiblioImage.getPathList().size() / nbColumn);
        int height = nbLine * (marging + BiblioImage.getDimension().height);
        contentPane.setPreferredSize(new Dimension(contentPane.getWidth(), height));
        contentPane.revalidate();
    }

    public static BibliothekDialog getInstance() {
        return instance;
    }
}
