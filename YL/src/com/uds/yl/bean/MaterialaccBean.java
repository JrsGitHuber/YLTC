package com.uds.yl.bean;

import com.uds.yl.annotation.FieldAnotation;
import com.uds.yl.common.FieldTypeEmu;

public class MaterialaccBean {
	@FieldAnotation(value="u8_category",type=FieldTypeEmu.REVISON)
	public String indexType="";//ָ������
	@FieldAnotation(value="object_name",type=FieldTypeEmu.REVISON)
	public String indexName="";//ָ������
	@FieldAnotation(value="U8_UPLINE")
	public String up="";//����
	@FieldAnotation(value="U8_DOWNLINE")
	public String down="";//����
	@FieldAnotation(value="U8_UP_OPERATION")
	public String upMark="";//���߷���
	@FieldAnotation(value="U8_DOWN_OPERATION")
	public String downMark="";//���߷���
	@FieldAnotation(value="U8_detectvalue")
	public String indexDec="";//��׼����
	public String getIndexType() {
		return indexType;
	}
	public void setIndexType(String indexType) {
		this.indexType = indexType;
	}
	public String getIndexName() {
		return indexName;
	}
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}
	public String getUp() {
		return up;
	}
	public void setUp(String up) {
		this.up = up;
	}
	public String getDown() {
		return down;
	}
	public void setDown(String down) {
		this.down = down;
	}
	public String getUpMark() {
		return upMark;
	}
	public void setUpMark(String upMark) {
		this.upMark = upMark;
	}
	public String getDownMark() {
		return downMark;
	}
	public void setDownMark(String downMark) {
		this.downMark = downMark;
	}
	public String getIndexDec() {
		return indexDec;
	}
	public void setIndexDec(String indexDec) {
		this.indexDec = indexDec;
	}
	
}
