package org.mpris.v2;

public interface ReturnableTypeRunnable<S, T> {
    S run(T value);
}
