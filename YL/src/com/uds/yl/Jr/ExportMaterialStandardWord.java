package com.uds.yl.Jr;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.aspose.words.BreakType;
import com.aspose.words.Cell;
import com.aspose.words.CellMerge;
import com.aspose.words.CellVerticalAlignment;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.Font;
import com.aspose.words.HorizontalAlignment;
import com.aspose.words.ImportFormatMode;
import com.aspose.words.Orientation;
import com.aspose.words.PaperSize;
import com.aspose.words.Paragraph;
import com.aspose.words.ParagraphAlignment;
import com.aspose.words.Row;
import com.aspose.words.Run;
import com.aspose.words.SectionStart;
import com.aspose.words.Shape;
import com.aspose.words.ShapeType;
import com.aspose.words.Table;
import com.aspose.words.TableAlignment;
import com.aspose.words.TextBox;
import com.aspose.words.WrapType;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.common.Const;
import com.uds.yl.tcutils.BomUtil;
import com.uds.yl.tcutils.DataSetUtil;

public class ExportMaterialStandardWord {
	TCComponentItemRevision mItemRev;
	
	// ����Ŀ¼ �ϴ����ݼ�ʱ·��ֻ����\\����ʹ//������ᵼ�����ݼ��ڵ��ļ����Ƴ���·��������
	String path = "c:\\temp\\ExportWord\\";
	// ��������
	String wordName = "";
	String wordName1 = "";
	// ���� ԭ�ϼ�����׼/���� ���ݼ�������
	String datasetName = "";
	String datasetName1 = "";
	// U8_AppendixRel��ϵ���Ƿ�����Ҫ�ϲ����ĵ�
	String wordNeedToMerge = "";
	// ��ҵ������
	String groupName = "";
	
	/**
	 * �ɰ汾������Ҫ��ȡ�ĸ�������</br>
	 * ע�⣺�������Ե����򲻿ɱ䣬�����������ֻ���������ӣ�
	 */
	String[] propertiesName = new String[] { "is_modifiable", "object_name",
			"item_id", "u8_releasedate", "u8_implementationdate", "u8_reface1",
			"u8_range", "u8_definitions", "u8_materialrequirement",
			"u8_productionprocess", "u8_ingredientsrequirement",
			"u8_foodadditives", "u8_nutritionenhancer",
			"u8_ygtag", "u8_package", "u8_nutritionlabelsrelated",
			"u8_shelf_life", "u8_transport_conditions",
			"u8_storage_conditions", "u8_supply_conditions",
			"u8_others", "owning_group", "u8_stdcondition" };
	/**
	 * ��BOMLine������Ҫ��ȡ�İ汾�ϵĸ�������</br>
	 * ע�⣺�������Ե����򲻿ɱ䣬�����������ֻ���������ӣ�
	 */
	String[] propertiesName1 = new String[] { "u8_category", "object_name", "u8_uom" };
	/**
	 * ��BOMLine������Ҫ��ȡ��BOMLine�ϵĸ�������</br>
	 * ע�⣺�������Ե����򲻿ɱ䣬�����������ֻ���������ӣ�
	 */
	String[] propertiesName2 = new String[] { "U8_DOWNLINE", "U8_UPLINE",
			"U8_testcriterion", "u8_standardunit", "U8_UP_OPERATION", "U8_DOWN_OPERATION",
			"U8_detectvalue", "U8_indexdesc", "U8_STAND_DOWNLINE", "U8_STAND_UPLINE",
			"U8_STDDOWN_OPERATION", "U8_STANDUP_OPERATION", "U8_testgist",
			"U8_EARLYWARN_UPLINE", "U8_EARLYWARNUP_OPT", "U8_EARLYWARN_DOWNLINE", "U8_EARLYWARN_DOWNOPT",
			"U8_EARLYWARNDESC", "U8_remark" };
	
	RevisionProertiesBean revisionProertiesBean = null;
	TablesBean tablesBean = null;
	// ��ϵ���Ƿ�����Ҫ�����ͼƬ
	ArrayList<String> fileList = null;
	ArrayList<String> fileList1 = null;
	
	TablesBean tablesBean1 = null;
	// ���ɱ�׼����˵�������б�����õ�������
	ArrayList<TableBean1> TablesBean1List;
	
	public ExportMaterialStandardWord(TCComponentItemRevision mItemRev, String sessionName, String dataSetName) throws Exception {
		this.mItemRev = mItemRev;
		this.datasetName = dataSetName;
		this.datasetName1 = "��׼����˵��";
		this.wordName = dataSetName + ".docx";
		this.wordName1 = "��׼����˵��.docx";
		
		path = path + sessionName + new SimpleDateFormat("_yyyyMMdd_HHmmss").format(new Date()) + "\\";
		
		fileList = new ArrayList<String>();
		fileList1 = new ArrayList<String>();
		
		TablesBean1List = new ArrayList<TableBean1>();
	}
	
	public void initGetDataAndExportWord() throws Exception {
		tablesBean = new TablesBean();
		
		revisionProertiesBean = new RevisionProertiesBean(mItemRev.getProperties(propertiesName));
		// ͳһ��ȡ������Ҫ������
		GetAllProperties();
		
		// �����û��дȨ��
		if(revisionProertiesBean.is_modifiable.equals("��")){
			throw new Exception("��ʾ\n\n��ѡ�еİ汾û��Ȩ�޲���");
		}
		
		// ����Ƿ���Ҫ�������У�ͨ���жϰ汾���Ƿ��Ѿ����˶�Ӧ�ı�
		CheckIfContinue();
		
		// ��������Ŀ¼
		File file = new File(path);
		if (file.exists()) {
			throw new Exception("����Ŀ¼�쳣����");
		} else {
			if (!file.mkdirs()) {
				throw new Exception("��������Ŀ¼ʧ��");
			}
		}
		
		// ���ع�ϵ�µ�ͼƬ  ע�⣺��Ҫ�Ȱѻ���Ŀ¼path��ʼ���˲���ȥ����ͼƬ��
		DownloadImage("U8_StructuredRel"); // ����Word���� ԭ��Ҫ��
		DownloadImage("U8_ProcessMapRel"); // ����Word���� ��������
		
		// ���ع�ϵ�µ��ĵ�
		DownloadDoc("U8_AppendixRel");
		
		// ������֯��TablesBean���ڱ�׼����˵������
        tablesBean1 = (TablesBean)tablesBean.clone();				
		// ��ȡU8_LawRel��ϵ�µİ汾�еĸ�����Ϣ
		GetTablesBean1List("U8_LawRel");
	}
	
	public void AllWordOperation() throws Exception {
		if (datasetName.equals("ԭ�ϼ�����׼")) {
			// ����ԭ�ϼ�����׼����
			AboutMaterialTechnicalStandard();
			// ������׼����˵��
			AboutStandardSpecification();
		} else {
			MessageBox.post("����ĵ���", "��ʾ", MessageBox.INFORMATION);
			return;
		}
		
		MessageBox.post("�������ɳɹ�", "��ʾ", MessageBox.INFORMATION);
	}
	
	private void AboutMaterialTechnicalStandard() throws Exception {

		// ��ȡ���ݲ���������
		Document doc = new Document();
        DocumentBuilder builder = new DocumentBuilder(doc);
        builder.writeln();
        builder.writeln();
        builder.writeln();
        builder.writeln();
        builder.writeln();
        
        // ����ͼƬ
        InputStream inputStream = getModelExcell();
        if(inputStream == null){
        	throw new Exception("Can Not Get Picture From Java Project");
        }
        builder.insertImage(inputStream, 0, 350, 0, 10, 0, 0, 3);
        
        // ��������
        builder.getParagraphFormat().setAlignment(ParagraphAlignment.DISTRIBUTED );
        builder.getFont().setSize(24);
        builder.getFont().setName("����");
        builder.writeln("���ɹ�����ʵҵ����ԭ�ϱ�׼");
        builder.writeln();
        builder.getParagraphFormat().setAlignment(ParagraphAlignment.RIGHT);
        builder.getFont().setSize(15);
        builder.getFont().setBold(true);
//        String groupName = UserInfoSingleFactory.getInstance().getTCSession().getGroup().toString();
        groupName = GetGroupName();
        builder.writeln(groupName);
        
        // ��������
        Shape shape = new Shape(doc, ShapeType.LINE);
		shape.setWrapType(WrapType.NONE);
		shape.setBehindText(false);
		shape.setHorizontalAlignment(HorizontalAlignment.CENTER);
		// shape.setVerticalAlignment(VerticalAlignment.CENTER);
		// //shape.setHeight(50);
		// shape.setWidth(300);
		shape.setBounds(new Rectangle2D.Float(0, 170, 460, 0));
        doc.getFirstSection().getBody().getFirstParagraph().appendChild(shape);
        
        // ��������
        builder.writeln();
        builder.writeln();
        builder.writeln();
        builder.writeln();
        builder.writeln();
        builder.writeln();
        builder.writeln();
        builder.getFont().setSize(26); // 4�����ز����һ�ţ�
        builder.getFont().setBold(false);
        builder.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
        builder.writeln(revisionProertiesBean.object_name + " ԭ�ϱ�׼");
        
        builder.getFont().setSize(15);
        builder.writeln();
        builder.writeln();
        builder.writeln("�ļ���ţ�" + revisionProertiesBean.item_id);
        builder.writeln();
        builder.writeln();
        builder.writeln();
        builder.writeln();
        
        // �����������޸�Ϊ��ʼ����
        Shape shapeForTable = new Shape(doc, ShapeType.TEXT_BOX);
        shapeForTable.setBounds(new Rectangle2D.Float(-9, 486, 450, 80));
        shapeForTable.setWrapType(WrapType.NONE);
        shapeForTable.setBehindText(false);  //���������Ϸ�
        shapeForTable.setStroked(false);  // ����û�б߿�
        shapeForTable.setFilled(false);  //  �����ڲ����Ϊ͸��
        
        Table approvalTable = CreateTable(doc);
        shapeForTable.appendChild(approvalTable);
        approvalTable.setAlignment(TableAlignment.CENTER);
        approvalTable.setAllowAutoFit(false);
        doc.getFirstSection().getBody().getFirstParagraph().appendChild(shapeForTable);
        
        // ԭ����builderֱ�Ӳ�����ķ�ʽ
//        InsertApprovalTable(builder);
        
        builder.insertBreak(BreakType.PAGE_BREAK);
        
        InsertShapeToTable(doc);
        InsertShapeForDate(doc);
        
        
        builder.getFont().setSize(13);
        Shape shape2 = new Shape(doc, ShapeType.TEXT_BOX);
        shape2.setWrapType(WrapType.NONE);
        shape2.setBehindText(true);
        shape2.setHorizontalAlignment(HorizontalAlignment.CENTER);
        shape2.setBounds(new Rectangle2D.Float(100, 650, 460, 100));
        shape2.setStrokeColor(Color.white);
        shape2.setFilled(false);
        shape2.appendChild(new Paragraph(doc));
        Paragraph para = shape2.getFirstParagraph();
        para.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);

        Run run = new Run(doc);
        Font font = run.getFont();
        font.setSize(14);
        font.setName("����");
        
        // �������岻ͬ�����⻻������Ļ���Ҫ�����ո�ĸ���
        run.setText("���ɹ�����ʵҵ���Źɷ����޹�˾  ����");
        para.appendChild(run);
        TextBox textBox = shape2.getTextBox();
        textBox.setFitShapeToText(true);
        doc.getFirstSection().getBody().getFirstParagraph().appendChild(shape2);
        
        // ��������
        Shape shape1 = new Shape(doc, ShapeType.LINE);
        shape1.setWrapType(WrapType.NONE);
        shape1.setBehindText(false);
        shape1.setHorizontalAlignment(HorizontalAlignment.CENTER);
        shape1.setBounds(new Rectangle2D.Float(0, 610, 460, 0));
        doc.getFirstSection().getBody().getFirstParagraph().appendChild(shape1);
        
        // ��������
        builder.getFont().setSize(16);
        builder.writeln("ǰ  ��");
        builder.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
        builder.writeln();
        builder.writeln();
        
        builder.getFont().setName("����");
        builder.getFont().setSize(12);
        builder.getParagraphFormat().setAlignment(ParagraphAlignment.LEFT);
        builder.writeln();
        builder.writeln(revisionProertiesBean.u8_reface1);
        builder.insertBreak(BreakType.PAGE_BREAK);
        
        // ��������
        builder.getFont().setName("����");
        builder.getFont().setSize(16);
        builder.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
        builder.writeln(revisionProertiesBean.object_name + " ԭ�ϱ�׼");
        builder.writeln();
        builder.writeln();
        
        // ��ʼ 1 - 21
        // ���� 1-4
        builder.getParagraphFormat().setAlignment(ParagraphAlignment.LEFT);
        builder.getFont().setSize(12);
        int index = 1;
        if (!revisionProertiesBean.u8_range.equals("")) {
        	builder.writeln(index + "  ��Χ");
        	builder.writeln();
        	builder.getFont().setName("����");
        	builder.writeln(revisionProertiesBean.u8_range);
        	builder.writeln();
        	index++;
        }
        if (!revisionProertiesBean.u8_definitions.equals("")) {
        	builder.getFont().setName("����");
        	builder.writeln(index + "  ���������");
        	builder.writeln();
        	builder.getFont().setName("����");
        	builder.writeln(revisionProertiesBean.u8_definitions);
        	builder.writeln();
        	index++;
        }
        if (!revisionProertiesBean.u8_materialrequirement.equals("") || fileList.size() != 0) {
        	builder.getFont().setName("����");
        	builder.writeln(index + "  ԭ��Ҫ��");
        	builder.writeln();
        	if (!revisionProertiesBean.u8_materialrequirement.equals("")) {
        		builder.getFont().setName("����");
	        	builder.writeln(revisionProertiesBean.u8_materialrequirement);
	        	builder.writeln();
        	}
        	if (fileList.size() != 0) {
        		for (String imagePath : fileList) {
        			Shape imageShape = builder.insertImage(imagePath);
        			SetImageMaxSize(imageShape);
        			imageShape.setWrapType(WrapType.INLINE);
        			builder.writeln();
        			builder.writeln();
        		}
        	}
        	
        	index++;
        }
        if (!revisionProertiesBean.u8_productionprocess.equals("") || fileList1.size() != 0) {
        	builder.getFont().setName("����");
        	builder.writeln(index + "  ��������");
        	builder.writeln();
        	if (!revisionProertiesBean.u8_productionprocess.equals("")) {
        		builder.getFont().setName("����");
	        	builder.writeln(revisionProertiesBean.u8_productionprocess);
	        	builder.writeln();
        	}
        	if (fileList1.size() != 0) {
        		for (String imagePath : fileList1) {
        			Shape imageShape = builder.insertImage(imagePath);
        			SetImageMaxSize(imageShape);
        			imageShape.setWrapType(WrapType.INLINE);
        			builder.writeln();
        			builder.writeln();
        		}
        	}
        	
        	index++;
        }
        if (!revisionProertiesBean.u8_ingredientsrequirement.equals("")) {
        	builder.getFont().setName("����");
        	builder.writeln(index + "  ����Ҫ��");
        	builder.writeln();
        	builder.getFont().setName("����");
        	builder.writeln(revisionProertiesBean.u8_ingredientsrequirement);
        	builder.writeln();
        	index++;
        }
        
        // ��� 5-11
        int tableIndex = 1;
        if (tablesBean.list != null) {
        	builder.getFont().setName("����");
        	builder.writeln(index + "  �й�Ҫ��");
        	builder.writeln();
        	builder.getFont().setName("����");
        	builder.writeln("   Ӧ���ϱ�" + tableIndex + "��Ҫ��");
        	builder.getFont().setName("����");
        	builder.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
        	builder.writeln("��" + tableIndex + "�й�Ҫ��");
        	builder.getFont().setName("����");
        	AddNewTableToDoc(tablesBean.list, builder);
        	builder.writeln();
        	builder.getParagraphFormat().setAlignment(ParagraphAlignment.LEFT);
        	
        	tableIndex++;
        	index++;
        }
        if (tablesBean.list1 != null) {
        	builder.getFont().setName("����");
        	builder.writeln(index + "  ��ָ��");
        	builder.writeln();
        	builder.getFont().setName("����");
        	builder.writeln("   Ӧ���ϱ�" + tableIndex + "��Ҫ��");
        	builder.getFont().setName("����");
        	builder.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
        	builder.writeln("��" + tableIndex + "��ָ��");
        	builder.getFont().setName("����");
        	AddNewTableToDoc(tablesBean.list1, builder);
        	builder.writeln();
        	builder.getParagraphFormat().setAlignment(ParagraphAlignment.LEFT);
        	
        	tableIndex++;
        	index++;
        }
        if (tablesBean.list2 != null) {
        	builder.getFont().setName("����");
        	builder.writeln(index + "  ��Ⱦ������ָ��");
        	builder.writeln();
        	builder.getFont().setName("����");
        	builder.writeln("   Ӧ���ϱ�" + tableIndex + "��Ҫ��");
        	builder.getFont().setName("����");
        	builder.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
        	builder.writeln("��" + tableIndex + "��Ⱦ������");
        	builder.getFont().setName("����");
        	AddNewTableToDoc(tablesBean.list2, builder);
        	builder.writeln();
        	builder.getParagraphFormat().setAlignment(ParagraphAlignment.LEFT);
        	
        	tableIndex++;
        	index++;
        }
        if (tablesBean.list3 != null) {
        	builder.getFont().setName("����");
        	builder.writeln(index + "  �����������ָ��");
        	builder.writeln();
        	builder.getFont().setName("����");
        	builder.writeln("   Ӧ���ϱ�" + tableIndex + "��Ҫ��");
        	builder.getFont().setName("����");
        	builder.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
        	builder.writeln("��" + tableIndex + "�����������");
        	builder.getFont().setName("����");
        	AddNewTableToDoc(tablesBean.list3, builder);
        	builder.writeln();
        	builder.getParagraphFormat().setAlignment(ParagraphAlignment.LEFT);
        	
        	tableIndex++;
        	index++;
        }
        if (tablesBean.list4 != null) {
        	builder.getFont().setName("����");
        	builder.writeln(index + "  ΢��������ָ��΢����ָ��");
        	builder.writeln();
        	builder.getFont().setName("����");
        	builder.writeln("   Ӧ���ϱ�" + tableIndex + "��Ҫ��");
        	builder.getFont().setName("����");
        	builder.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
        	builder.writeln("��" + tableIndex + "΢��������");
        	builder.getFont().setName("����");
        	AddNewTableToDoc(tablesBean.list4, builder);
        	builder.writeln();
        	builder.getParagraphFormat().setAlignment(ParagraphAlignment.LEFT);
        	
        	tableIndex++;
        	index++;
        }
        if (tablesBean.list5 != null) {
        	builder.getFont().setName("����");
        	builder.writeln(index + "  ����ָ��");
        	builder.writeln();
        	builder.getFont().setName("����");
        	builder.writeln("   Ӧ���ϱ�" + tableIndex + "��Ҫ��");
        	builder.getFont().setName("����");
        	builder.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
        	builder.writeln("��" + tableIndex + "����ָ��");
        	builder.getFont().setName("����");
        	AddNewTableToDoc(tablesBean.list5, builder);
        	builder.writeln();
        	builder.getParagraphFormat().setAlignment(ParagraphAlignment.LEFT);
        	
        	tableIndex++;
        	index++;
        }
        
        // ���� 12-21
        if (!revisionProertiesBean.u8_foodadditives.equals("")) {
        	builder.getFont().setName("����");
        	builder.writeln(index + "  ʳƷ��Ӽ�");
        	builder.writeln();
        	builder.getFont().setName("����");
        	builder.writeln(revisionProertiesBean.u8_foodadditives);
        	builder.writeln();
        	index++;
        }
        if (!revisionProertiesBean.u8_nutritionenhancer.equals("")) {
        	builder.getFont().setName("����");
        	builder.writeln(index + "  Ӫ��ǿ����");
        	builder.writeln();
        	builder.getFont().setName("����");
        	builder.writeln(revisionProertiesBean.u8_nutritionenhancer);
        	builder.writeln();
        	index++;
        }
        if (!revisionProertiesBean.u8_ygtag.equals("")) {
        	builder.getFont().setName("����");
        	builder.writeln(index + "  ��־");
        	builder.writeln();
        	builder.getFont().setName("����");
        	builder.writeln(revisionProertiesBean.u8_ygtag);
        	builder.writeln();
        	index++;
        }
        if (!revisionProertiesBean.u8_package.equals("")) {
        	builder.getFont().setName("����");
        	builder.writeln(index + "  ��װ");
        	builder.writeln();
        	builder.getFont().setName("����");
        	builder.writeln(revisionProertiesBean.u8_package);
        	builder.writeln();
        	index++;
        }
        if (!revisionProertiesBean.u8_nutritionlabelsrelated.equals("")) {
        	builder.getFont().setName("����");
        	builder.writeln(index + "  Ӫ����ǩ�������");
        	builder.writeln();
        	builder.getFont().setName("����");
        	builder.writeln(revisionProertiesBean.u8_nutritionlabelsrelated);
        	builder.writeln();
        	index++;
        }
        if (!revisionProertiesBean.u8_shelf_life.equals("")) {
        	builder.getFont().setName("����");
        	builder.writeln(index + "  ������");
        	builder.writeln();
        	builder.getFont().setName("����");
        	builder.writeln(revisionProertiesBean.u8_shelf_life);
        	builder.writeln();
        	index++;
        }
        if (!revisionProertiesBean.u8_transport_conditions.equals("")) {
        	builder.getFont().setName("����");
        	builder.writeln(index + "  ������");
        	builder.writeln();
        	builder.getFont().setName("����");
        	builder.writeln(revisionProertiesBean.u8_transport_conditions);
        	builder.writeln();
        	index++;
        }
        if (!revisionProertiesBean.u8_storage_conditions.equals("")) {
        	builder.getFont().setName("����");
        	builder.writeln(index + "  ��������");
        	builder.writeln();
        	builder.getFont().setName("����");
        	builder.writeln(revisionProertiesBean.u8_storage_conditions);
        	builder.writeln();
        	index++;
        }
        if (!revisionProertiesBean.u8_supply_conditions.equals("")) {
        	builder.getFont().setName("����");
        	builder.writeln(index + "  ����Ҫ��");
        	builder.writeln();
        	builder.getFont().setName("����");
        	builder.writeln(revisionProertiesBean.u8_supply_conditions);
        	builder.writeln();
        	index++;
        }
        if (!revisionProertiesBean.u8_others.equals("")) {
        	builder.getFont().setName("����");
        	builder.writeln(index + "  ����");
        	builder.writeln();
        	builder.getFont().setName("����");
        	builder.writeln(revisionProertiesBean.u8_others);
        	builder.writeln();
        	index++;
        }
        
        builder.writeln();
        builder.writeln();
        builder.writeln(" ������ĿӦ���Ϲ��ұ�׼�ͷ����Ҫ��");
        builder.writeln("   ע�����Ǳ���׼�в�ע���ڵĹ淶�������ļ�������ǰ�ԡ����ϡ����鷽������־�������õĹ��ұ�׼��"
        		+ "��ҵ��׼�����ʱ�׼��������ط��ɷ��棩�������°汾���������е��޸ĵ��������ڱ���׼��");
//        builder.insertBreak(BreakType.PAGE_BREAK);
        
        
        // �ϲ����ݼ�
        // �ڻ�ȡ��ʱ���������ֻ��ȡһ���ĵ�����������ϲ����ݼ�ʱ����Ҫforѭ��
        if (!wordNeedToMerge.equals("")) {
        	builder.writeln();
            builder.writeln();
            builder.writeln();
            
        	int paperSize = doc.getFirstSection().getPageSetup().getPaperSize();
        	Document srcDoc = new Document(wordNeedToMerge);
        	srcDoc.getFirstSection().getPageSetup().setSectionStart(SectionStart.CONTINUOUS);
        	srcDoc.getFirstSection().getPageSetup().setPaperSize(paperSize);
        	doc.appendDocument(srcDoc, ImportFormatMode.KEEP_SOURCE_FORMATTING);
        }
        
        
        // �ϲ����ݼ�new
//        if (!wordNeedToMerge.equals("")) {
//        	Document srcDoc = new Document(wordNeedToMerge);
//        	
//        	for (Node srcNode : srcDoc)
//	        {
//	            Section srcSection = (Section)srcNode;
//	            srcSection.getPageSetup().setSectionStart(SectionStart.CONTINUOUS);
//
//	            // Because we are copying a section from one document to another,
//	            // it is required to import the Section node into the destination document.
//	            // This adjusts any document-specific references to styles, lists, etc.
//	            //
//	            // Importing a node creates a copy of the original node, but the copy
//	            // is ready to be inserted into the destination document.
//	            Node dstSection = doc.importNode(srcSection, true, ImportFormatMode.KEEP_SOURCE_FORMATTING);
//
//	            // Now the new section node can be appended to the destination document.
//	            doc.appendChild(dstSection);
//	        }
//        }
        
        // �����ɺõ�Word����
        String outFilePath = path + StringUtils.GetNameByString(revisionProertiesBean.item_id) + wordName;
        File outFile = new File(outFilePath);
		if (outFile.exists()) {
			outFile.delete();
		}			
        doc.save(outFilePath);
		
		// �ϴ����ݼ�
		OrganiseUpload(outFilePath, datasetName);	
	}
	
	private void AboutStandardSpecification() throws Exception {
		
		Document doc = new Document();
		// ����ҳ��Ϊ����A4
		doc.getFirstSection().getPageSetup().setPaperSize(PaperSize.A4);
		doc.getFirstSection().getPageSetup().setOrientation(Orientation .LANDSCAPE);
		
        DocumentBuilder builder = new DocumentBuilder(doc);
        
        builder.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
        builder.getFont().setSize(16); // 4�����ز����һ�ţ�
        builder.getFont().setBold(false);
        builder.getFont().setName("����");
        builder.writeln("��" + revisionProertiesBean.object_name + "������˵��");
        builder.writeln();
        
        builder.getParagraphFormat().setAlignment(ParagraphAlignment.LEFT);
        builder.getFont().setSize(12);
        if (!revisionProertiesBean.u8_stdcondition.equals("")) {
	        builder.getFont().setBold(true);
        	builder.writeln("һ����׼�ƶ����޶����Ļ������");
        	builder.writeln();
        	builder.getFont().setBold(false);
        	builder.getFont().setName("����");
        	builder.writeln(revisionProertiesBean.u8_stdcondition);
        	builder.writeln();
        }
        
    	// ����׼�ͱ�׼��ϵ�µķ��涼û��ͼ��ʱ�򣬸ñ��ǲ����ֵ�
    	if (TablesBean1List.size() != 0) {
    		builder.getFont().setBold(true);
        	builder.getFont().setName("����");
        	if (revisionProertiesBean.u8_stdcondition.equals("")) {
        		builder.writeln("һ����Ҫ����Ҫ����ƶ����޶�������");
        	} else {
        		builder.writeln("������Ҫ����Ҫ����ƶ����޶�������");
        	}
        	builder.writeln();

            // ��ʼ��֯���
            Table approvalTable = builder.startTable();   
            // ��һ��
            builder.insertCell();
            approvalTable.setLeftIndent(0);
            builder.getRowFormat().setHeight(20);
            builder.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
            builder.getCellFormat().setVerticalAlignment(CellVerticalAlignment.CENTER);
            builder.getCellFormat().getShading().setBackgroundPatternColor(Color.LIGHT_GRAY);
            builder.getFont().setSize(10.5);
            builder.getFont().setName("����");
            
            builder.getCellFormat().setWidth(100.0);
            builder.write("��Ŀ");
            builder.getCellFormat().setVerticalMerge(CellMerge.FIRST);
            builder.insertCell();
            builder.getCellFormat().setWidth(460.0);
            builder.write("ָ������");
            builder.insertCell();
            builder.getCellFormat().setWidth(140.0);
            builder.write("���鷽������");
            builder.endRow();
            
            // �ڶ���
            builder.insertCell();
            builder.getRowFormat().setHeight(40);
            builder.getCellFormat().setVerticalMerge(CellMerge.PREVIOUS);
            builder.getCellFormat().setWidth(100.0);
            builder.write("");
            
            // �����м��460�����أ����ݹ�ϵ�µ�revision�ĸ�������
            int revisionCount = TablesBean1List.size();
            double sumWidth = 460.0;
            int everyWidth = (int) sumWidth / revisionCount;
            double lastWidth = sumWidth - everyWidth * (revisionCount - 1);
            for (int i = 0; i < revisionCount; i++) {
            	builder.insertCell();
            	if (i == 0) {
            		builder.getCellFormat().setVerticalMerge(CellMerge.NONE);
            	}
                if ( i == revisionCount - 1) {
                	builder.getCellFormat().setWidth(lastWidth);
                } else {
                	builder.getCellFormat().setWidth(everyWidth);
                }
                builder.write(TablesBean1List.get(i).itemId);
            }
            
            builder.insertCell();
            builder.getCellFormat().setWidth(70.0);
            builder.write("ִ�з���");
            builder.insertCell();
            builder.getCellFormat().setWidth(70.0);
            builder.write("����");
            builder.endRow();
            
            // ���Ȼ��tablesBean������List��ϵ�sumList
            ArrayList<BOMLinePropertiesBean> sumList = new ArrayList<BOMLinePropertiesBean>();
            if (tablesBean1.list != null) {
            	sumList.addAll(tablesBean1.list);
            }
            if (tablesBean1.list1 != null) {
            	sumList.addAll(tablesBean1.list1);
            }
            if (tablesBean1.list2 != null) {
            	sumList.addAll(tablesBean1.list2);
            }
            if (tablesBean1.list3 != null) {
            	sumList.addAll(tablesBean1.list3);
            }
            if (tablesBean1.list4 != null) {
            	sumList.addAll(tablesBean1.list4);
            }
            if (tablesBean1.list5 != null) {
            	sumList.addAll(tablesBean1.list5);
            }
            
            // ����sumList������ʣ�µĲ���
            builder.getFont().setBold(false);
            builder.getRowFormat().setHeight(20);
            builder.getCellFormat().getShading().setBackgroundPatternColor(Color.WHITE);
            for (BOMLinePropertiesBean bean : sumList) {
            	// ��׼����˵�������в����ֱ�ע��
            	if (bean.object_name.equals("��ע")) {
            		continue;
            	}
            	
            	builder.insertCell();
            	builder.getCellFormat().setWidth(100.0);
            	builder.write(GetProjectNameStr(bean));
            	
            	for (int i = 0; i < revisionCount; i++) {
                	builder.insertCell();
                    if ( i == revisionCount - 1) {
                    	builder.getCellFormat().setWidth(lastWidth);
                    } else {
                    	builder.getCellFormat().setWidth(everyWidth);
                    }
                    BOMLinePropertiesBean tempBean = TablesBean1List.get(i).bomLineMap.get(bean.UID);
                    if (tempBean == null) {
                    	builder.write("");
                    } else {
                    	// ���ڱ�׼�ͱ�׼��ϵ�µķ����ȡָ�����õ��ֶ��ǲ�ͬ�ģ�Ҫ�ֲ�ͬ�ĺ���ȥ���
                    	String indicatorStr = "";
                    	if (i == 0) {
                    		indicatorStr = GetIndicatorStr(bean);
                    	} else {
                    		indicatorStr = GetIndicatorStr1(tempBean);
                    	}
                    	indicatorStr = indicatorStr.equals("") ? "-" : indicatorStr;
                    	builder.write(indicatorStr);
                    }
                }
            	
            	builder.insertCell();
            	builder.getCellFormat().setWidth(70.0);
            	String testcriterion = bean.bomline_U8_testcriterion;
            	testcriterion = testcriterion.equals("") ? "-" : testcriterion;
            	builder.write(testcriterion);
            	
            	builder.insertCell();
            	builder.getCellFormat().setWidth(70.0);
            	String testgist = bean.bomline_U8_testgist;
            	testgist = testgist.equals("") ? "-" : testgist;
            	builder.write(testgist);
            	builder.endRow();
            }
            
            builder.endTable();
            approvalTable.setAllowAutoFit(false);        	
    	}
        
        // ��������������
        builder.writeln();
        builder.getFont().setSize(12);
        builder.getFont().setName("����");
        builder.getFont().setBold(false);
        builder.getParagraphFormat().setAlignment(ParagraphAlignment.RIGHT);
        builder.writeln("����" + groupName);
        builder.writeln(new SimpleDateFormat("yyyy��MM��dd��").format(new Date()));
        
        // �����ɺõ�Word����
        String outFilePath = path + StringUtils.GetNameByString(revisionProertiesBean.item_id) + wordName1;
        File outFile = new File(outFilePath);
		if (outFile.exists()) {
			outFile.delete();
		}			
        doc.save(outFilePath);	    
		
		// �ϴ����ݼ�
		OrganiseUpload(outFilePath, datasetName1);
	}
	
	
	private void AddListToMap(ArrayList<BOMLinePropertiesBean> list, Map<String, BOMLinePropertiesBean> map) {
		for (BOMLinePropertiesBean bean : list) {
			String UID = bean.UID;
			if (!map.containsKey(UID)) {
				map.put(UID, bean);
			}
		}
	}
	
	private void GetTablesBean1List(String RelationshipName) throws Exception {
		// ����׼��ͼΪ��ʱ����׼����˵�������²����ɱ��
		if (tablesBean.ifNull) {
			return;
		}
		
		// ���Ȱ�ѡ�еı�׼�ĸ���BomLine��ӽ�ȥTablesBean1List
		TableBean1 tableBeanFirst = new TableBean1();
		// 2018.05.04 �޸�����Ϊitem_id�ĵ�һ���ո���������
		// tableBeanFirst.revisionName = revisionProertiesBean.object_name;
		String itemId = revisionProertiesBean.item_id;
		String expression = "\\d+";
		if (itemId.startsWith(expression)) {
			itemId = itemId.replaceFirst(expression, "");
		}
		tableBeanFirst.itemId = itemId;
		tableBeanFirst.bomLineMap = new HashMap<String, BOMLinePropertiesBean>();
		if (tablesBean.list != null) {
			AddListToMap(tablesBean.list, tableBeanFirst.bomLineMap);
        }
        if (tablesBean.list1 != null) {
        	AddListToMap(tablesBean.list1, tableBeanFirst.bomLineMap);
        }
        if (tablesBean.list2 != null) {
        	AddListToMap(tablesBean.list2, tableBeanFirst.bomLineMap);
        }
        if (tablesBean.list3 != null) {
        	AddListToMap(tablesBean.list3, tableBeanFirst.bomLineMap);
        }
        if (tablesBean.list4 != null) {
        	AddListToMap(tablesBean.list4, tableBeanFirst.bomLineMap);
        }
        if (tablesBean.list5 != null) {
        	AddListToMap(tablesBean.list5, tableBeanFirst.bomLineMap);
        }
        TablesBean1List.add(tableBeanFirst);
                
		mItemRev.refresh();
		TCComponent[] components = mItemRev.getReferenceListProperty(RelationshipName);
		if (components == null) {
			return;
		}
		for (TCComponent component : components) {
			// ���� component.getProperty("object_type") �� component.getType() !!!
			if(component.getType().equals("U8_LawRevision")){
				TCComponentBOMLine topBomLine = BomUtil.getTopBomLine((TCComponentItemRevision)component, Const.Formulator.MATERIALBOMNAME);
				if (topBomLine == null || !topBomLine.hasChildren()) {
					CloseWindow(topBomLine);
					break;
				}
				TableBean1 tableBean1 = new TableBean1();
				// �޸�����Ϊitem_id�ĵ�һ���ո���������
				//tableBean1.revisionName = component.getProperty("object_name");
				String itemId1 = component.getProperty("item_id");
				if (itemId1.startsWith(expression)) {
					itemId1 = itemId1.replaceFirst(expression, "");
				}
				tableBean1.itemId = itemId1;
				tableBean1.bomLineMap = new HashMap<String, BOMLinePropertiesBean>();
				AIFComponentContext[] children = topBomLine.getChildren();
				if(children == null || children.length == 0) {
					break;
				}
				for (AIFComponentContext context : children) {
					TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
					TCComponentItemRevision revision = bomLine.getItemRevision();
					String[] properties = revision.getProperties(propertiesName1);
					String[] properties1 = bomLine.getProperties(propertiesName2);
					String UID = revision.getUid();
					BOMLinePropertiesBean bean = new BOMLinePropertiesBean(properties, properties1, UID);
					tableBean1.bomLineMap.put(UID, bean);
					
					if (!tableBeanFirst.bomLineMap.containsKey(UID)) {
						AddToTablesBean(tablesBean1, bean);
					}
				}
				TablesBean1List.add(tableBean1);
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void InsertApprovalTable(DocumentBuilder builder) throws Exception {
		Table approvalTable = builder.startTable();	        
        builder.insertCell();
        approvalTable.setLeftIndent(0);
        builder.getRowFormat().setHeight(20);
        builder.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
        builder.getFont().setSize(10.5);
        builder.getFont().setName("����");

        builder.getCellFormat().setWidth(210.0);
        builder.write("��ҵ��");

        builder.insertCell();
        builder.getCellFormat().setWidth(240.0);
        builder.write("��������");
        builder.endRow();

        builder.getCellFormat().getShading().setBackgroundPatternColor(Color.WHITE);
        builder.getCellFormat().setWidth(70.0);
        builder.getCellFormat().setVerticalAlignment(CellVerticalAlignment.CENTER);

        builder.getRowFormat().setHeight(20.0);
        builder.insertCell();

        builder.write("���");
        builder.insertCell();
        builder.write("���");

        builder.insertCell();
        builder.write("����");
        builder.insertCell();
        builder.getCellFormat().setWidth(150.0);
        builder.write("����");
        builder.insertCell();
        builder.getCellFormat().setWidth(90.0);
        builder.write("����");
        builder.endRow();

        builder.insertCell();
        builder.getCellFormat().setWidth(70.0);
        builder.write("");
        builder.insertCell();
        builder.write("");
        builder.insertCell();
        builder.write("");
        
        builder.insertCell();
        builder.getCellFormat().setWidth(150.0);
        builder.write("");
        builder.insertCell();
        builder.getCellFormat().setWidth(90.0);
        builder.write("");
        builder.endRow();
        builder.endTable();
        approvalTable.setAllowAutoFit(false);
	}

	private String GetGroupName() {
		String owning_group = revisionProertiesBean.owning_group;
		
		// �����ҵ�� ����Ϊ XX��ҵ������������ҵ����ʲô��дʲô
		if (owning_group.contains("����")) {
			return "������ҵ��";
		} else if (owning_group.contains("��������")) {
			return "��������";
		} else if (owning_group.contains("Һ��")) {
			return "Һ����ҵ��";
		} else if (owning_group.contains("����")) {
			return "������ҵ��";
		} else if (owning_group.contains("�̷�")) {
			return "�̷���ҵ��";
		} else if (owning_group.contains("")) {
			return "";
		} else {
			return owning_group;
		}
	}

	private Table CreateTable(Document doc)
    {
        Table table = new Table(doc);
        
        // ------------------------------row
        Row row = new Row(doc);
        row.getRowFormat().setHeight(20.0);
        table.appendChild(row);
        
        Cell row_cell = new Cell(doc);
        row_cell.getCellFormat().setWidth(210.0);
        row_cell.getCellFormat().setVerticalAlignment(CellVerticalAlignment.CENTER);
        row.appendChild(row_cell);
        row_cell.appendChild(new Paragraph(doc));
        row_cell.getFirstParagraph().appendChild(new Run(doc, "��ҵ��"));
        row_cell.getFirstParagraph().getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
        
        Cell row_cell1 = new Cell(doc);
        row_cell1.getCellFormat().setWidth(240.0);
        row_cell1.getCellFormat().setVerticalAlignment(CellVerticalAlignment.CENTER);
        row.appendChild(row_cell1);
        row_cell1.appendChild(new Paragraph(doc));
        row_cell1.getFirstParagraph().appendChild(new Run(doc, "��������"));
        row_cell1.getFirstParagraph().getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
        
        // ------------------------------row1
        Row row1 = new Row(doc);
        row1.getRowFormat().setHeight(20.0);
        table.appendChild(row1);
        
        Cell row1_cell = new Cell(doc);
        row1_cell.getCellFormat().setWidth(70.0);
        row1_cell.getCellFormat().setVerticalAlignment(CellVerticalAlignment.CENTER);
        row1.appendChild(row1_cell);
        row1_cell.appendChild(new Paragraph(doc));
        row1_cell.getFirstParagraph().appendChild(new Run(doc, "���"));
        row1_cell.getFirstParagraph().getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
        
        Cell row1_cell1 = new Cell(doc);
        row1_cell1.getCellFormat().setWidth(70.0);
        row1_cell1.getCellFormat().setVerticalAlignment(CellVerticalAlignment.CENTER);
        row1.appendChild(row1_cell1);
        row1_cell1.appendChild(new Paragraph(doc));
        row1_cell1.getFirstParagraph().appendChild(new Run(doc, "���"));
        row1_cell1.getFirstParagraph().getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
        
        Cell row1_cell2 = new Cell(doc);
        row1_cell2.getCellFormat().setWidth(70.0);
        row1_cell2.getCellFormat().setVerticalAlignment(CellVerticalAlignment.CENTER);
        row1.appendChild(row1_cell2);
        row1_cell2.appendChild(new Paragraph(doc));
        row1_cell2.getFirstParagraph().appendChild(new Run(doc, "����"));
        row1_cell2.getFirstParagraph().getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
        
        Cell row1_cell3 = new Cell(doc);
        row1_cell3.getCellFormat().setWidth(150.0);
        row1_cell3.getCellFormat().setVerticalAlignment(CellVerticalAlignment.CENTER);
        row1.appendChild(row1_cell3);
        row1_cell3.appendChild(new Paragraph(doc));
        row1_cell3.getFirstParagraph().appendChild(new Run(doc, "����"));
        row1_cell3.getFirstParagraph().getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
        
        Cell row1_cell4 = new Cell(doc);
        row1_cell4.getCellFormat().setWidth(90.0);
        row1_cell4.getCellFormat().setVerticalAlignment(CellVerticalAlignment.CENTER);
        row1.appendChild(row1_cell4);
        row1_cell4.appendChild(new Paragraph(doc));
        row1_cell4.getFirstParagraph().appendChild(new Run(doc, "����"));
        row1_cell4.getFirstParagraph().getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
        
        // ------------------------------row2
        Row row2 = new Row(doc);
        row2.getRowFormat().setHeight(20.0);
        table.appendChild(row2);
        
        Cell row2_cell = new Cell(doc);
        row2_cell.getCellFormat().setWidth(70.0);
        row2_cell.getCellFormat().setVerticalAlignment(CellVerticalAlignment.CENTER);
        row2.appendChild(row2_cell);
        row2_cell.appendChild(new Paragraph(doc));
        row2_cell.getFirstParagraph().appendChild(new Run(doc, ""));
        
        Cell row2_cell1 = new Cell(doc);
        row2_cell1.getCellFormat().setWidth(70.0);
        row2_cell1.getCellFormat().setVerticalAlignment(CellVerticalAlignment.CENTER);
        row2.appendChild(row2_cell1);
        row2_cell1.appendChild(new Paragraph(doc));
        row2_cell1.getFirstParagraph().appendChild(new Run(doc, ""));
        
        Cell row2_cell2 = new Cell(doc);
        row2_cell2.getCellFormat().setWidth(70.0);
        row2_cell2.getCellFormat().setVerticalAlignment(CellVerticalAlignment.CENTER);
        row2.appendChild(row2_cell2);
        row2_cell2.appendChild(new Paragraph(doc));
        row2_cell2.getFirstParagraph().appendChild(new Run(doc, ""));
        
        Cell row2_cell3 = new Cell(doc);
        row2_cell3.getCellFormat().setWidth(150.0);
        row2_cell3.getCellFormat().setVerticalAlignment(CellVerticalAlignment.CENTER);
        row2.appendChild(row2_cell3);
        row2_cell3.appendChild(new Paragraph(doc));
        row2_cell3.getFirstParagraph().appendChild(new Run(doc, ""));
        
        Cell row2_cell4 = new Cell(doc);
        row2_cell4.getCellFormat().setWidth(90.0);
        row2_cell4.getCellFormat().setVerticalAlignment(CellVerticalAlignment.CENTER);
        row2.appendChild(row2_cell4);
        row2_cell4.appendChild(new Paragraph(doc));
        row2_cell4.getFirstParagraph().appendChild(new Run(doc, ""));
        
        return table;
    }
	
	private void InsertShapeToTable(Document doc) throws Exception {
		Shape shapeInTable = new Shape(doc, ShapeType.TEXT_BOX);
		shapeInTable.setWrapType(WrapType.NONE);
		shapeInTable.setBehindText(false);
		shapeInTable.setAlternativeText("$[#�ύ.name]");
		//shapeInTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
		shapeInTable.setBounds(new Rectangle2D.Float(-8, 532, 70, 20));  // x y weight height
//        shapeInTable.setStrokeColor(Color.white);
		shapeInTable.setStroked(false);
		shapeInTable.setFilled(false);
		SetTextBoxMarginZero(shapeInTable.getTextBox());
		AddTextToShape(doc, shapeInTable, "����", 14, "");
		doc.getFirstSection().getBody().getFirstParagraph().appendChild(shapeInTable);
		
		Shape shapeInTable1 = new Shape(doc, ShapeType.TEXT_BOX);
		shapeInTable1.setWrapType(WrapType.NONE);
		shapeInTable1.setBehindText(false);
		shapeInTable1.setAlternativeText("$[#��ҵ�����.name]");
		shapeInTable1.setBounds(new Rectangle2D.Float(62, 532, 70, 20));
//        shapeInTable1.setStrokeColor(Color.white);
		shapeInTable1.setStroked(false);
        shapeInTable1.setFilled(false);
        SetTextBoxMarginZero(shapeInTable1.getTextBox());
        AddTextToShape(doc, shapeInTable1, "����", 14, "");
		doc.getFirstSection().getBody().getFirstParagraph().appendChild(shapeInTable1);
		
		Shape shapeInTable2 = new Shape(doc, ShapeType.TEXT_BOX);
		shapeInTable2.setWrapType(WrapType.NONE);
		shapeInTable2.setBehindText(false);
		shapeInTable2.setAlternativeText("$[#��ҵ������.name]");
		shapeInTable2.setBounds(new Rectangle2D.Float(131, 532, 70, 20));
//        shapeInTable2.setStrokeColor(Color.white);
		shapeInTable2.setStroked(false);
        shapeInTable2.setFilled(false);
        SetTextBoxMarginZero(shapeInTable2.getTextBox());
        AddTextToShape(doc, shapeInTable2, "����", 14, "");
		doc.getFirstSection().getBody().getFirstParagraph().appendChild(shapeInTable2);
		
		Shape shapeInTable3 = new Shape(doc, ShapeType.TEXT_BOX);
		shapeInTable3.setWrapType(WrapType.NONE);
		shapeInTable3.setBehindText(false);
		shapeInTable3.setAlternativeText("$[#������������.name]");
		shapeInTable3.setBounds(new Rectangle2D.Float(200, 532, 150, 20));
//        shapeInTable3.setStrokeColor(Color.white);
		shapeInTable3.setStroked(false);
        shapeInTable3.setFilled(false);
        SetTextBoxMarginZero(shapeInTable3.getTextBox());
        AddTextToShape(doc, shapeInTable3, "����", 14, "");
        doc.getFirstSection().getBody().getFirstParagraph().appendChild(shapeInTable3);
		
		Shape shapeInTable4 = new Shape(doc, ShapeType.TEXT_BOX);
		shapeInTable4.setWrapType(WrapType.NONE);
		shapeInTable4.setBehindText(false);
		shapeInTable4.setAlternativeText("$[#������������.name]");
		shapeInTable4.setBounds(new Rectangle2D.Float(350, 532, 89, 20));
//        shapeInTable4.setStrokeColor(Color.white);
		shapeInTable4.setStroked(false);
        shapeInTable4.setFilled(false);
        SetTextBoxMarginZero(shapeInTable4.getTextBox());
        AddTextToShape(doc, shapeInTable4, "����", 14, "");
        AddTextToShape(doc, shapeInTable4, "");
		doc.getFirstSection().getBody().getFirstParagraph().appendChild(shapeInTable4);
	}
	
	private void InsertShapeForDate(Document doc) throws Exception {
		Shape shapeForDate = new Shape(doc, ShapeType.TEXT_BOX);
		shapeForDate.setWrapType(WrapType.NONE);
		shapeForDate.setBehindText(false);
		shapeForDate.setAlternativeText("$[#������������.date]");
		//shapeInTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
		shapeForDate.setBounds(new Rectangle2D.Float(-5, 585, 80, 20));  // x y weight height
//		shapeForDate.setStrokeColor(Color.white);
		shapeForDate.setStroked(false);
		shapeForDate.setFilled(false);
		SetTextBoxMarginZero(shapeForDate.getTextBox());
		AddTextToShape(doc, shapeForDate, "");
		doc.getFirstSection().getBody().getFirstParagraph().appendChild(shapeForDate);
		
		Shape shapeText = new Shape(doc, ShapeType.TEXT_BOX);
		shapeText.setWrapType(WrapType.NONE);
		AddTextToShape(doc, shapeText, "����");        
        shapeText.setBounds(new Rectangle2D.Float(80, 585, 30, 20));  // x y weight height
        shapeText.setStroked(false);
        shapeText.setFilled(false);
        SetTextBoxMarginZero(shapeText.getTextBox());
        doc.getFirstSection().getBody().getFirstParagraph().appendChild(shapeText);
		
		Shape shapeForDate1 = new Shape(doc, ShapeType.TEXT_BOX);
		shapeForDate1.setWrapType(WrapType.NONE);
		shapeForDate1.setBehindText(false);
//		shapeForDate1.setAlternativeText("");
		//shapeForDate1.setHorizontalAlignment(HorizontalAlignment.CENTER);
		shapeForDate1.setBounds(new Rectangle2D.Float(330, 585, 80, 20));  // x y weight height
//		shapeForDate1.setStrokeColor(Color.white);
		shapeForDate1.setStroked(false);
		shapeForDate1.setFilled(false);
		SetTextBoxMarginZero(shapeForDate1.getTextBox());
		AddTextToShape(doc, shapeForDate1, "");
		doc.getFirstSection().getBody().getFirstParagraph().appendChild(shapeForDate1);
		
		Shape shapeText1 = new Shape(doc, ShapeType.TEXT_BOX);
		shapeText1.setWrapType(WrapType.NONE);
		AddTextToShape(doc, shapeText1, "ʵʩ");        
		shapeText1.setBounds(new Rectangle2D.Float(410, 585, 30, 20));  // x y weight height
		shapeText1.setStroked(false);
        shapeText1.setFilled(false);
        SetTextBoxMarginZero(shapeText1.getTextBox());
        doc.getFirstSection().getBody().getFirstParagraph().appendChild(shapeText1);
	}
	
	private void AddTextToShape(Document doc, Shape shape, String fontName, double foneSize, String text) throws Exception {
		shape.appendChild(new Paragraph(doc));
		Paragraph para = shape.getFirstParagraph();
        para.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
        Run run = new Run(doc);
        Font font = run.getFont();
        font.setSize(foneSize);
        font.setName(fontName);
        run.setText(text);
        para.appendChild(run);
	}
	
	private void AddTextToShape(Document doc, Shape shape, String text) throws Exception {
		shape.appendChild(new Paragraph(doc));
		Paragraph para = shape.getFirstParagraph();
        para.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
        Run run = new Run(doc);
        Font font = run.getFont();
        font.setSize(14);
        font.setName("����");
        run.setText(text);
        para.appendChild(run);
	}
	
	private void SetTextBoxMarginZero(TextBox textBox) {
		textBox.setInternalMarginLeft(0);
        textBox.setInternalMarginRight(0);
        textBox.setInternalMarginTop(0);
        textBox.setInternalMarginBottom(0);
	}

	private void SetImageMaxSize(Shape image) {
        int maxSize = 450;
        if (image.getWidth() > maxSize) {
            double num = image.getWidth() / maxSize;
            image.setWidth(maxSize);
            image.setHeight(image.getHeight() / num);
        }
        
        if (image.getHeight() > maxSize) {
            double num = image.getHeight() / maxSize;
            image.setHeight(maxSize);
            image.setWidth(image.getWidth() / num);
        
        }
    }
	
	private void AddNewTableToDoc(ArrayList<BOMLinePropertiesBean> list, DocumentBuilder builder) throws Exception {
		Table table = builder.startTable();
        builder.insertCell();
        table.setLeftIndent(0);
        builder.getRowFormat().setHeight(20);
        builder.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
        builder.getFont().setSize(10.5);
        builder.getFont().setName("����");

        builder.getCellFormat().setWidth(150.0);
        builder.write("��Ŀ");
        builder.getCellFormat().setHorizontalMerge(CellMerge.NONE);
        builder.insertCell();
        builder.write("ָ��");
        builder.insertCell();
        builder.write("���鷽��");
        builder.endRow();
        builder.getCellFormat().setVerticalAlignment(CellVerticalAlignment.CENTER);
        
        // 2018.05.04 ��ע�޸�Ϊ��ȡÿ��BomLine��U8_remarkͳһ���ڱ�����
//        // ���������Ϊ ��ע �ģ���Ҫ���ڱ�����һ��
//        for (BOMLinePropertiesBean bean : list) {
//        	if (bean.object_name.equals("��ע")) {
//        		if (!list.remove(bean)) {
//        			throw new Exception("Can Not Remove ��ע Bean From List");
//        		}
//        		list.add(bean);
//        		break;
//        	}
//        }
        
        String remark = "";
        
        // ��ʼ�������ÿһ��
        for (BOMLinePropertiesBean bean : list) {
        	if (!bean.bomline_U8_remark.equals("")) {
            	remark = remark + bean.object_name + "��" + bean.bomline_U8_remark + "\n";
            }
        	
        	// ��Ϊ��ע��BomLineֻȡ��bomline_U8_remark������в���ʾ����
        	if (bean.object_name.equals("��ע")) {
        		continue;
        	}
        	
        	builder.insertCell();
            builder.write(GetProjectNameStr(bean));
            
            // ���ݹ�����ָ����ʽ
            builder.insertCell();
            builder.write(GetIndicatorStr(bean));
            
            builder.insertCell();
            String testStr = bean.bomline_U8_testcriterion;
            if (testStr.equals("")) {
            	testStr = "-";
            }
            builder.write(testStr);
            builder.endRow();
        }
        
        if (!remark.equals("")) {
        	remark = remark.substring(0, remark.length() - 1);
        	
        	builder.insertCell();
            builder.write(remark);
            builder.getCellFormat().setHorizontalMerge(CellMerge.FIRST);
            builder.getParagraphFormat().setAlignment(ParagraphAlignment.LEFT);
            builder.insertCell();
            builder.write("");
            builder.getCellFormat().setHorizontalMerge(CellMerge.PREVIOUS);
            builder.insertCell();
            builder.write("");
            builder.getCellFormat().setHorizontalMerge(CellMerge.PREVIOUS);
            builder.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
        }
        
        builder.endTable();
	}
	
	private String GetProjectNameStr(BOMLinePropertiesBean bean) {
		String cellStr = bean.object_name;
    	if (!bean.bomline_U8_indexdesc.equals("")) {
    		cellStr += "(" + bean.bomline_U8_indexdesc + ")";
    	}
    	if (!bean.bomline_u8_standardunit.equals("")) {
    		cellStr += " ," + bean.bomline_u8_standardunit;
    	} else if(!bean.u8_uom.equals("")) {
    		cellStr += " ," + bean.u8_uom;
    	}
    	
    	return cellStr;
	}
	
	/**
	 * ����ԭ�ϱ�׼���б���ȡָ���ַ���</br>
	 * ʹ�õ���U8_DOWNLINE��U8_DOWN_OPERATION��U8_UP_OPERATION��U8_UPLINE
	 */
	private String GetIndicatorStr(BOMLinePropertiesBean bean) {
		String cellStr = "";
		
		if (bean.bomline_U8_DOWNLINE.equals("") && bean.bomline_U8_UPLINE.equals("")) {
			cellStr = bean.bomline_U8_detectvalue;
		} else if (bean.bomline_U8_DOWNLINE.equals("")) {
			if (bean.bomline_U8_UP_OPERATION.equals("")) {
				cellStr = "<=  " + bean.bomline_U8_UPLINE;
			} else {
				cellStr = bean.bomline_U8_UP_OPERATION + bean.bomline_U8_UPLINE;
			}
		} else if (bean.bomline_U8_UPLINE.equals("")) {
			if (bean.bomline_U8_DOWN_OPERATION.equals("")) {
				cellStr = ">=  " + bean.bomline_U8_DOWNLINE;
			} else {
				cellStr = bean.bomline_U8_DOWN_OPERATION + bean.bomline_U8_DOWNLINE;
			}		
		} else {
			if ((bean.bomline_U8_UP_OPERATION.equals("") || bean.bomline_U8_UP_OPERATION.equals("<="))
					&& (bean.bomline_U8_DOWN_OPERATION.equals("") || bean.bomline_U8_DOWN_OPERATION.equals(">="))) {
				cellStr = bean.bomline_U8_DOWNLINE + " - " + bean.bomline_U8_UPLINE;
			} else {
				cellStr = bean.bomline_U8_DOWNLINE + bean.bomline_U8_DOWN_OPERATION
						+ " X "
						+ bean.bomline_U8_UP_OPERATION + bean.bomline_U8_UPLINE;
			}
		}
		
		if (cellStr.contains(" X ")) {
			cellStr = cellStr.replace(">", "<");
		}
		
		if (cellStr.equals("")) {
			cellStr = "-";
        } else {
        	cellStr = cellStr.replace("<=", "��");
        	cellStr = cellStr.replace(">=", "��");
        }
		
        return cellStr;
	}
	
	/**
	 * ���ڱ�׼����˵���б���ȡָ���ַ���</br>
	 * ʹ�õ���U8_STAND_DOWNLINE��U8_STDDOWN_OPERATION��U8_STANDUP_OPERATION��U8_STAND_UPLINE
	 */
	private String GetIndicatorStr1(BOMLinePropertiesBean bean) {
		String cellStr = "";
		
		if (bean.bomline_U8_STAND_DOWNLINE.equals("") && bean.bomline_U8_STAND_UPLINE.equals("")) {
			cellStr = bean.bomline_U8_detectvalue;
		} else if (bean.bomline_U8_STAND_DOWNLINE.equals("")) {
			if (bean.bomline_U8_STANDUP_OPERATION.equals("")) {
				cellStr = "<=  " + bean.bomline_U8_STAND_UPLINE;
			} else {
				cellStr = bean.bomline_U8_STANDUP_OPERATION + bean.bomline_U8_STAND_UPLINE;
			}
		} else if (bean.bomline_U8_STAND_UPLINE.equals("")) {
			if (bean.bomline_U8_STDDOWN_OPERATION.equals("")) {
				cellStr = ">=  " + bean.bomline_U8_STAND_DOWNLINE;
			} else {
				cellStr = bean.bomline_U8_STDDOWN_OPERATION + bean.bomline_U8_STAND_DOWNLINE;
			}		
		} else {
			if ((bean.bomline_U8_STANDUP_OPERATION.equals("") || bean.bomline_U8_STANDUP_OPERATION.equals("<="))
					&& (bean.bomline_U8_STDDOWN_OPERATION.equals("") || bean.bomline_U8_STDDOWN_OPERATION.equals(">="))) {
				cellStr = bean.bomline_U8_STAND_DOWNLINE + " - " + bean.bomline_U8_STAND_UPLINE;
			} else {
				cellStr = bean.bomline_U8_STAND_DOWNLINE + bean.bomline_U8_STDDOWN_OPERATION
						+ " X "
						+ bean.bomline_U8_STANDUP_OPERATION + bean.bomline_U8_STAND_UPLINE;
			}
		}
		
		if (cellStr.contains(" X ")) {
			cellStr = cellStr.replace(">", "<");
		}
		
		if (cellStr.equals("")) {
			cellStr = "-";
        } else {
        	cellStr = cellStr.replace("<=", "��");
        	cellStr = cellStr.replace(">=", "��");
        }
		
        return cellStr;
	}
	
	private InputStream getModelExcell() {
        InputStream excelInputStream;
        try {
//            excelInputStream = GetDataAndExportWord.class.getClassLoader().getResourceAsStream(
//                    "\\logo.png");
          ClassLoader classloader = Thread.currentThread().getContextClassLoader();
          excelInputStream = classloader.getResourceAsStream("//logo.png");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        return excelInputStream;
    }
	
	private void CheckIfContinue() throws Exception {
		String warningMessage = "";
		mItemRev.refresh();
		TCComponent[] components = mItemRev.getReferenceListProperty("IMAN_specification");
		if (components != null && components.length !=0) {
			for (TCComponent component : components) {
				if (component instanceof TCComponentDataset) {
					if (component.getProperty("object_name").equals(datasetName)) {
						warningMessage += datasetName + " ";
					}
					if (component.getProperty("object_name").equals(datasetName1)) {
						warningMessage += datasetName1 + " ";
					}
				}
			}
		}
		
		if (!warningMessage.equals("")) {
			warningMessage = "��ʾ\n\n" + mItemRev.getProperty("object_name") + "(idΪ" + mItemRev.getProperty("item_id") + ")���Ѵ���\n" + warningMessage + "�ļ�\n";
			throw new Exception(warningMessage);
		}
	}
	
	private void DownloadImage(String RelationshipName) throws Exception {
		mItemRev.refresh();
		TCComponent[] components = mItemRev.getReferenceListProperty(RelationshipName);
		ArrayList<TCComponentDataset> componentList = new ArrayList<TCComponentDataset>();
		for (TCComponent component : components) {
			if(component.getProperty("object_type").equals("U8_image")){
				componentList.add((TCComponentDataset)component);
			}
		}
		if (componentList.size() == 0) {
			return;
		}
		
		if (RelationshipName.equals("U8_StructuredRel")) {
			DownDateSetToLocalDir(componentList, "U8_image", "fileList");
		} else if (RelationshipName.equals("U8_ProcessMapRel")) {
			DownDateSetToLocalDir(componentList, "U8_image", "fileList1");
		}
	}
	
	private void DownloadDoc(String RelationshipName) throws Exception {
		mItemRev.refresh();
		TCComponent[] components = mItemRev.getReferenceListProperty(RelationshipName);
		if (components == null) {
			return;
		}
		ArrayList<TCComponentDataset> componentList = new ArrayList<TCComponentDataset>();
		for (TCComponent component : components) {
			// ���� component.getProperty("object_type") �� component.getType() !!!
			if(component.getType().equals("U8_Word")){
				componentList.add((TCComponentDataset)component);
			}
		}
		if (componentList.size() == 0) {
			return;
		}
		
		DownDateSetToLocalDir(componentList, "U8_word", "wordNeedToMerge");
	}
	
	private void DownDateSetToLocalDir(ArrayList<TCComponentDataset> datasetList, String namedRefName, String listName) throws Exception {
		
		for (TCComponentDataset componentDataset : datasetList) {
			componentDataset = componentDataset.latest();

			// ע�⣺��������[������]��ͬ���ļ����ܴ��ڶ��
			String namedRefFileName[] = componentDataset.getFileNames(namedRefName);
			if ((namedRefFileName == null) || (namedRefFileName.length == 0)) {
				return;
			}

			for (int i = 0; i < namedRefFileName.length; i++) {
				File tempFileObject = new File(path, namedRefFileName[i]);
				if (tempFileObject.exists()) {
					tempFileObject.delete();
				}
				File fileObject = componentDataset.getFile(namedRefName, namedRefFileName[i], path);
				if (listName.equals("fileList")) {
					fileList.add(fileObject.getAbsolutePath());
				} else if (listName.equals("fileList1")) {
					fileList1.add(fileObject.getAbsolutePath());
				} else if (listName.equals("wordNeedToMerge")) {
					// ֻ��ȡ��һ���ĵ�
					wordNeedToMerge = fileObject.getAbsolutePath();
					return;
				}
			}
		}
	}
	
	@SuppressWarnings("unused")
	private String[] GetDateFromProperties() throws Exception {
		String date1 = revisionProertiesBean.u8_releasedate;
		String date2 = revisionProertiesBean.u8_implementationdate;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy��MM��dd��");
		if (date1.equals("")) {
			date1 = "    ��  ��  ��";
		} else {
			date1 = sdf1.format(sdf.parse(date1));
		}
		if (date2.equals("")) {
			date2 = "    ��  ��  ��";
		} else {
			date2 = sdf1.format(sdf.parse(date2));
		}
		
		return new String[] { date1, date2 };
	}
	
	private void GetAllProperties() throws Exception {		
		TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(mItemRev, Const.Formulator.MATERIALBOMNAME);
		if (topBomLine == null || !topBomLine.hasChildren()) {
			CloseWindow(topBomLine);
			return;
		}
		AIFComponentContext[] children = topBomLine.getChildren();
		if (children == null || children.length == 0) {
			return;
		}
		tablesBean.ifNull = false;
		for (AIFComponentContext context : children) {
			TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
			TCComponentItemRevision revision = bomLine.getItemRevision();
			String[] properties = revision.getProperties(propertiesName1);
			String[] properties1 = bomLine.getProperties(propertiesName2);
			BOMLinePropertiesBean bean = new BOMLinePropertiesBean(properties, properties1, revision.getUid());
			AddToTablesBean(tablesBean, bean);
		}
	}
	
	private void AddToTablesBean(TablesBean tablesBean, BOMLinePropertiesBean bean) {
		switch(bean.u8_category){
		case "�й�ָ��":
			if(tablesBean.list == null){
				tablesBean.list = new ArrayList<BOMLinePropertiesBean>();
			}
			tablesBean.list.add(bean);
			break;
		case "��ָ��":
			if(tablesBean.list1 == null){
				tablesBean.list1 = new ArrayList<BOMLinePropertiesBean>();
			}
			tablesBean.list1.add(bean);
			break;
		case "��Ⱦ��":
			if(tablesBean.list2 == null){
				tablesBean.list2 = new ArrayList<BOMLinePropertiesBean>();
			}
			tablesBean.list2.add(bean);
			break;
		case "����":
			if(tablesBean.list3 == null){
				tablesBean.list3 = new ArrayList<BOMLinePropertiesBean>();
			}
			tablesBean.list3.add(bean);
			break;
		case "΢����ָ��":
			if(tablesBean.list4 == null){
				tablesBean.list4 = new ArrayList<BOMLinePropertiesBean>();
			}
			tablesBean.list4.add(bean);
			break;
		default:
			if(tablesBean.list5 == null){
				tablesBean.list5 = new ArrayList<BOMLinePropertiesBean>();
			}
			tablesBean.list5.add(bean);
			break;
		}
	}
	
	private void CloseWindow(TCComponentBOMLine topBomLine) {

		// �رղ�����Bom View
		if (topBomLine != null) {
			TCComponentBOMWindow bomWindow = topBomLine.getCachedWindow();
			try {
				bomWindow.save();
				bomWindow.close();
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void OrganiseUpload(String filePath, String datasetName) throws Exception {
		UploadFile(mItemRev, filePath, datasetName);
	}
	
	private void UploadFile(TCComponent component, String path1, String DatasetName) throws Exception {
		File file = new File(path1);
		if (file.exists()) {
			// ����MSExcelXҪ�����ù�ϵexcel��Ӧ���У�����
//			TCComponentDataset dataSet = DataSetUtil.setDatasetFileToTC(
//					path1,
//					"MSExcelX",
//					"excel",
//					DatasetName);
			TCComponentDataset dataSet = DataSetUtil.setDatasetFileToTC(
					path1,
					"U8_Word",
					"U8_word",
					DatasetName);
			dataSet.setProperty("u8_category", "YLBZ");
			component.add("IMAN_specification", dataSet);
		}
	}
	
}

/**
 * ��BOMLine������Ҫ��ȡ������ ��
 */
class BOMLinePropertiesBean {
	// ��������
	String u8_category = "";
	String object_name = "";
	String bomline_U8_DOWNLINE = "";
	String bomline_U8_UPLINE = "";
	String bomline_U8_testcriterion = "";
	String bomline_U8_detectvalue = "";
	String bomline_U8_indexdesc = "";	
	String bomline_U8_STAND_DOWNLINE = "";
	String bomline_U8_STAND_UPLINE = "";
	String bomline_U8_STDDOWN_OPERATION = "";
	String bomline_U8_STANDUP_OPERATION = "";
	String bomline_U8_testgist = "";
	String bomline_U8_remark = "";
	
	// ר�����ڲ�Ʒ������׼
	String bomline_U8_EARLYWARN_UPLINE = "";
	String bomline_U8_EARLYWARNUP_OPT = "";
	String bomline_U8_EARLYWARN_DOWNLINE = "";
	String bomline_U8_EARLYWARN_DOWNOPT = "";
	String bomline_U8_EARLYWARNDESC = "";
	
	String UID = "";
	
	// ���滻����
	String bomline_u8_standardunit = "";
	String u8_uom = "";
	
	String bomline_U8_UP_OPERATION = "";
	String bomline_U8_DOWN_OPERATION = "";
	
	public BOMLinePropertiesBean(String[] properties, String[] properties1, String UID) {
		u8_category = properties[0];
		object_name = properties[1];
		u8_uom = properties[2];		
		
		bomline_U8_DOWNLINE = properties1[0];
		bomline_U8_UPLINE = properties1[1];
		bomline_U8_testcriterion = properties1[2];
		bomline_u8_standardunit = properties1[3];
		bomline_U8_UP_OPERATION = properties1[4];
		bomline_U8_DOWN_OPERATION = properties1[5];
		bomline_U8_detectvalue = properties1[6];
		bomline_U8_indexdesc = properties1[7];
		bomline_U8_STAND_DOWNLINE = properties1[8];
		bomline_U8_STAND_UPLINE = properties1[9];
		bomline_U8_STDDOWN_OPERATION = properties1[10];
		bomline_U8_STANDUP_OPERATION = properties1[11];
		bomline_U8_testgist = properties1[12];
		bomline_U8_remark = properties1[18];
		
		// ר�����ڲ�Ʒ������׼
		bomline_U8_EARLYWARN_UPLINE = properties1[13];
		bomline_U8_EARLYWARNUP_OPT = properties1[14];
		bomline_U8_EARLYWARN_DOWNLINE = properties1[15];
		bomline_U8_EARLYWARN_DOWNOPT = properties1[16];
		bomline_U8_EARLYWARNDESC = properties1[17];
		this.UID = UID;
	}
	
	public BOMLinePropertiesBean(String[] properties) {
		object_name = properties[1];
	}
}

/**
 * ����ԭ�ϼ�����׼�����и������Ĵ洢BOMLinePropertiesBean�������
 */
class TablesBean implements Cloneable {
	boolean ifNull = true;
	/**
	 * �й�Ҫ�� ����Ӧ����
	 */
	ArrayList<BOMLinePropertiesBean> list = null;
	/**
	 * ��ָ�� ����Ӧ����
	 */
	ArrayList<BOMLinePropertiesBean> list1 = null;
	/**
	 * ��Ⱦ������ ����Ӧ����
	 */
	ArrayList<BOMLinePropertiesBean> list2 = null;
	/**
	 * ����������� ����Ӧ����
	 */
	ArrayList<BOMLinePropertiesBean> list3 = null;
	/**
	 * ΢�������� ����Ӧ����
	 */
	ArrayList<BOMLinePropertiesBean> list4 = null;
	/**
	 * ����ָ�� ����Ӧ����
	 */
	ArrayList<BOMLinePropertiesBean> list5 = null;
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		TablesBean o = null;
		try{
			o = (TablesBean)super.clone();
		}catch(CloneNotSupportedException e){
			throw e;
		}
		return o;
	}
}

/**
 * ���ɱ�׼����˵�������б��Ĵ洢BOMLinePropertiesBean����
 */
class TableBean1 {
	String revisionName = "";
	String itemId = "";
	Map<String, BOMLinePropertiesBean> bomLineMap;
}

/**
 * �ɰ汾������Ҫ��ȡ������ ��
 */
class RevisionProertiesBean {
	String is_modifiable = "";
	String object_name = "";
	String item_id = "";
	String u8_releasedate = "";
	String u8_implementationdate = "";
	String u8_reface1 = "";
	String u8_range = "";
	String u8_definitions = "";
	String u8_materialrequirement = "";
	String u8_productionprocess = "";
	String u8_ingredientsrequirement = "";
	
	String u8_foodadditives = "";
	String u8_nutritionenhancer = "";
	String u8_ygtag = "";
	String u8_package = "";
	String u8_nutritionlabelsrelated = "";
	String u8_shelf_life = "";
	String u8_transport_conditions = "";
	String u8_storage_conditions = "";
	String u8_supply_conditions = "";
	String u8_others = "";
	String owning_group = "";
	String u8_stdcondition = "";
	
	public RevisionProertiesBean(String[] properties) {
		is_modifiable = properties[0];
		object_name = properties[1];
		item_id = properties[2];
		u8_releasedate = properties[3];
		u8_implementationdate = properties[4];
		u8_reface1 = properties[5];
		u8_range = properties[6];
		u8_definitions = properties[7];
		u8_materialrequirement = properties[8];
		u8_productionprocess = properties[9];
		u8_ingredientsrequirement = properties[10];
		
		u8_foodadditives = properties[11];
		u8_nutritionenhancer = properties[12];
		u8_ygtag = properties[13];
		u8_package = properties[14];
		u8_nutritionlabelsrelated = properties[15];
		u8_shelf_life = properties[16];
		u8_transport_conditions = properties[17];
		u8_storage_conditions = properties[18];
		u8_supply_conditions = properties[19];
		u8_others = properties[20];
		owning_group = properties[21];
		u8_stdcondition = properties[22];
	}
}