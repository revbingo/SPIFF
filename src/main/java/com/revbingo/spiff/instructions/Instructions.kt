package com.revbingo.spiff.instructions

import com.revbingo.spiff.evaluator.Evaluator
import com.revbingo.spiff.events.EventListener
import java.math.BigDecimal
import java.nio.ByteBuffer
import java.nio.ByteOrder

interface Instruction {
    open fun execute(buffer: ByteBuffer, eventDispatcher: EventListener, evaluator: Evaluator)
}

abstract class AdfInstruction: Instruction {
    var lineNumber: Int = 0
    var isBreakpoint: Boolean = false
}

abstract class InstructionWithExpression(val expression:String): AdfInstruction()

class JumpInstruction(expression: String): InstructionWithExpression(expression) {
    override fun execute(buffer: ByteBuffer, eventDispatcher: EventListener, evaluator: Evaluator) {
        val result = evaluator.evaluate(expression, Int::class.java)
        buffer.position(result)
    }
}

class MarkInstruction(name: String): InstructionWithExpression(name) {
    override fun execute(buffer: ByteBuffer, eventDispatcher: EventListener, evaluator: Evaluator) {
        evaluator.addVariable(expression, buffer.position())
        evaluator.addVariable("${expression}_address", buffer.position())
    }
}

class SetInstruction(expression: String, val varname: String): InstructionWithExpression(expression) {
    override fun execute(buffer: ByteBuffer, eventDispatcher: EventListener, evaluator: Evaluator) {
        val result = evaluator.evaluate(expression)
        evaluator.addVariable(varname, result)
    }
}

class SetOrderInstruction(val order: ByteOrder): AdfInstruction() {
    override fun execute(buffer: ByteBuffer, eventDispatcher: EventListener, evaluator: Evaluator) {
        buffer.order(order)
    }
}

class SkipInstruction(expression: String): InstructionWithExpression(expression) {
    override fun execute(buffer: ByteBuffer, eventDispatcher: EventListener, evaluator: Evaluator) {
        val length = evaluator.evaluate(expression, Int::class.java)
        buffer.position(buffer.position() + length)
    }
}

class GroupInstruction(groupName: String): InstructionWithExpression(groupName) {

    override fun execute(buffer: ByteBuffer, eventDispatcher: EventListener, evaluator: Evaluator) {
        eventDispatcher.notifyGroup(expression, true)
    }
}

class EndGroupInstruction(groupName: String): InstructionWithExpression(groupName) {

    override fun execute(buffer: ByteBuffer, eventDispatcher: EventListener, evaluator: Evaluator) {
        eventDispatcher.notifyGroup(expression, false)
    }
}

open class Block(val instructions: List<Instruction>): AdfInstruction() {

    override fun execute(buffer: ByteBuffer, eventDispatcher: EventListener, evaluator: Evaluator) {
        instructions.forEach {
            it.execute(buffer, eventDispatcher, evaluator)
        }
    }
}

class IfBlock(val ifExpression: String, instructions: List<Instruction>, val elseBlock: Block): Block(instructions) {

    override fun execute(buffer: ByteBuffer, eventDispatcher: EventListener, evaluator: Evaluator) {
        val result = evaluator.evaluate(ifExpression, BigDecimal::class.java)
        if(result.compareTo(BigDecimal(1)) == 0) {
            super.execute(buffer, eventDispatcher, evaluator)
        } else {
            elseBlock.execute(buffer, eventDispatcher, evaluator)
        }
    }
}

class RepeatBlock(val repeatCountExpression: String, instructions: List<Instruction>): Block(instructions) {

    override fun execute(buffer: ByteBuffer, eventDispatcher: EventListener, evaluator: Evaluator) {
        val d = evaluator.evaluate(repeatCountExpression, Double::class.java)
        val repeatCount = d.toInt()
        for(i in 1..repeatCount) {
            super.execute(buffer, eventDispatcher, evaluator)
        }
    }

}