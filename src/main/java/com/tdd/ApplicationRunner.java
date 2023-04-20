package com.tdd;

public class ApplicationRunner {
    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "sniper";
    public static final String XMPP_HOSTNAME = "openfire-server";
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
        driver.showsSniperStatus("App.STATUS_JOINING");
    }
    
    public void showsSniperHasLostAuction() {
        driver.showsSniperStatus("App.STATUS_LOST");
    }
    
    public void stop() {
        if (driver != null) {
            driver.dispose();
            }
        }
    }