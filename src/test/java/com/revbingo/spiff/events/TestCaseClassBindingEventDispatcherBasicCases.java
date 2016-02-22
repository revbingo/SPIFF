/*******************************************************************************
 * Copyright 2012 Mark Piper
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.revbingo.spiff.events;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.annotations.Binding;
import com.revbingo.spiff.datatypes.ByteInstruction;
import com.revbingo.spiff.datatypes.Datatype;
import com.revbingo.spiff.datatypes.FixedLengthString;
import com.revbingo.spiff.datatypes.IntegerInstruction;

public class TestCaseClassBindingEventDispatcherBasicCases {

	ClassBindingEventListener<RootBinding> unit;

	@Before
	public void setUp() {
		unit = new ClassBindingEventListener<RootBinding>(RootBinding.class);
	}

	@Test
	public void createsRootClassInstanceOnConstructionAndReturnsCorrectType() throws Exception {
		RootBinding binding = unit.getResult();

		assertThat(binding, is(notNullValue()));
	}

	@Test(expected=ExecutionException.class)
	public void executionExceptionThrownIfClassCannotBeInstantiatedWithNoArgs() throws Exception {
		new ClassBindingEventListener<ArgsRequired>(ArgsRequired.class);
	}

	@Test(expected=ExecutionException.class)
	public void executionExceptionThrownIfClassHasPrivateConstructor() throws Exception {
		new ClassBindingEventListener<Private>(Private.class);
	}

	@Test
	public void populatesPublicFieldWhenInstructionNameMatchesField() throws Exception {
		Datatype ri = new ByteInstruction();
		ri.setName("byteOne");
		ri.setValue((byte) 3);

		unit.notifyData(ri);

		RootBinding binding = unit.getResult();
		assertThat(binding, is(notNullValue()));
		assertThat(binding.byteOne, is((byte) 3));
	}

	@Test
	public void populatesPrivateFieldUsingPublicSetterWhenInstructionNameMatchesSetter() throws Exception {
		Datatype ri = new ByteInstruction();
		ri.setName("privateByte");
		ri.setValue((byte) 3);

		unit.notifyData(ri);

		RootBinding binding = unit.getResult();
		assertThat(binding.getPrivateByte(), is((byte) 3));
	}

	@Test(expected=ExecutionException.class)
	public void exceptionThrownIfSecurityManagerPreventsFieldBeingSet() throws Exception {
		Datatype ri = new ByteInstruction();
		ri.setName("byteOne");
		ri.setValue((byte) 3);

		SecurityManager oldManager = System.getSecurityManager();
		System.setSecurityManager(new SecurityManager() {

			@Override
			public void checkPermission(Permission perm) {
				if(perm.getName().equals("suppressAccessChecks")) throw new SecurityException();
			}
		});

		try {
			unit.notifyData(ri);
		} catch(SecurityException e) {
			fail("SecurityException should not get this far");
		} finally {
			System.setSecurityManager(oldManager);
		}
	}

	@Test(expected=ExecutionException.class)
	public void exceptionThrownIfNoSuchFieldAndStrictMode() throws Exception {
		unit.isStrict(true);
		ByteInstruction inst = new ByteInstruction();
		inst.setName("nonExistant");
		unit.notifyData(inst);
	}

	@Test
	public void noExceptionThrownIfNoSuchFieldAndInNonStrictMode() throws Exception {
		unit.isStrict(false);
		ByteInstruction inst = new ByteInstruction();
		inst.setName("nonExistant");
		unit.notifyData(inst);

		assertThat(unit.getResult(), is(notNullValue()));
	}

	@Test
	public void dispatchesToNamedSetterIfNoSuchField() throws Exception {
		Datatype ri = new ByteInstruction();
		ri.setName("setterWithADifferentName");
		ri.setValue(Byte.valueOf((byte) 3));
		unit.notifyData(ri);

		assertThat(unit.getResult().gotASetter, is((byte) 3));
	}

	@Test(expected=ExecutionException.class)
	public void exceptionThrownIfWrongType() throws Exception {
		Datatype ri = new FixedLengthString("US-ASCII");
		ri.setName("byteOne");
		ri.setValue("oops");
		unit.notifyData(ri);
	}

	@Test
	public void finalFieldsCanBeAltered() throws Exception {
		Datatype ri = new ByteInstruction();
		ri.setName("finalByte");
		ri.setValue(Byte.valueOf((byte) 3));
		unit.notifyData(ri);

		assertThat(unit.getResult().finalByte, is((byte) 3));
	}

	@Test(expected=ExecutionException.class)
	public void exceptionThrownIfMethodThrowsException() throws Exception {
		Datatype ri = new ByteInstruction();
		ri.setName("exceptionThrower");
		ri.setValue((byte) 3);
		unit.notifyData(ri);
	}

	@Test
	public void canMakePrivateFieldAccessible() throws Exception {
		Datatype ri = new ByteInstruction();
		ri.setName("notSoPrivateByte");
		ri.setValue((byte) 3);
		unit.notifyData(ri);

		RootBinding binding = unit.getResult();
		assertThat(binding.getNotSoPrivateByte(), is((byte) 3));
	}

	@Test
	public void fieldWithBindingAnnotationIsCalled() throws Exception {
		Datatype ri = new ByteInstruction();
		ri.setName("boundField");
		ri.setValue((byte) 3);
		unit.notifyData(ri);

		RootBinding binding = unit.getResult();
		assertThat(binding.notTheSameName, is((byte) 3));
	}

	@Test
	public void collectionTypesArePopulated() throws Exception {
		Datatype ri = new FixedLengthString("US-ASCII");
		ri.setName("stringList");

		for(int i=0;i<3;i++) {
			ri.setValue("inst" + i);
			unit.notifyData(ri);
		}

		RootBinding binding = unit.getResult();
		assertThat(binding.stringList.size(), is(3));
		assertThat(binding.stringList.get(0), is("inst0"));
		assertThat(binding.stringList.get(1), is("inst1"));
		assertThat(binding.stringList.get(2), is("inst2"));
	}

	@Test
	public void setInterfaceHasConcreteImplementationMapped() throws Exception {
		Datatype ri = new FixedLengthString("US-ASCII");
		ri.setName("aSet");
		ri.setValue("one");

		unit.notifyData(ri);

		RootBinding binding = unit.getResult();
		assertThat(binding.aSet.size(), is(1));
	}

	@Test
	public void queueInterfaceHasConcreteImplementationMapped() throws Exception {
		Datatype ri = new FixedLengthString("US-ASCII");
		ri.setName("aQueue");
		ri.setValue("one");

		unit.notifyData(ri);

		RootBinding binding = unit.getResult();
		assertThat(binding.aQueue.size(), is(1));
	}

	@Test
	public void collectionsWorkWithRenamedBinding() throws Exception {
		Datatype ri = new FixedLengthString("US-ASCII");
		ri.setName("notTheList");

		for(int i=0;i<3;i++) {
			ri.setValue("inst" + i);
			unit.notifyData(ri);
		}

		RootBinding binding = unit.getResult();
		assertThat(binding.anotherList.size(), is(3));
		assertThat(binding.anotherList.get(0), is("inst0"));
		assertThat(binding.anotherList.get(1), is("inst1"));
		assertThat(binding.anotherList.get(2), is("inst2"));
	}

	@Test
	public void existingCollectionIsAddedTo() {
		Datatype ri = new FixedLengthString("US-ASCII");
		ri.setName("existingStringList");
		ri.setValue("anotherElement");
		unit.notifyData(ri);

		RootBinding binding = unit.getResult();
		assertThat(binding.existingStringList.size(), is(2));
		assertThat(binding.existingStringList.get(0), is("existingElement"));
		assertThat(binding.existingStringList.get(1), is("anotherElement"));
	}

	@Test
	public void strictModeIgnoresUnboundedFields() {
		Datatype ri = new FixedLengthString("US-ASCII");
		ri.setName("existingStringList");
		ri.setValue("anotherElement");
		unit.notifyData(ri);
	}

	@Test
	public void autoboxedTypesAreHandled() {
		Datatype ri = new IntegerInstruction();
		ri.setName("primitiveInt");
		ri.setValue(Integer.valueOf(10));

		unit.notifyData(ri);

		RootBinding binding = unit.getResult();
		assertThat(binding, is(notNullValue()));
		assertThat(binding.primitiveInt, is(10));
	}


	@Test(expected=ExecutionException.class)
	public void exceptionThrownIfGroupInstructionUsedForPrimitiveField() throws Exception {
		unit.notifyGroup("stringList", true);
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

		public Set<String> aSet;
		public Queue<String> aQueue;

		public int primitiveInt;

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

}
