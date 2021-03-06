package io.github.config4k

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import org.junit.Assert
import org.opentest4j.AssertionFailedError

internal inline fun <reified T> Config.assertEqualsAtPath(path: String, expected: T) {
    extract<T>(path).also {
        Assert.assertEquals(expected, it)
    }
}

internal inline fun <reified T> Config.assertEqualsAfterRepositioning(path: String, expected: T) {
    getConfig(path).extract<T>().also {
        Assert.assertEquals(expected, it)
    }
}

internal fun <R> withConfig(config: String, block: Config.() -> R) = ConfigFactory.parseString(config).block()

inline fun <reified T> shouldThrow(block: () -> Unit): Exception {
    try {
        block()
        throw AssertionFailedError("expected exception of type ${T::class.qualifiedName} to be thrown")
    } catch (ex: Exception) {
        if (ex.javaClass !== T::class.java) {
            throw AssertionFailedError("expected exception of type ${T::class.qualifiedName} to be thrown got ${ex.javaClass.canonicalName}")
        } else {
            return ex
        }
    }
}

internal inline infix fun <reified T> T.shouldBe(expected: T) { Assert.assertEquals(expected, this) }