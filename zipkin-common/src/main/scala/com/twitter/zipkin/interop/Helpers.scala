package com.twitter.zipkin.interop

import com.google.common.util.concurrent.{ListenableFuture, Futures, FutureCallback}
import com.twitter.util.{Future, Promise}

object Helpers {
  // Convert a Google ListenableFuture into a Twitter Future
  def mkTwitterFuture[T](future: ListenableFuture[T]) : Future[T] = {
    val promise = new Promise[T]
    Futures.addCallback(future, new FutureCallback[T] {
      override def onSuccess(result: T) = promise.setValue(result)
      override def onFailure(throwable: Throwable) = promise.setException(throwable)
    })
    promise
  }

  // Turn a Future[Void] into a Future[Unit]
  def boxedUnitFuture(future: Future[Void]) : Future[Unit] = future.map { _ => Unit }
}
