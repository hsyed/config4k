package io.github.config4k.readers

import com.typesafe.config.Config

/**
 * Don't implement this class.
 * Same as [io.github.config4k.readers.SelectReader]
 *
 * To support new type, implement this class.
 *
 * @param T support type
 */
internal open class Reader<out T>(read: (Config, String) -> T) {
    open val permitEmptyPath: Boolean = false

    val getValue: (Config, String) -> T? = {
        config, path ->
        if(permitEmptyPath || config.hasPath(path)) read(config, path) else null
    }
}