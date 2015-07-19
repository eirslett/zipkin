package com.twitter.zipkin.storage;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.twitter.util.Duration;
import com.twitter.util.ExecutorServiceFuturePool;
import com.twitter.util.Function;
import com.twitter.util.Future;
import com.twitter.zipkin.common.Span;
import com.twitter.zipkin.interop.Helpers;
import scala.runtime.BoxedUnit;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public final class JavaStorageImpl extends AbstractStorage {

    @Override
    public Future<Set<Long>> java_tracesExist(List<Long> traceIds) {
        // A ListenableFuture would probably come from some async library
        ListenableFuture<Set<Long>> lf = MoreExecutors.listeningDecorator(new ForkJoinPool()).submit(new Callable<Set<Long>>() {
            @Override
            public Set<Long> call() throws Exception {
                return ImmutableSet.of(123L, 456L, 789L);
            }
        });
        return Helpers.mkTwitterFuture(lf);
    }

    @Override
    public Future<List<List<Span>>> getSpansByTraceIds(List<Long> traceIds) {
        // In this case, we don't do anything that can block the thread, so just return the value wrapped in a Future
        return Future.value(Arrays.asList(Arrays.asList((Span)null)));
    }

    @Override
    public Future<List<Span>> java_getSpansByTraceId(long traceId) {
        // Here, we assume that we need to perform blocking IO, because some library doesn't support
        // non-blocking operations (eg. JDBC 4). The way we do it is to make an ExecutorServiceFuturePool,
        // which delegates to an underlying ExecutorService.
        ExecutorServiceFuturePool pool = new ExecutorServiceFuturePool(Executors.newCachedThreadPool());

        // Then, we submit tasks to the pool:
        return pool.apply(Function.ofCallable(new Callable<List<Span>>() {
            @Override
            public List<Span> call() throws Exception {
                // This is where we do the blocking computation
                return Arrays.asList();
            }
        }));
    }

    @Override
    public void close() {

    }

    @Override
    public Future<BoxedUnit> storeSpan(Span span) {
        // Again, assume that some library you use returns a ListenableFuture. This time, it returns Future<Void>
        ListenableFuture<Void> lf = MoreExecutors.listeningDecorator(new ForkJoinPool()).submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // Store the span, and then return null (since this is a void method)
                return null;
            }
        });
        Future<Void> twFuture = Helpers.mkTwitterFuture(lf);
        // Void = Unit in Scala, so we do a simple conversion
        return Helpers.boxedUnitFuture(twFuture);
    }

    @Override
    public Future<BoxedUnit> setTimeToLive(long traceId, Duration ttl) {
        // Your implementation goes here
        int ttlSeconds = ttl.inSeconds();
        return Future.Unit();
    }

    @Override
    public Future<Duration> getTimeToLive(long traceId) {
        // Your implementation goes here
        return Future.value(Duration.fromSeconds(3600));
    }

    @Override
    public int getDataTimeToLive() {
        return 0;
    }
}
