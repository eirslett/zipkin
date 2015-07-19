package com.twitter.zipkin.storage;

import org.junit.Test;
import scala.Option;

public final class JavaStorageCompilationTest {
    @Test
    public void make_storage_implementation() {
        JavaStorageImpl impl = new JavaStorageImpl();
    }
}
