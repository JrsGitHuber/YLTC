package com.uds.yl.annotation;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.uds.yl.common.FieldTypeEmu;



public class AnnotationFactory {
	
	public static <T> T getInstcnce(Class<T> cls,TCComponent component) throws InstantiationException, IllegalAccessException{
		T t = (T) cls.newInstance();
		Field[] fields = cls.getFields();
		Method[] methods = cls.getMethods();
		List<String> methodList = new ArrayList<>(); 
		Map<String, Method> methodMap = new HashMap<>();
		for(Method method : methods){
			methodList.add(method.getName());
			methodMap.put(method.getName(), method);
		}
		
		for(Field field : fields){
			String fieldName = field.getName();
			FieldTypeEmu typeEmu;
			
			String getFieldName = "get"+fieldName;
			String setFieldName = "set"+fieldName;
			Method setMethod = null;
			for(Method method : methods){
				String methodName = method.getName();
				if(setFieldName.equalsIgnoreCase(method.getName())){
					setMethod = method;
				}
			}
			try {
				FieldAnotation annotation = field.getAnnotation(FieldAnotation.class);
				if(annotation==null){
					continue;//���û��ע�������
				}
				fieldName = annotation.value();
				typeEmu = annotation.type();
				switch (typeEmu.getTypeCode()) {
				case 0://TCComponent
					String property = component.getProperty(fieldName);
					setMethod.invoke(t, property);//Ĭ�ϵĻ���ֱ��component����ȡֵ
					break;
				case 1://TCComponentItem
					if(component instanceof TCComponentBOMLine){
						property = ((TCComponentBOMLine)component).getItem().getProperty(fieldName);
						setMethod.invoke(t, property);
					}else if(component instanceof TCComponentItem){//Ҫ�ľ���item������
						property = component.getProperty(fieldName);
						setMethod.invoke(t, property);
					}else if(component instanceof TCComponentItemRevision){//���ݵĶ�����ItemRevision
						property = ((TCComponentItemRevision)component).getItem().getProperty(fieldName);
						setMethod.invoke(t, property);
					}
					break;
				case 2://TCComponetnItemRevision
					if(component instanceof TCComponentBOMLine){
						property = ((TCComponentBOMLine)component).getItemRevision().getProperty(fieldName);
						setMethod.invoke(t, property);
					}else if(component instanceof TCComponentItemRevision){//Ҫ�ľ��ǰ汾������
						property = component.getProperty(fieldName);
						setMethod.invoke(t, property);
					}else if(component instanceof TCComponentItem){//���ݵ���Item����
						property = ((TCComponentItem)component).getLatestItemRevision().getProperty(fieldName);
						setMethod.invoke(t, property);
					}
					break;
				case 3://TCComponentBomLine
					if(component instanceof TCComponentBOMLine){//������ݵ���BOMLine�����ȡBOMLine��ֵ
						property = component.getProperty(fieldName);
						setMethod.invoke(t, property);
					}
					
					break;
				default:
					break;
				}
				
				
			}catch(NullPointerException e){
				
			}catch (TCException e) {
//				logger.error("��ȡ�����쳣", e);
			} catch (IllegalArgumentException e) {
//				logger.error("set�����Ƿ������쳣", e);
			} catch (InvocationTargetException e) {
//				logger.error("���䷽��ִ���쳣", e);
			} 
			
		}
		return t;
		
	}
	
	
	
	//��һ������д������TC�����������ȥ
	public static <T> void setObjectInTC(T t,TCComponent component) throws InstantiationException, IllegalAccessException{
		Class cls = t.getClass();
		Field[] fields = cls.getFields();
		Method[] methods = cls.getMethods();
		List<String> methodList = new ArrayList<>(); 
		Map<String, Method> methodMap = new HashMap<>();
		for(Method method : methods){
			methodList.add(method.getName());
			methodMap.put(method.getName(), method);
		}
		
		for(Field field : fields){
			String fieldName = field.getName();
			FieldTypeEmu typeEmu;
			
			String getFieldName = "get"+fieldName;
			String setFieldName = "set"+fieldName;
			Method getMethod = null;
			for(Method method : methods){
				String methodName = method.getName();
				if(getFieldName.equalsIgnoreCase(method.getName())){
					getMethod = method;
				}
			}
			try {
				FieldAnotation annotation = field.getAnnotation(FieldAnotation.class);
				if(annotation==null){
					continue;//���û��ע�������
				}
				fieldName = annotation.value();
				typeEmu = annotation.type();
				String str = String.valueOf(getMethod.invoke(t));
				switch (typeEmu.getTypeCode()) {
				case 0://TCComponent
					component.setProperty(fieldName,str);//Ĭ�ϵĻ���ֱ��component����ȡֵ
					break;
				case 1://TCComponentItem
					if(component instanceof TCComponentBOMLine){
						((TCComponentBOMLine)component).getItem().setProperty(fieldName,str);	
					}else if(component instanceof TCComponentItem){
						component.setProperty(fieldName,str);
					}else if(component instanceof TCComponentItemRevision){
						((TCComponentItemRevision)component).getItem().setProperty(fieldName,str);
					}
					break;
				case 2://TCComponetnItemRevision
					if(component instanceof TCComponentBOMLine){
						((TCComponentBOMLine)component).getItemRevision().setProperty(fieldName,str);	
					}else if(component instanceof TCComponentItem){
						((TCComponentItem)component).getLatestItemRevision().setProperty(fieldName,str);
					}else if(component instanceof TCComponentItemRevision){
						component.setProperty(fieldName,str);	
					}
					break;
				case 3://TCComponentBomLine
					if(component instanceof TCComponentBOMLine){
						component.setProperty(fieldName,str);	
					}
				default:
					break;
				}
			} catch (TCException e) {
//				logger.error("��ȡ�����쳣", e);
			} catch (IllegalArgumentException e) {
//				logger.error("set�����Ƿ������쳣", e);
			} catch (InvocationTargetException e) {
//				logger.error("���䷽��ִ���쳣", e);
			}
		}
	}
}
