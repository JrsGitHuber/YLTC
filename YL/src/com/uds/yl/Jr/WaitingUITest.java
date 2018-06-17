package com.uds.yl.Jr;

public class WaitingUITest {

	public static void main(String[] args) {
        try {
            WaitingUI.ShowProcessBar(null);
            Thread.sleep(10000);
            WaitingUI.CloseProcessBar();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
