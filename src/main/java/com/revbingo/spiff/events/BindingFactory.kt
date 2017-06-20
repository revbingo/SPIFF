package com.revbingo.spiff.events

import com.revbingo.spiff.ExecutionException
import com.revbingo.spiff.annotations.Binding
import com.revbingo.spiff.annotations.BindingCollection
import com.revbingo.spiff.binders.*
import java.lang.reflect.*

class BindingFactory() {

    private val matchers: List<Matcher> = listOf(BoundMethodMatcher(), BoundFieldMatcher(), SetterMatcher(), FieldMatcher())

    private val binderCache = mutableMapOf<Class<*>, MutableMap<String, Binder>>()

    fun getBindingFor(name: String, clazz: Class<*>): Binder? {
        val classCache = binderCache.getOrPut(clazz, { mutableMapOf<String, Binder>() })
        if(classCache.containsKey(name)) {
            return classCache[name]
        }

        try {
            for(matcher in matchers) {
                val binder = matcher.match(name, clazz) ?: continue
                classCache.put(name, binder)
                return binder
            }
        } catch(e: SecurityException) {
            throw ExecutionException("SecurityManager prevents access to method/field", e)
        }
        return null
    }

    abstract class Matcher {
        abstract fun match(name: String, clazz: Class<*>): Binder?
    }

    private class BoundMethodMatcher : Matcher() {

        override fun match(name: String, clazz: Class<*>): Binder? {
            val matchingMethod = clazz.methods.find { it.annotatedName() == name } ?: return null
            return matchingMethod.binder()
        }
    }

    private class BoundFieldMatcher : Matcher() {

        override fun match(name: String, clazz: Class<*>): Binder? {
            val matchingField = clazz.declaredFields.find { it.annotatedName() == name } ?: return null
            return matchingField.binder()
        }
    }

    private class SetterMatcher : Matcher() {

        override fun match(name: String, clazz: Class<*>): Binder? {
            val matchingMethod = clazz.methods.find { it.name == "set${name.capitalize()}" } ?: return null
            return matchingMethod.binder()
        }
    }

    private class FieldMatcher : Matcher() {

        override fun match(name: String, clazz: Class<*>): Binder? {
            val matchingField = clazz.declaredFields.find { it.name == name && it.annotatedName().isNullOrEmpty() } ?: return null
            return matchingField.binder()
        }
    }
}

val primitiveTypes: List<Class<*>?> = listOf(Int::class.java, Float::class.java, Double::class.java, Short::class.java,
                                            Long::class.java, Byte::class.java, Char::class.java, String::class.java)

fun AccessibleObject.annotatedName() = this.getAnnotation(BindingCollection::class.java)?.value ?: this.getAnnotation(Binding::class.java)?.value ?: null

fun Method.binder() = MethodBinder(this)

fun Field.binder() : Binder {
    if (Collection::class.java.isAssignableFrom(this.type)) {
        var genericType: Class<*>? = null
        val fieldType = this.genericType
        if (fieldType is ParameterizedType) {
            genericType = fieldType.actualTypeArguments[0] as Class<*>
        }

        if (primitiveTypes.contains(genericType)) {
            return PrimitiveCollectionBinder(this)
        } else {
            return ObjectCollectionBinder(this, genericType)
        }
    } else {
        return FieldBinder(this)
    }
}