package com.revbingo.spiff.instructions

import com.revbingo.spiff.evaluator.Evaluator
import com.revbingo.spiff.events.EventListener
import java.nio.ByteBuffer
import java.nio.ByteOrder

class JumpInstruction: AdfInstruction() {

    var expression: String? = null

    override fun execute(buffer: ByteBuffer, eventDispatcher: EventListener, evaluator: Evaluator) {
        val result = evaluator.evaluate(expression, Int::class.java)
        buffer.position(result)
    }
}

class MarkInstruction: AdfInstruction() {
    var name: String? = null

    override fun execute(buffer: ByteBuffer, eventDispatcher: EventListener, evaluator: Evaluator) {
        evaluator.addVariable(name, buffer.position())
        evaluator.addVariable("$name.address", buffer.position())
    }
}

class SetInstruction: AdfInstruction() {
    var expression: String? = null
    var varname: String? = null

    override fun execute(buffer: ByteBuffer, eventDispatcher: EventListener, evaluator: Evaluator) {
        val result = evaluator.evaluate(expression)
        evaluator.addVariable(varname, result)
    }
}

class SetOrderInstruction: AdfInstruction() {
    var order: ByteOrder? = null

    override fun execute(buffer: ByteBuffer, eventDispatcher: EventListener, evaluator: Evaluator) {
        buffer.order(order!!)
    }
}

