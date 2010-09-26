package com.revbingo.spiff.binders;

import com.revbingo.spiff.ExecutionException;

public interface Binder {

	void bind(Object target, Object value) throws ExecutionException;
}
