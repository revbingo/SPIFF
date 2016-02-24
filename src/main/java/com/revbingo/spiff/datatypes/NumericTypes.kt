/*******************************************************************************
 * Copyright 2012 Mark Piper

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.revbingo.spiff.datatypes

import com.revbingo.spiff.ExecutionException
import java.nio.ByteBuffer

import com.revbingo.spiff.evaluator.Evaluator

abstract class NumberType : Datatype() {

    var literalExpr: String? = null

    fun verifyNumber(numberToCheck: Number, type: Class<out Any>, evaluator: Evaluator): Unit {
        if(literalExpr != null) {
            val expressionResult = evaluator.evaluate(literalExpr, type as Class<Any>)
            if(numberToCheck != expressionResult) throw ExecutionException("Value $numberToCheck did not match expected value $literalExpr")
        }
    }
}

abstract class SimpleNumberType(val type: Class<out Any>,
                                val bufferFunc: (ByteBuffer) -> Number): NumberType() {

    override fun evaluate(buffer: ByteBuffer, evaluator: Evaluator): Any {
        val value = bufferFunc(buffer)
        verifyNumber(value, type, evaluator)
        return value
    }
}

class ByteInstruction: SimpleNumberType(Byte::class.java, { it.get() })

class ShortInstruction: SimpleNumberType(Short::class.java, { it.short })

class IntegerInstruction: SimpleNumberType(Int::class.java, { it.int })

class LongInstruction: SimpleNumberType(Long::class.java, { it.long })

class DoubleInstruction : SimpleNumberType(Double::class.java, { it.double })

class FloatInstruction: SimpleNumberType(Float::class.java, { it.float })

/*
* Unsigned numbers are represented by widening to the next widest type i.e. unsigned bytes are shorts,
* unsigned shorts are ints, unsigned ints are longs, unsigned longs are not supported
* */
abstract class FixedLengthUnsignedNumber: NumberType() {


}

fun ByteArray.convertToInts(): IntArray = this.map { byte -> 0x000000FF and byte.toInt() }.toIntArray()

class UnsignedByteInstruction: NumberType() {

    override fun evaluate(buffer: ByteBuffer, evaluator: Evaluator): Any {
        val bytes = ByteArray(1)
        buffer.get(bytes)

        val s = bytes.convertToInts()[0].toShort()

        this.verifyNumber(s, Short::class.java, evaluator)
        return s
    }
}

class UnsignedIntegerInstruction: NumberType() {

    override fun evaluate(buffer: ByteBuffer, evaluator: Evaluator): Any {
        val bytes = ByteArray(4)
        val signedInt = buffer.int

        bytes[0] = (signedInt shr 24).toByte()
        bytes[1] = (signedInt shr 16).toByte()
        bytes[2] = (signedInt shr 8).toByte()
        bytes[3] = signedInt.toByte()

        val ubytes = bytes.convertToInts()

        val long = ((ubytes[0] shl 24)
                 or (ubytes[1] shl 16)
                 or (ubytes[2] shl 8)
                 or (ubytes[3])).toLong()

        verifyNumber(long, Long::class.java, evaluator)
        return long
    }
}

class UnsignedShortInstruction: NumberType() {

    override fun evaluate(buffer: ByteBuffer, evaluator: Evaluator): Any {
        val bytesAsInts = IntArray(2)
        val signedShortAsInt = buffer.short.toInt()

        bytesAsInts[0] = (signedShortAsInt shr 8) and 0xFF
        bytesAsInts[1] = signedShortAsInt and 0xFF

        val i = (bytesAsInts[0] shl 8) or bytesAsInts[1]

        verifyNumber(i, Int::class.java, evaluator)
        return i
    }
}