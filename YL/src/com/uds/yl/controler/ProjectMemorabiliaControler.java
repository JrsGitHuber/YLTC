package com.uds.yl.controler;

import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;

import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.Jr.ExportExcelProjectMemorabilia;
import com.uds.yl.Jr.JDateChooser;
import com.uds.yl.ui.ProgressBar;


// Һ�̴��¼�
public class ProjectMemorabiliaControler {
	
	private Rectangle rectangleObj;
	private static ProjectMemorabilia frame;
		
	public ProjectMemorabiliaControler(Rectangle rectangleObj){
		this.rectangleObj = rectangleObj;
	}
	
	public void userTask() {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					if(frame == null){
						frame = new ProjectMemorabilia();
					}
					
					frame.setVisible(true);
					frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					frame.setResizable(false);
				} catch (Exception e) {
					frame = null;
					MessageBox.post("���г�������ϵ����Ա�鿴����̨����Ĵ�����Ϣ", "��ʾ", MessageBox.INFORMATION);
					e.printStackTrace();
				}
			}
		});
	}

	class ProjectMemorabilia extends JFrame {
		
	    private static final long serialVersionUID = 1L;
	    
	    private JPanel contentPane;
	    JButton button;
	    JButton button_1;
	    JLabel label_3;
	    JLabel label_4;
	    JCheckBox checkBox;
	    JCheckBox checkBox_1;
	    JRadioButton radioButton;
	    JRadioButton radioButton_1;
	    JRadioButton radioButton_2;
	    
	    public ProjectMemorabilia() {
	        setAlwaysOnTop(true);
	        setResizable(false);
	        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	        
	        setTitle(" ��Ŀ���¼�");
	        setType(Type.UTILITY);
	        // ���ݴ�����Rectangle���ô���λ��
	        int centerX = rectangleObj.x + rectangleObj.width / 2;
 			int centerY = rectangleObj.y + rectangleObj.height / 2;
 			setBounds(centerX - 210, centerY - 130, 420, 250);
	        
	        contentPane = new JPanel();
	        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	        setContentPane(contentPane);
	        contentPane.setLayout(null);
	        
	        JLabel label = new JLabel("�������ͣ�");
	        label.setBounds(42, 28, 69, 27);
	        contentPane.add(label);
	        
	        JLabel label_1 = new JLabel("����״̬��");
	        label_1.setBounds(42, 73, 69, 27);
	        contentPane.add(label_1);
	        
	        JLabel label_2 = new JLabel("��ʼ���ڣ�");
	        label_2.setBounds(42, 118, 69, 27);
	        contentPane.add(label_2);
	        
	        checkBox = new JCheckBox("��������");
	        checkBox.setBounds(127, 29, 103, 23);
	        contentPane.add(checkBox);
	        
	        checkBox_1 = new JCheckBox("�з�����");
	        checkBox_1.setBounds(252, 30, 103, 23);
	        contentPane.add(checkBox_1);
	        
	        radioButton = new JRadioButton("ȫ��");
	        radioButton.setBounds(127, 74, 71, 23);
	        contentPane.add(radioButton);
	        
	        radioButton_1 = new JRadioButton("δ���");
	        radioButton_1.setBounds(213, 74, 71, 23);
	        contentPane.add(radioButton_1);
	        
	        radioButton_2 = new JRadioButton("���");
	        radioButton_2.setBounds(310, 75, 71, 23);
	        contentPane.add(radioButton_2);
	        
	        ButtonGroup bg = new ButtonGroup();
	        bg.add(radioButton);
	        bg.add(radioButton_1);
	        bg.add(radioButton_2);
	        bg.setSelected(radioButton.getModel(), true);
	        
	        button = new JButton("ȷ��");
	        button.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	// �����������
	            	String taskType = "";
	            	if (checkBox.isSelected() && checkBox_1.isSelected()) {
	            		taskType = "�������ϰ汾;�з��������ϰ汾";
	            	} else if (checkBox.isSelected()) {
	            		taskType = "�������ϰ汾";
	            	} else if (checkBox_1.isSelected()) {
	            		taskType = "�з��������ϰ汾";
	            	} else {
	            		JOptionPane.showMessageDialog(contentPane, "��ѡ����������", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
	                    return;
	            	}
	            	
	            	// ��鿪ʼ����
	            	// ��TC�в�ѯʱ�������yyyy-M-d HH:mm
	                final String date = label_3.getText() + " 00:00";
	                final String date1 = label_4.getText() + " 00:00";
	                if (date.compareTo(date1) > 0) {
	                    JOptionPane.showMessageDialog(contentPane, "��ʼ���ڲ������ڽ�������", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
	                    return;
	                }
	                
	                // ��ȡ����״̬
	                String status = "";
	                if (radioButton.isSelected()) {
	                	status = "ȫ��";
	                }
	                if (radioButton_1.isSelected()) {
	                	status = "δ���";
	                }
	                if (radioButton_2.isSelected()) {
	                	status = "���";
	                }
	                final String status1 = status;
	                
	                // ѡ���ļ������Ŀ¼
	                String filePath = "";
	                JFileChooser fileChooser  = new JFileChooser();
	                FileSystemView fsv = FileSystemView.getFileSystemView();
	                fileChooser .setCurrentDirectory(fsv.getHomeDirectory());
	                fileChooser .setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	                int returnVal = fileChooser.showDialog(contentPane, "����");
	                if (returnVal == JFileChooser.APPROVE_OPTION) {
	                    filePath= fileChooser.getSelectedFile().getAbsolutePath();
	                }
	                if (filePath.equals("")) {
	                	JOptionPane.showMessageDialog(contentPane, "��ѡ�񱣴��ļ���Ŀ¼", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
	                	return;
	                }
	                final String filePath1 = filePath;
	                dispose();
	                
	                // ��ʼ��������
	                final String taskType1 = taskType;
	                Thread thread = new Thread()
					{
						@Override
						public void run() {
							try {
								ExportExcelProjectMemorabilia obj = new ExportExcelProjectMemorabilia(filePath1, taskType1, status1, date, date1);
								obj.initGetDataAndExportWord();
								obj.GetDateAndExportExcel();
							} catch (Exception e) {
								MessageBox.post("��֯���ݳ�������ϵ����Ա�鿴����̨����Ĵ�����Ϣ", "��ʾ", MessageBox.INFORMATION);
								e.printStackTrace();
							}
						}
					};
					ProgressBar progressBar = new ProgressBar("������֯����", thread, rectangleObj);
					progressBar.Start();
	            }
	        });
	        button.setBounds(89, 174, 93, 23);
	        contentPane.add(button);
	        
	        button_1 = new JButton("ȡ��");
	        button_1.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                dispose();
	            }
	        });
	        button_1.setBounds(234, 174, 93, 23);
	        contentPane.add(button_1);
	        
	        String dateNow = new SimpleDateFormat("yyyy-M-d").format(new Date());
	        String dateStart = dateNow.substring(0, dateNow.lastIndexOf("-")) + "-1";
	        label_3 = new JLabel(dateStart);
	        label_3.setBounds(127, 124, 71, 15);
	        contentPane.add(label_3);
	        
	        label_4 = new JLabel(dateNow);
	        label_4.setBounds(265, 124, 71, 15);
	        contentPane.add(label_4);
	        
	        JButton button_2 = new JButton();
	        //button_2.setIcon(new ImageIcon(ProjectMemorabilia.class.getResource("/com/uds/yl/Jr/calendar_Test.jpg")));
	        button_2.setIcon(new ImageIcon(ProjectMemorabilia.class.getResource("/calendar_Test.jpg")));
	        button_2.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                try {
	                    String text = JDateChooser.GetDate(GeDateChoosertRectangle("date1"), label_3.getText());
	                    label_3.setText(text);
	                } catch (Exception e1) {
	                    JOptionPane.showMessageDialog(contentPane, "����ѡ���������ϵ����Ա�鿴����̨����Ĵ�����Ϣ", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
	                    e1.printStackTrace();
	                }
	            }
	        });
	        button_2.setBounds(196, 124, 18, 15);
	        button_2.setMargin(new Insets(0, 0, 0, 0));
	        contentPane.add(button_2);
	        
	        JLabel label_5 = new JLabel("~");
	        label_5.setBounds(237, 124, 18, 15);
	        contentPane.add(label_5);
	        
	        JButton button_3 = new JButton();
	        //button_3.setIcon(new ImageIcon(ProjectMemorabilia.class.getResource("/com/uds/yl/Jr/calendar_Test.jpg")));
	        button_3.setIcon(new ImageIcon(ProjectMemorabilia.class.getResource("/calendar_Test.jpg")));
	        button_3.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                try {
	                    String text = JDateChooser.GetDate(GeDateChoosertRectangle("date2"), label_4.getText());
	                    label_4.setText(text);
	                } catch (Exception e1) {
	                    JOptionPane.showMessageDialog(contentPane, "����ѡ���������ϵ����Ա�鿴����̨����Ĵ�����Ϣ", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
	                    e1.printStackTrace();
	                }
	            }
	        });
	        button_3.setBounds(337, 124, 18, 15);
	        button_3.setMargin(new Insets(0, 0, 0, 0));
	        contentPane.add(button_3);
	    }
	    
	    public void dispose() {
			super.dispose();
			frame = null;
		}
	    
	    private Rectangle GeDateChoosertRectangle(String buttonName){
	        int offsetX = 0;
	        int offsetY = 0;
	        if (buttonName.equals("date1")) {
	            offsetX = 122;
	            offsetY = 170;
	        } else {
	            offsetX = 260;
	            offsetY = 170;
	        }
	        
	        Rectangle rectangle = GetRectangle();
	        int centerX = rectangle.x + offsetX;
	        int centerY = rectangle.y + offsetY;
	        Rectangle newRectangle = new Rectangle(centerX, centerY, 300, 220);
	        return newRectangle;
	    }
	    private Rectangle GetRectangle() {
	        return this.getBounds();
	    }

	}
}
