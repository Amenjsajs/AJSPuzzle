package com.ajs;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageResizer {
    public static Image scaleImage(Image source, int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(source, 0, 0, width, height, null);
        g.dispose();
        return img;
    }

    //avec un facteur (<1 pour rétrécir, >1 pour agrandir):
    public static Image scaleImage(final Image source, final double factor) {
        int width = (int) (source.getWidth(null) * factor);
        int height = (int) (source.getHeight(null) * factor);
        return scaleImage(source, width, height);
    }

    //avec une taille en pixels (=hauteur si portrait, largeur si paysage):
    public static Image scaleImage(Image source, int size) {
        int width = source.getWidth(null);
        int height = source.getHeight(null);
        double f = 0;
        if (width < height) {//portrait
            f = (double) height / (double) width;
            width = (int) (size / f);
            height = size;
        } else {//paysage
            f = (double) width / (double) height;
            width = size;
            height = (int) (size / f);
        }
        return scaleImage(source, width, height);
    }
}
