package com.ajs;

import java.io.File;
import java.io.Serializable;

public class Piece implements Serializable {
    private static final long serialVersionUID = 1L;
    private int x;
    private int y;
    private int initialX;
    private int initialY;
    private int subX;
    private int subY;
    private int size;
    private File imageFile;

    public Piece(){}

    public Piece(int x, int y, int subX, int subY, int size, File imageFile){
        this.x = x;
        this.y = y;
        this.initialX = x;
        this.initialY = y;
        this.subX = subX;
        this.subY = subY;
        this.size = size;
        this.imageFile = imageFile;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getSubX() {
        return subX;
    }

    public int getSubY() {
        return subY;
    }

    public int getSize() {
        return size;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile){
        this.imageFile = imageFile;
    }

    public int getInitialX() {
        return initialX;
    }

    public void setInitialX(int initialX) {
        this.initialX = initialX;
    }

    public int getInitialY() {
        return initialY;
    }

    public void setInitialY(int initialY) {
        this.initialY = initialY;
    }

    public void initPosition(){
        this.x = this.initialX;
        this.y = this.initialY;
    }

    @Override
    public String toString() {
        return "Piece[initialX: "+initialX+", initialY: "+initialY+"x: "+x+", y: "+y+", subX: "+ subX+", subY: "+subY+", size: "+size+", "+imageFile.getAbsolutePath()+"]";
    }
}
