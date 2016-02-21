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


class FixedLengthString(charsetName: String) : StringInstruction(charsetName) {

    var lengthExpr: String? = null

    public override fun getBytes(buffer: ByteBuffer, evaluator: Evaluator): ByteArray {
        val length = (evaluator.evaluate(lengthExpr) as Number).toInt()
        val bytes = ByteArray(length)
        buffer.get(bytes)
        return bytes
    }
}
