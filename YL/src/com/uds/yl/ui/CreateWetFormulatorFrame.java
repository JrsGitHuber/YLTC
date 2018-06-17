package com.uds.yl.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.utils.StringsUtil;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class CreateWetFormulatorFrame extends JFrame {

	private JPanel contentPane;
	private JTextField wetFormulatorNameEdt;
	private JCheckBox isWetFormulatorCheckBox;

	
	private AbstractCallBack mCallBack;

	/**
	 * Create the frame.
	 */
	public CreateWetFormulatorFrame(AbstractCallBack callBack) {
		
		this.mCallBack = callBack;
		setTitle("创建湿法配方");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		isWetFormulatorCheckBox = new JCheckBox("选中配方为基粉配方");
		isWetFormulatorCheckBox.setBounds(91, 33, 212, 23);
		contentPane.add(isWetFormulatorCheckBox);
		
		JButton okBtn = new JButton("确定");
		okBtn.setBounds(40, 189, 93, 23);
		contentPane.add(okBtn);
		
		JButton cancleBtn = new JButton("取消");
		cancleBtn.setBounds(235, 189, 93, 23);
		contentPane.add(cancleBtn);
		
		wetFormulatorNameEdt = new JTextField();
		wetFormulatorNameEdt.setBounds(91, 92, 173, 21);
		contentPane.add(wetFormulatorNameEdt);
		wetFormulatorNameEdt.setColumns(10);
		
		//确定
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean selected = isWetFormulatorCheckBox.isSelected();
				String name = wetFormulatorNameEdt.getText().toString()+"";
				if(!selected && StringsUtil.isEmpty(name)){//如果不选中就需要填写名称
					MessageBox.post("请填写基粉配方的名称","",MessageBox.INFORMATION);
					return ;
				}
				mCallBack.createWetFormulator(selected, name);
				dispose();
			}
		});
	}
}
