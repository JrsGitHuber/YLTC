package com.uds.yl.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.uds.yl.common.FieldTypeEmu;


/**
 * @author GLF
 * Bean�����Ե�ע��һ��Ҫ�е���TYPE�������
 * ���ȸ������Ե�����ʲô����switch:���ڲ��жϴ�����������Դͷ�����ͺ�TYPE�����Ƿ�ƥ��
 * Ĭ��ΪTCCOMPONENT�����ԣ�����ʾ��ָ��TYPE�Ļ��������в�������ģ���������Value��ֵҲ�ǴӴ��ݽ����Ķ�����ȡֵ
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldAnotation {
	String value() default "object_name";
	FieldTypeEmu type() default FieldTypeEmu.TCCOMPONENT;
}
