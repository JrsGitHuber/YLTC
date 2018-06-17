package com.uds.yl.Jr;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.UIManager;

public class FormulaModelChoose extends JDialog {
    
    private static final long serialVersionUID = 1L;
    private String message = "";
    
    public FormulaModelChoose(Rectangle rectangleObj) {
        setIconImage(Toolkit.getDefaultToolkit().getImage(FormulaModelChoose.class.getResource("/teamcenter_app_256.png")));
        setModal(true);
        setAlwaysOnTop(true);
        setResizable(false);
        setTitle(" 选择操作模式");
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setBounds(200, 200, 340, 120);
        if (rectangleObj == null) {
        	setLocationRelativeTo(null);
        } else {
        	int centerX = rectangleObj.x + rectangleObj.width / 2;
    		int centerY = rectangleObj.y + rectangleObj.height / 2;
    		setBounds(centerX - 175, centerY - 60, 340, 120);
        }
        
        getContentPane().setLayout(null);
        {
            JButton button = new JButton("新增模式");
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    message = "新增模式";
                    dispose();
                }
            });
            button.setBounds(52, 26, 100, 43);
            button.setFocusPainted(false);
            getContentPane().add(button);
        }
        {
            final JButton button = new JButton("编辑模式");
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    message = "编辑模式";
                    dispose();
                }
            });
            button.setBounds(193, 26, 100, 43);
            button.setFocusPainted(false);
            getContentPane().add(button);
        }
//        {
//            JButton button = new JButton("确定");
//            button.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent e) {
//                    dispose();
//                }
//            });
//            button.setBounds(193, 88, 89, 23);
//            button.setFocusPainted(false);
//            getContentPane().add(button);
//        }
    }
    
    public static String GetChooseMessage(Rectangle rectangleObj) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        
        FormulaModelChoose dialog = new FormulaModelChoose(rectangleObj);
        dialog.setVisible(true);
        return dialog.GetMessage();
    }
    
    private String GetMessage() {
        return message;
    }

}
