package io.deuxsept.dndice.Utils

import java.util.*
import kotlin.reflect.KProperty

/**
 * Allows lazy initialization to be resetted
 * http://stackoverflow.com/questions/35752575/kotlin-lazy-properties-and-values-reset-a-resettable-lazy-delegate
 */
class ResettableLazyManager {
    // we synchronize to make sure the timing of a reset() call and new inits do not collide
    val managedDelegates = LinkedList<Resettable>()

    /**
     * Register a lazy initialized property to handle
     */
    fun register(managed: Resettable) {
        synchronized (managedDelegates) {
            managedDelegates.add(managed)
        }
    }

    /**
     * Mark all lazy initialized properties followed as un-initialized
     */
    fun reset() {
        synchronized (managedDelegates) {
            managedDelegates.forEach { it.reset() }
            managedDelegates.clear()
        }
    }
}

interface Resettable {
    fun reset()
}

/**
 * Handles the by resettableLazy {} logic
 */
class ResettableLazy<PROPTYPE>(val manager: ResettableLazyManager, val init: ()->PROPTYPE): Resettable {
    @Volatile var lazyHolder = makeInitBlock()

    operator fun getValue(thisRef: Any?, property: KProperty<*>): PROPTYPE {
        return lazyHolder.value
    }

    override fun reset() {
        lazyHolder = makeInitBlock()
    }

    fun makeInitBlock(): Lazy<PROPTYPE> {
        return lazy {
            manager.register(this)
            init()
        }
    }
}

/**
 * Initializes lazily a property that can be reset.
 */
fun <PROPTYPE> resettableLazy(manager: ResettableLazyManager, init: ()->PROPTYPE): ResettableLazy<PROPTYPE> {
    return ResettableLazy(manager, init)
}

fun resettableManager(): ResettableLazyManager = ResettableLazyManager()