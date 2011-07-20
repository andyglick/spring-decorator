package com.schlimm.decorator.resolver;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.decorator.Decorator;
import javax.decorator.Delegate;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

@SuppressWarnings("rawtypes")
public class DecoratorInfo {

	private Class decoratorClass;

	private BeanDefinitionHolder decoratorBeanDefinitionHolder;

	private List<DelegateField> delegateFields;

	public static boolean isDecorator(Class candidate) {
		return AnnotationUtils.findAnnotation(candidate, Decorator.class) != null;
	}

	public DecoratorInfo(String beanName, BeanDefinition beanDefinition, Class decoratorClass) {
		this.decoratorClass = decoratorClass;
		decoratorBeanDefinitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);
		delegateFields = new ArrayList<DelegateField>();
		ReflectionUtils.doWithFields(decoratorClass, new FieldCallback() {
			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				if (field.isAnnotationPresent(Delegate.class)) {
					delegateFields.add(new DelegateField(field, new DelegateDependencyDescriptor(field, false)));
				}
			}
		});
	}

	public boolean equals(DecoratorInfo otherDecoratorInfo) {
		return this.decoratorClass.equals(otherDecoratorInfo.getDecoratorClass());
	}

	public Class getDecoratorClass() {
		return decoratorClass;
	}

	public void setDecoratorClass(Class decoratorClass) {
		this.decoratorClass = decoratorClass;
	}

	public void setDecoratorBeanDefinitionHolder(BeanDefinitionHolder beanDefinitionHolder) {
		this.decoratorBeanDefinitionHolder = beanDefinitionHolder;
	}

	public BeanDefinitionHolder getDecoratorBeanDefinitionHolder() {
		return decoratorBeanDefinitionHolder;
	}

	public Set<DelegateDependencyDescriptor> getAllDelegateDependencyDescriptors() {
		Set<DelegateDependencyDescriptor> descs = new HashSet<DelegateDependencyDescriptor>();
		for (DelegateField delegateField : delegateFields) {
			descs.add(delegateField.getDependencyDescriptor());
		}
		return descs;
	}

	public List<DelegateField> getDelegateFields() {
		return delegateFields;
	}
	
	public List<Field> getDeclaredDelegateFields() {
		List<Field> fields = new ArrayList<Field>();
		for (DelegateField delegateField : delegateFields) {
			fields.add(delegateField.getDeclaredField());
		}
		return fields;
	}

	public void setDelegateFields(List<DelegateField> delegateFields) {
		this.delegateFields = delegateFields;
	}

}