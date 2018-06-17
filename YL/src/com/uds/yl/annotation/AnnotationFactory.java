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
					continue;//如果没有注解就跳过
				}
				fieldName = annotation.value();
				typeEmu = annotation.type();
				switch (typeEmu.getTypeCode()) {
				case 0://TCComponent
					String property = component.getProperty(fieldName);
					setMethod.invoke(t, property);//默认的话就直接component本身取值
					break;
				case 1://TCComponentItem
					if(component instanceof TCComponentBOMLine){
						property = ((TCComponentBOMLine)component).getItem().getProperty(fieldName);
						setMethod.invoke(t, property);
					}else if(component instanceof TCComponentItem){//要的就是item的属性
						property = component.getProperty(fieldName);
						setMethod.invoke(t, property);
					}else if(component instanceof TCComponentItemRevision){//传递的对象是ItemRevision
						property = ((TCComponentItemRevision)component).getItem().getProperty(fieldName);
						setMethod.invoke(t, property);
					}
					break;
				case 2://TCComponetnItemRevision
					if(component instanceof TCComponentBOMLine){
						property = ((TCComponentBOMLine)component).getItemRevision().getProperty(fieldName);
						setMethod.invoke(t, property);
					}else if(component instanceof TCComponentItemRevision){//要的就是版本的属性
						property = component.getProperty(fieldName);
						setMethod.invoke(t, property);
					}else if(component instanceof TCComponentItem){//传递的是Item对象
						property = ((TCComponentItem)component).getLatestItemRevision().getProperty(fieldName);
						setMethod.invoke(t, property);
					}
					break;
				case 3://TCComponentBomLine
					if(component instanceof TCComponentBOMLine){//如果传递的是BOMLine对象就取BOMLine的值
						property = component.getProperty(fieldName);
						setMethod.invoke(t, property);
					}
					
					break;
				default:
					break;
				}
				
				
			}catch(NullPointerException e){
				
			}catch (TCException e) {
//				logger.error("获取属性异常", e);
			} catch (IllegalArgumentException e) {
//				logger.error("set方法非法参数异常", e);
			} catch (InvocationTargetException e) {
//				logger.error("反射方法执行异常", e);
			} 
			
		}
		return t;
		
	}
	
	
	
	//将一个对象写到属性TC对象的属性中去
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
					continue;//如果没有注解就跳过
				}
				fieldName = annotation.value();
				typeEmu = annotation.type();
				String str = String.valueOf(getMethod.invoke(t));
				switch (typeEmu.getTypeCode()) {
				case 0://TCComponent
					component.setProperty(fieldName,str);//默认的话就直接component本身取值
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
//				logger.error("获取属性异常", e);
			} catch (IllegalArgumentException e) {
//				logger.error("set方法非法参数异常", e);
			} catch (InvocationTargetException e) {
//				logger.error("反射方法执行异常", e);
			}
		}
	}
}
