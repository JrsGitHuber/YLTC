package com.uds.yl.ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.KeyStore.PrivateKeyEntry;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.teamcenter.rac.commands.voidDS.VoidDigitalSignatureDataBean;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.utils.StringsUtil;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

//补足选项
/**
 * @author infodba
 * "料液", "果酱", "二投蛋卷", "二投碎粒", "二投巧克力", "二投其他"
 *	"1000", "100", "*"
 */
public class ComplementFrame extends JFrame {

	private JPanel contentPane;
	private JButton cancleBtn;
	private JButton okBtn;
	private JComboBox typeCombox;//补足的类型
	private JComboBox complementCombox;//补足数额

	private AbstractCallBack mCallBack;

	/**
	 * Create the frame.
	 */
	public ComplementFrame(String type,String content,AbstractCallBack callBack) {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		this.mCallBack = callBack;
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		JLabel lblNewLabel_1 = new JLabel("补足中*表示添加多少就是多少不补足");
		lblNewLabel_1.setBounds(111, 21, 255, 15);
		contentPane.add(lblNewLabel_1);
		
		
		typeCombox = new JComboBox();
		typeCombox.setModel(new DefaultComboBoxModel(new String[] {"料液", "果酱", "二投蛋卷", "二投碎粒", "二投巧克力", "二投其他"}));
		typeCombox.setBounds(202, 53, 106, 30);
		contentPane.add(typeCombox);
		
		JLabel lblNewLabel = new JLabel("类型：");
		lblNewLabel.setBounds(74, 53, 54, 30);
		contentPane.add(lblNewLabel);
		
		JLabel label = new JLabel("补足：");
		label.setBounds(74, 129, 54, 30);
		contentPane.add(label);
		
		complementCombox = new JComboBox();
		complementCombox.setModel(new DefaultComboBoxModel(new String[] {"1000", "100", "*"}));
		complementCombox.setBounds(202, 129, 106, 30);
		contentPane.add(complementCombox);
		
		
		okBtn = new JButton("确定");
		okBtn.setBounds(72, 199, 93, 30);
		contentPane.add(okBtn);
		
		cancleBtn = new JButton("取消");
		cancleBtn.setBounds(273, 199, 93, 30);
		contentPane.add(cancleBtn);
		
		//初始化补足的类型和数值
		initCombox(type,content);
		
		cancleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
				return ;
			}
		});
		
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String type =  typeCombox.getSelectedItem().toString();
				String complementText = complementCombox.getSelectedItem().toString();
				boolean passFlag = false;
				//需要根据类型做判断
				if (!StringsUtil.isEmpty(type)) {// 如果type不为空则一定要保证填入数据的正确性质
					if ("料液".equals(type)) {// 补水1000kg 水中含钠
						if("1000".equals(complementText)){//1000内容
							passFlag = true;
						}
					}
					if ("果酱".equals(type)) {// 补水到100kg  水中含钠
						if("100".equals(complementText)){
							passFlag = true;
						}
					}
					if ("二投蛋卷".equals(type)) {//投多少是多少  要计算干物质
						if("*".equals(complementText)){
							passFlag = true;
						}
					}
					if ("二投碎粒".equals(type)) {//投多少是多少  要就算干物质
						if("*".equals(complementText)){
							passFlag = true;
						}
					}
					if ("二投巧克力".equals(type)) {//投多少是多少"*"  以*号代替投多少就是多少
						if("*".equals(complementText)){
							passFlag = true;
						}
					}
					if ("二投其他".equals(type)) {// 1固定
						if("*".equals(complementText)){
							passFlag = true;
						}
					}
					
				}
				
				if(!passFlag){//不符合规定
					MessageBox.post("请检查补足的类型和对应的补足的值！","",MessageBox.INFORMATION);
					return;
				}
				mCallBack.setCompelemnet(type, complementText);
				dispose();
			}
		});
	}
	
	
	/**
	 * @param type		组分的类型
	 * @param complementContent  组分的补足的单位值
	 */
	private void initCombox(String type,String complementContent){
		if ("料液".equals(type)) {// 0
			typeCombox.setSelectedIndex(0);
		}
		if ("果酱".equals(type)) {// 1
			typeCombox.setSelectedIndex(1);
		}
		if ("二投蛋卷".equals(type)) {//2
			typeCombox.setSelectedIndex(2);
		}
		if ("二投碎粒".equals(type)) {//3
			typeCombox.setSelectedIndex(3);
		}
		if ("二投巧克力".equals(type)) {//4
			typeCombox.setSelectedIndex(4);
		}
		if ("二投其他".equals(type)) {// 5
			typeCombox.setSelectedIndex(5);
		}
		
		if(StringsUtil.isEmpty(complementContent)){
			complementCombox.setSelectedIndex(2);//如果为空的话默认是选择*
		}else if("1000".equals(complementContent)){
			complementCombox.setSelectedIndex(0);
		}else if("100".equals(complementContent)){
			complementCombox.setSelectedIndex(1);
		}else if("*".equals(complementContent)){
			complementCombox.setSelectedIndex(2);
		}
	}
}
