package com.quri.management

import com.quri.management.handlers.bills.CreateBill
import com.quri.management.handlers.bills.DeleteBill
import com.quri.management.handlers.bills.GetBill
import com.quri.management.handlers.bills.ListBills
import com.quri.management.handlers.profiles.CreateProfile
import com.quri.management.handlers.profiles.DeleteProfile
import com.quri.management.handlers.profiles.GetProfile
import com.quri.management.handlers.profiles.ListProfiles
import com.quri.server.service.Quri
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.smithy.java.server.Server
import software.amazon.smithy.java.server.netty.NettyServerProvider
import java.net.URI

/**
 * Configures and wires the Smithy Netty server into the Spring application context.
 *
 * Responsible for two things:
 * - Assembling the [Quri] service with all operation handlers
 * - Starting the Netty HTTP server on the configured endpoint
 *
 * To add a new operation, register its handler in [quriService] via the
 * staged builder — the compiler will enforce that all operations are provided.
 */
@Configuration
class QuriServerConfig {

    /**
     * Assembles the [Quri] service by wiring all operation handlers.
     *
     * The staged builder pattern enforced by the Smithy-generated [Quri.builder]
     * ensures every operation has a handler at compile time — missing one will
     * fail to compile rather than fail at runtime.
     */
    @Bean
    fun quriService(
        createBillHandler: CreateBill,
        createProfileHandler: CreateProfile,
        deleteBillHandler: DeleteBill,
        deleteProfileHandler: DeleteProfile,
        getBillHandler: GetBill,
        getProfileHandler: GetProfile,
        listBillsHandler: ListBills,
        listProfilesHandler: ListProfiles
    ): Quri = Quri.builder()
        .addCreateBillOperation(createBillHandler)
        .addDeleteBillOperation(deleteBillHandler)
        .addGetBillOperation(getBillHandler)
        .addListBillsOperation(listBillsHandler)
        .addCreateProfileOperation(createProfileHandler)
        .addDeleteProfileOperation(deleteProfileHandler)
        .addGetProfileOperation(getProfileHandler)
        .addListProfilesOperation(listProfilesHandler)
        .build()

    /**
     * Starts the Smithy Netty HTTP server and registers it as a Spring bean
     * so its lifecycle is managed by the application context.
     *
     * The server port should be externalized to configuration rather than
     * hardcoded — consider reading from application properties via [@Value].
     *
     * @param quriService the fully assembled [Quri] service
     * @return the running [Server] instance
     */
    @Bean
    fun smithyServer(
        quriService: Quri,
        @Value("\${smithy.server.port}") port: Int): Server {
        val server = NettyServerProvider()
            .serverBuilder()
            .endpoints(URI.create("http://localhost:$port"))
            .addService(quriService)
            .build()

        server.start()

        return server
    }
}