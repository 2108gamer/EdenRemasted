package dev.hely.tab.api.thread;

import dev.hely.tab.api.Tab;

/**
 * Created By LeandroSSJ
 * Created on 22/09/2021
 */
public class TabThread extends Thread {

    private final Tab tab;

    public TabThread(Tab tab) {
        setName("Shadow - Tab Thread");
        setDaemon(true);

        this.tab = tab;
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                tab.getPlayerTablist().forEach((uniqueId, tab) -> tab.update());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(250L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
