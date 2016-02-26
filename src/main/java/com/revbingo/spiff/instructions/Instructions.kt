package com.revbingo.spiff.instructions

import com.revbingo.spiff.evaluator.Evaluator
import com.revbingo.spiff.events.EventListener
import java.nio.ByteBuffer
import java.nio.ByteOrder

interface Instruction {
    open fun execute(buffer: ByteBuffer, eventDispatcher: EventListener, evaluator: Evaluator)
}

abstract class AdfInstruction: Instruction {
    var lineNumber: Int? = 0
    var isBreakpoint: Boolean? = false
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
        evaluator.addVariable("$expression.address", buffer.position())
    }
}

class SetInstruction(expression: String, val varname: String?): InstructionWithExpression(expression) {
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

open class Block: AdfInstruction(), Iterable<Instruction> {
    var instructions: List<Instruction>? = null

    override fun execute(buffer: ByteBuffer, eventDispatcher: EventListener, evaluator: Evaluator) {
        instructions?.forEach {
            it.execute(buffer, eventDispatcher, evaluator)
        }
    }

    override fun iterator(): Iterator<Instruction> {
        return instructions!!.iterator()
    }

}

class IfBlock(val ifExpression: String): Block() {

    var elseInstructions: Block? = null

    override fun execute(buffer: ByteBuffer, eventDispatcher: EventListener, evaluator: Evaluator) {
        val result = evaluator.evaluate(ifExpression, Boolean::class.java)
        if(result) {
            super.execute(buffer, eventDispatcher, evaluator)
        } else {
            elseInstructions?.forEach { it.execute(buffer, eventDispatcher, evaluator) }
        }
    }

    fun setElseInstructions(inst: List<Instruction>) {
        elseInstructions = Block()
        elseInstructions!!.instructions = inst
    }

    fun getIfInstructions(): Block {
        val ifInstructions = Block()
        ifInstructions.instructions = instructions
        return ifInstructions
    }
}

class RepeatBlock: Block() {

    var repeatCountExpression: String? = null
    override fun execute(buffer: ByteBuffer, eventDispatcher: EventListener, evaluator: Evaluator) {
        val d = evaluator.evaluate(repeatCountExpression, Double::class.java)
        val repeatCount = d.toInt()
        repeatCount.times_do {
            super.execute(buffer, eventDispatcher, evaluator)
        }
    }

}

fun Int.times_do(func: () -> Any): Unit {
    for(i in 1..this.toInt()) {
        func()
    }
}