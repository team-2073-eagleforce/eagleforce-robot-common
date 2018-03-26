package com.team2073.common.inject;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.lang.reflect.Field;

public class InjectNamedTypeListener implements TypeListener {
	@Override
	public <T> void hear(TypeLiteral<T> type, TypeEncounter<T> encounter) {
		Class<?> clazz = type.getRawType();
		while (clazz != null) {
			for (Field field : clazz.getDeclaredFields()) {
				InjectNamed annotation = field.getAnnotation(InjectNamed.class);
				if (annotation == null) {
					continue;
				}
				String name = annotation.value();
				if (name.isEmpty()) {
					name = field.getName();
				}
				Provider<?> provider = encounter.getProvider(Key.get(field.getType(), Names.named(name)));
				encounter.register(new InjectNamedMembersInjector<T>(field, provider));
			}
			clazz = clazz.getSuperclass();
		}
	}
}
