package com.team2073.common.inject;

import java.lang.reflect.Field;

import com.google.inject.MembersInjector;
import com.google.inject.Provider;

public class InjectNamedMembersInjector<T> implements MembersInjector<T> {
	private final Field field;
	private final Provider<?> provider;

	public InjectNamedMembersInjector(Field field, Provider<?> provider) {
		this.field = field;
		this.provider = provider;
		field.setAccessible(true);
	}

	@Override
	public void injectMembers(T instance) {
		try {
			field.set(instance, provider.get());
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
