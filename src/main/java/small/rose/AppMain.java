package small.rose;

import small.rose.gui.MainScreen;

import javax.swing.*;

/**
 * Hello world!
 */
public class AppMain {
    public static void main(String[] args) {
        //System.out.println("Hello World!");
        SwingUtilities.invokeLater(() -> new MainScreen().setVisible(true));
    }
}
