package com.twitter.zipkin.common;

import com.twitter.util.Duration;
import org.junit.Test;
import scala.Option;
import scala.Some;
import scala.collection.immutable.List;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public final class JavaHelpersTest {
    @Test
    public void make_span() {
        Option<Endpoint> ep = scala.Option.empty();
        Annotation annotation = new Annotation(0, "cs", ep, Some.apply(Duration.apply(10, TimeUnit.SECONDS)));
        BinaryAnnotation binaryAnnotation = new BinaryAnnotation("foo", ByteBuffer.wrap("bar".getBytes()), AnnotationType.String(), Some.apply(new Endpoint(-100, (short)8080, "my-service")));
        Option<Long> parentId = Some.apply(789L);
        Span span = JavaHelpers.makeSpan(123, "hello", 456, parentId, Arrays.asList(annotation), Arrays.asList(binaryAnnotation), false);


        java.util.List ann = scala.collection.JavaConversions.asJavaList(span.annotations());
        long id = span.id();
    }
}
