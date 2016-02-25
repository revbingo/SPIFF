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

class FixedLengthNumberFactory {

    fun getInstruction(type: String): NumberType {
        when (type) {
            "int" -> return IntegerInstruction()
            "long" -> return LongInstruction()
            "float" -> return FloatInstruction()
            "short" -> return ShortInstruction()
            "double" -> return DoubleInstruction()
            "byte" -> return ByteInstruction()
            "ubyte" -> return UnsignedByteInstruction()
            "ushort" -> return UnsignedShortInstruction()
            else -> return UnsignedIntegerInstruction()
        }
    }
}

abstract class NumberType(val type: Class<out Any>,
                          val bufferFunc: (ByteBuffer) -> Number) : Datatype() {

    var literalExpr: String? = null

    override fun evaluate(buffer: ByteBuffer, evaluator: Evaluator): Any {
        val value = bufferFunc(buffer)
        if(literalExpr != null) {
            val expressionResult = evaluator.evaluate(literalExpr, type as Class<Any>)
            if(value != expressionResult) throw ExecutionException("Value $value did not match expected value $literalExpr")
        }
        return value
    }
}

class ByteInstruction: NumberType(Byte::class.java, { it.get() })

class ShortInstruction: NumberType(Short::class.java, { it.short })

class IntegerInstruction: NumberType(Int::class.java, { it.int })

class LongInstruction: NumberType(Long::class.java, { it.long })

class DoubleInstruction : NumberType(Double::class.java, { it.double })

class FloatInstruction: NumberType(Float::class.java, { it.float })

/*
* Unsigned numbers are represented by widening to the next widest type i.e. unsigned bytes are shorts,
* unsigned shorts are ints, unsigned ints are longs, unsigned longs are not supported
* */
fun ByteArray.convertToUnsignedInts(): IntArray = this.map { byte -> byte.toInt() and 0xFF }.toIntArray()

class UnsignedByteInstruction: NumberType(Short::class.java, {
    val bytes = ByteArray(1)
    it.get(bytes)

    bytes.convertToUnsignedInts()[0].toShort()
})

class UnsignedIntegerInstruction: NumberType(Long::class.java, {
    val bytes = ByteArray(4)
    val signedInt = it.int

    bytes[0] = (signedInt shr 24).toByte()
    bytes[1] = (signedInt shr 16).toByte()
    bytes[2] = (signedInt shr 8).toByte()
    bytes[3] = signedInt.toByte()

    val ubytes = bytes.convertToUnsignedInts()

    ((ubytes[0] shl 24)
            or (ubytes[1] shl 16)
            or (ubytes[2] shl 8)
            or (ubytes[3])).toLong()
})


class UnsignedShortInstruction: NumberType(Int::class.java, {
    val bytesAsInts = IntArray(2)
    val signedShortAsInt = it.short.toInt()

    bytesAsInts[0] = (signedShortAsInt shr 8) and 0xFF
    bytesAsInts[1] = signedShortAsInt and 0xFF

    (bytesAsInts[0] shl 8) or bytesAsInts[1]
})