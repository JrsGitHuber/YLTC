package com.uds.yl.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.border.LineBorder;

import org.apache.axis.encoding.ser.DateSerializer;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.eventusermodel.examples.FromHowTo;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCException;
import com.uds.yl.common.Const;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.common.UserInfoSingleFactory;
import com.uds.yl.common.Const.QueryKey;
import com.uds.yl.tcutils.QueryUtil;
import com.uds.yl.utils.StringsUtil;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
//液奶绩效打分汇总
public class YNKPIStatisticsframe extends JFrame {

	private JPanel contentPane;
	private JTextField ownerEdt;//数据集所属的编辑框


	/**
	 * Create the frame.
	 */
	public YNKPIStatisticsframe() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 519, 486);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("液奶绩效统计");
		lblNewLabel.setFont(new Font("宋体", Font.BOLD, 15));
		lblNewLabel.setBounds(10, 22, 100, 30);
		contentPane.add(lblNewLabel);

		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBounds(10, 62, 483, 365);
		contentPane.add(panel);
		panel.setLayout(null);

		JLabel label_1 = new JLabel("输入需要统计时间段");
		label_1.setFont(new Font("宋体", Font.BOLD, 14));
		label_1.setBounds(34, 20, 186, 30);
		panel.add(label_1);

		JLabel label_2 = new JLabel("开始日期：");
		label_2.setFont(new Font("宋体", Font.PLAIN, 14));
		label_2.setBounds(76, 76, 80, 30);
		panel.add(label_2);

		final DateChooser dateChooserStart = new DateChooser(this);
		dateChooserStart.setBounds(166, 76, 200, 30);
		panel.add(dateChooserStart);

		JLabel label_3 = new JLabel("截止日期：");
		label_3.setFont(new Font("宋体", Font.PLAIN, 14));
		label_3.setBounds(76, 132, 80, 30);
		panel.add(label_3);

		final DateChooser dateChooserEnd = new DateChooser(this);
		dateChooserEnd.setBounds(166, 132, 200, 30);
		panel.add(dateChooserEnd);

		JButton okBtn = new JButton("确定");
		okBtn.setFont(new Font("宋体", Font.PLAIN, 14));
		okBtn.setBounds(77, 309, 93, 30);
		panel.add(okBtn);

		JButton cancleBtn = new JButton("取消");
		cancleBtn.setFont(new Font("宋体", Font.PLAIN, 14));
		cancleBtn.setBounds(273, 309, 93, 30);
		panel.add(cancleBtn);
		
		JLabel label = new JLabel("用户名称：");
		label.setFont(new Font("宋体", Font.PLAIN, 14));
		label.setBounds(76, 197, 80, 30);
		panel.add(label);
		
		ownerEdt = new JTextField();
		ownerEdt.setBounds(166, 202, 200, 25);
		panel.add(ownerEdt);
		ownerEdt.setColumns(10);

		ButtonGroup radioGroup = new ButtonGroup();

		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// =============================初始化面板上所需要的所有的字段信息
				String dateStart = dateChooserStart.getDateField().getText();
				String dateEnd = dateChooserEnd.getDateField().getText();
				String userName = ownerEdt.getText().toString();
				
				TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_YNKPI.getValue());
				TCComponent[] searchResult = null;
				List<KPIBean> kpiBeanList = null;
				if(StringsUtil.isEmpty(userName)){//这个值就按照默认来使用
					try {
						userName  = UserInfoSingleFactory.getInstance().getUser().getOSUserName().toString();
					} catch (TCException e1) {
						e1.printStackTrace();
					}
					searchResult = QueryUtil.getSearchResult(query, 
							new String[]{Const.QueryKey.DATE_START,Const.QueryKey.DATE_END,QueryKey.OWNER}, 
							new String[]{dateStart,dateEnd,userName});
					kpiBeanList = getKPIBean(searchResult, userName);
					
				}else{
					searchResult = QueryUtil.getSearchResult(query, 
							new String[]{Const.QueryKey.DATE_START,Const.QueryKey.DATE_END,Const.QueryKey.OWNER}, 
							new String[]{dateStart,dateEnd,userName});
					
					kpiBeanList = getKPIBean(searchResult, userName);
				}
				
				writeToExcel(kpiBeanList);
			}
		});
		
		
		cancleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		
	}
	
	
	/**
	 *  根据传递过来的数据集来计算总分
	 * @param searchResult
	 * @param userName
	 * @return
	 */
	public List<KPIBean> getKPIBean(TCComponent[] searchResult,String userName){
		List<KPIBean> kpiBeanList = new ArrayList<>();
		try {
			for(TCComponent component : searchResult){
				KPIBean bean = new KPIBean();
				TCComponentDataset dataset = (TCComponentDataset) component;
				bean.score = dataset.getProperty("u8_self");
				bean.dateSetName = dataset.getProperty("object_name");
				bean.userName = userName;
				bean.comments = "";
				kpiBeanList.add(bean);
				
				//每一次的打分信息
				TCComponent[] components = dataset.getRelatedComponents("IMAN_external_object_link");
				for(TCComponent formCompont : components){
					KPIBean formBean = new KPIBean();
					TCComponentForm form = (TCComponentForm) formCompont;
					
					formBean.score = form.getProperty("u8_score");
					formBean.dateSetName = dataset.getProperty("object_name");
					formBean.userName = userName;
					formBean.comments = form.getProperty("u8_comments");
					kpiBeanList.add(formBean);
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return kpiBeanList;
	} 
	
	
	public void writeToExcel(List<KPIBean> kpiBeanList){
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("液奶绩效统计");
		sheet.setDefaultColumnWidth((short) 300);
		XSSFCellStyle style = wb.createCellStyle();
		XSSFCellStyle style1 = wb.createCellStyle();
		Row row = sheet.createRow(0);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		Cell cell = row.createCell((short) 0);
		cell.setCellValue("所有者");
		cell.setCellStyle(style);

		cell = row.createCell((short) 1);
		cell.setCellValue("文档名称");
		cell.setCellStyle(style);

		cell = row.createCell((short) 2);
		cell.setCellValue("分数");
		cell.setCellStyle(style);
		
		cell = row.createCell((short) 3);
		cell.setCellValue("评语");
		cell.setCellStyle(style);
		

		int k = 0;
		for (int i = 0; i < kpiBeanList.size(); i++) {

			XSSFRow row1 = sheet.createRow(k + 1);
			k++;
			XSSFCell cell2 = row1.createCell(0);
			cell2.setCellValue(kpiBeanList.get(i).userName);
			cell2 = row1.createCell(1);
			cell2.setCellValue(kpiBeanList.get(i).dateSetName);
			cell2 = row1.createCell(2); 
			cell2.setCellValue(kpiBeanList.get(i).score);
			cell2 = row1.createCell(3); 
			cell2.setCellValue(kpiBeanList.get(i).comments);
		}

		try {
			FileOutputStream out = new FileOutputStream("C:\\液奶绩效打分汇总.xlsx");
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				Runtime.getRuntime().exec("cmd /c start " + "C:\\液奶绩效打分汇总.xlsx");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * 获取当前的分数
	 * @param dataSet 选中的数据集
	 * @param sumScore   自己打的分
	 * @return
	 */
	private String getCurrentSumScore(TCComponentDataset dataSet,String sumScore){
		Double currentSumScore = StringsUtil.convertStr2Double(sumScore);
		try {
			TCComponent[] components = dataSet.getRelatedComponents("IMAN_external_object_link");
			for(TCComponent component : components){
				TCComponentForm form = (TCComponentForm) component;
				String formScore = form.getProperty("u8_score");
				if('+'==formScore.charAt(0)){
					currentSumScore += StringsUtil.convertStr2Double(formScore.substring(1));
				}else if('-' == formScore.charAt(0)){
					currentSumScore -= StringsUtil.convertStr2Double(formScore.substring(1));
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return ""+currentSumScore;
	}
	
	
}




class KPIBean{
	String userName;
	String dateSetName;
	String score;
	String comments;
}
