package com.revbingo.spiff.events;

import java.security.Permission;

import org.junit.Test;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.annotations.Binding;
import com.revbingo.spiff.instructions.ByteInstruction;
import com.revbingo.spiff.instructions.FixedLengthNumberFactory;
import com.revbingo.spiff.instructions.FixedLengthString;
import com.revbingo.spiff.instructions.ReferencedInstruction;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class TestCaseClassBindingEventDispatcher {

	@Test
	public void createsRootClassInstanceOnConstructionAndReturnsCorrectType() throws Exception {
		ClassBindingEventDispatcher<RootBinding> unit = new ClassBindingEventDispatcher<RootBinding>(RootBinding.class);
	
		RootBinding binding = unit.getBoundValue();
		
		assertThat(binding, is(notNullValue()));
	}
	
	@Test(expected=ExecutionException.class)
	public void executionExceptionThrownIfClassCannotBeInstantiatedWithNoArgs() throws Exception {
		new ClassBindingEventDispatcher<ArgsRequired>(ArgsRequired.class);
	}
	
	@Test(expected=ExecutionException.class)
	public void executionExceptionThrownIfClassHasPrivateConstructor() throws Exception {
		new ClassBindingEventDispatcher<Private>(Private.class);
	}
	
	@Test
	public void populatesPublicFieldWhenInstructionNameMatchesField() throws Exception {
		ClassBindingEventDispatcher<RootBinding> unit = new ClassBindingEventDispatcher<RootBinding>(RootBinding.class);
		
		ReferencedInstruction ri = new ByteInstruction("byteOne");
		ri.value = (byte) 3;
		
		unit.notifyData(ri);
		
		RootBinding binding = unit.getBoundValue();
		assertThat(binding, is(notNullValue()));
		assertThat(binding.byteOne, is((byte) 3));
	}
	
	@Test
	public void populatesPrivateFieldUsingPublicSetterWhenInstructionNameMatchesSetter() throws Exception {
		ClassBindingEventDispatcher<RootBinding> unit = new ClassBindingEventDispatcher<RootBinding>(RootBinding.class);
		
		ReferencedInstruction ri = new ByteInstruction("privateByte");
		ri.value = (byte) 3;
		
		unit.notifyData(ri);
		
		RootBinding binding = unit.getBoundValue();
		assertThat(binding.getPrivateByte(), is((byte) 3));
	}
	
	@Test(expected=ExecutionException.class)
	public void exceptionThrownIfSecurityManagerPreventsFieldBeingSet() throws Exception {
		ClassBindingEventDispatcher<RootBinding> unit = new ClassBindingEventDispatcher<RootBinding>(RootBinding.class);
		
		ReferencedInstruction ri = new ByteInstruction("byteOne");
		ri.value = (byte) 3;
	
		SecurityManager oldManager = System.getSecurityManager();
		System.setSecurityManager(new SecurityManager() {
			
			@Override
			public void checkPermission(Permission perm) {
			}

			@Override
			public void checkMemberAccess(Class<?> clazz, int which) {
				throw new SecurityException();
			}
		});

		try {
			unit.notifyData(ri);
		} finally {
			System.setSecurityManager(oldManager);
		}
	}
	
	@Test(expected=ExecutionException.class)
	public void exceptionThrownIfNoSuchField() throws Exception {
		ClassBindingEventDispatcher<RootBinding> unit = new ClassBindingEventDispatcher<RootBinding>(RootBinding.class);
		unit.notifyData(new ByteInstruction("nonExistant"));
	}
	
	@Test(expected=ExecutionException.class)
	public void exceptionThrownIfWrongType() throws Exception {
		ClassBindingEventDispatcher<RootBinding> unit = new ClassBindingEventDispatcher<RootBinding>(RootBinding.class);
		ReferencedInstruction ri = new FixedLengthString();
		ri.name = "byteOne";
		ri.value = "oops";
		unit.notifyData(ri);
	}
	
	@Test
	public void finalFieldsCanBeAltered() throws Exception {
		ClassBindingEventDispatcher<RootBinding> unit = new ClassBindingEventDispatcher<RootBinding>(RootBinding.class);
		ReferencedInstruction ri = new ByteInstruction("finalByte");
		ri.value = new Byte((byte) 3);
		unit.notifyData(ri);
		
		assertThat(unit.getBoundValue().finalByte, is((byte) 3));
	}
	
	@Test(expected=ExecutionException.class)
	public void exceptionThrownIfMethodThrowsException() throws Exception {
		ClassBindingEventDispatcher<RootBinding> unit = new ClassBindingEventDispatcher<RootBinding>(RootBinding.class);
		ReferencedInstruction ri = new ByteInstruction("exceptionThrower");
		ri.value = (byte) 3;
		unit.notifyData(ri);
	}
	
	@Test
	public void canMakePrivateFieldAccessible() throws Exception {
		ClassBindingEventDispatcher<RootBinding> unit = new ClassBindingEventDispatcher<RootBinding>(RootBinding.class);
		ReferencedInstruction ri = new ByteInstruction("notSoPrivateByte");
		ri.value = (byte) 3;
		unit.notifyData(ri);
		
		RootBinding binding = unit.getBoundValue();
		assertThat(binding.getNotSoPrivateByte(), is((byte) 3));
	}
	
	@Test
	public void fieldWithBindingAnnotationIsCalled() throws Exception {
		ClassBindingEventDispatcher<RootBinding> unit = new ClassBindingEventDispatcher<RootBinding>(RootBinding.class);
		ReferencedInstruction ri = new ByteInstruction("boundField");
		ri.value = (byte) 3;
		unit.notifyData(ri);
		
		RootBinding binding = unit.getBoundValue();
		assertThat(binding.notTheSameName, is((byte) 3));
	}
	
	public static class RootBinding {
		public byte byteOne;
		//need to use Byte because primtive byte would get inlined, so test assertion fails
		public final Byte finalByte = 1;
		private byte privateByte;
		private byte publicByte;
		private byte notSoPrivateByte;
		
		@Binding("boundField")
		public byte notTheSameName;
		
		public void setPrivateByte(byte b) {
			privateByte = b;
		}
		
		public byte getPrivateByte() {
			return privateByte;
		}
		
		public byte getNotSoPrivateByte() {
			return notSoPrivateByte;
		}
		
		public void setPublicByte(byte b) {
			publicByte = b;
		}
		
		public byte getPublicByte() {
			return publicByte;
		}
		
		public void setExceptionThrower(byte b) {
			throw new RuntimeException();
		}
		
	}
	
	public class ArgsRequired {
		public ArgsRequired(String arg) {}
	}
	
	public static class Private {
		private Private() {}
	}

	
}
