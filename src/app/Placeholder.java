package app;

import javafx.concurrent.Task;

public abstract class Placeholder extends Task<Void> {
    static final int cols = 8;
    static final int rows = 8;

    static final int queen = 8;

    private final int[] x0 = new int[queen];
    private final int[] y0 = new int[queen];
    private int size = 0;

    private void add(int x, int y) {
        x0[size] = x;
        y0[size] = y;
        size++;
    }
    private void remove() {
        size--;
    }

    private boolean shot(int queenX, int queenY, int x, int y) {
        return (queenX == x) || (queenY == y) ||
                (Math.abs(x - queenX) == Math.abs(y - queenY));
    }
    private boolean canMove(int x, int y) {
        if ((x < 0 || x >= cols) || (y < 0 || y >= rows))
            return false;
        for (int i = 0; i < size; i++)
            if (shot(x0[i], y0[i], x, y))
                return false;
        return true;
    }
    private void move(int y) {
        for (int x = 0; x < cols; x++) {
            if (isCancelled())
                return;
            onChanged(x, y);
            if (canMove(x, y)) {
                add(x, y);
                move(y + 1);
                remove();
                onChanged(x, y);
            }
        }
    }
    public Void call() {
        while (!isCancelled()) {
            size = 0;
            move(0);
        }
        return null;
    }

    protected boolean isPlaceholder() {
        return size == queen;
    }
    protected final int size() {
        return size;
    }
    protected final int getX(int index) {
        return x0[index];
    }
    protected final int getY(int index) {
        return y0[index];
    }

    protected abstract void onChanged(int x, int y);
}