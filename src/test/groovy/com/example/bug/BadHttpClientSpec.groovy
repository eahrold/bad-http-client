package com.example.bug

import io.micronaut.context.annotation.Property
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.client.annotation.Client

import io.micronaut.test.extensions.spock.annotation.MicronautTest
import spock.lang.Specification
import jakarta.inject.Inject


@Client(id="my-service", path = "/test")
interface WorkingClient {
    @Get("/{tenantId}/here")
    HttpResponse<String> here(@PathVariable UUID tenantId)
}

@Client(id="my-service", path = "/test/{tenantId}")
interface BrokenClient {
    @Get("/here")
    HttpResponse<String> here(@PathVariable UUID tenantId)
}

@MicronautTest
@Property(name = "micronaut.server.port", value = "8080")
@Property(name = "micronaut.http.services.my-service.url", value = "http://localhost:8080")
class BadHttpClientSpec extends Specification {

    @Controller("/test/{tenantId}")
    static class HereController {

        @Get("here")
        HttpResponse<String> here(@PathVariable UUID tenantId) {
            String response = "welcome to "+tenantId.toString()
            return HttpResponse.ok(response)
        }
    }


    @Inject
    WorkingClient workingClient

    @Inject
    BrokenClient brokenClient

    void 'Test client with PathVariable expansion in method annotation'() {
        when:
        UUID theUuid = UUID.randomUUID();
        def response = workingClient.here(theUuid)

        then:
        response.status() == HttpStatus.OK
        response.body() == "welcome to ${theUuid}"

    }

    void 'Test client with PathVariable expansion in @Client path'() {
        when:
        UUID theUuid = UUID.randomUUID();
        def response = brokenClient.here(theUuid)

        then:
        response.status() == HttpStatus.OK
        response.body() == "welcome to ${theUuid}"
    }

}
