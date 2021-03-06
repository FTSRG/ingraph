package ingraph.ire.nodes.binary

import ingraph.ire.datatypes.Tuple
import ingraph.ire.messages.{ChangeSet, ReteMessage}
import ingraph.ire.util.SizeCounter
import ingraph.ire.messages.SingleForwarder

import scala.collection.mutable

// all == true means bag union, all == false means distinct union
class UnionNode(override val next: (ReteMessage) => Unit,
                val all: Boolean) extends BinaryNode with SingleForwarder {

  val result = mutable.ListBuffer[Tuple]()

  override def onSizeRequest(): Long = SizeCounter.count(result)

  def onPrimary(changeSet: ChangeSet) {
    onUpdate(changeSet)
  }

  def onSecondary(changeSet: ChangeSet) {
    onUpdate(changeSet)
  }

  def onUpdate(changeSet: ChangeSet) {
    if (all) { // bag semantics
      forward(changeSet)
    } else { // set semantics
      val positive = changeSet.positive
      val negative = changeSet.negative
  
      val forwardPositive = for {
        tuple <- positive if !result.contains(tuple)
      } yield tuple
  
      val forwardNegative = for {
        tuple <- negative if !result.contains(tuple)
      } yield tuple
  
      result ++= forwardPositive
      result --= forwardNegative
  
      forward(ChangeSet(forwardPositive, forwardNegative))
    }
  }

}
