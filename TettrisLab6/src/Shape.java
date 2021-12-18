import java.util.Random;

public class Shape {

    private Shapes piece;
    private int coords[][];
    private int[][][] coordsTable;


    public Shape() {

        coords = new int[4][2];
        setShape(Shapes.NoShape);

    }

    private void setX(int index, int x) {
        coords[index][0] = x;
    }

    private void setY(int index, int y) {
        coords[index][1] = y;
    }

    public int x(int index) {
        return coords[index][0];
    }

    public int y(int index) {
        return coords[index][1];
    }

    public Shapes getShape() {
        return piece;
    }

    public void setShape(Shapes shape) {

        coordsTable = new int[][][]
                {
                        {{0, 0}, {0, 0}, {0, 0}, {0, 0}},
                        {{0, -1}, {0, 0}, {-1, 0}, {-1, 1}},
                        {{0, -1}, {0, 0}, {1, 0}, {1, 1}},
                        {{0, -1}, {0, 0}, {0, 1}, {0, 2}},
                        {{-1, 0}, {0, 0}, {1, 0}, {0, 1}},
                        {{0, 0}, {1, 0}, {0, 1}, {1, 1}},
                        {{-1, -1}, {0, -1}, {0, 0}, {0, 1}},
                        {{1, -1}, {0, -1}, {0, 0}, {0, 1}}
                };

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; ++j) {
                coords[i][j] = coordsTable[shape.ordinal()][i][j];
            }
        }
        piece = shape;

    }

    public void setRandomShape() {
        Random r = new Random();
        int x = Math.abs(r.nextInt()) % 7 + 1;
        Shapes[] values = Shapes.values();
        setShape(values[x]);
    }

    public int minX() {
        int m = coords[0][0];
        for (int i = 0; i < 4; i++) {
            m = Math.min(m, coords[i][0]);
        }
        return m;
    }


    public int minY() {
        int m = coords[0][1];
        for (int i = 0; i < 4; i++) {
            m = Math.min(m, coords[i][1]);
        }
        return m;
    }

    public Shape rotateLeft() {
        if (piece == Shapes.Quadrat)
            return this;

        Shape result = new Shape();
        result.piece = piece;

        for (int i = 0; i < 4; ++i) {
            result.setX(i, y(i));
            result.setY(i, -x(i));
        }
        return result;
    }

    public Shape rotateRight() {
        if (piece == Shapes.Quadrat)
            return this;

        Shape result = new Shape();
        result.piece = piece;

        for (int i = 0; i < 4; ++i) {
            result.setX(i, -y(i));
            result.setY(i, x(i));
        }
        return result;
    }
}
