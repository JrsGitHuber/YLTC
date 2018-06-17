package com.uds.yl.controler;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;


import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.soa.exceptions.NotLoadedException;
import com.uds.yl.base.BaseControler;
import com.uds.yl.bean.DispatchLogBean;
import com.uds.yl.common.Const;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.service.IDispatchDocumentToProjectService;
import com.uds.yl.service.impl.DispatchDocumentToProjectImpl;
import com.uds.yl.tcutils.QueryUtil;

public class DispatchDocumentToProjectControler implements BaseControler {

	private IDispatchDocumentToProjectService iDispatchDocumentToProjectService = new DispatchDocumentToProjectImpl();// 服务实例
	private List<TCComponentProject> projectList;// 一个项目组中的所用的项目list
	private List<DispatchLogBean> logBeanList;// 指派和移除的记录
	private TCComponentTask task;

	private List<TCComponentForm> allFormList = new ArrayList<>();
	private int selectFormIndex = 0;

	@Override
	public void userTask(TCComponentItemRevision itemRev) {
	}

	public void douUserTask(TCComponentTask task) {
		this.task = task;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					SendDocFrame frame = new SendDocFrame();
					frame.setVisible(true);
					frame.setResizable(false);
					frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	class SendDocFrame extends JFrame {

		JPanel contentPane;
		JComboBox<String> comBox;
		JButton sendBtn;

		/**
		 * Create the frame.
		 */
		public SendDocFrame() {
			setTitle("发文系统");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setBounds(100, 100, 545, 222);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);

			JLabel lblNewLabel = new JLabel("选择分发组：");
			lblNewLabel.setFont(new Font("宋体", Font.BOLD, 17));
			lblNewLabel.setBounds(30, 66, 113, 34);
			contentPane.add(lblNewLabel);

			comBox = new JComboBox();
			comBox.setBounds(165, 66, 113, 29);
			contentPane.add(comBox);

			sendBtn = new JButton("发送");
			sendBtn.setBounds(353, 66, 93, 30);
			contentPane.add(sendBtn);

			List<String> allFormNameList = new ArrayList<>();// 存储查询到的所有Form的名字
			TCComponentQuery formQuery = QueryUtil.getTCComponentQuery(QueryClassConst.U8_FORM.getValue());
			TCComponent[] formQueryResult = QueryUtil.getSearchResult(formQuery,
					new String[] { Const.DispatchDocumentToProject.FORM_QUERY_NAME }, new String[] { "*" });

			allFormList.clear();
			for (TCComponent component : formQueryResult) {
				TCComponentForm form = (TCComponentForm) component;
				allFormList.add(form);
				try {
					String formName = form.getProperty("object_name");
					allFormNameList.add(formName);
				} catch (TCException e) {
					e.printStackTrace();
				}
			}

			for (String formName : allFormNameList) {// 为下拉框赋值
				comBox.addItem(formName);
			}

			sendBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// 根据界面选择相应的项目组（就是Form的名称）
					selectFormIndex = comBox.getSelectedIndex();
					TCComponentForm form = allFormList.get(selectFormIndex);
					// 获取Form下的的项目的名字
					List<String> projectIDList = new ArrayList<>();
					try {//projectIDList
						String formValue = form.getPropertyDisplayableValues(Const.DispatchDocumentToProject.FROM_PROTERTY_ID).get(0);
						String[] splitValue = formValue.split(",");
						for(String idValue : splitValue){
							projectIDList.add(idValue.trim());
						}
					} catch (NotLoadedException e) {
						e.printStackTrace();
					}
					// 根据项目的名字查找对应的项目，然后封装成项目对象的List集合
					projectList = iDispatchDocumentToProjectService.getProjectListByNames(projectIDList);
					// 根据任务获取数据集的List
					List<TCComponentDataset> dateSetList = iDispatchDocumentToProjectService.getDateSetList(task);
					// 将文件指派到项目List中去
					logBeanList = iDispatchDocumentToProjectService.assignToProject(dateSetList, projectList);

					// 将文件从项目中移除
					// logBeanList =
					// iDispatchDocumentToProjectService.removeFromProject(dateSetList,
					// projectList);

					// 经指派的记录记录在文档中
					iDispatchDocumentToProjectService.logInfo(logBeanList);
					MessageBox.post("","OK",MessageBox.INFORMATION);
					
				}
			});

		}
	}

}
