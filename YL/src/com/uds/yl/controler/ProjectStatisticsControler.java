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
		// �����itemRev�ǲ�ʹ�õ�
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

			JLabel lblNewLabel = new JLabel("��Ŀ���ͳ��");
			lblNewLabel.setFont(new Font("����", Font.BOLD, 15));
			lblNewLabel.setBounds(21, 22, 100, 30);
			contentPane.add(lblNewLabel);

			JPanel panel = new JPanel();
			panel.setBorder(new LineBorder(new Color(0, 0, 0)));
			panel.setBounds(31, 62, 748, 365);
			contentPane.add(panel);
			panel.setLayout(null);

			JLabel label = new JLabel("ͳ��ģʽ��");
			label.setFont(new Font("����", Font.PLAIN, 14));
			label.setBounds(47, 37, 70, 30);
			panel.add(label);

			JLabel label_1 = new JLabel("������Ҫͳ��ʱ���");
			label_1.setFont(new Font("����", Font.BOLD, 14));
			label_1.setBounds(47, 88, 186, 30);
			panel.add(label_1);

			JLabel label_2 = new JLabel("��ʼ���ڣ�");
			label_2.setFont(new Font("����", Font.PLAIN, 14));
			label_2.setBounds(47, 158, 80, 30);
			panel.add(label_2);

			final DateChooser dateChooserStart = new DateChooser(this);
			dateChooserStart.setBounds(140, 158, 200, 30);
			panel.add(dateChooserStart);

			JLabel label_3 = new JLabel("��ֹ���ڣ�");
			label_3.setFont(new Font("����", Font.PLAIN, 14));
			label_3.setBounds(47, 201, 80, 30);
			panel.add(label_3);

			final DateChooser dateChooserEnd = new DateChooser(this);
			dateChooserEnd.setBounds(140, 201, 200, 30);
			panel.add(dateChooserEnd);

			final JComboBox comboBox = new JComboBox();
			comboBox.setModel(new DefaultComboBoxModel(new String[] { "    ʱ����ڿ�ʼ", "    ʱ��������" }));
			comboBox.setBounds(155, 261, 146, 30);
			panel.add(comboBox);

			JLabel label_4 = new JLabel("��ʼ/����");
			label_4.setFont(new Font("����", Font.PLAIN, 14));
			label_4.setBounds(47, 261, 70, 30);
			panel.add(label_4);

			JButton okBtn = new JButton("ȷ��");
			okBtn.setFont(new Font("����", Font.PLAIN, 14));
			okBtn.setBounds(491, 311, 93, 30);
			panel.add(okBtn);

			JButton cancleBtn = new JButton("ȡ��");
			cancleBtn.setFont(new Font("����", Font.PLAIN, 14));
			cancleBtn.setBounds(614, 311, 93, 30);
			panel.add(cancleBtn);

			final JRadioButton projectRadioBtn = new JRadioButton("��Ŀִ�����");
			projectRadioBtn.setBounds(180, 37, 121, 30);
			panel.add(projectRadioBtn);
			projectRadioBtn.setSelected(true);

			JRadioButton workRadioBtn = new JRadioButton("����ִ�����");
			workRadioBtn.setBounds(343, 37, 121, 30);
			panel.add(workRadioBtn);

			ButtonGroup radioGroup = new ButtonGroup();
			radioGroup.add(projectRadioBtn);
			radioGroup.add(workRadioBtn);

			okBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// =============================��ʼ�����������Ҫ�����е��ֶ���Ϣ
					if (projectRadioBtn.isSelected()) {
						projectStatisticsMode = Const.ProjectStatistics.PROJECT_EXCUTE;
					} else {
						projectStatisticsMode = Const.ProjectStatistics.WORK_EXCUTE;
					}
					dateStart = dateChooserStart.getDateField().getText();
					dateEnd = dateChooserEnd.getDateField().getText();
					startOrCompleteStr = comboBox.getSelectedItem().toString().trim();

					// ʱ����ڿ�ʼ:ʵ�ʿ�ʼʱ�����뵽����Ŀ�ʼʱ����ڵ�
					// ʱ��������:ʵ�����ʱ�����뵽��������ʱ����ڵ�

					if (Const.ProjectStatistics.PROJECT_EXCUTE.equals(projectStatisticsMode)) {
						// ============��Ŀִ�����==��ʱ����ѯ
						List<TCComponentSchedule> querySchedules = iProjectStatisticsService
								.getQueryScheduleList(dateStart, dateEnd, startOrCompleteStr);// ��ȡ��ѯ����ʱ���List
						if (querySchedules == null || querySchedules.size() == 0) {
							MessageBox.post("û���ҵ�ʱ���", "", MessageBox.ERROR);
							return;
						}

						List<ProjectStatisticsBean> scheduleBeanList = iProjectStatisticsService
								.generateScheduleBeanList(querySchedules);// ��ȡʱ�������Է�װ��List�У���ʼ��ÿ��ʱ����е�ʱ����������

						iProjectStatisticsService.writeSchedule2Excel(scheduleBeanList);// ��ʱ�����ϤList�е�����д��Excel��
					}else if(Const.ProjectStatistics.WORK_EXCUTE.equals(projectStatisticsMode)){
						// ============����ִ�����==��ʱ�������Ĳ�ѯ
						List<TCComponentScheduleTask> scheduleTaskList = iProjectStatisticsService
								.getQueryScheduleTaskList(dateStart, dateEnd, startOrCompleteStr);// ��ȡ��ѯ����ʱ�������List
						if(scheduleTaskList==null || scheduleTaskList.size()==0){
							MessageBox.post("û���ҵ�ʱ�������", "", MessageBox.ERROR);
							return;
						}
						
						
						List<WorkStatisticsBean> scheduleBeanList = iProjectStatisticsService
								.generateScheduleTaskBeanList(scheduleTaskList);// ��װʱ�������Bean��List
						
						iProjectStatisticsService.writeScheduleTask2Excel(scheduleBeanList);// ������д��EXCEL��ȥ
						
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
