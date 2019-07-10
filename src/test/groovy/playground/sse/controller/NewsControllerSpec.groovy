package playground.sse.controller

import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.sse.RxSseClient
import io.micronaut.http.sse.Event
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.annotation.MicronautTest
import io.reactivex.Flowable
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

@MicronautTest
class NewsControllerSpec extends Specification {

    @Shared
    @AutoCleanup
    private EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer)

    @Shared
    @AutoCleanup
    private RxSseClient rxSseClient = embeddedServer.applicationContext
                                                    .createBean(RxSseClient, embeddedServer.getURL())

    @Shared
    @AutoCleanup
    private RxHttpClient client = embeddedServer.applicationContext
                                                .createBean(RxHttpClient, embeddedServer.getURL())

    void "test news endpoint for subscribe and publish"() {
        given: "container to hold the events and subscription for sse"
        List<Map> eventsFired = []
        def request = HttpRequest.GET("/v1/topics/news/events")
        Flowable<Event<Map>> response = rxSseClient.eventStream(request, Map)
        def disposable = response.subscribe({ event -> eventsFired.add(event.data) })

        when: "an event was fired to the observer"
        def req = HttpRequest.POST("/v1/topics/news/publish", [name: "a"])
        client.toBlocking().exchange(req)

        then: "response got pushed"
        PollingConditions conditions = new PollingConditions()
        conditions.within(10) {
            eventsFired.size() == 1
            eventsFired[0].name == "a"
        }

        cleanup:
        disposable.dispose()
    }
}
