package playground.sse.service;

import io.micronaut.security.utils.SecurityService;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import playground.sse.domain.Author;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class AuthorService {

    private Subject<Author> subject = PublishSubject.create();
    private Map<String, Subject<Author>> subjectMap = new HashMap<>();

    private SecurityService securityService;

    AuthorService(SecurityService securityService) {
        this.securityService = securityService;
        subjectMap.put("1", PublishSubject.create());
    }

    public void publish(Author author) {
        var subject = subjectMap.get("1");
        subject.onNext(author);
    }

    public Flowable<Author> getAuthors() {
        var subject = subjectMap.get("1");
        return subject.hide()
                      .toFlowable(BackpressureStrategy.BUFFER);
    }
}
