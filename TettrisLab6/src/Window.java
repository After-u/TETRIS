import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Window extends JPanel implements ActionListener {


    @Serial
    private static final long serialVersionUID = 1L;
    final int BoardWidth = 10;
    final int BoardHeight = 22;

    Timer timer;
    boolean Fall_it = false;
    boolean Start = false;
    boolean Pause = false;
    int Score = 0;
    int X = 0;
    int Y = 0;
    JLabel statusbar;
    Shape now_piece;
    Shapes[] board;


    public Window(LETS_PLAY parent) {

        setFocusable(true);
        now_piece = new Shape();
        timer = new Timer(400, this);
        timer.start();

        statusbar = parent.getStatusBar();
        board = new Shapes[BoardWidth * BoardHeight];
        addKeyListener(new TAdapter());
        clearBoard();
    }

    public void actionPerformed(ActionEvent e) {
        if (Fall_it) {
            Fall_it = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }


    int squareWidth() {
        return (int) getSize().getWidth() / BoardWidth;
    }

    int squareHeight() {
        return (int) getSize().getHeight() / BoardHeight;
    }

    Shapes shapeAt(int x, int y) {
        return board[(y * BoardWidth) + x];
    }


    public void start() {
        if (Pause)
            return;

        Start = true;
        Fall_it = false;
        Score = 0;
        clearBoard();

        newPiece();
        timer.start();
    }

    private void pause() {

        if (!Start)
            return;

        Pause = !Pause;
        if (Pause) {
            timer.stop();
            statusbar.setText("Пауза, нажмите p");

        } else {
            timer.start();
            statusbar.setText("Счет: " + String.valueOf(Score));
        }
        repaint();
    }

    private void New_game() {

        start();
        statusbar.setText("Счет:  " + String.valueOf(Score));

    }

    private void High_score() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("HS.txt"));
        String s = br.readLine();
        statusbar.setText("Лучший результат : " + s);
        br.close();
    }


    private void Get_high_score() throws IOException {

        FileReader fin = new FileReader("ok.txt");

        int c;
        int number = 0;
        boolean ncheck = false;
        ArrayList<Integer> n = new ArrayList<Integer>();

        while ((c = fin.read()) != -1) {
            if (c >= 48 && c < 58) { //проверка соответствуют ли символы юникода цифрам
                number = number * 10 + Character.getNumericValue((char) c);
                ncheck = true;
            } else if (ncheck) {
                n.add(number);
                number = 0;
                ncheck = false;
            }

        }

        if (ncheck == true) { // не нашел решения получше,решает проблему если последним символом в файле является цифра,то предыдущая проверка не происходит.
            n.add(number);
            number = 0;
            ncheck = false;
        }


        Collections.sort(n, Collections.reverseOrder());


        FileWriter fw = new FileWriter(new File("Highscore.txt"));
        for (int i : n) {
            fw.write(Integer.toString(i) + (char) 13 + (char) 10);//"(char)13" и "(char)10" символы юникода управления строкой и кареткой
            fw.flush();
        }

    }


    public void paint(Graphics g) {
        super.paint(g);

        Dimension size = getSize();
        int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();


        for (int i = 0; i < BoardHeight; ++i) {
            for (int j = 0; j < BoardWidth; ++j) {
                Shapes shape = shapeAt(j, BoardHeight - i - 1);
                if (shape != Shapes.NoShape)
                    drawSquare(g, 0 + j * squareWidth(),
                            boardTop + i * squareHeight(), shape);
            }
        }

        if (now_piece.getShape() != Shapes.NoShape) {
            for (int i = 0; i < 4; ++i) {
                int x = X + now_piece.x(i);
                int y = Y - now_piece.y(i);
                drawSquare(g, 0 + x * squareWidth(),
                        boardTop + (BoardHeight - y - 1) * squareHeight(),
                        now_piece.getShape());
            }
        }
    }

    private void dropDown() {
        int newY = Y;
        while (newY > 0) {
            if (!tryMove(now_piece, X, newY - 1))
                break;
            --newY;
        }
        Drop_it();
    }

    private void oneLineDown() {
        if (!tryMove(now_piece, X, Y - 1))
            Drop_it();
    }


    private void clearBoard() {
        for (int i = 0; i < BoardHeight * BoardWidth; ++i)
            board[i] = Shapes.NoShape;
    }

    private void Drop_it() {
        for (int i = 0; i < 4; ++i) {
            int x = X + now_piece.x(i);

            int y = Y - now_piece.y(i);

            board[(y * BoardWidth) + x] = now_piece.getShape();
        }

        removeFullLines();

        if (!Fall_it)
            newPiece();
    }

    private void newPiece() {
        now_piece.setRandomShape();
        X = BoardWidth / 2 + 1;
        Y = BoardHeight - 1 + now_piece.minY();

        if (!tryMove(now_piece, X, Y)) {
            now_piece.setShape(Shapes.NoShape);
            timer.stop();

            statusbar.setText("GAME OVER");
            try (FileWriter writer = new FileWriter("score.txt", true)) {
                String text = Integer.toString(Score);
                String text1 = "\n";

                writer.write(text);
                writer.write(text1);
                writer.flush();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            int confirmed = JOptionPane.showConfirmDialog(null, "Начать новую игру?", "Exit Program Message Box", JOptionPane.YES_NO_OPTION);

            if (confirmed == JOptionPane.NO_OPTION) {
                Start = false;
            }
            if (confirmed == JOptionPane.YES_OPTION) {
                New_game();
            }
        }

    }

    private boolean tryMove(Shape newPiece, int newX, int newY) {
        for (int i = 0; i < 4; ++i) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);
            if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
                return false;
            if (shapeAt(x, y) != Shapes.NoShape)
                return false;
        }

        now_piece = newPiece;
        X = newX;
        Y = newY;
        repaint();
        return true;
    }

    private void removeFullLines() {
        int numFullLines = 0;

        for (int i = BoardHeight - 1; i >= 0; --i) {
            boolean lineIsFull = true;

            for (int j = 0; j < BoardWidth; ++j) {
                if (shapeAt(j, i) == Shapes.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                ++numFullLines;
                for (int k = i; k < BoardHeight - 1; ++k) {
                    for (int j = 0; j < BoardWidth; ++j)
                        board[(k * BoardWidth) + j] = shapeAt(j, k + 1);
                }
            }
        }

        if (numFullLines > 0) {
            Score =Score + 10;
            statusbar.setText(String.valueOf(Score));
            Fall_it = true;
            now_piece.setShape(Shapes.NoShape);

            repaint();
        }
    }


    private void drawSquare(Graphics g, int x, int y, Shapes shape) {
        Color colors[] = {
                new Color(133, 0, 0),
                new Color(5, 14, 190),
                new Color(224, 176, 255),
                new Color(255, 117, 56),
                new Color(204, 204, 102),
                new Color(22, 37, 28),
                new Color(9, 152, 184),
                new Color(218, 170, 0)
        };


        Color color = colors[shape.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth(), squareHeight());

        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
    }

    class TAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {

            if (!Start || now_piece.getShape() == Shapes.NoShape) {
                return;
            }

            int keycode = e.getKeyCode();

            if (keycode == 'p' || keycode == 'P') {
                pause();
                return;
            }

            if (keycode == 'n' || keycode == 'N') {

                New_game();

                return;
            }

            if (keycode == 'h' || keycode == 'H') {
                try {
                    pause();
                    Get_high_score();


                    High_score();
                } catch (IOException ex) {
                    Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
                }
                return;
            }
            if (Pause) {
                return;
            }

            switch (keycode) {
                case KeyEvent.VK_LEFT -> tryMove(now_piece, X - 1, Y);
                case KeyEvent.VK_RIGHT -> tryMove(now_piece, X + 1, Y);
                case KeyEvent.VK_DOWN -> tryMove(now_piece.rotateRight(), X, Y);
                case KeyEvent.VK_UP -> tryMove(now_piece.rotateLeft(), X, Y);
                case KeyEvent.VK_SPACE -> dropDown();
                case 'd' -> oneLineDown();
                case 'D' -> oneLineDown();
            }
        }
    }
}
