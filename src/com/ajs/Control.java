package com.ajs;

import java.awt.*;

public class Control {
    private int x;
    private int y;
    private int width;
    private int height;

    private String text;

    private Color fontColor = Color.white;
    private Color bgColorEnabled = new Color(250, 100, 0);
    private Color bgColorDisabled = new Color(150, 150, 150);
    private Color bgColorHovered = new Color(150, 220, 0);

    private ControlListener controlListener;
    private boolean enable = true;
    private boolean hovered = false;

    public Control() {
    }

    public Control(String text, int x, int y, int width, int height) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Color getFontColor() {
        return fontColor;
    }

    public void setFontColor(Color fontColor) {
        this.fontColor = fontColor;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Color getBgColor() {
        return enable ? (!hovered ? bgColorEnabled : bgColorHovered) : bgColorDisabled;
    }

    public void addControlListener(ControlListener controlListener) {
        this.controlListener = controlListener;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public void doClick() {
        if (controlListener != null) {
            if (enable)
                controlListener.click();
        }
    }
}
