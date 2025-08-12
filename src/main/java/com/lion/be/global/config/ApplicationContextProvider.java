package com.lion.be.global.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {

	private static final AtomicReference<ApplicationContext> contextRef = new AtomicReference<>();

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		contextRef.set(applicationContext);
	}

	public static <T> T getBean(Class<T> beanClass) {
		ApplicationContext context = contextRef.get();
		if (context == null) {
			throw new IllegalStateException("ApplicationContext가 초기화되지 않았습니다.");
		}
		return context.getBean(beanClass);
	}

	public static ApplicationContext getApplicationContext() {
		return contextRef.get();
	}
}
