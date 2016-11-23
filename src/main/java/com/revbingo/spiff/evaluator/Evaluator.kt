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

package com.revbingo.spiff.evaluator

import com.revbingo.spiff.ExecutionException
import gnu.jel.CompilationException
import gnu.jel.CompiledExpression
import gnu.jel.Library
import java.util.*

class Evaluator {

    private val lib: Library
    private val variableMap: EvaluatorMap
    private val context: Array<Any>
    private val compiledExpressions = HashMap<String, CompiledExpression>()

    init {
        val staticLib = arrayOf(java.lang.Math::class.java)
        val dynamicLib = arrayOf(EvaluatorMap::class.java)
        val dotLib = arrayOf<Class<*>>()

        variableMap = EvaluatorMap()
        lib = Library(staticLib, dynamicLib, dotLib, variableMap, null)
        context = arrayOf<Any>(variableMap)
    }

    fun addVariable(name: String, variable: Any) {
        variableMap.addVariable(name, variable)
    }

    private fun compile(expression: String): CompiledExpression {
        try {
            val c = gnu.jel.Evaluator.compile(expression, lib)
            compiledExpressions.put(expression, c)
            return c
        } catch(t: Throwable) {
            throw ExecutionException("Could not compile expression ${expression}")
        }
    }

    private fun compile(expression: String, type: Class<*>): CompiledExpression {
         try {
            val c = gnu.jel.Evaluator.compile(expression, lib, type)
            compiledExpressions.put(expression + "@" + type.simpleName, c)
            return c
        } catch(t: Throwable) {
            throw ExecutionException("Could not compile expression ${expression}")
        }
    }

    fun evaluate(expression: String): Any {
        try {
            val c = getCompiledExpression(expression)
            return c.evaluate(context)
        } catch(t: Throwable) {
            throw ExecutionException("Could not evaluate expression ${expression}")
        }
    }

    fun <X> evaluate(expression: String, type: Class<X>): X {
        try {
            val c = getCompiledExpression(expression, type)
            return c.evaluate(context) as X
        } catch(t: Throwable) {
            throw ExecutionException("Could not evaluate expression ${expression}")
        }
    }

    fun getCompiledExpression(expression: String, type: Class<*>): CompiledExpression {
        return compiledExpressions["${expression}@${type.simpleName}"] ?: compile(expression, type)
    }

    fun getCompiledExpression(expression: String): CompiledExpression {
        return compiledExpressions[expression] ?: compile(expression)
    }

    fun clear() {
        variableMap.clear()
        compiledExpressions.clear()
    }
}
