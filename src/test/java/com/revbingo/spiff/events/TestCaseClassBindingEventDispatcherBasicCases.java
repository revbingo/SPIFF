/*
 * Copyright Mark Piper 2016
 *
 * This file is part of SPIFF.
 *
 * SPIFF is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SPIFF is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SPIFF.  If not, see <http://www.gnu.org/licenses/>.
 */
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
		unit = new ClassBindingEventListener<>(RootBinding.class);
	}

	@Test
	public void createsRootClassInstanceOnConstructionAndReturnsCorrectType() throws Exception {
		RootBinding binding = unit.getResult();

		assertThat(binding, is(notNullValue()));
	}

	@Test(expected=ExecutionException.class)
	public void executionExceptionThrownIfClassCannotBeInstantiatedWithNoArgs() throws Exception {
		new ClassBindingEventListener<>(ArgsRequired.class);
	}

	@Test(expected=ExecutionException.class)
	public void executionExceptionThrownIfClassHasPrivateConstructor() throws Exception {
		new ClassBindingEventListener<>(Private.class);
	}

	@Test
	public void populatesPublicFieldWhenInstructionNameMatchesField() throws Exception {
		Datatype ri = new ByteInstruction("byteOne");
		ri.setValue((byte) 3);

		unit.notifyData(ri);

		RootBinding binding = unit.getResult();
		assertThat(binding, is(notNullValue()));
		assertThat(binding.byteOne, is((byte) 3));
	}

	@Test
	public void populatesPrivateFieldUsingPublicSetterWhenInstructionNameMatchesSetter() throws Exception {
		Datatype ri = new ByteInstruction("privateByte");
		ri.setValue((byte) 3);

		unit.notifyData(ri);

		RootBinding binding = unit.getResult();
		assertThat(binding.getPrivateByte(), is((byte) 3));
	}

	@Test(expected=ExecutionException.class)
	public void exceptionThrownIfSecurityManagerPreventsFieldBeingSet() throws Exception {
		Datatype ri = new ByteInstruction("byteOne");
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
		unit.setStrict(true);
		ByteInstruction inst = new ByteInstruction("nonExistant");
		unit.notifyData(inst);
	}

	@Test
	public void noExceptionThrownIfNoSuchFieldAndInNonStrictMode() throws Exception {
		unit.setStrict(false);
		ByteInstruction inst = new ByteInstruction("nonExistant");
		unit.notifyData(inst);

		assertThat(unit.getResult(), is(notNullValue()));
	}

	@Test
	public void dispatchesToNamedSetterIfNoSuchField() throws Exception {
		Datatype ri = new ByteInstruction("setterWithADifferentName");
		ri.setValue((byte) 3);
		unit.notifyData(ri);

		assertThat(unit.getResult().gotASetter, is((byte) 3));
	}

	@Test(expected=ExecutionException.class)
	public void exceptionThrownIfWrongType() throws Exception {
		Datatype ri = new FixedLengthString("byteOne", "", "US-ASCII");
		ri.setValue("oops");
		unit.notifyData(ri);
	}

	@Test
	public void finalFieldsCanBeAltered() throws Exception {
		Datatype ri = new ByteInstruction("finalByte");
		ri.setValue((byte) 3);
		unit.notifyData(ri);

		assertThat(unit.getResult().finalByte, is((byte) 3));
	}

	@Test(expected=ExecutionException.class)
	public void exceptionThrownIfMethodThrowsException() throws Exception {
		Datatype ri = new ByteInstruction("exceptionThrown");
		ri.setValue((byte) 3);
		unit.notifyData(ri);
	}

	@Test
	public void canMakePrivateFieldAccessible() throws Exception {
		Datatype ri = new ByteInstruction("notSoPrivateByte");
		ri.setValue((byte) 3);
		unit.notifyData(ri);

		RootBinding binding = unit.getResult();
		assertThat(binding.getNotSoPrivateByte(), is((byte) 3));
	}

	@Test
	public void fieldWithBindingAnnotationIsCalled() throws Exception {
		Datatype ri = new ByteInstruction("boundField");
		ri.setValue((byte) 3);
		unit.notifyData(ri);

		RootBinding binding = unit.getResult();
		assertThat(binding.notTheSameName, is((byte) 3));
	}

	@Test
	public void collectionTypesArePopulated() throws Exception {
		Datatype ri = new FixedLengthString("stringList", "", "US-ASCII");

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
		Datatype ri = new FixedLengthString("aSet", "", "US-ASCII");
		ri.setValue("one");

		unit.notifyData(ri);

		RootBinding binding = unit.getResult();
		assertThat(binding.aSet.size(), is(1));
	}

	@Test
	public void queueInterfaceHasConcreteImplementationMapped() throws Exception {
		Datatype ri = new FixedLengthString("aQueue", "", "US-ASCII");
		ri.setValue("one");

		unit.notifyData(ri);

		RootBinding binding = unit.getResult();
		assertThat(binding.aQueue.size(), is(1));
	}

	@Test
	public void collectionsWorkWithRenamedBinding() throws Exception {
		Datatype ri = new FixedLengthString("notTheList", "", "US-ASCII");

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
		Datatype ri = new FixedLengthString("existingStringList", "", "US-ASCII");
		ri.setValue("anotherElement");
		unit.notifyData(ri);

		RootBinding binding = unit.getResult();
		assertThat(binding.existingStringList.size(), is(2));
		assertThat(binding.existingStringList.get(0), is("existingElement"));
		assertThat(binding.existingStringList.get(1), is("anotherElement"));
	}

	@Test
	public void strictModeIgnoresUnboundedFields() {
		Datatype ri = new FixedLengthString("existingStringList", "", "US-ASCII");
		ri.setValue("anotherElement");
		unit.notifyData(ri);
	}

	@Test
	public void autoboxedTypesAreHandled() {
		Datatype ri = new IntegerInstruction("primitiveInt");
		ri.setValue(10);

		unit.notifyData(ri);

		RootBinding binding = unit.getResult();
		assertThat(binding, is(notNullValue()));
		assertThat(binding.primitiveInt, is(10));
	}


	@Test(expected=ExecutionException.class)
	public void exceptionThrownIfGroupInstructionUsedForPrimitiveField() throws Exception {
		unit.notifyGroup("stringList", true);
	}

	@SuppressWarnings("unused")
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
		public List<String> existingStringList = new ArrayList<>();

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
