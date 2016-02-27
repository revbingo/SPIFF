/*
 * Copyright Mark Piper 2016
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

package com.revbingo.spiff.vm

import com.revbingo.spiff.datatypes.Datatype
import com.revbingo.spiff.evaluator.Evaluator
import com.revbingo.spiff.events.EventListener
import com.revbingo.spiff.instructions.AdfInstruction
import com.revbingo.spiff.instructions.Instruction
import java.nio.ByteBuffer

class SpiffVm(private val instructions: List<Instruction>, private val buffer: ByteBuffer,
              private val eventListener: EventListener = NullEventListener()) {

    private val evaluator = Evaluator()
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
    override fun notifyGroup(groupName: String?, start: Boolean) {}
    override fun notifyData(ins: Datatype?) {}
}