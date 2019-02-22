package com.ajs.com.ajs.bibliothek;

import com.ajs.ImageResizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class BiblioImage extends JPanel {
    private static BiblioImage current = null;
    private final BiblioImage self;
    private boolean isCurrent;
    private static ArrayList<BiblioImage> biblioImageList = new ArrayList<>();
    private static ArrayList<Path> pathList = new ArrayList<>();
    private final Image image;
    private final Path path;
    private final JDialog parent;
    private String parentTitle;
    private static final Dimension dimension = new Dimension(150, 150);
    private Color borderColorHovered = new Color(0, 0, 0, 0);
    private final int BORDER_WIDTH = 6;

    public BiblioImage(Image image, Path path, JDialog parent) {
        this.image = ImageResizer.scaleImage(image, (int) dimension.getWidth() - BORDER_WIDTH, (int) dimension.getHeight() - BORDER_WIDTH);
        this.path = path;
        this.parent = parent;
        parentTitle = parent.getTitle();
        self = this;
        isCurrent = false;
        setPreferredSize(dimension);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if(!pathList.contains(path)){
            pathList.add(path);
            biblioImageList.add(this);
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                current = self;
                isCurrent = true;
                parent.setTitle(parentTitle+" "+current.getPath().getFileName());
                for (BiblioImage biblioImage: biblioImageList){
                    if(!current.equals(biblioImage) && biblioImage.isCurrent){
                        biblioImage.isCurrent = false;
                        biblioImage.borderColorHovered = new Color(0,0,0,0);
                        biblioImage.repaint();
                        break;
                    }
                }
                repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                borderColorHovered = new Color(250, 6, 167);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if(!isCurrent){
                    borderColorHovered = new Color(0, 0, 0, 0);
                }
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setStroke(new BasicStroke(
                BORDER_WIDTH,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_BEVEL));
        g2d.setColor(borderColorHovered);
        g2d.drawRect(0, 0, (int) dimension.getWidth(), (int) dimension.getHeight());
        g2d.drawImage(image, BORDER_WIDTH / 2, BORDER_WIDTH / 2, null);
    }

    public static Dimension getDimension() {
        return BiblioImage.dimension;
    }

    public static ArrayList<Path> getPathList() {
        return pathList;
    }

    public static BiblioImage getCurrent() {
        return current;
    }

    public static void clean(){
        if(current != null){
            current.borderColorHovered = new Color(0,0,0,0);
            current.isCurrent = false;
            current.repaint();
            current = null;
        }
    }

    public static void deleteCurrent(){
        if(current != null){
            Path path = current.path;
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            pathList.remove(path);
            biblioImageList.remove(current);
            current = null;
        }
    }

    public Path getPath() {
        return path;
    }
}
