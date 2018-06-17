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
	
	private File[] selectedFiles;//选中的文件数组
	
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
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"添加剂", "指标"}));
		comboBox.setBounds(134, 41, 81, 30);
		contentPane.add(comboBox);
		
		JLabel lblNewLabel = new JLabel("类型：");
		lblNewLabel.setBounds(70, 41, 54, 30);
		contentPane.add(lblNewLabel);
		
		JButton upLoadFile = new JButton("选择文件");
		upLoadFile.setBounds(276, 41, 156, 30);
		contentPane.add(upLoadFile);
		
		JButton okBtn = new JButton("确定");
		okBtn.setBounds(84, 152, 93, 30);
		contentPane.add(okBtn);
		
		JButton cancleBtn = new JButton("取消");
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
				
				if(!StringsUtil.isEmpty(type)){//file选择成功，type不为空
					
					
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							//进度条
							progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
							//检查文件的 体系ID 是否都已经存在了
							List<String> exitLawList = checkLawExitList(selectedFiles);
							
							if(exitLawList.size() > 0){//说明有重复的文件
								progressBarDialog.stop();
								
								StringBuilder stringBuilder = new StringBuilder();
								
								for(String fileName : exitLawList){
									stringBuilder.append("#"+fileName);
									//提示并且返回
									logger.fine("[Failure]请检查"+fileName+"excel在系统中已经存在,检查后重新导入");
								}
								MessageBox.post(stringBuilder.toString(),"已存在法规，请去C盘YLLog文件夹中查看日志",MessageBox.INFORMATION);
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
		        int result = jfc.showDialog(new JLabel(), "选择"); //0表示确认   1表示取消
		        if(result==1) return;
		        selectedFiles = jfc.getSelectedFiles();
		        
		        if(selectedFiles.length<1){  
		            MessageBox.post("请选择文件","",MessageBox.ERROR);
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
	 * 检查要导入的法规 已经存在的
	 * @param selectedFiles
	 * @return
	 */
	public List<String> checkLawExitList( File[] selectedFiles){
		
		List<String> exitLawList = new ArrayList<String>();
		for(File file : selectedFiles){
			//导入一个 要先分出来    体系ID  名称   版本   
			String fileName;
			String id;
			String revId;
			String revNum;
			String revName;
			try{
				fileName = file.getName().split("\\.")[0];//去除后缀后的名字
				id = fileName.split(" ")[0];//体系ID
				revId = fileName.split(" ")[1]+" "+fileName.split(" ")[2].split("-")[0];//法规的名称
				revNum = fileName.split(" ")[2].split("-")[1];//法规的版本号
				revName = fileName.split(" ")[3];//法规的名字 
				
				TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_LawRevsion.getValue());
				TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[]{"体系ID","版次号"}, new String[]{id, revNum});
				
				if(searchResult.length > 0 ){//查到相同的   体系ID 和 版本号 标识唯一
					exitLawList.add(fileName);
				}
			}catch(ArrayIndexOutOfBoundsException e){
			}
		}
		
		return exitLawList;
	}
}