package com.twitter.zipkin.storage;

import com.twitter.util.Duration;
import com.twitter.util.Future;
import com.twitter.util.Futures;
import com.twitter.zipkin.common.Span;
import scala.runtime.BoxedUnit;

import java.util.List;
import java.util.Set;

public final class JavaStorageImpl extends AbstractStorage {

    @Override
    public Future<Set<Object>> tracesExist(List<Object> traceIds) {
        return null;
    }

    @Override
    public Future<List<List<Span>>> getSpansByTraceIds(List<Object> straceIds) {
        return null;
    }

    @Override
    public Future<List<Span>> java_getSpansByTraceId(long traceId) {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public Future<BoxedUnit> storeSpan(Span span) {
        return null;
    }

    @Override
    public Future<BoxedUnit> setTimeToLive(long traceId, Duration ttl) {
        return null;
    }

    @Override
    public Future<Duration> getTimeToLive(long traceId) {
        return null;
    }

    @Override
    public int getDataTimeToLive() {
        return 0;
    }
}
