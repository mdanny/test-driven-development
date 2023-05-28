package com.tdd;

import com.tdd.App.MainWindow;

/*
 *  An ApplicationRunner is an object that wraps up all management and communicating with the Swing application
 *  we’re building. It runs the application as if from the command line, obtaining and holding a reference to its
 *  main window for querying the state of the GUI and for shutting down the application at the end of the test.
 */
public class ApplicationRunner {
    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "sniper";
    public static final String XMPP_HOSTNAME = "localhost";
    private AuctionSniperDriver driver;

    public void startBiddingIn(final FakeAuctionServer auction) {
        Thread thread = new Thread("Test Application") {    
            @Override public void run() {
                try {   
                App.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
                } catch (Exception e) {
                e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        driver = new AuctionSniperDriver(1000);
        driver.showsSniperStatus(MainWindow.STATUS_JOINING);
    }
    
    public void showsSniperHasLostAuction() {
        driver.showsSniperStatus(MainWindow.STATUS_LOST);
    }
    
    public void stop() {
        if (driver != null) {
            driver.dispose();
            }
        }
    }