package org.metacorp.mindbug.card.effect;

public interface ResolvableEffect<T> {
    void resolve(T choiceResolver);
}
