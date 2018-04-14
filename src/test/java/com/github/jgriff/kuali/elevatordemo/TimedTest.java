package com.github.jgriff.kuali.elevatordemo;

import java.time.Duration;

/**
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
public interface TimedTest {
    /**
     * @return default timeout, or maximum amount of time a test is allowed before considered failing.
     */
    default Duration timeout() { return Duration.ofSeconds(2); }
}
