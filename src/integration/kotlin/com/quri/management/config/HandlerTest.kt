package com.quri.management.config

import com.jayway.jsonpath.JsonPath
import com.quri.management.api.errors.GlobalExceptionHandler
import com.quri.management.api.security.TestSecurityConfig
import com.quri.management.api.serialization.SmithyJacksonConfig
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.EntityExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient

/**
 * Base class for handler tests.
 * SpringBoot context included with payload serialization.
 */
@WebFluxTest
@ActiveProfiles("integration")
@ApplyExtension(SpringExtension::class)
@Import(
    TestSecurityConfig::class,
    SmithyJacksonConfig::class,
    GlobalExceptionHandler::class,
)
abstract class HandlerTest : DescribeSpec() {

    @Autowired
    lateinit var webTestClient: WebTestClient

    /** Reads a value from the response body at the given [path] using JSONPath syntax.
     *
     * @param path JSONPath expression (e.g. `$.bill.id`, `$.items[0].name`)
     * @return the value at the given path, or throws if the path does not exist
     */
    fun EntityExchangeResult<ByteArray>.jsonPath(path: String) =
        JsonPath.parse(String(responseBody!!)).read<Any>(path)!!
}
