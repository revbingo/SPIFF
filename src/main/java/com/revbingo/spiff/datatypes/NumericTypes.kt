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

    fun verifyNumber(n: Number, expr: String?, evalFunc: () -> Number): Unit {
        if(expr != null) {
            val exprResult = evalFunc()
            if(n != exprResult) throw ExecutionException("Value $n did not match expected value $expr")
        }
    }
}

abstract class SimpleNumberType(val bufferFunc: (ByteBuffer) -> Number,
                                val evaluationFunc: (Evaluator, String?) -> Number): NumberType() {

    override fun evaluate(buffer: ByteBuffer, evaluator: Evaluator): Any {
        val value = bufferFunc(buffer)
        verifyNumber(value, literalExpr) { evaluationFunc(evaluator, literalExpr) }
        return value
    }
}

class ByteInstruction: SimpleNumberType({ it.get() }, { eval, expr -> eval.evaluateByte(expr) })

class ShortInstruction: SimpleNumberType({ it.short }, { eval, expr -> eval.evaluateShort(expr) })

class IntegerInstruction: SimpleNumberType({ it.int }, { eval, expr -> eval.evaluateInt(expr) })

class LongInstruction: SimpleNumberType({ it.long }, { eval, expr -> eval.evaluateLong(expr) })

class DoubleInstruction : SimpleNumberType({ it.double }, { eval, expr -> eval.evaluateDouble(expr) })

class FloatInstruction: SimpleNumberType({ it.float }, { eval, expr -> eval.evaluateFloat(expr) })

/*
* Unsigned numbers are represented by widening to the next widest type i.e. unsigned bytes are shorts,
* unsigned shorts are ints, unsigned ints are longs, unsigned longs are not supported
* */
abstract class FixedLengthUnsignedNumber: NumberType() {

    protected fun convertBytesToInts(bytes: ByteArray): IntArray {
        return bytes.map { byte -> 0x000000FF and byte.toInt() }.toIntArray()
    }

}

class UnsignedByteInstruction: FixedLengthUnsignedNumber() {

    override fun evaluate(buffer: ByteBuffer, evaluator: Evaluator): Any {
        val bytes = ByteArray(1)
        buffer.get(bytes)

        val s = convertBytesToInts(bytes)[0].toShort()

        verifyNumber(s, literalExpr) { evaluator.evaluateShort(literalExpr) }
        return s
    }
}

class UnsignedIntegerInstruction: FixedLengthUnsignedNumber() {

    override fun evaluate(buffer: ByteBuffer, evaluator: Evaluator): Any {
        val bytes = ByteArray(4)
        val signedInt = buffer.int

        bytes[0] = (signedInt shr 24).toByte()
        bytes[1] = (signedInt shr 16).toByte()
        bytes[2] = (signedInt shr 8).toByte()
        bytes[3] = signedInt.toByte()

        val ubytes = convertBytesToInts(bytes)

        val long = ((ubytes[0] shl 24)
                 or (ubytes[1] shl 16)
                 or (ubytes[2] shl 8)
                 or (ubytes[3])).toLong()

        verifyNumber(long, literalExpr) { evaluator.evaluateLong(literalExpr) }
        return long
    }
}

class UnsignedShortInstruction: FixedLengthUnsignedNumber() {

    override fun evaluate(buffer: ByteBuffer, evaluator: Evaluator): Any {
        val bytes = ByteArray(2)
        val signedShort = buffer.short

        bytes[0] = ((signedShort.toInt() shr 8) and 0xFF).toByte()
        bytes[1] = (signedShort.toInt() and 0xFF).toByte()

        val ubytes = convertBytesToInts(bytes)

        val i = (ubytes[0] shl 8) or ubytes[1]

        verifyNumber(i, literalExpr) { evaluator.evaluateInt(literalExpr) }
        return i
    }
}