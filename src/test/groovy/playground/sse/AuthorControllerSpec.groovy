package playground.sse

import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.sse.RxSseClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.annotation.MicronautTest
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

@MicronautTest
class AuthorControllerSpec extends Specification {

    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer)

    @Shared
    @AutoCleanup
    private RxSseClient rxClient = embeddedServer.applicationContext
                                               .createBean(RxSseClient, embeddedServer.getURL())

    @Shared
    @AutoCleanup
    private RxHttpClient client1 = embeddedServer.applicationContext
                                                 .createBean(RxHttpClient, embeddedServer.getURL())


    void "test events"() {
        given:
        def request = HttpRequest.GET("/author/events")

        when:
        def response = rxClient.eventStream(request, Map)
                             .take(1)
                             .toList()
                             .blockingGet()

        then:
        response.isEmpty()

        when: "publish a new instance"
        def req = HttpRequest.POST("/author/publish", [name: "a"])
        client1.toBlocking()
                       .retrieve(req)

        then: "response got pushed"
        response.size() == 1
    }
}
