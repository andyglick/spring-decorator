package com.schlimm.decorator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.util.ClassUtils;

import com.schlimm.decorator.resolver.DecoratorInfo;
import com.schlimm.decorator.resolver.DelegateField;
import com.schlimm.decorator.resolver.descriptorrules.DelegateDependencyDescriptorTag;
import com.schlimm.decorator.resolver.descriptorrules.DescriptorRuleUtils;

public class SimpleDelegateResolutionStrategy implements DelegateResolutionStrategy {

	@SuppressWarnings({ "rawtypes", "unused" })
	@Override
	public String getRegisteredDelegate(ConfigurableListableBeanFactory beanFactory, DecoratorInfo decoratorInfo) {
		DelegateField arbitraryDelegateField = decoratorInfo.getDelegateFields().get(0);
		DependencyDescriptor desc = DescriptorRuleUtils.createRuleBasedDescriptor(arbitraryDelegateField.getDeclaredField(), new Class[] { DelegateDependencyDescriptorTag.class});
		String[] bdNames = beanFactory.getBeanNamesForType(arbitraryDelegateField.getDeclaredField().getType(), true, false);
		List<String> registeredDelegates = new ArrayList<String>();
		for (String bdName : bdNames) {
			BeanDefinition bd = beanFactory.getBeanDefinition(bdName);
			// Annotierte Bean aus dem Classpath
			if (bd instanceof AnnotatedBeanDefinition) {
				AnnotatedBeanDefinition abd = (AnnotatedBeanDefinition) bd;
				// Kein @Decorator
				if (!DecoratorInfo.isDecorator(abd)) {
					Class decoratorClass = null;
					try {
						decoratorClass = ClassUtils.forName(abd.getBeanClassName(), this.getClass().getClassLoader());
					} catch (Exception e) {
						throw new DecoratorAwareBeanFactoryPostProcessorException("Could not find decorator class: " + abd.getBeanClassName(), e);
					} 
					// Wenn es ein target object ist, dann die proxy bean definition ziehen, die auch als delegate injected werden soll
					if (bdName.startsWith("scopedTarget.")) {
						bdName = bdName.replace("scopedTarget.", "");
					}
					// Die aktuelle bean definition muss auf den delegate dependency descriptor passen
					if ((((DefaultListableBeanFactory) beanFactory).isAutowireCandidate(bdName, desc))) {
						registeredDelegates.add(bdName);
					}
				}
			}
		}
		if (registeredDelegates.size()>1) {
			throw new DecoratorAwareBeanFactoryPostProcessorException("Could not find unique delegate for decorator info: " + decoratorInfo.toString());
		}
		return registeredDelegates.get(0);
	}


}
