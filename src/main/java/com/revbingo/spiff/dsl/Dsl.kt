/*
 * Copyright Mark Piper 2017
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

package com.revbingo.spiff.dsl

import com.revbingo.spiff.datatypes.*
import com.revbingo.spiff.evaluator.JELEvaluator
import com.revbingo.spiff.events.EventListener
import com.revbingo.spiff.instructions.*
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset

fun readBinary(file: File, listener: EventListener, init: BinaryReader.() -> Unit): BinaryReader {
    val fc = FileInputStream(file).channel
    val buffer = ByteBuffer.allocate(file.length().toInt())
    fc.read(buffer)
    buffer.flip()

    val evaluator = JELEvaluator().apply {
        addVariable("fileLength", buffer.limit())
    }

    val adf = BinaryReader(buffer, listener, evaluator).apply {
        init()
    }

    for(instruction in adf.instructions) {
        instruction.execute(buffer, listener, evaluator)
    }

    return adf
}

class BinaryReader(val buffer: ByteBuffer, val listener: EventListener, val evaluator: JELEvaluator) {
    val instructions: MutableList<Instruction> = mutableListOf<Instruction>()

    var defaultEncoding: String = "utf-8"

    fun group(groupName: String, init: BinaryReader.() -> Unit) {
        add(GroupInstruction(groupName))
        init()
        add(EndGroupInstruction(groupName))
    }

    private fun add(instr: Instruction) {
        instructions.add(instr)
    }

    fun string(instName: String = "", lengthExpr: String? = null, length: Int? = null, charset: String = defaultEncoding) {
        val l = if(length != null) length.toString() else lengthExpr
        if(l != null) {
            add(FixedLengthString(instName, l.toString(), charset))
        } else {
            add(TerminatedString(instName, charset))
        }
    }

    fun stringLiteral(instName: String = "", literal: String, charset: String = defaultEncoding) {
        add(LiteralStringInstruction(instName, literal, charset))
    }

    fun nullTerminatedString(instName: String = "", charset: String = defaultEncoding) {
        add(TerminatedString(instName, charset))
    }

    fun setOrder(order: ByteOrder) {
        this.add(SetOrderInstruction(order))
    }

    fun setEncoding(charset: Charset) {
        defaultEncoding = charset.displayName()
    }

    fun int(instName: String) {
        this.add(IntegerInstruction(instName))
    }

    fun short(instName: String) {
        this.add(ShortInstruction(instName))
    }

    fun _if(expr: String, init: BinaryReader.() -> Unit): IfBlockHolder {
        val ifAdf = BinaryReader(buffer, listener, evaluator)
        ifAdf.init()

        add(IfBlock(expr, ifAdf.instructions, Block(listOf<Instruction>()))) //TODO: Implement else block

        return IfBlockHolder(this)
    }

    fun set(varName: String, expr: String) {
        add(SetInstruction(expr, varName))
    }

    fun repeat(expr: String, init: BinaryReader.() -> Unit) {
        val repeatBlock = BinaryReader(buffer, listener, evaluator)
        repeatBlock.init()
        add(RepeatBlock(expr, repeatBlock.instructions))
    }

    fun ubyte(varName: String) {
        add(UnsignedByteInstruction(varName))
    }

    fun byte(varName: String) {
        add(ByteInstruction(varName))
    }

    fun long(varName: String) {
        add(LongInstruction(varName))
    }

    fun mark(varName: String) {
        add(MarkInstruction(varName))
    }

    fun skip(expr: String) {
        add(SkipInstruction(expr))
    }

    fun jump(expr: String) {
        add(JumpInstruction(expr))
    }

}

class IfBlockHolder(val context: BinaryReader) {
    infix fun _else(init: BinaryReader.() -> Unit) {
        context.init()
    }
}
