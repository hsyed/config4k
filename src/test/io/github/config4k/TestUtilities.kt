package io.github.config4k

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.it

object TestUtilities : Spek({
    context("Config.forPackageOf<T>()") {
        it("should be able to descend into a config object using the package of a class") {
            withConfig("""io.github.config4k { test = "boom" } """) {
                forPackageOf<TestUtilities>().also {
                    val test: String = it.getString("test")
                    assert(test == "boom")
                }
            }
        }
    }
})