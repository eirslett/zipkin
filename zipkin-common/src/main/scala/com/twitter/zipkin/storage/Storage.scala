/*
 * Copyright 2012 Twitter Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.twitter.zipkin.storage

import com.twitter.util.{Duration, Future}
import com.twitter.zipkin.common.Span

trait Storage {

  /**
   * Close the storage
   */
  def close()

  /**
   * Store the span in the underlying storage for later retrieval.
   * @return a future for the operation
   */
  def storeSpan(span: Span) : Future[Unit]

  /**
   * Set the ttl of a trace. Used to store a particular trace longer than the
   * default. It must be oh so interesting!
   */
  def setTimeToLive(traceId: Long, ttl: Duration): Future[Unit]

  /**
   * Get the time to live for a specific trace.
   * If there are multiple ttl entries for one trace, pick the lowest one.
   */
  def getTimeToLive(traceId: Long): Future[Duration]

  def tracesExist(traceIds: Seq[Long]): Future[Set[Long]]

  /**
   * Get the available trace information from the storage system.
   * Spans in trace should be sorted by the first annotation timestamp
   * in that span. First event should be first in the spans list.
   *
   * The return list will contain only spans that have been found, thus
   * the return list may not match the provided list of ids.
   */
  def getSpansByTraceIds(traceIds: Seq[Long]): Future[Seq[Seq[Span]]]
  def getSpansByTraceId(traceId: Long): Future[Seq[Span]]
  /**
   * How long do we store the data before we delete it? In seconds.
   */
  def getDataTimeToLive: Int

}

// For Java-friendliness
abstract class AbstractStorage extends Storage {
  import java.{util => ju}
  import scala.collection.JavaConverters._
  import com.twitter.zipkin.interop.Helpers._

  // Java implementations can implement these methods, that use Java types, instead of the Scala types
  def tracesExist(traceIds: ju.List[java.lang.Long]) : Future[ju.Set[java.lang.Long]]
  def getSpansByTraceIds(traceIds: ju.List[java.lang.Long]) : Future[ju.List[ju.List[Span]]]
  def java_getSpansByTraceId(traceId: Long) : Future[ju.List[Span]]

  // Here, we delegate calls to the Java implementation, after having converted types to/from Java
  final override def tracesExist(traceIds: Seq[Long]): Future[Set[Long]] = tracesExist(traceIds.map(Long.box).asJava).map(result => result.asScala.toSet.map(Long.unbox))
  final override def getSpansByTraceIds(traceIds: Seq[Long]): Future[Seq[Seq[Span]]] =
    getSpansByTraceIds(traceIds.map(Long.box).asJava).map { traces =>
      traces.asScala.map(_.asScala)
  }
  final override def getSpansByTraceId(traceId: Long) : Future[Seq[Span]] = java_getSpansByTraceId(traceId).map(_.asScala)
}
