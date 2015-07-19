package com.twitter.zipkin.javahelpers

import com.google.common.util.concurrent.{FutureCallback, Futures, ListenableFuture}
import com.twitter.util.{Future, Promise}
import com.twitter.zipkin.common.{Annotation, BinaryAnnotation, Span}

import scala.collection.JavaConverters._

object JavaHelpers {
  type ParentId = Option[Long]
  def makeSpan(traceId: Long,
               name: String,
               id: Long,
               parentId: ParentId,
               annotations: java.util.List[Annotation],
               binaryAnnotations: java.util.List[BinaryAnnotation],
               debug: Boolean) = {
    Span.apply(traceId, name, id, parentId, annotations.asScala.toList, binaryAnnotations.asScala, debug)
  }

  def makeTwitterFuture[T](future: ListenableFuture[T]) : Future[T] = {
    val promise = new Promise[T]
    Futures.addCallback(future, new FutureCallback[T] {
      override def onSuccess(result: T) = promise.setValue(result)
      override def onFailure(throwable: Throwable) = promise.setException(throwable)
    })
    promise
  }

  def foo(f: Option[String]) : Unit = {}
}
