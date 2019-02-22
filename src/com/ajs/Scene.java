package com.ajs;

import com.ajs.com.ajs.bibliothek.BiblioImage;
import com.ajs.com.ajs.bibliothek.BibliothekDialog;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static javax.swing.JOptionPane.*;

public class Scene extends JPanel {
    private final Path CURRENT_RELATIVE_PATH = Paths.get("").toAbsolutePath();

    private boolean isWin = false;
    private boolean isBackup = false; //Pour vérifier si le puzzle est une sauvegarde
    private Path backupPath;

    private final int CONTROL_SPACE_WIDTH = 250;
    private final String CONTROL_TEXT_SHUFFLE = "Mélanger";
    private final String CONTROL_TEXT_NB_PIECE = "Changer nombre de pièces";
    private final String CONTROL_TEXT_CHOOSE_IMAGE = "Choisir une image";
    private final String CONTROL_TEXT_SAVE = "Sauvegarder";
    private final String CONTROL_TEXT_LOAD = "Charger une sauvegarde";
    private final String CONTROL_TEXT_REFRESH = "Rafraichir";
    private final String CONTROL_TEXT_DELETE = "Supprimer la sauvegarde";
    private String[] controlText = {CONTROL_TEXT_SHUFFLE, CONTROL_TEXT_NB_PIECE, CONTROL_TEXT_CHOOSE_IMAGE,
            CONTROL_TEXT_SAVE, CONTROL_TEXT_LOAD, CONTROL_TEXT_REFRESH, CONTROL_TEXT_DELETE};
    private int[] controlX = new int[controlText.length];
    private int[] controlY = new int[controlText.length];
    private int CONTROL_MARGING_TOP = 10;
    private int CONTROL_MARGING_LEFT = 10;
    private int CONTROL_WIDTH = CONTROL_SPACE_WIDTH - (CONTROL_MARGING_LEFT * 2);
    private int CONTROL_HEIGHT = 40;
    private int currentControlIndex = -1;
    private Integer[] nbPieceList = {4, 9, 16, 25, 36, 49, 64, 81, 100};

    private int BORDER_WIDTH = 5;
    private final int IMAGE_PUZZLE_SIZE = 400;
    private int tmpImagePuzzleSize = 400;

    private Piece currentPiece;
    private static Scene instance = null;
    private HashMap<Piece, Image> pieceImage;
    private ArrayList<Piece> piecesList;
    private int nbPieces = 9;

    private File imageFile;
    private BufferedImage imagePuzzle;

    private boolean isBuild = false;

    private final static String MOVE_LEFT = "move_left";
    private final static String MOVE_RIGHT = "move_right";
    private final static String MOVE_TOP = "move_top";
    private final static String MOVE_BOTTOM = "move_bottom";

    //Sert pour le melange et le refresh
    int[] tmpX;
    int[] tmpY;

    private final Color controlColorEnable = new Color(250, 100, 0);
    private final Color controlDiseabledColor = new Color(150, 150, 150);
    private final Color controlColorHover = new Color(150, 220, 0);

    private final Color gradientColor1 = new Color(32, 150, 250);
    private final Color gradientColor2 = new Color(5, 250, 153);

    private Scene() {
        Path path = Paths.get(CURRENT_RELATIVE_PATH + "/images/amenjs.png");
        if (Files.exists(path)) {
            imageFile = path.toFile();
        } else {
            imageFile = null;
        }
        this.piecesList = new ArrayList<>();
        this.pieceImage = new HashMap<>();

        setPreferredSize(new Dimension(CONTROL_SPACE_WIDTH + tmpImagePuzzleSize * 2 + 20 + BORDER_WIDTH * 2, tmpImagePuzzleSize + BORDER_WIDTH * 2));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentControlIndex != -1) {
                    String text = controlText[currentControlIndex];
                    switch (text) {
                        case CONTROL_TEXT_SHUFFLE:
                            controlShuffle();
                            break;
                        case CONTROL_TEXT_NB_PIECE:
                            controlSave("Sauvegardez cette partie avant de changer de nombre de pièces\n" +
                                    "Sinon elle sera définitivement perdue");
                            if (imageFile != null) {
                                controlChangeNbPieces();
                            }
                            break;
                        case CONTROL_TEXT_CHOOSE_IMAGE:
                            controlSave("Sauvegardez cette partie avant de changer d'image\n" +
                                    "Sinon elle sera définitivement perdue");
                            controlChooseImage();
                            break;
                        case CONTROL_TEXT_SAVE:
                            if (imageFile != null) {
                                try {
                                    controlSave();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                            break;
                        case CONTROL_TEXT_LOAD:
                            controlSave("Sauvegardez cette partie avant de charger une sauvegarde\n" +
                                    "Sinon elle sera définitivement perdue");
                            controlLoad();
                            break;
                        case CONTROL_TEXT_REFRESH:
                            if (imageFile != null) {
                                controlRefresh();
                            }
                            break;
                        case CONTROL_TEXT_DELETE:
                            if (isBackup) {
                                int rep = JOptionPane.showConfirmDialog(null,
                                        "Voulez-vous vraiment supprimer cette sauvegarde?",
                                        "Confirmation", OK_CANCEL_OPTION);
                                if (rep == OK_OPTION) {
                                    controlDeleteBackup();
                                }
                            }
                            break;
                    }
                }

                if (imageFile != null) {
                    if (!isWin) {
                        move();
                        winTested();
                        if (isWin) {
                            repaint();
                            JOptionPane.showMessageDialog(null, "Bravo!!!\nVous avez gagné.", "Congratulation", INFORMATION_MESSAGE);
                        }
                    }
                }

                repaint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                boolean isPieceHovered = isPieceHovered(e);
                boolean isControlHovered = isControlHovered(e);
                if (isPieceHovered) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
                if (isControlHovered) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }

                if (!isPieceHovered && !isControlHovered) {
                    setCursor(Cursor.getDefaultCursor());
                }
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(0, 0, gradientColor1, 0, getHeight() / 2, gradientColor2, false);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

        drawBorder(g2d);
        drawControls(g2d);
        if (isBuild) {
            drawImage(g2d);
            drawPieceImage(g2d);
        }
        g.dispose();
    }

    private void drawBorder(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(
                5,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_BEVEL));

        g2d.setColor(new Color(50, 53, 127));
        g2d.drawRect(CONTROL_SPACE_WIDTH + BORDER_WIDTH - 3, BORDER_WIDTH - 3, tmpImagePuzzleSize + BORDER_WIDTH, tmpImagePuzzleSize + BORDER_WIDTH);
        g2d.drawRect(CONTROL_SPACE_WIDTH + BORDER_WIDTH - 3 + tmpImagePuzzleSize + BORDER_WIDTH + 15,
                BORDER_WIDTH - 3, tmpImagePuzzleSize + BORDER_WIDTH, tmpImagePuzzleSize + BORDER_WIDTH);
    }

    private void drawImage(Graphics2D g2d) {
        g2d.drawImage(this.imagePuzzle,
                CONTROL_SPACE_WIDTH + BORDER_WIDTH - 3 + tmpImagePuzzleSize + BORDER_WIDTH + 18
                , BORDER_WIDTH, null);
    }

    private void drawPieceImage(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(
                5,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_BEVEL));

        for (Piece piece : piecesList) {
            g2d.drawImage(pieceImage.get(piece), piece.getX(), piece.getY(), null);
        }

        if (currentPiece != null) {
            g2d.setColor(controlColorHover);
            g2d.drawRect(currentPiece.getX(), currentPiece.getY(), currentPiece.getSize(), currentPiece.getSize());
        }
    }

    private void drawControls(Graphics2D g2d) {
        int textX;
        int textY;

        for (int i = 0, len = controlText.length; i < len; i++) {
            controlX[i] = CONTROL_MARGING_LEFT;
            controlY[i] = (CONTROL_HEIGHT + CONTROL_MARGING_TOP) * i + CONTROL_MARGING_TOP;

            if (i == currentControlIndex) {
                g2d.setColor(controlColorHover);
            } else {
                g2d.setColor(controlColorEnable);
            }

            if (imageFile == null) {
                if (controlText[i].equals(CONTROL_TEXT_SHUFFLE) || controlText[i].equals(CONTROL_TEXT_SAVE) || controlText[i].equals(CONTROL_TEXT_NB_PIECE) ||
                        controlText[i].equals(CONTROL_TEXT_REFRESH)) {
                    g2d.setColor(controlDiseabledColor);
                }
            }
            if ((!isBackup && controlText[i].equals(CONTROL_TEXT_DELETE))) {
                g2d.setColor(controlDiseabledColor);
            }

            g2d.fillRoundRect(controlX[i], controlY[i], CONTROL_WIDTH, CONTROL_HEIGHT, 30, 30);

            g2d.setColor(Color.white);
            g2d.setFont(new Font("Arial", Font.PLAIN, 18));
            FontMetrics fm = g2d.getFontMetrics();

            textX = (CONTROL_SPACE_WIDTH - fm.stringWidth(controlText[i])) / 2;
            textY = controlY[i];
            g2d.drawString(controlText[i], textX, textY + 25);
        }

    }

    public void build() {
        if (imageFile != null) {
            isBuild = true;
            Image image = new ImageIcon(imageFile.getAbsolutePath()).getImage();
            this.imagePuzzle = (BufferedImage) ImageResizer.scaleImage(image, IMAGE_PUZZLE_SIZE, IMAGE_PUZZLE_SIZE);

            this.piecesList = new ArrayList<>();
            this.pieceImage = new HashMap<>();

            int nbPiecesSqrt = (int) Math.sqrt(nbPieces);
            int pieceSize = this.imagePuzzle.getWidth() / nbPiecesSqrt;

            tmpX = new int[this.nbPieces];
            tmpY = new int[this.nbPieces];

            tmpImagePuzzleSize = nbPiecesSqrt * pieceSize;

            Piece piece;
            for (int i = 0, indexTmp = 0; i < nbPiecesSqrt; i++) {
                for (int j = 0; j < nbPiecesSqrt; j++, indexTmp++) {
                    int subX = j * pieceSize;
                    int subY = i * pieceSize;
                    tmpX[indexTmp] = subX + CONTROL_SPACE_WIDTH + BORDER_WIDTH;
                    tmpY[indexTmp] = subY + BORDER_WIDTH;

                    piece = new Piece(tmpX[indexTmp], tmpY[indexTmp], subX, subY, pieceSize, imageFile);
                    this.piecesList.add(piece);
                    image = subImageRender(subX, subY, pieceSize);
                    this.pieceImage.put(piece, image);
                }
            }

            controlShuffle();
        }
    }

    private boolean isPieceHovered(MouseEvent e) {
        for (Piece piece : this.piecesList) {
            if ((piece.getX() <= e.getX() && piece.getX() + piece.getSize() >= e.getX()) &&
                    (piece.getY() <= e.getY() && piece.getY() + piece.getSize() >= e.getY())) {
                currentPiece = piece;
                return true;
            }
        }
        currentPiece = null;
        return false;
    }

    private boolean isControlHovered(MouseEvent e) {
        for (int i = 0, len = controlText.length; i < len; i++) {
            if ((controlX[i] <= e.getX() && controlX[i] + CONTROL_WIDTH >= e.getX())
                    && (controlY[i] <= e.getY() && controlY[i] + CONTROL_HEIGHT >= e.getY())) {
                currentControlIndex = i;
                return true;
            }
        }
        currentControlIndex = -1;
        return false;
    }

    private Image subImageRender(int x, int y, int size) {
        return imagePuzzle.getSubimage(x, y, size, size);
    }

    private int getStep(String direction) {
        int pieceSize = currentPiece.getSize();

        int step = direction.equals(MOVE_LEFT) || direction.equals(MOVE_TOP) ? -pieceSize : pieceSize;

        boolean isAxisX = direction.equals(MOVE_LEFT) || direction.equals(MOVE_RIGHT);

        int afterAddStep = isAxisX ? currentPiece.getX() + step : currentPiece.getY() + step;

        int limitAxisX = (int) Math.sqrt(this.nbPieces) * pieceSize + CONTROL_SPACE_WIDTH + BORDER_WIDTH;
        int limitAxisY = (int) Math.sqrt(this.nbPieces) * pieceSize + BORDER_WIDTH;

        for (Piece piece : this.piecesList) {
            if (!piece.equals(currentPiece)) {
                if ((isAxisX && (afterAddStep < CONTROL_SPACE_WIDTH + BORDER_WIDTH || afterAddStep >= limitAxisX)) ||
                        (!isAxisX && (afterAddStep < 0 || afterAddStep >= limitAxisY))) {
                    return 0;
                } else {
                    if ((isAxisX && (piece.getX() == afterAddStep && piece.getY() == currentPiece.getY())) ||
                            (!isAxisX && (piece.getY() == afterAddStep && piece.getX() == currentPiece.getX()))) {
                        return 0;
                    }
                }
            }
        }
        return step;
    }

    private void move() {
        if (currentPiece != null) {
            boolean isAxisX = false;
            int step = getStep(MOVE_LEFT);
            if (step == 0) {
                step = getStep(MOVE_RIGHT);
                if (step == 0) {
                    step = getStep(MOVE_TOP);
                    if (step == 0) {
                        step = getStep(MOVE_BOTTOM);
                        if (step != 0) {
                            isAxisX = false;
                        }
                    } else {
                        isAxisX = false;
                    }
                } else {
                    isAxisX = true;
                }
            } else {
                isAxisX = true;
            }

            if (isAxisX) {
                currentPiece.setX(currentPiece.getX() + step);
            } else {
                currentPiece.setY(currentPiece.getY() + step);
            }
            currentPiece = null;
        }
    }

    private void controlShuffle() {
        if (imageFile != null) {
            isWin = false;
            Piece lastPiece = piecesList.get(nbPieces - 1);

            //On melange la liste des pieces
            Collections.shuffle(this.piecesList);

            //On rédefinit les nouvelles coordnnées des pieces
            int i = 0;
            for (Piece piece : piecesList) {
                if (lastPiece.equals(piece)) {
                    piece.setX(-piece.getSize());
                    piece.setY(-piece.getSize());
                    piece.setInitialX(-piece.getSize());
                    piece.setInitialY(-piece.getSize());
                } else {
                    piece.setX(tmpX[i]);
                    piece.setY(tmpY[i]);
                    piece.setInitialX(tmpX[i]);
                    piece.setInitialY(tmpY[i]);
                }
                i++;
            }
        }
    }

    private void controlChangeNbPieces() {
        isWin = false;
        Integer nb = (Integer) showInputDialog(null,
                "Choisir le nombre de pièces",
                "Choix du nombre de pièces",
                QUESTION_MESSAGE,
                null,
                nbPieceList,
                nbPieceList[1]
        );

        if (nb != null) {
            nbPieces = nb;
            build();
        }
    }

    private void controlChooseImage() {
        int rep = BibliothekDialog.getInstance().showDialog();
        if (rep == BibliothekDialog.OK_OPTION) {
            isWin = false;
            imageFile = BiblioImage.getCurrent().getPath().toFile();
            build();
        }
    }

    private void controlSave() throws IOException {
        ObjectOutputStream oos = null;

        Path path = Paths.get(CURRENT_RELATIVE_PATH + "/sauvegardes");
        if (Files.notExists(path)) {
            Files.createDirectory(path);
        }

        Path pathFichierPieces = Paths.get(path + "/pieces" + Math.random() + ".ser");
        if (Files.notExists(pathFichierPieces)) {
            Files.createFile(pathFichierPieces);
        }

        path = Paths.get(CURRENT_RELATIVE_PATH + "/images");
        if (Files.notExists(path)) {
            Files.createDirectory(path);
        }

        Path pathImage = Paths.get(path + "/" + imageFile.getName());
        if (Files.notExists(pathImage)) {
            BufferedImage img = ImageIO.read(imageFile);
            File file = new File(String.valueOf(pathImage));
            ImageIO.write(img, "jpg", file);
        }

        try {
            final FileOutputStream fichier = new FileOutputStream(String.valueOf(pathFichierPieces));
            oos = new ObjectOutputStream(new BufferedOutputStream(fichier));
            for (Piece piece : piecesList) {
                piece.setInitialX(piece.getX());
                piece.setInitialY(piece.getY());
                piece.setImageFile(new File(String.valueOf(pathImage)));
                oos.writeObject(piece);
            }
            oos.flush();
        } catch (final java.io.IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (oos != null) {
                    oos.flush();
                    oos.close();
                    showMessageDialog(null, "Sauvegarde réussie", "Sauvegarde", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void controlSave(String msg) {
        if (imageFile != null) {
            int rep = JOptionPane.showConfirmDialog(null, msg, "Confirmation", YES_NO_OPTION);
            if (rep == YES_OPTION) {
                try {
                    controlSave();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private void controlLoad() {
        Path path = Paths.get(CURRENT_RELATIVE_PATH + "/sauvegardes");

        if (Files.exists(path)) {
            ArrayList<Path> paths = new ArrayList<>();

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.{ser}")) {
                for (Path p : stream) {
                    paths.add(p);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (paths.size() > 0) {
                Path[] arrPaths = paths.toArray(new Path[0]);

                path = (Path) showInputDialog(null,
                        "Choisir la sauvegarde",
                        "Choix de la sauvegarde",
                        QUESTION_MESSAGE,
                        null,
                        arrPaths,
                        arrPaths[0]
                );
            } else {
                path = null;
                JOptionPane.showMessageDialog(null, "Il n'ya aucune sauvegarde", "Information", INFORMATION_MESSAGE);
            }


            if (path != null) {
                backupPath = path;
                isBackup = true;
                piecesList = new ArrayList<>();
                pieceImage = new HashMap<>();
                imagePuzzle = null;
                ObjectInputStream ois = null;
                try {
                    final FileInputStream fichier = new FileInputStream(String.valueOf(path));
                    ois = new ObjectInputStream(fichier);

                    nbPieces = 0;
                    Piece piece;
                    boolean eof = false;

                    while (!eof) {
                        try {
                            piece = (Piece) ois.readObject();
                            piecesList.add(piece);
                        } catch (EOFException e) {
                            tmpX = new int[piecesList.size()];
                            tmpY = new int[piecesList.size()];
                            nbPieces = 0;

                            Piece piece1 = piecesList.get(0);
                            imageFile = piece1.getImageFile();
                            Image image = new ImageIcon(imageFile.getAbsolutePath()).getImage();
                            imagePuzzle = (BufferedImage) ImageResizer.scaleImage(image, IMAGE_PUZZLE_SIZE, IMAGE_PUZZLE_SIZE);

                            for (Piece piece2 : piecesList) {
                                tmpX[nbPieces] = piece2.getX();
                                tmpY[nbPieces] = piece2.getY();
                                pieceImage.put(piece2, subImageRender(piece2.getSubX(), piece2.getSubY(), piece2.getSize()));
                                nbPieces++;
                            }

                            tmpImagePuzzleSize = (int) Math.sqrt(nbPieces) * piece1.getSize();
                            isBuild = true;
                            repaint();
                            eof = true;
                        }
                    }
                } catch (final IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (ois != null) {
                            ois.close();
                        }
                    } catch (final IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Il n'ya aucune sauvegarde", "Information", INFORMATION_MESSAGE);
        }
    }

    private void controlRefresh() {
        isWin = false;
        for (Piece piece : piecesList) {
            piece.initPosition();
        }
    }

    private void controlDeleteBackup() {
        if (isBackup) {
            try {
                Files.deleteIfExists(backupPath);
                imageFile = null;
                pieceImage = new HashMap<>();
                piecesList = new ArrayList<>();
                isBackup = false;
                isBuild = false;
                currentPiece = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void winTested() {
        isWin = true;
        Piece lastPiece = new Piece();
        for (Piece piece : piecesList) {
            if (piece.getInitialX() < 0) {
                lastPiece = piece;
            } else {
                if (piece.getX() - CONTROL_SPACE_WIDTH - BORDER_WIDTH != piece.getSubX() || piece.getY() - BORDER_WIDTH != piece.getSubY()) {
                    isWin = false;
                }
            }
        }

        if (isWin) {
            lastPiece.setX(lastPiece.getSubX() + CONTROL_SPACE_WIDTH + BORDER_WIDTH);
            lastPiece.setY(lastPiece.getSubY() + BORDER_WIDTH);
        }
    }

    public static Scene getInstance() {
        if (instance == null) {
            instance = new Scene();
        }
        return instance;
    }
}