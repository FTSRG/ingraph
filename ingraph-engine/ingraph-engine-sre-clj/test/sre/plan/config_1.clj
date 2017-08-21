(ns sre.plan.config-1
    (:require [sre.plan.dsl.config :refer [defconfig]]
              [sre.plan.dsl.constraint :refer [defconstraint]]
              [sre.plan.dsl.op :refer [defop]]))

(defconfig Config1)

(defconstraint TestConstraint01 [a])

(defconstraint TestConstraint02 [a b] :implies TestConstraint01 [a])

(defop TestOp01 [a b c]
       :requires TestConstraint02 [a b]
       :satisfies TestConstraint02 [b c])

(defop TestOp02 [a b]
       :requires TestConstraint01 [a] TestConstraint01 [b]
       :satisfies TestConstraint02 [a b])

(defop TestOp03 [a b]
       :satisfies TestConstraint02 [a b])