package playground.sse.controller;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.sse.Event;
import org.reactivestreams.Publisher;
import playground.sse.domain.Author;
import playground.sse.service.NewsService;

@Controller("/v1/topics/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @Post("/publish")
    public void publish(@Body Author input) {
        newsService.publish(input);
    }

    @Get("/events")
    Publisher<Event<Author>> events() {
        return newsService.getStream()
                          .map(Event::of);
    }
}
