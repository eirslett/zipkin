package com.twitter.zipkin.aggregate

import java.util.Date

import com.twitter.algebird.{Moments, Monoid, Semigroup}
import com.twitter.scalding._
import com.twitter.util.Time
import com.twitter.zipkin.common.{Dependencies, DependencyLink, Service, Span}

final class ZipkinAggregateJob
   (args: Args) extends Job(args)
{
  val dateRange: DateRange = DateRange(new Date(0L), new Date)

  @transient
  val (extraConfig, spanSource) = SpanSourceProvider(args)

  override def config = super.config ++ extraConfig ++ Map(
    "mapreduce.map.memory.mb" -> args.getOrElse("mapMb", "4096"),
    "mapreduce.reduce.memory.mb" -> args.getOrElse("reduceMb", "4096"),
    "mapred.child.java.opts" ->	("-Xmx"+args.getOrElse("childXmxMb", "4000")+"m"),
    "numSplitsToProcess" -> args.getOrElse("numSplitsToProcess", "-1")
  )

  val allSpans = TypedPipe.from(spanSource)
    .groupBy { span: Span => (span.id, span.traceId) }
    .reduce { (s1, s2) => s1.mergeSpan(s2) }
    .filter { case (key, span) => span.isValid }

  val parentSpans = allSpans
    .group

  val childSpans = allSpans
    .filter { case (key, span) => span.parentId.isDefined }
    .map { case (key, span) => ((span.parentId.get, span.traceId), span)}
    .group

  def serviceNameOrUnknown(span: Span) = span.serviceName.getOrElse("unknown") // (should be span.erviceName.get)

  val result = parentSpans.join(childSpans)
    .map { case (_,(parent: Span,child: Span)) =>
    val moments = child.duration.map { d => Moments(d.toDouble) }.getOrElse(Monoid.zero[Moments])
    val dlink = DependencyLink(Service(serviceNameOrUnknown(parent)), Service(serviceNameOrUnknown(child)), moments)
    ((serviceNameOrUnknown(parent), serviceNameOrUnknown(child)), dlink)
  }
    .group
    .sum
    .values
    .map { dlink => Dependencies(Time.fromMilliseconds(dateRange.start.timestamp), Time.fromMilliseconds(dateRange.end.timestamp), Seq(dlink))}
    .sum

  result.write(spanSource)
}

object SpanSourceProvider {
  def apply(args: Args) : (Map[AnyRef,AnyRef], Source with TypedSource[Span] with TypedSink[Dependencies]) = args.required("source") match {
    case "cassandra" => {
      (Map("hosts" -> args.required("hosts"), "port" -> args.getOrElse("port", "9160")), new cassandra.SpanSource)
    }
    case s:String => throw new ArgsException(s+" is not an implemented source.")
  }
}