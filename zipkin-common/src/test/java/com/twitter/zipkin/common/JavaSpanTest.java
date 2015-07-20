package com.twitter.zipkin.common;

import com.twitter.util.Duration;
import com.twitter.zipkin.common.*;
import com.twitter.zipkin.javahelpers.JavaHelpers;
import org.junit.Test;
import scala.Option;
import scala.Some;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class JavaSpanTest {
    @Test
    public void make_span() {
        Option<Endpoint> ep = scala.Option.empty();
        Annotation annotation = new Annotation(0, "cs", ep, Some.apply(Duration.apply(10, TimeUnit.SECONDS)));
        Option<Endpoint> someEndpoint = Some.apply(new Endpoint(-100, (short) 8080, "my-service"));
        BinaryAnnotation binaryAnnotation = new BinaryAnnotation("foo", ByteBuffer.wrap("bar".getBytes()), AnnotationType.String(), someEndpoint);
        Option parentId = Some.apply(789L);
        Span span = JavaHelpers.makeSpan(123, "hello", 456, parentId, Arrays.asList(annotation), Arrays.asList(binaryAnnotation), false);

        assertThat(span.traceId(), is(123L));
        assertThat(span.id(), is(456L));
        assertThat(span.name(), is("hello"));
        assertThat((long)span.parentId().get(), is(789L));
    }
}
