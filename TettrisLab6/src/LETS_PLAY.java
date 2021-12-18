import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serial;

public class LETS_PLAY extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;
    JLabel statusbar, score;

    public LETS_PLAY() {
        score = new JLabel("Счет:");
        statusbar = new JLabel("0");
        add(score, BorderLayout.SOUTH);
        add(statusbar, BorderLayout.SOUTH);
        Window window = new Window(this);
        JOptionPane.showMessageDialog(window,
                """
                        TETRIS\s
                         Кнопки :\s
                         1. Cдвинуться влево : ←
                         2. Сдвинуться вправо: →
                         
                         1. Поворот влево: ↓
                         2. Поворот вправо: ↑
                         Быстрое падение:Пробел
                         Пауза: P
                         Лучший результат: H
                         Новая игра: N
                        """);
        add(window);
        window.start();

        setSize(400, 800);
        setTitle("TETRIS");
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        LETS_PLAY game = new LETS_PLAY();
        game.setLocationRelativeTo(null);
        game.setVisible(true);
    }

    public JLabel getStatusBar() {
        return statusbar;
    }
}