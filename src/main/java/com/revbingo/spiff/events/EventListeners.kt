package com.revbingo.spiff.events

import com.revbingo.spiff.ExecutionException
import com.revbingo.spiff.datatypes.Datatype
import java.util.*

interface EventListener {

    fun notifyData(ins: Datatype)
    fun notifyGroup(groupName: String, start: Boolean)
}

class ClassBindingEventListener<T>(val clazz:Class<T>): EventListener {

    private val rootBinding: T
    private var currentBinding: Any
    private val bindingStack: Stack<Any> = Stack()

    private val bindingFactory = BindingFactory()
    private var skipCount = 0

    var skipUnboundGroups = false
    var isStrict = true

    init {
        try {
            rootBinding = clazz.newInstance()
            currentBinding = rootBinding as Any
        } catch (e: InstantiationException) {
            throw ExecutionException("Could not instantiate ${clazz.getCanonicalName()}", e);
        } catch (e: IllegalAccessException) {
            throw ExecutionException("Could not access ${clazz.getCanonicalName()}", e);
        }
    }

    override fun notifyData(ins: Datatype) {
        if(skipUnboundGroups && skipCount > 0) return

        val binder = bindingFactory.getBindingFor(ins.name, currentBinding.javaClass)
        if(binder != null) {
            binder.bind(currentBinding, ins.value)
        } else {
            if(isStrict) throw ExecutionException("Could not get binding for instruction ${ins.name}")
        }
    }

    override fun notifyGroup(groupName: String, start: Boolean) {
        if(start) {
            bindingStack.push(currentBinding)

            val binder = bindingFactory.getBindingFor(groupName, currentBinding.javaClass)
            if(binder != null) {
                currentBinding = binder.createAndBind(currentBinding)
            } else {
                if(isStrict) {
                    throw ExecutionException("Cound not get binding for group ${groupName}")
                } else {
                    if(skipUnboundGroups) skipCount++
                }
            }
        } else {
            if(skipUnboundGroups && skipCount > 0) skipCount--
            currentBinding = bindingStack.pop()
        }
    }

    fun getResult(): T = rootBinding
}

class DebugEventListener(val wrappedListener: EventListener): EventListener {

    var tabCount = 0

    constructor(): this(object: EventListener {
        override fun notifyData(ins: Datatype) { }
        override fun notifyGroup(groupName: String, start: Boolean) {}
    })

    override fun notifyData(ins: Datatype) {
        repeat(tabCount, { print("\t") })
        println("[${ins.name}] ${ins.value}")
        wrappedListener.notifyData(ins)
    }

    override fun notifyGroup(groupName: String, start: Boolean) {
        if(!start) {
            tabCount--
        }

        repeat(tabCount, { print("\t") })
        val leader = if(start) ">>" else "<<"
        println("${leader} [$groupName]")

        if(start) tabCount++

        wrappedListener.notifyGroup(groupName, start)
    }
}