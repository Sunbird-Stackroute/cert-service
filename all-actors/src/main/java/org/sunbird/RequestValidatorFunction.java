package org.sunbird;

import java.util.concurrent.ExecutionException;

@FunctionalInterface
public interface RequestValidatorFunction<T, R> {
    R apply(T t) throws BaseException, ExecutionException, InterruptedException;
}