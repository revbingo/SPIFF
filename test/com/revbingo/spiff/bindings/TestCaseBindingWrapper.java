package com.revbingo.spiff.bindings;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import com.revbingo.spiff.ExecutionException;

public class TestCaseBindingWrapper {

	@Test
	public void addBindingPutsBindingInMap() {
		BindingWrapper unit = new DummyBindingWrapper();
		
		BindingWrapper otherWrapper = new DummyBindingWrapper();
		
		unit.addBinding("myBinding", otherWrapper);
		
		assertThat(unit.getBindingFor("myBinding"), is(otherWrapper));
	}
	
	@Test
	public void getBindingReturnsNullIfNoBindingsAddedYet() {
		BindingWrapper unit = new DummyBindingWrapper();
		
		assertNull(unit.getBindingFor("someBinding"));
	}
	
	@Test
	public void hasBindingReturnsFalseIfNoBindingsAddedYet() {
		BindingWrapper unit = new DummyBindingWrapper();
		
		assertFalse(unit.hasBindingFor("someBinding")); 
	}
	
	private class DummyBindingWrapper extends BindingWrapper {

		@Override
		public void attachTo(BindingWrapper container)
				throws ExecutionException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void attach(BindingWrapper child) throws ExecutionException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setValue(Object value) throws ExecutionException {
			throw new UnsupportedOperationException();
		}

		@Override
		public Class<?> getType() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void instantiate() throws ExecutionException {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object getBoundObject() {
			throw new UnsupportedOperationException();
		}

		@Override
		public BindingWrapper createFromTemplate() {
			throw new UnsupportedOperationException();
		}
		
	}

}