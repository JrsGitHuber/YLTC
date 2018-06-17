package com.uds.yl.bean;

import java.io.Serializable;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.xerces.dom.ParentNode;

import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.uds.yl.annotation.FieldAnotation;
import com.uds.yl.common.FieldTypeEmu;

public class NodeBean implements Serializable{
	
	public String lawName="";// ��Ӧ�ķ������֣�������ע�� ����ʹ��
	
	public String nodeType;//����ڵ������
	
	public List<NodeBean> childNodeBeans;//���ӽڵ�
	
	public TCComponentItemRevision nodeItemRev;//�ýڵ�����Ӧ�İ汾  ԭ�ϰ汾 �����䷽�汾����ָ��汾
	
	public DefaultMutableTreeNode node;//����ڵ��node�ڵ�
	
	public List<DefaultMutableTreeNode> chidNodes;//���ӽڵ�
	
	public TCComponentBOMLine nodeBomLine;//�ڵ��Ӧ��BOMLine
	
	public DefaultMutableTreeNode parentNode;//���ڵ�
	
	
	public String sourceOfCompound="";//��������Դ
	
	public MaterialBean replaceMaterialBean;//��Ϊ��Ǹ�ԭ�ϵĿ����ԭ��
	
	public List<IndexItemBean> indexBeanList;//ԭ�������ָ����Ŀ
	
	public boolean isNutrition=false;//��Ӫ����
	
	public String standardValue;//�ڿ�ֵ
	public String lawValue;//����ֵ
	
	public String minValue = "";//��Сֵ  ������ı�ǩ
	public String middleValue = "";//�м�ֵ  ������ı�ǩ
	public String maxValue = "";//���ֵ ������ı�ǩ
	
	@FieldAnotation(value = "u8_isbacteria", type = FieldTypeEmu.REVISON)
	public String isbacteria=""; // �Ƿ��Ǿ�
	
	@FieldAnotation(value = "uom_tag" ,type =FieldTypeEmu.ITEM)
	public String uom_tag = "U8_Cust";
	
	@FieldAnotation(value="item_id",type=FieldTypeEmu.ITEM)
	public String itemID="";
	
	@FieldAnotation(value = "U8_MilkPowderMethod", type = FieldTypeEmu.BOMLINE)
	public String productMethod="";//��Ӧʪ�����߸ɷ�

	@FieldAnotation(value = "object_name", type = FieldTypeEmu.REVISON)
	public String objectName="";
	
	@FieldAnotation(value = "u8_samplename" ,type = FieldTypeEmu.REVISON)
	public String sampleName = "";//����Աȵ�ʱ�� �䷽�е���Ŀ��������Դ

	@FieldAnotation(value = "u8_code", type = FieldTypeEmu.REVISON) // ���ܴ���
	public String code="";

	@FieldAnotation(value = "u8_supplierinfo", type = FieldTypeEmu.REVISON) // ��Ӧ��
	public String suppplier="";

	@FieldAnotation(value = "u8_price", type = FieldTypeEmu.REVISON)
	public String price="";// ��λ�ɱ�

	@FieldAnotation(value = "U8_inventory", type = FieldTypeEmu.BOMLINE)
	public String U8_inventory="";// Ͷ���� bom

	@FieldAnotation(value = "bl_quantity", type = FieldTypeEmu.BOMLINE)
	public String bl_quantity="";// ��� bom

	@FieldAnotation(value = "U8_STAND_UPLINE", type = FieldTypeEmu.BOMLINE)
	public String up="";// ���� ���ڷ�������ֵ

	@FieldAnotation(value = "U8_STAND_DOWNLINE", type = FieldTypeEmu.BOMLINE)
	public String down="";// ���� �ڷ�������ֵ
	
	@FieldAnotation(value = "U8_STANDUP_OPERATION", type = FieldTypeEmu.BOMLINE)
	public String upSymbol="";// ���޷���

	@FieldAnotation(value = "U8_STDDOWN_OPERATION", type = FieldTypeEmu.BOMLINE)
	public String downSymbol="";// ���޷����ڷ�������ֵ

	@FieldAnotation(value="U8_detectvalue", type=FieldTypeEmu.BOMLINE)
	public String detectValue="";//����ֵ�ڷ�������ֵ
	
	@FieldAnotation(value="U8_UPLINE",type=FieldTypeEmu.BOMLINE)
	public String ICS_UP;//�ڿر�׼����
	@FieldAnotation(value="U8_DOWNLINE",type=FieldTypeEmu.BOMLINE)
	public String ICS_DOWN;//�ڿر�׼����
	
	@FieldAnotation(value="U8_UP_OPERATION",type=FieldTypeEmu.BOMLINE)
	public String ICS_UP_SYMBOL;//�ڿر�׼���޷���
	@FieldAnotation(value="U8_DOWN_OPERATION",type=FieldTypeEmu.BOMLINE)
	public String ICS_DOWN_SYMBOL;//�ڿر�׼���޷���
	
	
	@FieldAnotation(value="U8_up",type=FieldTypeEmu.BOMLINE)
	public String u8Up;//����
	
	@FieldAnotation(value="U8_down",type=FieldTypeEmu.BOMLINE)
	public String u8Down;//����

	
	@FieldAnotation(value = "u8_type", type = FieldTypeEmu.REVISON)
	public String type="";// �䷽�е��������� rev��

	@FieldAnotation(value = "U8_category", type = FieldTypeEmu.BOMLINE)
	public String category="";// ��Ʒ���

	@FieldAnotation(value = "U8_warnings", type = FieldTypeEmu.BOMLINE)
	public String warning="";// ����

	@FieldAnotation(value = "U8_alternate", type = FieldTypeEmu.BOMLINE)
	public String alternate="";// ����˵��

	@FieldAnotation(value = "U8_groupitem", type = FieldTypeEmu.BOMLINE)
	public String groupItem="";// ���˵��

	@FieldAnotation(value = "U8_alternateitem", type = FieldTypeEmu.BOMLINE)
	public String alternateItem="";// �滻��
	
	@FieldAnotation(value ="u8_uom",type = FieldTypeEmu.REVISON)
	public String u8Uom="";//��λ kg g ml ��
	
	@FieldAnotation(value="U8_minmaterial",type = FieldTypeEmu.BOMLINE)
	public String minMaterialType="";//С������
	
	@FieldAnotation(value = "U8_orderinventory",type = FieldTypeEmu.BOMLINE)
	public String orderInventory="";//��������Ͷ����

	public boolean canReplace;//�Ƿ�����滻
	
	@FieldAnotation(value="U8_COMPONENTTYPE",type = FieldTypeEmu.BOMLINE)
	public String componentType="";//��ֵĲ�������
	
	@FieldAnotation(value = "U8_COMPONENTVALUE",type=FieldTypeEmu.BOMLINE)
	public String componentValue="";//��ֵĲ����ֵ
	
	@FieldAnotation(value = "u8_water",type=FieldTypeEmu.REVISON)
	public String u8Water="";//�汾�ϵĺ�ˮ������
	
	@FieldAnotation(value = "u8_isNutrition",type=FieldTypeEmu.REVISON)
	public String u8IsNutrition="";//�ж�һ��ԭ���Ƿ���Ӫ��
	
	@FieldAnotation(value = "u8_isBaseFormulator",type=FieldTypeEmu.REVISON)
	public String u8IsBaseFormulator="";//�ж�һ���䷽�Ƿ��ǻ���
	
	@FieldAnotation("U8_SystemID")
	public String systemId;//��ϵID
	
	@FieldAnotation("U8_AssociationID")
	public String relatedSystemId;//������ϵID
	
	@FieldAnotation("U8_SystemNameNote")
	public String systemNameNote;//��ϵ���Ʊ�ע
	
	
	@FieldAnotation("U8_indexrequirment")
	public String indicatorRequire;//ָ��Ҫ��
	
	
	
	
	public String getIsbacteria() {
		return isbacteria;
	}

	public void setIsbacteria(String isbacteria) {
		this.isbacteria = isbacteria;
	}
	
	public String getSourceOfCompound() {
		return sourceOfCompound;
	}

	public void setSourceOfCompound(String sourceOfCompound) {
		this.sourceOfCompound = sourceOfCompound;
	}

	public String getIndicatorRequire() {
		return indicatorRequire;
	}

	public void setIndicatorRequire(String indicatorRequire) {
		this.indicatorRequire = indicatorRequire;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getRelatedSystemId() {
		return relatedSystemId;
	}

	public void setRelatedSystemId(String relatedSystemId) {
		this.relatedSystemId = relatedSystemId;
	}

	public String getSystemNameNote() {
		return systemNameNote;
	}

	public void setSystemNameNote(String systemNameNote) {
		this.systemNameNote = systemNameNote;
	}

	
	public String getComponentType() {
		return componentType;
	}

	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}

	public String getComponentValue() {
		return componentValue;
	}

	public void setComponentValue(String componentValue) {
		this.componentValue = componentValue;
	}

	public String getU8Uom() {
		return u8Uom;
	}

	public void setU8Uom(String u8Uom) {
		this.u8Uom = u8Uom;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSuppplier() {
		return suppplier;
	}

	public void setSuppplier(String suppplier) {
		this.suppplier = suppplier;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getU8_inventory() {
		return U8_inventory;
	}

	public void setU8_inventory(String u8_inventory) {
		U8_inventory = u8_inventory;
	}

	public String getBl_quantity() {
		return bl_quantity;
	}

	public void setBl_quantity(String bl_quantity) {
		this.bl_quantity = bl_quantity;
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getWarning() {
		return warning;
	}

	public void setWarning(String warning) {
		this.warning = warning;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLawName() {
		return lawName;
	}

	public void setLawName(String lawName) {
		this.lawName = lawName;
	}

	public String getAlternate() {
		return alternate;
	}

	public void setAlternate(String alternate) {
		this.alternate = alternate;
	}

	public String getGroupItem() {
		return groupItem;
	}

	public void setGroupItem(String groupItem) {
		this.groupItem = groupItem;
	}

	public String getAlternateItem() {
		return alternateItem;
	}

	public void setAlternateItem(String alternateItem) {
		this.alternateItem = alternateItem;
	}

	public String getMinMaterialType() {
		return minMaterialType;
	}

	public void setMinMaterialType(String minMaterialType) {
		this.minMaterialType = minMaterialType;
	}

	public String getOrderInventory() {
		return orderInventory;
	}

	public void setOrderInventory(String orderInventory) {
		this.orderInventory = orderInventory;
	}

	

	public String getProductMethod() {
		return productMethod;
	}

	public void setProductMethod(String productMethod) {
		this.productMethod = productMethod;
	}

	public String getUpSymbol() {
		return upSymbol;
	}

	public void setUpSymbol(String upSymbol) {
		this.upSymbol = upSymbol;
	}

	public String getDownSymbol() {
		return downSymbol;
	}

	public void setDownSymbol(String downSymbol) {
		this.downSymbol = downSymbol;
	}

	public String getDetectValue() {
		return detectValue;
	}

	public void setDetectValue(String detectValue) {
		this.detectValue = detectValue;
	}

	public String getUom_tag() {
		return uom_tag;
	}

	public void setUom_tag(String uom_tag) {
		this.uom_tag = uom_tag;
	}

	public String getItemID() {
		return itemID;
	}

	public void setItemID(String itemID) {
		this.itemID = itemID;
	}

	public String getU8Up() {
		return u8Up;
	}

	public void setU8Up(String u8Up) {
		this.u8Up = u8Up;
	}

	public String getU8Down() {
		return u8Down;
	}

	public void setU8Down(String u8Down) {
		this.u8Down = u8Down;
	}

	public String getSampleName() {
		return sampleName;
	}

	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}
	
	@Override
	public String toString() {
		return objectName;
	}
	
	
	
	//�ýڵ����޺��ӽڵ�
	public boolean hasChild(){
		if(childNodeBeans == null || childNodeBeans.size()==0){
			return false;//û�к���
		}
		
		return true;
	}
	
	//���صڼ������ӽڵ�
	public NodeBean getChildAt(int position){
		if(childNodeBeans == null || childNodeBeans.size()<= position){
			return null;
		}
		return childNodeBeans.get(position);
	}
	
	//���غ��ӽڵ�ĸ���
	public int getChildCount(){
		if(childNodeBeans == null || childNodeBeans.size() == 0){
			return 0;
		}
		return childNodeBeans.size();
	}

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

	public String getU8Water() {
		return u8Water;
	}

	public void setU8Water(String u8Water) {
		this.u8Water = u8Water;
	}

	public String getU8IsNutrition() {
		return u8IsNutrition;
	}

	public void setU8IsNutrition(String u8IsNutrition) {
		this.u8IsNutrition = u8IsNutrition;
	}

	public String getU8IsBaseFormulator() {
		return u8IsBaseFormulator;
	}

	public void setU8IsBaseFormulator(String u8IsBaseFormulator) {
		this.u8IsBaseFormulator = u8IsBaseFormulator;
	}
	
	

}
