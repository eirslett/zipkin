package com.twitter.zipkin.common

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
}
