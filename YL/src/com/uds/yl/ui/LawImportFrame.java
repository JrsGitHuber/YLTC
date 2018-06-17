package com.uds.yl.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.common.LogLevel;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.tcutils.QueryUtil;
import com.uds.yl.utils.LogFactory;
import com.uds.yl.utils.StringsUtil;

public class LawImportFrame extends JFrame {

	private JPanel contentPane;
	
	private String type="";

	private JLabel lawNameText;
	
	private JComboBox comboBox;
	
	private File[] selectedFiles;//ѡ�е��ļ�����
	
	Logger logger = null;
	
	private ProgressBarDialog progressBarDialog = null;
	
	/**
	 * Create the frame.
	 */
	public LawImportFrame(final AbstractCallBack callBack, final Logger logger) {
		
		this.logger = logger;
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 533, 263);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"��Ӽ�", "ָ��"}));
		comboBox.setBounds(134, 41, 81, 30);
		contentPane.add(comboBox);
		
		JLabel lblNewLabel = new JLabel("���ͣ�");
		lblNewLabel.setBounds(70, 41, 54, 30);
		contentPane.add(lblNewLabel);
		
		JButton upLoadFile = new JButton("ѡ���ļ�");
		upLoadFile.setBounds(276, 41, 156, 30);
		contentPane.add(upLoadFile);
		
		JButton okBtn = new JButton("ȷ��");
		okBtn.setBounds(84, 152, 93, 30);
		contentPane.add(okBtn);
		
		JButton cancleBtn = new JButton("ȡ��");
		cancleBtn.setBounds(345, 152, 93, 30);
		contentPane.add(cancleBtn);
		
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int selectedIndex = comboBox.getSelectedIndex();
				if(selectedIndex==-1){
					type = "";
					return;
				}
				type = (String) comboBox.getSelectedItem();
				
				if(!StringsUtil.isEmpty(type)){//fileѡ��ɹ���type��Ϊ��
					
					
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							//������
							progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
							//����ļ��� ��ϵID �Ƿ��Ѿ�������
							List<String> exitLawList = checkLawExitList(selectedFiles);
							
							if(exitLawList.size() > 0){//˵�����ظ����ļ�
								progressBarDialog.stop();
								
								StringBuilder stringBuilder = new StringBuilder();
								
								for(String fileName : exitLawList){
									stringBuilder.append("#"+fileName);
									//��ʾ���ҷ���
									logger.fine("[Failure]����"+fileName+"excel��ϵͳ���Ѿ�����,�������µ���");
								}
								MessageBox.post(stringBuilder.toString(),"�Ѵ��ڷ��棬��ȥC��YLLog�ļ����в鿴��־",MessageBox.INFORMATION);
								return;
								
							}else{
								
								callBack.setFilesAndType(type, selectedFiles);
								progressBarDialog.stop();
							}
							
							
						}
					}).start();
				}
			}
		});
		
		
		upLoadFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc=new JFileChooser();  
		        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY );  
		        jfc.setMultiSelectionEnabled(true);
		        int result = jfc.showDialog(new JLabel(), "ѡ��"); //0��ʾȷ��   1��ʾȡ��
		        if(result==1) return;
		        selectedFiles = jfc.getSelectedFiles();
		        
		        if(selectedFiles.length<1){  
		            MessageBox.post("��ѡ���ļ�","",MessageBox.ERROR);
		            return;
		        } 
			}
		});
		
		cancleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(progressBarDialog.isLive()){
					progressBarDialog.stop();
				}
				dispose();
				return;
			}
		});
		
		
	}
	
	
	/**
	 * ���Ҫ����ķ��� �Ѿ����ڵ�
	 * @param selectedFiles
	 * @return
	 */
	public List<String> checkLawExitList( File[] selectedFiles){
		
		List<String> exitLawList = new ArrayList<String>();
		for(File file : selectedFiles){
			//����һ�� Ҫ�ȷֳ���    ��ϵID  ����   �汾   
			String fileName;
			String id;
			String revId;
			String revNum;
			String revName;
			try{
				fileName = file.getName().split("\\.")[0];//ȥ����׺�������
				id = fileName.split(" ")[0];//��ϵID
				revId = fileName.split(" ")[1]+" "+fileName.split(" ")[2].split("-")[0];//���������
				revNum = fileName.split(" ")[2].split("-")[1];//����İ汾��
				revName = fileName.split(" ")[3];//��������� 
				
				TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_LawRevsion.getValue());
				TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[]{"��ϵID","��κ�"}, new String[]{id, revNum});
				
				if(searchResult.length > 0 ){//�鵽��ͬ��   ��ϵID �� �汾�� ��ʶΨһ
					exitLawList.add(fileName);
				}
			}catch(ArrayIndexOutOfBoundsException e){
			}
		}
		
		return exitLawList;
	}
}