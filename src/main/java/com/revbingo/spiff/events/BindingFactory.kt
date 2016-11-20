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
import org.junit.runners.Parameterized
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.util.*
import kotlin.reflect.KClass

class BindingFactory() {

    private val matchers: List<Matcher> = listOf(BoundMethodMatcher(), BoundFieldMatcher(), SetterMatcher(), FieldMatcher())

    private val binderCache = mutableMapOf<Class<*>, MutableMap<String, Binder>>();

    fun getBindingFor(name: String, clazz: Class<*>): Binder? {
        val classCache = binderCache.get(clazz) ?: emptyMap<String, Binder>()
        if(classCache.containsKey(name)) {
            return classCache.get(name)!!
        }

        var b: Binder? = null
        try {
            for(matcher in matchers) {
                b = matcher.match(name, clazz)
                if (b != null) {
                    addToCache(clazz, name, b)
                    return b
                }
            }
        } catch(e: SecurityException) {
            throw ExecutionException("SecurityManager prevents access to method/field", e)
        }
        return null
    }

    private fun addToCache(clazz: Class<*>, name: String, b: Binder): Unit {
        var classCache = binderCache.get(clazz)
        if(classCache == null) {
            classCache = mutableMapOf<String, Binder>()
            binderCache.put(clazz, classCache)
        }
        classCache.put(name, b)
    }

    abstract class Matcher {
        open val primitiveTypes = Arrays.asList(*arrayOf(Int::class, Float::class, Double::class, Short::class, Long::class, Byte::class, Char::class, String::class))

        abstract fun match(name: String, clazz: Class<*>): Binder?

        fun Method.hasBindingAnnotationFor(name: String) : Boolean {
            return this.isAnnotationPresent(Binding::class.java) && this.getAnnotation(Binding::class.java).value == name
        }

        fun Field.hasBindingAnnotationFor(name: String) : Boolean {
            return this.isAnnotationPresent(Binding::class.java) && this.getAnnotation(Binding::class.java).value == name
        }

        fun Method.binder() : MethodBinder {
            return MethodBinder(this)
        }
    }

    private class BoundMethodMatcher : Matcher() {

        override fun match(name: String, clazz: Class<*>): Binder? {
            val method = clazz.methods.find { it.hasBindingAnnotationFor(name) } ?: return null
            return method.binder()
        }
    }

    private class BoundFieldMatcher : Matcher() {
        override fun match(name: String, clazz: Class<*>): Binder? {
            for (f in clazz.declaredFields) {
                if(f.hasBindingAnnotationFor(name)) {
                    if(Collection::class.java.isAssignableFrom(f.type)) {
                        var genericType: Class<*>? = null
                        val fieldType = f.genericType
                        if(fieldType is ParameterizedType) {
                            genericType = fieldType.actualTypeArguments[0] as Class<*>
                        }

                        if(primitiveTypes.contains(genericType?.kotlin)) {
                            return PrimitiveCollectionBinder(f)
                        } else {
                            return ObjectCollectionBinder(f, genericType)
                        }
                    } else {
                        return FieldBinder(f)
                    }
                } else if(f.isAnnotationPresent(BindingCollection::class.java) && f.getAnnotation(BindingCollection::class.java).value == name) {
                    val genericType: KClass<*> = f.getAnnotation(BindingCollection::class.java).type
                    if(primitiveTypes.contains(genericType)) {
                        return PrimitiveCollectionBinder(f)
                    } else {
                        return ObjectCollectionBinder(f, genericType.java)
                    }
                }
            }
            return null
        }
    }

    private class SetterMatcher : Matcher() {
        override fun match(name: String, clazz: Class<*>): Binder? {
            val setterName = "set${name.capitalize()}"
            val method = clazz.methods.find { it.name == setterName }

            return method?.binder() ?: null
        }
    }

    private class FieldMatcher : Matcher() {

        override fun match(name: String, clazz: Class<*>): Binder? {
            for (f in clazz.declaredFields) {
                if (getAnnotatedName(f) != null &&
                        getAnnotatedName(f) != "" &&
                        f.name != getAnnotatedName(f))
                    continue
                if (f.name == name) {
                    if (Collection::class.java.isAssignableFrom(f.type)) {

                        var genericType: Class<*>? = null
                        val fieldType = f.genericType
                        if (fieldType is ParameterizedType) {
                            genericType = fieldType.actualTypeArguments[0] as Class<*>
                        }

                        if (primitiveTypes.contains(genericType?.kotlin)) {
                            return PrimitiveCollectionBinder(f)
                        } else {
                            return ObjectCollectionBinder(f, genericType)
                        }
                    } else {
                        return FieldBinder(f)
                    }
                }
            }
            return null
        }

        private fun getAnnotatedName(f: Field): String? {
            val bcAnno = f.getAnnotation(BindingCollection::class.java)
            if (bcAnno != null) return bcAnno.value

            val bAnno = f.getAnnotation(Binding::class.java)
            if (bAnno != null) return bAnno.value

            return null
        }

    }
}