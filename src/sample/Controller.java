package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Objects;

public class Controller {

    @FXML
    private Canvas canvas;

    @FXML
    private Label state;

    @FXML
    private ComboBox<Integer> combobox;

    @FXML
    private ComboBox<Integer> combobox1;

    @FXML
    void startAction() {
        if (thread != null)
            thread.stop();
        thread = new Thread(this::start);
        thread.start();
        showState("Searching");
    }

    @FXML
    void initialize() {
        bg = getImage("chessboard.png");
        queen = getImage("queen.png");
        drawField(canvas.getGraphicsContext2D());
        ObservableList<Integer> list = FXCollections.observableArrayList(
                10, 100, 200, 300, 500, 700, 1000, 1500, 2000, 3000, 5000);
        combobox.setItems(list);
        combobox.setOnAction(event -> delay = combobox.getValue());
        combobox.setValue(field);
        combobox1.setItems(list);
        combobox1.setOnAction(event -> sleep = combobox1.getValue());
        combobox1.setValue(sleep);
    }

    Image getImage(String name) {
        return new Image(Objects.requireNonNull(
                getClass().getClassLoader().
                        getResourceAsStream(name)));
    }

    private Thread thread;

    private Image bg, queen;

    private final int field = 400;
    private final int dotSize = 50;

    private int delay = 500;
    private int sleep = 2000;

    private final int cols = 8;
    private final int rows = 8;

    private final int countQueens = 8;
    private final int[] x0 = new int[countQueens];
    private final int[] y0 = new int[countQueens];
    private int size = 0;

    public void start() {
        size = 0;
        move(0);
    }

    private boolean canMove(int x, int y) {
        if ((x < 0 || x >= cols) || (y < 0 || y >= rows))
            return false;
        for (int i = 0; i < size; i++)
            if (shot(x0[i], y0[i], x, y))
                return false;
        return true;
    }

    private boolean shot(int queenX, int queenY, int x, int y) {
        return (queenX == x) || (queenY == y) ||
                (Math.abs(x - queenX) == Math.abs(y - queenY));
    }

    private void move(int y) {
        for (int x = 0; x < cols; x++) {
            drawNextPoint(x, y);
            if (canMove(x, y)) {
                addPoint(x, y);
                drawQueens();
                move(y + 1);
                removePoint();
                drawNextPoint(x, y);
            }
        }
    }

    private void removePoint() {
        size--;
    }

    private void addPoint(int x, int y) {
        x0[size] = x;
        y0[size] = y;
        size++;
    }

    private void drawQueens() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawField(gc);
        if (size == countQueens) {
            showState("Found");
            trySleep(sleep);
            showState("Searching");
        } else {
            trySleep(delay);
        }
    }

    private void showState(String message) {
        Platform.runLater(() -> {
            state.setText(message);
        });
    }

    private void drawNextPoint(int x, int y) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawField(gc);
        gc.setFill(Color.RED);
        gc.fillOval(x * dotSize, y * dotSize, dotSize, dotSize);
        trySleep(delay);
    }

    private void drawField(GraphicsContext gc) {
        gc.drawImage(bg, 0, 0, field, field);
        for (int i = 0; i < size; i++) {
            gc.drawImage(queen, x0[i] * dotSize, y0[i] * dotSize, dotSize, dotSize);
        }
    }

    private void trySleep(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}