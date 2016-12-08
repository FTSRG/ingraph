package ingraph.cypher2relalg.util

import java.util.Iterator
import java.util.List
import relalg.BinaryLogicalOperator
import relalg.BinaryOperator
import relalg.EdgeLabel
import relalg.EdgeVariable
import relalg.ExpandOperator
import relalg.GetVerticesOperator
import relalg.JoinOperator
import relalg.LeftOuterJoinOperator
import relalg.LogicalExpression
import relalg.Operator
import relalg.RelalgContainer
import relalg.RelalgFactory
import relalg.UnionOperator
import relalg.VertexLabel
import relalg.VertexVariable

class Cypher2RelalgUtil {

	extension RelalgFactory factory = RelalgFactory.eINSTANCE

	def ensureLabel(VertexVariable vertexVariable, VertexLabel label) {
		if (!vertexVariable.vertexLabels.contains(label)) {
			vertexVariable.vertexLabels.add(label)
		}
	}

	def ensureLabel(EdgeVariable edgeVariable, EdgeLabel label) {
		edgeVariable.edgeLabels.add(label) // TODO this is not correct, see #10
	}

	def Operator buildLeftDeepTree(Class<? extends BinaryOperator> binaryOperatorType,
		Iterator<Operator> i) {
		var Operator retVal = null

		// build a left deep tree of Joins from the match clauses
		if (i?.hasNext) {
			for (retVal = i.next; i.hasNext;) {
				val nextAE = switch (binaryOperatorType) {
					case typeof(JoinOperator): createJoinOperator
					case typeof(UnionOperator): createUnionOperator
					case typeof(LeftOuterJoinOperator): createLeftOuterJoinOperator
					default: throw new IllegalArgumentException(
						"Got unexpected BinaryOperator type to build left-deep-tree")
				}
				nextAE.rightInput = i.next
				nextAE.leftInput = retVal
				retVal = nextAE
			}
		}

		return retVal
	}

	def LogicalExpression buildLeftDeepTree(BinaryLogicalOperator binaryLogicalOperator,
		Iterator<? extends LogicalExpression> i, RelalgContainer outerContainer) {
		var LogicalExpression retVal = null

		// build a left deep tree of logical expressions with AND/OR
		if (i?.hasNext) {
			for (retVal = i.next; i.hasNext;) {
				val nextAE = createBinaryLogicalExpression => [
					operator = binaryLogicalOperator
					container = outerContainer
				]
				nextAE.rightOperand = i.next
				nextAE.leftOperand = retVal
				retVal = nextAE
			}
		}

		return retVal
	}

	/**
	 * Chain expand operators together and add sourceVertexVariables
	 */
	def chainExpandOperators(GetVerticesOperator gvo, List<ExpandOperator> expandList) {
		var lastVertexVariable = gvo.vertexVariable
		var Operator lastAlgebraExpression = gvo

		for (ExpandOperator op : expandList) {
			op.sourceVertexVariable = lastVertexVariable
			op.input = lastAlgebraExpression

			lastVertexVariable = op.targetVertexVariable
			lastAlgebraExpression = op
		}

		lastAlgebraExpression
	}

	/**
	 * Chain binary operators together to build a left deep tree.
	 *
	 * head is put on the leftInput for the 1st element of the tail list, which in turn will be put on the leftInput on the 2nd element of the tail list and so on.
	 */
	def chainBinaryOperatorsLeft(Operator head, List<? extends BinaryOperator> tail) {
		var lastAlgebraExpression = head

		for (BinaryOperator op : tail) {
			op.leftInput = lastAlgebraExpression
			lastAlgebraExpression = op
		}

		lastAlgebraExpression
	}
}
