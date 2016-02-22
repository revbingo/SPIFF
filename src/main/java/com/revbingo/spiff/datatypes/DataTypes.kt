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
}


class ByteInstruction(): NumberType() {

    constructor(name: String?) : this() {
        this.name = name
    }

    override fun evaluate(buffer: ByteBuffer, evaluator: Evaluator): Any {
        val b = buffer.get()
        if(literalExpr != null) {
            if(b != evaluator.evaluateByte(literalExpr)) {
                throw ExecutionException("Value $b did not match expected value $literalExpr")
            }
        }
        return b;
    }
}

class DoubleInstruction : NumberType() {

    override fun evaluate(buffer: ByteBuffer, evaluator: Evaluator): Any {
        return buffer.double
    }

}

class FloatInstruction: NumberType() {

    override fun evaluate(buffer: ByteBuffer, evaluator: Evaluator): Any {
        return buffer.float
    }
}

class ShortInstruction: NumberType() {

    override fun evaluate(buffer: ByteBuffer, evaluator: Evaluator): Any {
        val s = buffer.short
        if(literalExpr != null) {
            if(s != evaluator.evaluateShort(literalExpr)) {
                throw ExecutionException("Value $s did not match expected value $literalExpr")
            }
        }
        return s
    }
}

class IntegerInstruction: NumberType() {

    override fun evaluate(buffer: ByteBuffer, evaluator: Evaluator): Any {
        val i = buffer.int
        if(literalExpr != null) {
            if(i != evaluator.evaluateInt(literalExpr)) {
                throw ExecutionException("Value $i did not match expected value $literalExpr")
            }
        }
        return i
    }
}

class LongInstruction: NumberType() {

    override fun evaluate(buffer: ByteBuffer, evaluator: Evaluator): Any {
        val l = buffer.long
        if(literalExpr != null) {
            if(l != evaluator.evaluateLong(literalExpr)) {
                throw ExecutionException("Value $l did not match expected value $literalExpr")
            }
        }
        return l
    }
}

abstract class FixedLengthUnsignedNumber: NumberType() {

    protected fun convertBytesToInts(bytes: ByteArray): IntArray {
        return bytes.map { byte -> 0x000000FF and byte.toInt() }.toIntArray()
    }
}

class UnsignedByteInstruction: FixedLengthUnsignedNumber() {

    override fun evaluate(buffer: ByteBuffer, evaluator: Evaluator): Any {
        super.address = buffer.position()
        val bytes = ByteArray(1)
        buffer.get(bytes)

        val s = convertBytesToInts(bytes)[0].toShort()

        if(literalExpr != null) {
            if(s != evaluator.evaluateShort(literalExpr)) {
                throw ExecutionException("Value $s did not match expected value $literalExpr")
            }
        }
        return s
    }
}

class UnsignedIntegerInstruction: FixedLengthUnsignedNumber() {
    override fun evaluate(buffer: ByteBuffer, evaluator: Evaluator): Any {
        super.address = buffer.position()
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
        if(literalExpr != null) {
            if(long != evaluator.evaluateLong(literalExpr)) {
                throw ExecutionException("Value $long did not match expected value $literalExpr")
            }
        }
        return long
    }
}

class UnsignedShortInstruction: FixedLengthUnsignedNumber() {
    override fun evaluate(buffer: ByteBuffer, evaluator: Evaluator): Any {
        super.address = buffer.position()
        val bytes = ByteArray(2)
        val signedShort = buffer.short

        bytes[0] = ((signedShort.toInt() shr 8) and 0xFF).toByte()
        bytes[1] = (signedShort.toInt() and 0xFF).toByte()

        val ubytes = convertBytesToInts(bytes)

        val i = (ubytes[0] shl 8) or ubytes[1]

        if(literalExpr != null) {
            if(i != evaluator.evaluateInt(literalExpr)) {
                throw ExecutionException("Value $i did not match expected value $literalExpr")
            }
        }
        return i

    }
}


class FixedLengthString(charsetName: String) : StringInstruction(charsetName) {

    var lengthExpr: String? = null

    public override fun getBytes(buffer: ByteBuffer, evaluator: Evaluator): ByteArray {
        val length = (evaluator.evaluate(lengthExpr) as Number).toInt()
        val bytes = ByteArray(length)
        buffer.get(bytes)
        return bytes
    }
}
