# Demonstrates Inconsistent with `@PathVariable`

Given the following controller
```java
@Controller("/test/{tenantId}")
class HereController {

    @Get("here")
    HttpResponse<String> here(@PathVariable UUID tenantId) {
        String response = "welcome to "+tenantId.toString()
        return HttpResponse.ok(response)
    }
}
```

I would expect both style of clients to work.

```java
@Client(id="my-service", path = "/test")
interface WorkingClient {
    @Get("/{tenantId}/here")
    HttpResponse<String> here(@PathVariable UUID tenantId)
}
```

```java
@Client(id="my-service", path = "/test/{tenantId}")
interface BrokenClient {
    @Get("/here")
    HttpResponse<String> here(@PathVariable UUID tenantId)
}
```

The error that is thrown by the `BrokenClient`

```shell
Caused by: java.net.URISyntaxException: Illegal character in path at index 6: /test/{tenantId}/here?tenantId=cc76f96d-e721-4a6b-b7bc-50f634e3bba1
```

The most interesting part is that it puts the tenantId as a `@QueryValue` even when explicitly defined as a `@PathVariable`
