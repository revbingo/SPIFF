package com.revbingo.spiff.events;

import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.annotations.Binding;
import com.revbingo.spiff.instructions.Block;
import com.revbingo.spiff.instructions.ByteInstruction;
import com.revbingo.spiff.instructions.FixedLengthNumberFactory;
import com.revbingo.spiff.instructions.FixedLengthString;
import com.revbingo.spiff.instructions.ReferencedInstruction;
import com.revbingo.spiff.instructions.RepeatBlock;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class TestCaseClassBindingEventDispatcher {

	ClassBindingEventDispatcher<RootBinding> unit;
	
	@Before
	public void setUp() {
		unit = new ClassBindingEventDispatcher<RootBinding>(RootBinding.class);
	}
	
	@Test
	public void createsRootClassInstanceOnConstructionAndReturnsCorrectType() throws Exception {
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
		ReferencedInstruction ri = new ByteInstruction("byteOne");
		ri.value = (byte) 3;
		
		unit.notifyData(ri);
		
		RootBinding binding = unit.getBoundValue();
		assertThat(binding, is(notNullValue()));
		assertThat(binding.byteOne, is((byte) 3));
	}
	
	@Test
	public void populatesPrivateFieldUsingPublicSetterWhenInstructionNameMatchesSetter() throws Exception {
		ReferencedInstruction ri = new ByteInstruction("privateByte");
		ri.value = (byte) 3;
		
		unit.notifyData(ri);
		
		RootBinding binding = unit.getBoundValue();
		assertThat(binding.getPrivateByte(), is((byte) 3));
	}
	
	@Test(expected=ExecutionException.class)
	public void exceptionThrownIfSecurityManagerPreventsFieldBeingSet() throws Exception {
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
		unit.notifyData(new ByteInstruction("nonExistant"));
	}
	
	@Test
	public void dispatchesToNamedSetterIfNoSuchField() throws Exception {
		ReferencedInstruction ri = new ByteInstruction("setterWithADifferentName");
		ri.value = new Byte((byte) 3);
		unit.notifyData(ri);
		
		assertThat(unit.getBoundValue().gotASetter, is((byte) 3));
	}
	
	@Test(expected=ExecutionException.class)
	public void exceptionThrownIfWrongType() throws Exception {
		ReferencedInstruction ri = new FixedLengthString();
		ri.name = "byteOne";
		ri.value = "oops";
		unit.notifyData(ri);
	}
	
	@Test
	public void finalFieldsCanBeAltered() throws Exception {
		ReferencedInstruction ri = new ByteInstruction("finalByte");
		ri.value = new Byte((byte) 3);
		unit.notifyData(ri);
		
		assertThat(unit.getBoundValue().finalByte, is((byte) 3));
	}
	
	@Test(expected=ExecutionException.class)
	public void exceptionThrownIfMethodThrowsException() throws Exception {
		ReferencedInstruction ri = new ByteInstruction("exceptionThrower");
		ri.value = (byte) 3;
		unit.notifyData(ri);
	}
	
	@Test
	public void canMakePrivateFieldAccessible() throws Exception {
		ReferencedInstruction ri = new ByteInstruction("notSoPrivateByte");
		ri.value = (byte) 3;
		unit.notifyData(ri);
		
		RootBinding binding = unit.getBoundValue();
		assertThat(binding.getNotSoPrivateByte(), is((byte) 3));
	}
	
	@Test
	public void fieldWithBindingAnnotationIsCalled() throws Exception {
		ReferencedInstruction ri = new ByteInstruction("boundField");
		ri.value = (byte) 3;
		unit.notifyData(ri);
		
		RootBinding binding = unit.getBoundValue();
		assertThat(binding.notTheSameName, is((byte) 3));
	}
	
	@Test
	public void collectionTypesArePopulated() throws Exception {
		ReferencedInstruction ri = new FixedLengthString();
		ri.name = "stringList";
		
		for(int i=0;i<3;i++) {
			ri.value = "inst" + i;
			unit.notifyData(ri);
		}
		
		RootBinding binding = unit.getBoundValue();
		assertThat(binding.stringList.size(), is(3));
		assertThat(binding.stringList.get(0), is("inst0"));
		assertThat(binding.stringList.get(1), is("inst1"));
		assertThat(binding.stringList.get(2), is("inst2"));
	}
	
	@Test
	public void collectionsWorkWithRenamedBinding() throws Exception {
		ReferencedInstruction ri = new FixedLengthString();
		ri.name = "notTheList";
		
		for(int i=0;i<3;i++) {
			ri.value = "inst" + i;
			unit.notifyData(ri);
		}
		
		RootBinding binding = unit.getBoundValue();
		assertThat(binding.anotherList.size(), is(3));
		assertThat(binding.anotherList.get(0), is("inst0"));
		assertThat(binding.anotherList.get(1), is("inst1"));
		assertThat(binding.anotherList.get(2), is("inst2"));
	}
	
	@Test(expected=ExecutionException.class)
	public void exceptionIfTryingToInstantiateUnknownInterface() throws Exception {
		ReferencedInstruction ri = new FixedLengthString();
		ri.name = "unknownCollection";
		unit.notifyData(ri);
	}
	
	@Test
	public void existingCollectionIsAddedTo() {
		ReferencedInstruction ri = new FixedLengthString();
		ri.name = "existingStringList";
		ri.value = "anotherElement";
		unit.notifyData(ri);
		
		RootBinding binding = unit.getBoundValue();
		assertThat(binding.existingStringList.size(), is(2));
		assertThat(binding.existingStringList.get(0), is("existingElement"));
		assertThat(binding.existingStringList.get(1), is("anotherElement"));
	}
	
	public static class RootBinding {
		public byte byteOne;
		//need to use Byte because primtive byte would get inlined, so test assertion fails
		public final Byte finalByte = 1;
		private byte privateByte;
		private byte publicByte;
		private byte notSoPrivateByte;
		
		public byte gotASetter;
		
		@Binding("boundField")
		public byte notTheSameName;
		
		public List<String> stringList;
		public List<String> existingStringList = new ArrayList<String>();
		
		@Binding("notTheList")
		public List<String> anotherList;
		
		public UnknownCollection unknownCollection;
		
		public RootBinding() {
			existingStringList.add("existingElement");
		}
		
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
		
		public void setSetterWithADifferentName(byte b) {
			gotASetter = b;
		}
	}
	
	public class ArgsRequired {
		public ArgsRequired(String arg) {}
	}
	
	public static class Private {
		private Private() {}
	}

	public interface UnknownCollection extends Collection {
		
	}
}
