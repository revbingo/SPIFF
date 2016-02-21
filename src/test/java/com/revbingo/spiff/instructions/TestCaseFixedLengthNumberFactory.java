package com.revbingo.spiff.instructions;

import com.revbingo.spiff.datatypes.*;
import org.junit.Test;

import static org.hamcrest.core.Is.isA;
import static org.junit.Assert.assertThat;

public class TestCaseFixedLengthNumberFactory {

    @Test
    public void createsTypes() throws Exception {
        FixedLengthNumberFactory unit = new FixedLengthNumberFactory();

        //the cast is unfortunately necessary (https://github.com/hamcrest/JavaHamcrest/issues/27)
        NumberType inst = unit.getInstruction("int");
        assertThat((IntegerInstruction) inst, isA(IntegerInstruction.class));

        inst = unit.getInstruction("long");
        assertThat((LongInstruction) inst, isA(LongInstruction.class));

        inst = unit.getInstruction("float");
        assertThat((FloatInstruction) inst, isA(FloatInstruction.class));

        inst = unit.getInstruction("double");
        assertThat((DoubleInstruction) inst, isA(DoubleInstruction.class));

        inst = unit.getInstruction("short");
        assertThat((ShortInstruction) inst, isA(ShortInstruction.class));

        inst = unit.getInstruction("byte");
        assertThat((ByteInstruction) inst, isA(ByteInstruction.class));

        inst = unit.getInstruction("ubyte");
        assertThat((UnsignedByteInstruction) inst, isA(UnsignedByteInstruction.class));

        inst = unit.getInstruction("ushort");
        assertThat((UnsignedShortInstruction) inst, isA(UnsignedShortInstruction.class));

        inst = unit.getInstruction("anythingelse");
        assertThat((UnsignedIntegerInstruction) inst, isA(UnsignedIntegerInstruction.class));
    }
}
