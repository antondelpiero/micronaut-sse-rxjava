package playground.sse.service;

import io.micronaut.security.utils.SecurityService;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import playground.sse.domain.Author;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class NewsService {

    private Subject<Author> subject = PublishSubject.create();

    @Inject
    private SecurityService securityService;

    public void publish(Author event) {
        subject.onNext(event);
    }

    public Flowable<Author> getStream() {
        return subject.hide()
                      .toFlowable(BackpressureStrategy.BUFFER);
    }
}
