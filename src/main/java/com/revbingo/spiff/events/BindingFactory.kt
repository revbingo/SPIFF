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
        val classCache = binderCache[clazz] ?: emptyMap<String, Binder>()
        if(classCache.containsKey(name)) {
            return classCache[name]
        }

        try {
            for(matcher in matchers) {
                val binder = matcher.match(name, clazz) ?: continue
                addToCache(clazz, name, binder)
                return binder
            }
        } catch(e: SecurityException) {
            throw ExecutionException("SecurityManager prevents access to method/field", e)
        }
        return null
    }

    private fun addToCache(clazz: Class<*>, name: String, binder: Binder): Unit {
        val classCache = binderCache[clazz] ?: mutableMapOf<String, Binder>()
        binderCache.put(clazz, classCache)
        classCache.put(name, binder)
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

val primitiveTypes: List<Type?> = listOf(Int::class.java, Float::class.java, Double::class.java, Short::class.java,
                                            Long::class.java, Byte::class.java, Char::class.java, String::class.java)

fun AccessibleObject.annotatedName() = this.getAnnotation(BindingCollection::class.java)?.value ?: this.getAnnotation(Binding::class.java)?.value ?: null

fun Method.binder() = MethodBinder(this)

fun Field.binder() : Binder {
    if (Collection::class.java.isAssignableFrom(this.type)) {
        var genericType: Type? = null
        val fieldType = this.genericType
        if (fieldType is ParameterizedType) {
            genericType = fieldType.actualTypeArguments[0]
        }

        if (primitiveTypes.contains(genericType)) {
            return PrimitiveCollectionBinder(this)
        } else {
            return ObjectCollectionBinder(this, genericType as Class<*>)
        }
    } else {
        return FieldBinder(this)
    }
}