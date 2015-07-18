package com.twitter.zipkin.common

import com.google.common.util.concurrent.{FutureCallback, Futures, ListenableFuture}
import com.twitter.util.{Promise, Future}

import scala.collection.JavaConverters._

object JavaHelpers {
  def makeSpan(traceId: Long,
               name: String,
               id: Long,
               parentId: Option[Long],
               annotations: java.util.List[Annotation],
               binaryAnnotations: java.util.List[BinaryAnnotation],
               debug: Boolean) = {
    Span.apply(traceId, name, id, parentId, annotations.asScala.toList, binaryAnnotations.asScala, debug)
  }

  def mkTwitterFuture[T](future: ListenableFuture[T]) : Future[T] = {
    val promise = new Promise[T]
    Futures.addCallback(future, new FutureCallback[T] {
      override def onSuccess(result: T) = promise.setValue(result)
      override def onFailure(throwable: Throwable) = promise.setException(throwable)
    })
    promise
  }
}
