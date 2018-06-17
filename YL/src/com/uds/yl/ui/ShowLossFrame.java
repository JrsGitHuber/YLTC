package com.uds.yl.ui;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.uds.yl.annotation.AnnotationFactory;
import com.uds.yl.bean.IndexItemBean;
import com.uds.yl.tcutils.BomUtil;

public class ShowLossFrame extends JFrame {

	private JPanel contentPane;
	private JTable lossTable;


	private DefaultTableModel lossTableModel;
	private TCComponentItemRevision lossItemRev;

	/**
	 * Create the frame.
	 */
	public ShowLossFrame(TCComponentItemRevision itemRev) {
		this.lossItemRev = itemRev;
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 414, 242);
		contentPane.add(scrollPane);
		
		lossTable = new JTable(){
			public boolean isCellEditable(int row, int column) {
				return false;//�����޸�
			};
		};
		lossTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"ָ������", "���ֵ"
			}
		));
		scrollPane.setViewportView(lossTable);
	
		
		lossTableModel = (DefaultTableModel) lossTable.getModel();
		
		
		
		
		TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(lossItemRev, "��ͼ");
		if(topBomLine==null){//���Ϊ�յĻ�����Ͳ��ù�
			return;
		}
		try {
			AIFComponentContext[] children = topBomLine.getChildren();
			for(AIFComponentContext context : children){
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				if(bomLine.getItem().getType().equals("U8_Material")){//�����ԭ�����͵Ĳ�����
					continue;
				}
				IndexItemBean indexItemBean = AnnotationFactory.getInstcnce(IndexItemBean.class, bomLine);
				lossTableModel.addRow(new String[]{indexItemBean.objectName,indexItemBean.u8Loss});
			}
		} catch (TCException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	
	
		
	}
}
