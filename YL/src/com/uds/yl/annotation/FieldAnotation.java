package com.uds.yl.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.uds.yl.common.FieldTypeEmu;


/**
 * @author GLF
 * Bean的属性的注解一定要有的是TYPE这个属性
 * 首先根据属性的类型什么进入switch:在内部判断传进来的属性源头的类型和TYPE类型是否匹配
 * 默认为TCCOMPONENT很明显，不显示的指名TYPE的话，工厂中不作处理的，哪怕是有Value的值也是从传递进来的对象中取值
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldAnotation {
	String value() default "object_name";
	FieldTypeEmu type() default FieldTypeEmu.TCCOMPONENT;
}
