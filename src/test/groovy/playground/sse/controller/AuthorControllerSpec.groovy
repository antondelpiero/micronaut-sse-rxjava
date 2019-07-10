package playground.sse.controller

import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.sse.RxSseClient
import io.micronaut.http.sse.Event
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.annotation.MicronautTest
import io.reactivex.Flowable
import playground.sse.domain.Author
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

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

    void "s"() {
        given:
        def request = HttpRequest.GET("/author/events")
        List<Author> authors = []
        PollingConditions conditions = new PollingConditions()

        when:
        Flowable<Event<Author>> response = rxClient.eventStream(request, Author)
        def disposable = response.subscribe({event ->
            authors.add(event.data)
        })
        def req = HttpRequest.POST("/author/publish", [name: "a"])
        client1.toBlocking().exchange(req)

        then: "response got pushed"
        conditions.within(13) {
            authors.size() == 1
            authors[0].name == "a"
        }

        cleanup:
        disposable.dispose()
    }
}
