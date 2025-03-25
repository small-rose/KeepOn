package small.rose;

import javax.swing.*;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        SwingUtilities.invokeLater(() -> new AntiLockScreen().setVisible(true));
    }
}
