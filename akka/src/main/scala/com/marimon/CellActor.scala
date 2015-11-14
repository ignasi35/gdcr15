package com.marimon

import akka.actor.{ActorRef, Actor, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.collection.mutable
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.{FiniteDuration, Duration}

object CellActor {

  def props(aliveness: Aliveness = Alive,
            neigbours: Set[ActorRef] = Set.empty): Props =
    Props(new CellActor(aliveness, neigbours))

  case object Evolve

  case class Bonjour(status: Aliveness)

  // underpopulation, survive, overcrowded, reproduction
  trait Evolution

  case object Spawn extends Evolution

  case object Die extends Evolution

  case object Survive extends Evolution

}

trait Aliveness

case object Dead extends Aliveness

case object Alive extends Aliveness


class CellActor(var aliveness: Aliveness = Alive, connections: Set[ActorRef]) extends Actor {

  import CellActor._

  private val oneSecond: FiniteDuration = Duration(1, "second")
  private implicit val timeout = Timeout(oneSecond)
  private implicit val ec = context.dispatcher

  private type N = mutable.Map[ActorRef, Aliveness]
  private val neighbours: N = mutable.Map.empty[ActorRef, Aliveness]

  Await.ready(
    Future.sequence(
      connections.map { x =>
        (x ? Bonjour(aliveness)).mapTo[Aliveness].map { a => neighbours += (x -> a) }
      }
    ), oneSecond)

  private val alive: ((_, Aliveness)) => Boolean = _._2 == Alive

  private def countAlive(n: N): Int = n.count(alive)

  override def receive: Receive = {
    case Bonjour(status) => neighbours += (sender -> status)
      sender ! aliveness
    case Evolve =>
      aliveness match {
        case Dead if countAlive(neighbours) == 3 => sender ! Spawn
        case Alive if countAlive(neighbours) < 2 => sender ! Die
        case Alive if countAlive(neighbours) > 3 => sender ! Die
        case Alive => sender ! Survive
      }
  }
}