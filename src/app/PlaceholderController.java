package app;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class PlaceholderController extends Placeholder {
    private final Object drawLock = new Object();
    private volatile boolean isDraw = false;

    private final Object searchLock = new Object();
    private volatile boolean searching = false;

    static final int cw = 70;
    static final int ch = 70;

    private final VBox root = new VBox();
    private final Canvas canvas = new Canvas(cw * rows, ch * cols);
    private final Label state = new Label("Press button to start searching");
    private final ToggleButton search = new ToggleButton("Search");
    private final Image queen = new Image("queen.png");

    public PlaceholderController() {
        root.setSpacing(5);
        root.setPadding(new Insets(10, 10, 10, 10));
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.CORNSILK, null, null)));
        root.getChildren().addAll(canvas, state, search);

        search.setPrefWidth(200);
        search.setOnAction(event -> {
            state.setText(search.isSelected() ? "Searching" : "Pause");
            synchronized (searchLock) {
                searching = search.isSelected();
                searchLock.notify();
            }
        });

        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setStroke(Color.BLACK);
        g.setLineWidth(3);
    }

    public Pane getRoot() {
        return root;
    }

    protected void onChanged(int x0, int y0) {
        repaint(x0, y0);
        if (isPlaceholder()) {
            Platform.runLater(() -> {
                state.setText("Found");
                search.setSelected(false);
            });
            searching = false;
        }
        if (!searching) {
            synchronized (searchLock) {
                while (!searching) {
                    try {
                        searchLock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        cancel();
                        return;
                    }
                }
            }
        } else {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                cancel();
            }
        }
    }

    private void repaint(int x0, int y0) {
        synchronized (drawLock) {
            isDraw = true;
            Platform.runLater(() -> {
                synchronized (drawLock) {
                    draw(x0, y0);
                    isDraw = false;
                    drawLock.notify();
                }
            });
            while (isDraw) {
                try {
                    drawLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    cancel();
                    return;
                }
            }
        }
    }

    private void draw(int x0, int y0) {
        GraphicsContext g = canvas.getGraphicsContext2D();
        for (int y = 0; y < cols; y++) {
            for (int x = 0; x < rows; x++) {
                g.setFill(((x + y) & 1) == 0 ? Color.CHOCOLATE : Color.TAN);
                g.fillRect(x * cw, y * ch, cw, ch);
            }
        }
        for (int i = 0; i < size(); i++) {
            g.drawImage(queen, getX(i) * cw, getY(i) * ch, cw, ch);
        }
        g.setFill(Color.RED);
        g.fillOval(x0 * cw, y0 * ch, cw, ch);
        g.strokeRect(0, 0, cols * cw, rows * ch);
    }
}
