package com.revbingo.spiff.evaluator

interface Evaluator {
    fun addVariable(name: String, variable: Any)

    fun evaluate(expression: String): Any
    fun <X: Number> evaluate(expression: String, type: Class<X>): X
    fun clear()
}
