package com.revbingo.spiff.datatypes

import com.revbingo.spiff.ExecutionException
import com.revbingo.spiff.evaluator.Evaluator
import com.revbingo.spiff.events.EventListener
import com.revbingo.spiff.instructions.AdfInstruction
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.*

abstract class Datatype(val name: String): AdfInstruction() {

    var address: Int? = null
    var value: Any? = null

    override fun execute(buffer: ByteBuffer, eventDispatcher: EventListener, evaluator: Evaluator): Unit {
        address = buffer.position()
        value = this.evaluate(buffer, evaluator)
        evaluator.addVariable(name, value!!)
        evaluator.addVariable("${name}_address", address!!)
        eventDispatcher.notifyData(this)
    }

    abstract fun evaluate(buffer: ByteBuffer, evaluator: Evaluator): Any

}

abstract class StringInstruction(name: String, val expression: String, charsetName: String): Datatype(name) {

    val encoding: Charset

    init {
        try {
            encoding = Charset.forName(charsetName)
        } catch(e: Exception) {
            throw ExecutionException("Unknown or unsupported charset : $charsetName")
        }
    }

    override fun evaluate(buffer: ByteBuffer, evaluator: Evaluator): Any {
        val bytes = getBytes(buffer, evaluator)
        val result = String(bytes, encoding)

        return result.trim({ char -> char.isWhitespace() or (char == 0x00.toChar()) })
    }

    abstract fun getBytes(buffer: ByteBuffer, evaluator: Evaluator): ByteArray
}

class FixedLengthString(name: String, expression: String, charsetName: String) : StringInstruction(name, expression, charsetName) {

    override fun getBytes(buffer: ByteBuffer, evaluator: Evaluator): ByteArray {
        val length = (evaluator.evaluate(expression) as Number).toInt()
        val bytes = ByteArray(length)
        buffer.get(bytes)
        return bytes
    }
}

class TerminatedString(name: String, charsetName: String) : StringInstruction(name, "", charsetName) {

    override fun getBytes(buffer: ByteBuffer, evaluator: Evaluator) : ByteArray {
        val baos = ByteArrayOutputStream()

        while(true) {
            val nextByte = buffer.get()
            if(nextByte.toInt() == 0x00) break;
            baos.write(nextByte.toInt())
        }
        return baos.toByteArray()
    }
}

class LiteralStringInstruction(name: String, expression: String, charsetName: String) : StringInstruction(name, expression, charsetName) {

    override fun getBytes(buffer: ByteBuffer, evaluator: Evaluator) : ByteArray {
        val expectedBytes = expression.toByteArray(encoding)
        val actualBytes = ByteArray(expectedBytes.size)

        buffer.get(actualBytes)

        if(Arrays.equals(expectedBytes, actualBytes)) {
            return actualBytes
        } else {
            throw ExecutionException("Expected literal string ${expression} but got ${String(actualBytes, encoding)}")
        }
    }
}

class BytesInstruction(name: String): Datatype(name) {

    var lengthExpr: String? = null

    override fun evaluate(buffer: ByteBuffer, evaluator: Evaluator): Any {
        val length = evaluator.evaluate(lengthExpr!!, Int::class.java)
        val bytes = ByteArray(length)
        buffer.get(bytes)
        return bytes
    }
}

class BitsInstruction(name: String): Datatype(name) {
    var numberOfBitsExpr: String? = null

    override fun evaluate(buffer: ByteBuffer, evaluator: Evaluator): Any {
        val numberOfBits = evaluator.evaluate(numberOfBitsExpr!!, Int::class.java)

        val bytesToGet = Math.ceil(numberOfBits/8.0).toInt()
        val bytes = ByteArray(bytesToGet)

        buffer.get(bytes)

        val result = BooleanArray(numberOfBits)

        bytes.forEachIndexed { i, byte ->
            var tempByte = byte.toInt()
            for(j in 7 downTo 0) {
                val b: Int = tempByte and 0x01
                val bitIndex = (i * 8) + j
                if(bitIndex < numberOfBits) {
                    result[bitIndex] = (b == 1)
                }
                tempByte = tempByte shr 1
            }
        }
        return result
    }
}