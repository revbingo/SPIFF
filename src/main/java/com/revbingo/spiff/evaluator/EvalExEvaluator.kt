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

package com.revbingo.spiff.evaluator

import com.revbingo.spiff.ExecutionException
import com.udojava.evalex.Expression
import java.math.BigDecimal

class EvalExEvaluator: Evaluator {

    val variableMap = mutableMapOf<String, Any>()

    override fun addVariable(name: String, variable: Any) {
        variableMap.put(name, variable)
    }

    override fun evaluate(expression: String): Any {
        return evaluate(expression, BigDecimal::class.java)
    }

    override fun <X: Number> evaluate(expression: String, type: Class<X>): X {
        try {
            var expressionObj = Expression(expression)

            variableMap.forEach { (k, v) ->
                expressionObj = when(v) {
                    is Int, is Short, is Byte, is Float -> expressionObj.with(k, BigDecimal(v.toString()))
                    is Long -> expressionObj.with(k, BigDecimal(v))
                    is Double -> expressionObj.with(k, BigDecimal(v))
                    is Boolean -> expressionObj.with(k, v.toString())
                    is BigDecimal -> expressionObj.with(k, v)
                    else -> expressionObj
                }
            }

            val evaluated = expressionObj.eval()
            return when (type) {
                Double::class.java -> evaluated.toDouble()
                Long::class.java -> evaluated.toLong()
                Int::class.java -> evaluated.toInt()
                Float::class.java -> evaluated.toFloat()
                Short::class.java -> evaluated.toShort()
                Byte::class.java -> evaluated.toByte()
                Boolean::class.java -> (evaluated.compareTo(BigDecimal(1)) == 0)
                else -> evaluated
            } as X
        } catch(e: Throwable) {
            throw ExecutionException("Exception occurred during evaluation: ${e.message}", e)
        }
    }

    override fun clear() {
        variableMap.clear()
    }

}