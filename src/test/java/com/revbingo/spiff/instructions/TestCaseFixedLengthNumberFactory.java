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
