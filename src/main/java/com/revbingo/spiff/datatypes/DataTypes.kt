package com.revbingo.spiff.datatypes

import com.revbingo.spiff.ExecutionException
import com.revbingo.spiff.evaluator.Evaluator
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.*

abstract class StringInstruction(charsetName: String): Datatype() {

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

class FixedLengthString(charsetName: String) : StringInstruction(charsetName) {

    var lengthExpr: String? = null

    public override fun getBytes(buffer: ByteBuffer, evaluator: Evaluator): ByteArray {
        val length = (evaluator.evaluate(lengthExpr) as Number).toInt()
        val bytes = ByteArray(length)
        buffer.get(bytes)
        return bytes
    }
}

class TerminatedString(charsetName: String) : StringInstruction(charsetName) {

    public override fun getBytes(buffer: ByteBuffer, evaluator: Evaluator) : ByteArray {
        val baos = ByteArrayOutputStream()

        while(true) {
            val nextByte = buffer.get()
            if(nextByte.toInt() == 0x00) break;
            baos.write(nextByte.toInt())
        }
        return baos.toByteArray()
    }
}

class LiteralStringInstruction(val literal: String, charsetName: String) : StringInstruction(charsetName) {

    public override fun getBytes(buffer: ByteBuffer, evaluator: Evaluator) : ByteArray {
        val expectedBytes = literal.toByteArray(encoding)
        val actualBytes = ByteArray(expectedBytes.size)

        buffer.get(actualBytes)

        if(Arrays.equals(expectedBytes, actualBytes)) {
            return actualBytes
        } else {
            throw ExecutionException("Expected literal string ${literal} but got ${String(actualBytes, encoding)}")
        }
    }
}