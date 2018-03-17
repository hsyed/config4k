package io.github.config4k

import com.typesafe.config.Config
import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory
import io.github.config4k.readers.SelectReader
import kotlin.reflect.full.primaryConstructor

/**
 * An extract function that does not require a starting path -- i.e., it attempts to map from the root of the object.
 */
inline fun <reified T> Config.extract(): T = doExtract("", true)

/**
 * Map [Config] to Kotlin types.
 *
 * @param path the config destructuring begins at this path
 */
inline fun <reified T> Config.extract(path: String): T = require(path.isNotEmpty()).let { doExtract(path, false) }

@PublishedApi
internal inline fun <reified T> Config.doExtract(path: String, permitEmptyPath: Boolean): T {
    val genericType = object : TypeReference<T>() {}.genericType()
    val result = SelectReader.getReader(ClassContainer(T::class, genericType), permitEmptyPath)(this, path)

    return try {
        result as T
    } catch (e: Exception) {
        throw result?.let { e } ?: ConfigException.BadPath(
                path, "take a look at your config")
    }
}

/**
 * Converts the receiver object to Config.
 *
 * @param name the returned config's name
 */
fun Any.toConfig(name: String): Config {
    val clazz = this.javaClass.kotlin
    val map = when {
        clazz.javaPrimitiveType != null -> mapOf(name to this)
        this is String -> mapOf(name to this)
        this is Enum<*> -> mapOf(name to this.name)
        this is Iterable<*> -> {
            val list = this.map {
                it?.toConfigValue()?.unwrapped()
            }
            mapOf(name to list)
        }
        this is Map<*, *> -> {
            val map = this.mapKeys {
                (it.key as? String) ?:
                        throw Config4kException.UnSupportedType(clazz)
            }.mapValues {
                it.value?.toConfigValue()?.unwrapped()
            }
            mapOf(name to map)
        }
        clazz.primaryConstructor != null ->
            mapOf(name to getConfigMap(this, clazz))
        clazz.objectInstance != null -> mapOf(name to emptyMap<String, Any>())
        else -> throw Config4kException.UnSupportedType(clazz)
    }

    return ConfigFactory.parseMap(map)
}
