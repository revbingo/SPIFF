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