package com.uds.yl.controler;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;

import javax.swing.border.LineBorder;

import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentSchedule;
import com.teamcenter.rac.kernel.TCComponentScheduleTask;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.base.BaseControler;
import com.uds.yl.bean.ProjectStatisticsBean;
import com.uds.yl.bean.WorkStatisticsBean;
import com.uds.yl.common.Const;
import com.uds.yl.service.IProjectStatisticsService;
import com.uds.yl.service.impl.ProjectStatisticsServiceImpl;
import com.uds.yl.ui.DateChooser;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;

public class ProjectStatisticsControler implements BaseControler {

	private String dateStart = "";
	private String dateEnd = "";
	private String projectStatisticsMode = "";
	private String startOrCompleteStr = "";
	private IProjectStatisticsService iProjectStatisticsService = new ProjectStatisticsServiceImpl();

	@Override
	public void userTask(TCComponentItemRevision itemRev) {
		// 这里的itemRev是不使用的
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					ProjectStatisticsJframe frame = new ProjectStatisticsJframe();
					frame.setVisible(true);
					frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					frame.setResizable(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public class ProjectStatisticsJframe extends JFrame {
		private JPanel contentPane;

		/**
		 * Create the frame.
		 */
		public ProjectStatisticsJframe() {
			setBounds(100, 100, 819, 486);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);

			JLabel lblNewLabel = new JLabel("项目情况统计");
			lblNewLabel.setFont(new Font("宋体", Font.BOLD, 15));
			lblNewLabel.setBounds(21, 22, 100, 30);
			contentPane.add(lblNewLabel);

			JPanel panel = new JPanel();
			panel.setBorder(new LineBorder(new Color(0, 0, 0)));
			panel.setBounds(31, 62, 748, 365);
			contentPane.add(panel);
			panel.setLayout(null);

			JLabel label = new JLabel("统计模式：");
			label.setFont(new Font("宋体", Font.PLAIN, 14));
			label.setBounds(47, 37, 70, 30);
			panel.add(label);

			JLabel label_1 = new JLabel("输入需要统计时间段");
			label_1.setFont(new Font("宋体", Font.BOLD, 14));
			label_1.setBounds(47, 88, 186, 30);
			panel.add(label_1);

			JLabel label_2 = new JLabel("开始日期：");
			label_2.setFont(new Font("宋体", Font.PLAIN, 14));
			label_2.setBounds(47, 158, 80, 30);
			panel.add(label_2);

			final DateChooser dateChooserStart = new DateChooser(this);
			dateChooserStart.setBounds(140, 158, 200, 30);
			panel.add(dateChooserStart);

			JLabel label_3 = new JLabel("截止日期：");
			label_3.setFont(new Font("宋体", Font.PLAIN, 14));
			label_3.setBounds(47, 201, 80, 30);
			panel.add(label_3);

			final DateChooser dateChooserEnd = new DateChooser(this);
			dateChooserEnd.setBounds(140, 201, 200, 30);
			panel.add(dateChooserEnd);

			final JComboBox comboBox = new JComboBox();
			comboBox.setModel(new DefaultComboBoxModel(new String[] { "    时间段内开始", "    时间段内完成" }));
			comboBox.setBounds(155, 261, 146, 30);
			panel.add(comboBox);

			JLabel label_4 = new JLabel("开始/结束");
			label_4.setFont(new Font("宋体", Font.PLAIN, 14));
			label_4.setBounds(47, 261, 70, 30);
			panel.add(label_4);

			JButton okBtn = new JButton("确定");
			okBtn.setFont(new Font("宋体", Font.PLAIN, 14));
			okBtn.setBounds(491, 311, 93, 30);
			panel.add(okBtn);

			JButton cancleBtn = new JButton("取消");
			cancleBtn.setFont(new Font("宋体", Font.PLAIN, 14));
			cancleBtn.setBounds(614, 311, 93, 30);
			panel.add(cancleBtn);

			final JRadioButton projectRadioBtn = new JRadioButton("项目执行情况");
			projectRadioBtn.setBounds(180, 37, 121, 30);
			panel.add(projectRadioBtn);
			projectRadioBtn.setSelected(true);

			JRadioButton workRadioBtn = new JRadioButton("工作执行情况");
			workRadioBtn.setBounds(343, 37, 121, 30);
			panel.add(workRadioBtn);

			ButtonGroup radioGroup = new ButtonGroup();
			radioGroup.add(projectRadioBtn);
			radioGroup.add(workRadioBtn);

			okBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// =============================初始化面板上所需要的所有的字段信息
					if (projectRadioBtn.isSelected()) {
						projectStatisticsMode = Const.ProjectStatistics.PROJECT_EXCUTE;
					} else {
						projectStatisticsMode = Const.ProjectStatistics.WORK_EXCUTE;
					}
					dateStart = dateChooserStart.getDateField().getText();
					dateEnd = dateChooserEnd.getDateField().getText();
					startOrCompleteStr = comboBox.getSelectedItem().toString().trim();

					// 时间段内开始:实际开始时间落入到输入的开始时间段内的
					// 时间段内完成:实际完成时间落入到输入的完成时间段内的

					if (Const.ProjectStatistics.PROJECT_EXCUTE.equals(projectStatisticsMode)) {
						// ============项目执行情况==对时间表查询
						List<TCComponentSchedule> querySchedules = iProjectStatisticsService
								.getQueryScheduleList(dateStart, dateEnd, startOrCompleteStr);// 获取查询到的时间表List
						if (querySchedules == null || querySchedules.size() == 0) {
							MessageBox.post("没有找到时间表", "", MessageBox.ERROR);
							return;
						}

						List<ProjectStatisticsBean> scheduleBeanList = iProjectStatisticsService
								.generateScheduleBeanList(querySchedules);// 获取时间表的属性封装到List中，初始化每个时间表中的时间表任务对象

						iProjectStatisticsService.writeSchedule2Excel(scheduleBeanList);// 将时间表熟悉List中的内容写到Excel中
					}else if(Const.ProjectStatistics.WORK_EXCUTE.equals(projectStatisticsMode)){
						// ============工作执行情况==对时间表任务的查询
						List<TCComponentScheduleTask> scheduleTaskList = iProjectStatisticsService
								.getQueryScheduleTaskList(dateStart, dateEnd, startOrCompleteStr);// 获取查询到的时间表任务List
						if(scheduleTaskList==null || scheduleTaskList.size()==0){
							MessageBox.post("没有找到时间表任务", "", MessageBox.ERROR);
							return;
						}
						
						
						List<WorkStatisticsBean> scheduleBeanList = iProjectStatisticsService
								.generateScheduleTaskBeanList(scheduleTaskList);// 封装时间表任务Bean的List
						
						iProjectStatisticsService.writeScheduleTask2Excel(scheduleBeanList);// 将数据写到EXCEL中去
						
					}

				}
			});
			
			
			cancleBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});

		}
	}

}
