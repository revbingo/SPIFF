package com.revbingo.spiff.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface BindingCollection {

	String value();
	Class<?> type();
}
