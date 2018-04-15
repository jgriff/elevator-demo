package com.github.jgriff.kuali.elevatordemo;

import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.atLeastOnce;

/**
 * Support for testing events hitting an {@link org.springframework.context.ApplicationEventPublisher}.
 * 
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
public interface EventTest extends TimedTest {
    
    default <E> List<E> captureEvents(
            ApplicationEventPublisher eventPublisher,
            ArgumentCaptor eventCaptor,
            Predicate<Object> matcher, int expectedCount) {
        AtomicReference<List<E>> expectedEvents = new AtomicReference<>();
        
        given().ignoreExceptions().await().atMost(timeout().getSeconds(), SECONDS).until(() -> {
            verify(eventPublisher, atLeastOnce()).publishEvent(eventCaptor.capture());

            // and we have our events
            List captured = (List) eventCaptor.getAllValues().stream().distinct()
                    .filter(matcher)
                    .collect(Collectors.toList());

            assertEquals(expectedCount, captured.size());
            
            expectedEvents.set(captured);
            return true;
        });
        
        return expectedEvents.get();
    }    
}
