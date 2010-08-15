package com.revbingo.spiff.events;

import static org.hamcrest.CoreMatchers.equalTo;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.revbingo.spiff.bindings.BindingWrapper;
import com.revbingo.spiff.bindings.ClassBindingWrapper;
import com.revbingo.spiff.instructions.ByteInstruction;
import com.revbingo.spiff.instructions.EndGroupInstruction;
import com.revbingo.spiff.instructions.GroupInstruction;
import com.revbingo.spiff.instructions.Instruction;


public class TestCaseMultipleBindingEventDispatcher {

	@Ignore
	@Test
	public void testMultipleBindings() throws Exception {
		byte[] bytes = new byte[] { 0x01, 0x02, 0x03, 0x04 };
		
		ByteBuffer testBuffer = ByteBuffer.wrap(bytes);
		
		MultipleBindingEventDispatcher unit = new MultipleBindingEventDispatcher(MockBindingOne.class, MockBindingTwo.class);

		ByteInstruction one = new ByteInstruction("one");
		ByteInstruction two = new ByteInstruction("two");

		GroupInstruction groupOne = new GroupInstruction("groupOne");
		EndGroupInstruction endGroupOne = new EndGroupInstruction("groupOne");
		
		GroupInstruction groupTwo = new GroupInstruction("groupTwo");
		EndGroupInstruction endGroupTwo = new EndGroupInstruction("groupTwo");
		
		Map<String, BindingWrapper> bindings = new HashMap<String, BindingWrapper>();
		bindings.put("groupOne", new ClassBindingWrapper(MockBindingOne.class));
		bindings.put("groupTwo", new ClassBindingWrapper(MockBindingTwo.class));
//		unit.getBindingFactory().setBindings(bindings);
		
		List<Instruction> instructions = Arrays.asList(new Instruction[] { groupOne, one, two, endGroupOne, groupTwo, one, two, endGroupTwo});
		
		for(Instruction i : instructions) {
			i.execute(testBuffer, unit);
		}
		
		ArrayList<?> result = unit.getResult();
		
		assertThat(result.size(), is(equalTo(2)));
		
		assertThat(result.get(0), is(instanceOf(MockBindingOne.class)));
		assertThat(result.get(1), is(instanceOf(MockBindingTwo.class)));
		
		MockBindingOne mockOne = (MockBindingOne) result.get(0);
		MockBindingTwo mockTwo = (MockBindingTwo) result.get(1);
		
		assertThat(mockOne.one, is(equalTo((byte) 1)));
		assertThat(mockOne.two, is(equalTo((byte) 2)));
		assertThat(mockTwo.one, is(equalTo((byte) 3)));
		assertThat(mockTwo.two, is(equalTo((byte) 4)));
	}
}
