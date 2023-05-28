package com.tdd;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

/**
 * Hello world!
 */
public final class App {
    
    public App() throws Exception {
        startUserInterface();
    }

    @SuppressWarnings("unused") private Chat notToBeGCd;

    private MainWindow ui;
    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;
    private static final int ARG_ITEM_ID = 3;

    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_RESOURCE = "Auction";
    public static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String... args) throws Exception {
        App app = new App();
        app.joinAuction(connection(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]), args[ARG_ITEM_ID]);
        
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                ui = new MainWindow();
            }
        });
    }

    private static XMPPConnection connection(String hostname, String username, String password) throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);

        return connection;
    }

    private void joinAuction(XMPPConnection connection, String itemId) throws XMPPException {
    final Chat chat = connection.getChatManager().createChat(auctionId(itemId, connection),
    new MessageListener() {
        public void processMessage(Chat aChat, Message message) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ui.showStatus(MainWindow.STATUS_LOST);
                }
            });
        }
    });
    this.notToBeGCd = chat;
    chat.sendMessage(new Message());
    }
    private static String auctionId(String itemId, XMPPConnection connection) {
        return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
    }

    public class MainWindow extends JFrame {
        static final String SNIPER_STATUS_NAME = "SNIPER STATUS";
        static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
        static final String STATUS_LOST = "LOST";
        static final String STATUS_JOINING = "JOINING";
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

        public void showStatus(String status) {
            sniperStatusLabel.setText(status);
        }
    }
}
