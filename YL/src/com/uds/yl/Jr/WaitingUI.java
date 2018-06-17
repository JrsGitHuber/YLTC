package com.uds.yl.Jr;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class WaitingUI extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private static WaitingUI dialog;
    private static Rectangle rectangleObj;
    
//    public static void main(String[] args) {
//        try {
//        	WaitingUI dialog = new WaitingUI();
//            dialog.setVisible(true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    
    public static void ShowProcessBar(Rectangle rectangleObj1) {
    	rectangleObj = rectangleObj1;
        new Thread(){
            @Override
            public void run() {
                try {
                    dialog = new WaitingUI();
                    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    dialog.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    
    public static void CloseProcessBar() {
        if (dialog != null) {
            dialog.dispose();
        }
    }
    
    public WaitingUI() {
        setType(Type.UTILITY);
        setAlwaysOnTop(true);
        getContentPane().setBackground(Color.WHITE);
        setUndecorated(true);
        
        setBounds(200, 200, 380, 180);
        if (rectangleObj == null) {
        	setLocationRelativeTo(null);
        } else {
        	int centerX = rectangleObj.x + rectangleObj.width / 2;
    		int centerY = rectangleObj.y + rectangleObj.height / 2;
    		setBounds(centerX - 190, centerY - 90, 380, 180);
        }
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//        com.sun.awt.AWTUtilities.setWindowOpacity(this, 0.9f);
        
        getContentPane().setLayout(null);
        
        JLabel lblNewLabel = new JLabel();
        
        Image image = Toolkit.getDefaultToolkit().createImage(WaitingUI.class.getResource("/com/uds/yl/Jr//waiting.gif"));
        Image smallImage = image.getScaledInstance(100, 100, Image.SCALE_FAST);
        ImageIcon smallIcon = new ImageIcon(smallImage);

        lblNewLabel.setIcon(smallIcon);
        lblNewLabel.setBounds(140, 26, 100, 100);
        getContentPane().add(lblNewLabel);
        
        JLabel lblNewLabel_1 = new JLabel("X");
        lblNewLabel_1.setBounds(365, 5, 15, 15);
        lblNewLabel_1.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
            
        });
        getContentPane().add(lblNewLabel_1);
        
        JLabel label = new JLabel("正在执行，请稍等...");
        label.setBounds(133, 130, 114, 15);
        getContentPane().add(label);
    }
}
