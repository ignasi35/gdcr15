package com.marimon

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest._


class CellActorSpec(_system: ActorSystem) extends TestKit(_system)
with ImplicitSender
with WordSpecLike
with Matchers
with BeforeAndAfterAll {

  def this() = this(ActorSystem("CellActorSpec"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  import CellActor._

  private def test(current: Aliveness, evolution: Evolution, aliveNeighbours: Int) = {
    val neighbours = (1 to aliveNeighbours).map { x => system.actorOf(CellActor.props()) }.toSet
    val subject = system.actorOf(CellActor.props(current, neighbours))
    subject ! Evolve
    expectMsg(evolution)
  }

  "A Cell Actor" when {

    "dead" must {
      "reproduct if there's 3 alive neighbours" in test(Dead, Spawn, 3)
    }

    "alive" must {
      "die if there's less than 2 alive neighbours" in test(Alive, Die, 1)
      "survive if there's 2 or 3 alive neighbours" in test(Alive, Survive, 2)
      "die if there's more than 3 alive neighbours" in test(Alive, Die, 4)
    }
  }
}