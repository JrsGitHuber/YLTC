package com.uds.yl.bean;


import com.teamcenter.fms.servercache.proxy.DefaultFSCWholeFileIOFactory.UPLOADER_TYPES;
import com.teamcenter.rac.stylesheet.PropertyObjectLink.DownButton;
import com.uds.yl.annotation.FieldAnotation;

public class TechUpDownProperty {
	
	
	
	@FieldAnotation(value="U8_UPLINE")
	public String ICS_UP;//�ڿر�׼����
	@FieldAnotation(value="U8_DOWNLINE")
	public String ICS_DOWN;//�ڿر�׼����
	
	@FieldAnotation(value="U8_UP_OPERATION")
	public String ICS_UP_SYMBOL;//�ڿر�׼���޷���
	@FieldAnotation(value="U8_DOWN_OPERATION")
	public String ICS_DOWN_SYMBOL;//�ڿر�׼���޷���
	
	
	
	
	@FieldAnotation(value="U8_EARLYWARN_UPLINE")
	public String WARING_UP;//Ԥ��ֵ����
	@FieldAnotation(value="U8_EARLYWARNDOWNLINE")
	public String WARING_DOWN;//Ԥ��ֵ����
	
	@FieldAnotation(value="U8_EARLYWARNUP_OPT")
	public String WARING_UP_SYMBOL;//Ԥ��ֵ���޷���
	@FieldAnotation(value="U8_EARLYWARNDOWNOPT")
	public String WARING_DOWN_SYMBOL;//Ԥ��ֵ�����Ʒ���
	
	@FieldAnotation(value="U8_EARLYWARNDESC")
	public String waring_detectValue;//Ԥ��ֵ������ֵ
	
	
	
	
	@FieldAnotation(value="U8_STAND_UPLINE")
	public String GB_UP;//��������
	@FieldAnotation(value="U8_STAND_DOWNLINE")
	public String GB_DOWN;//��������
	
	@FieldAnotation(value="U8_STANDUP_OPERATION")
	public String GB_UP_SYMBOL;//�������޷���
	@FieldAnotation(value="U8_STDDOWN_OPERATION")
	public String GB_DOWN_SYMBOL;//�������޷���
	
	@FieldAnotation(value="U8_detectvalue")
	public String detectValue;//���ֵ����

	public String getICS_UP() {
		return ICS_UP;
	}

	public void setICS_UP(String iCS_UP) {
		ICS_UP = iCS_UP;
	}

	public String getICS_DOWN() {
		return ICS_DOWN;
	}

	public void setICS_DOWN(String iCS_DOWN) {
		ICS_DOWN = iCS_DOWN;
	}

	public String getICS_UP_SYMBOL() {
		return ICS_UP_SYMBOL;
	}

	public void setICS_UP_SYMBOL(String iCS_UP_SYMBOL) {
		ICS_UP_SYMBOL = iCS_UP_SYMBOL;
	}

	public String getICS_DOWN_SYMBOL() {
		return ICS_DOWN_SYMBOL;
	}

	public void setICS_DOWN_SYMBOL(String iCS_DOWN_SYMBOL) {
		ICS_DOWN_SYMBOL = iCS_DOWN_SYMBOL;
	}

	public String getGB_UP() {
		return GB_UP;
	}

	public void setGB_UP(String gB_UP) {
		GB_UP = gB_UP;
	}

	public String getGB_DOWN() {
		return GB_DOWN;
	}

	public void setGB_DOWN(String gB_DOWN) {
		GB_DOWN = gB_DOWN;
	}

	public String getGB_UP_SYMBOL() {
		return GB_UP_SYMBOL;
	}

	public void setGB_UP_SYMBOL(String gB_UP_SYMBOL) {
		GB_UP_SYMBOL = gB_UP_SYMBOL;
	}

	public String getGB_DOWN_SYMBOL() {
		return GB_DOWN_SYMBOL;
	}

	public void setGB_DOWN_SYMBOL(String gB_DOWN_SYMBOL) {
		GB_DOWN_SYMBOL = gB_DOWN_SYMBOL;
	}

	public String getDetectValue() {
		return detectValue;
	}

	public void setDetectValue(String detectValue) {
		this.detectValue = detectValue;
	}

	public String getWARING_UP() {
		return WARING_UP;
	}

	public void setWARING_UP(String wARING_UP) {
		WARING_UP = wARING_UP;
	}

	public String getWARING_DOWN() {
		return WARING_DOWN;
	}

	public void setWARING_DOWN(String wARING_DOWN) {
		WARING_DOWN = wARING_DOWN;
	}

	public String getWARING_UP_SYMBOL() {
		return WARING_UP_SYMBOL;
	}

	public void setWARING_UP_SYMBOL(String wARING_UP_SYMBOL) {
		WARING_UP_SYMBOL = wARING_UP_SYMBOL;
	}

	public String getWARING_DOWN_SYMBOL() {
		return WARING_DOWN_SYMBOL;
	}

	public void setWARING_DOWN_SYMBOL(String wARING_DOWN_SYMBOL) {
		WARING_DOWN_SYMBOL = wARING_DOWN_SYMBOL;
	}

	public String getWaring_detectValue() {
		return waring_detectValue;
	}

	public void setWaring_detectValue(String waring_detectValue) {
		this.waring_detectValue = waring_detectValue;
	}
	
	
	
	
	
	
}
