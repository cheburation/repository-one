package ru.cheburation

import io.vertx.core.Future
import io.vertx.kotlin.coroutines.CoroutineVerticle
import ru.cheburation.flow.LoggerNode
import ru.cheburation.flow.PlainMessage
import java.time.Duration
import java.util.concurrent.atomic.AtomicLong

class Chatterbox : CoroutineVerticle() {

  companion object {
    private val node = LoggerNode("talking-head")
    private val delay = Duration.ofSeconds(1)!!
  }

  override fun start(startFuture: Future<Void>?) {

    vertx.eventBus().consumer<String>(node.id) {
      val message = PlainMessage(it.body(), node.id)
      node.receive(message)
      node.process(message)
    }

    val sent = AtomicLong()
    vertx.setPeriodic(delay.toMillis()) {
      val id = sent.incrementAndGet().toString()
      vertx.eventBus().send(node.id, id)
      node.send(PlainMessage(id, node.id), node.id)
    }

    startFuture?.complete()
  }
}
