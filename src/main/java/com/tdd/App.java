package com.tdd;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

/**
 * Hello world!
 */
public final class App {
    
    public App() throws Exception {
        startUserInterface();
    }

    private MainWindow ui;

    public static String SNIPER_STATUS_NAME = "SNIPER STATUS";
    public static String STATUS_JOINING = "JOINING";
    public static String STATUS_LOST = "LOST";

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String... args) throws Exception {
        App app = new App();
        System.out.println("Running Application...");
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                ui = new MainWindow();
            }
        });
    }

    public class MainWindow extends JFrame {
        static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
        final JLabel sniperStatusLabel = createLabel(STATUS_JOINING);
        public MainWindow() {
            super("Auction Sniper");
            setName(MAIN_WINDOW_NAME);
            add(sniperStatusLabel);
            pack();
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setVisible(true);
        }

        private JLabel createLabel(String initialText) {
            JLabel result = new JLabel(initialText);
            result.setName(SNIPER_STATUS_NAME);
            result.setBorder(new LineBorder(Color.BLACK));
            return result;
        }
    }
}
