package com.snc.zero.lib.kotlin.reflect

import timber.log.Timber
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class Reflector {

    companion object {

        @JvmStatic
        @Throws(IllegalArgumentException::class)
        fun getMethod(instance: Any, methodName: String): Method? {
            val methods = instance.javaClass.declaredMethods
            Timber.i("methods = $methods, len = ${methods.size}")
            for (method in methods) {
                if (methodName == method.name) {
                    return method
                }
            }
            return null
        }

        @JvmStatic
        @Throws(InvocationTargetException::class, IllegalAccessException::class)
        operator fun invoke(instance: Any, method: Method, vararg param: Any?) {
            method.isAccessible = true
            method.invoke(instance, *param)
            method.isAccessible = false
        }
    }
}