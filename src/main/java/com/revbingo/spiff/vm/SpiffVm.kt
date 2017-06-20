package com.revbingo.spiff.vm

import com.revbingo.spiff.datatypes.Datatype
import com.revbingo.spiff.evaluator.EvalExEvaluator
import com.revbingo.spiff.events.EventListener
import com.revbingo.spiff.instructions.AdfInstruction
import com.revbingo.spiff.instructions.Instruction
import java.nio.ByteBuffer

class SpiffVm(private val instructions: List<Instruction>, private val buffer: ByteBuffer,
              private val eventListener: EventListener = NullEventListener()) {

    private val evaluator = EvalExEvaluator()
    private val suspendedLock = Object()

    private var programCounter: Int = 0
    var isSuspended: Boolean = false
        private set

    val nextLineNumber: Int?
        get() = programCounter

    init {
        evaluator.addVariable("fileLength", buffer.limit())
    }

    fun start() {
        for (i in instructions) {
            if (i is AdfInstruction) {
                programCounter = i.lineNumber

                if (i.isBreakpoint) {
                    isSuspended = true
                    synchronized (suspendedLock) {
                        suspendedLock.wait()
                    }
                }
            }

            i.execute(buffer, eventListener, evaluator)
        }
    }

    fun getVar(expression: String): Any {
        return evaluator.evaluate(expression)
    }

    fun setBreakpoint(i: Int) {
        for (inst in instructions) {
            if(inst is AdfInstruction) {
                if (inst.lineNumber === i) {
                    inst.isBreakpoint = true
                }
            }
        }
    }

    fun resume() {
        synchronized (suspendedLock) {
            suspendedLock.notify()
        }
    }
}

class NullEventListener: EventListener {
    override fun notifyGroup(groupName: String, start: Boolean) {}
    override fun notifyData(ins: Datatype) {}
}