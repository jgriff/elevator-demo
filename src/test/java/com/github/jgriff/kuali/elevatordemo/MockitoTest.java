package com.github.jgriff.kuali.elevatordemo;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;

/**
 * Interface to be implemented by any test that is using Mockito for mocking objects.  
 * Handles {@link MockitoAnnotations#initMocks(Object) initializing} the mocks 
 * so every test doesn't have to.
 * 
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 * @see <a href="http://static.javadoc.io/org.mockito/mockito-core/2.18.0/org/mockito/Mockito.html#mock_annotation">Mockito</a>
 */
public interface MockitoTest {
    @BeforeEach
    default void initMocks() { MockitoAnnotations.initMocks(this); }
}
