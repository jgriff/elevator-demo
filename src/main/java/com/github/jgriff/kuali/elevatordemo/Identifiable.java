package com.github.jgriff.kuali.elevatordemo;

/**
 * Simple strategy interface for explicitly marking an object as having
 * an identifier, which can be any object (such as {@link java.util.UUID}).
 * 
 * @author mailto:justinrgriffin@gmail.com[Justin Griffin]
 * @since 0.0.0
 */
public interface Identifiable<ID> {
    ID getId();
}
