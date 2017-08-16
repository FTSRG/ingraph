(ns sre.plan.config-2
  (:require [sre.plan.dsl.config :refer [defconfig]]
            [sre.plan.dsl.constraint :refer [defconstraint]]
            [sre.plan.dsl.op :refer [defop]]
            [sre.plan.dsl.estimation :refer :all]))

(defconfig Basic)

(defconstraint Element [element])
(defconstraint Edge [edge] :implies Element [edge])
(defconstraint Vertex [vertex] :implies Element [vertex])
(defconstraint DirectedEdge [source edge target] :implies
               Vertex [source]
               Edge [edge]
               Vertex [target])

(defop GetVertices [vertex]
       :satisfies Vertex [vertex])
(defop GetEdges [source edge target]
       :satisfies DirectedEdge [source edge target])
(defop ExtendOut [source edge target]
       :requires Vertex [source]
       :satisfies DirectedEdge [source edge target])
(defop ExtendIn [target edge source]
       :requires Vertex [target]
       :satisfies DirectedEdge [source edge target])

(defmulti weight (fn [op] (:name op)))

(defmethod weight #'GetVertices [op] 5)
(defmethod weight #'GetEdges [op] 5)
(defmethod weight #'ExtendOut [op] 2)
(defmethod weight #'ExtendIn [op] 2)

(defrecord CostCalculator [c p]
  Cost
  (update-cost [this weight]
    (as-> this this
      (update-in this [:p] #(* %1 weight))
      (update-in this [:c] #(+ %1 (:p this)))))
  Comparable
  (compareTo [this other] (compare (:c this) (:c other))))

(defrecord WeightCalculator [w]
  Weight
  (update-weight [this bound-op constraint-lookup]
    (assoc-in this [:w] (weight bound-op)))
  (get-weight [this] (:w this)))


