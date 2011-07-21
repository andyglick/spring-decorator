package com.schlimm.decorator.resolver.longtwochains;

import java.lang.reflect.Field;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.schlimm.decorator.DecoratorAwareBeanFactoryPostProcessor;
import com.schlimm.decorator.SimpleDecoratorResolutionStrategy;
import com.schlimm.decorator.SimpleDelegateResolutionStrategy;
import com.schlimm.decorator.resolver.DelegateAwareAutowireCandidateResolver;
import com.schlimm.decorator.resolver.QualifiedDecoratorChain;
import com.schlimm.decorator.resolver.SimpleCDIAutowiringRules;

@ContextConfiguration("/test-context-decorator-resolver-long-two-chains.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class DelegateAwareAutowireCandidateResolverTest {

	@Autowired
	private ConfigurableListableBeanFactory beanFactory;

	private DecoratorAwareBeanFactoryPostProcessor beanPostProcessor;
	
	private DelegateAwareAutowireCandidateResolver resolver;

	private SomeTestBean someTestBean = new SomeTestBean();

	private QualifiedDecoratorChain anotherChain;
	
	private Field autowiredInjectionPoint;
	
	private DependencyDescriptor autowiredInjectionPointDependencyDescriptor;
	
	private BeanDefinitionHolder firstDecoratorBeanDef;

	@Before
	public void setUp() {
		beanPostProcessor = new DecoratorAwareBeanFactoryPostProcessor(new SimpleDecoratorResolutionStrategy(), new SimpleDelegateResolutionStrategy());
		beanPostProcessor.postProcessBeanFactory(beanFactory);
		resolver = (DelegateAwareAutowireCandidateResolver) ((DefaultListableBeanFactory) beanFactory).getAutowireCandidateResolver();
		List<QualifiedDecoratorChain> chains = ((SimpleCDIAutowiringRules)resolver.getCdiAutowiringRules()).getDecoratorChains();
		// QualifiedDecoratorChain chainMy= chains.get(0).getDelegateBeanDefinitionHolder().getBeanName().equals("myDelegate") ?
		// chains.get(0) : chains.get(1);
		try {
			autowiredInjectionPoint = someTestBean.getClass().getDeclaredField("decoratedInterface");
		} catch (SecurityException e) {
			TestCase.fail(e.getMessage());
		} catch (NoSuchFieldException e) {
			TestCase.fail(e.getMessage());
		}
		anotherChain = chains.get(1).getDelegateBeanDefinitionHolder().getBeanName().equals("anotherDelegate") ? chains.get(1) : chains.get(0);
		firstDecoratorBeanDef = anotherChain.getDecorators().get(0).getDecoratorBeanDefinitionHolder();
		autowiredInjectionPointDependencyDescriptor = new DependencyDescriptor(autowiredInjectionPoint, true);
	}

	@Test
	public void testAutowiringCandidateResolving_AnotherDelegateIsCandidateForLastAnotherDecorator() {
		Assert.isTrue(resolver.isAutowireCandidate(firstDecoratorBeanDef, autowiredInjectionPointDependencyDescriptor));
	}

}
