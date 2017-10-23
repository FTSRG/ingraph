(ns ingraph.sre.queries.poslength-test
  (:require [clojure
             [pprint :as pprint]
             [test :refer :all]]
            [ingraph.sre.ingraph-proto-1 :refer :all]
            [ingraph.sre.queries
             [poslength-pattern :refer [p]]
             [utils :as utils]]
            [ingraph.sre.queries.utils.tb-loader :as tb-loader]
            [sre.plan.pattern :as pattern])
  (:import [ingraph.ire Indexer IngraphVertex]
           [scala.collection.immutable HashMap HashSet]
           scala.Tuple2))

(def poslength-query (pattern/compile p Ingraph {:k 5}))

(comment (pprint/pprint (utils/get-tasks poslength-query)))

(deftest test-poslength-smoke
  (testing "PosLength query should return 1 row"
    (let [smoke (doto (Indexer.)
                  (.addVertex (IngraphVertex. 0
                                              (-> (HashSet.) (.$plus "Segment"))
                                              (-> (HashMap.) (.$plus (Tuple2. "length" -1)))))
                  (.addVertex (IngraphVertex. 1
                                              (-> (HashSet.) (.$plus "Segment"))
                                              (-> (HashMap.) (.$plus (Tuple2. "length" 9))))))]
      (is (= (count (into [] (pattern/run poslength-query [:segment :length] {:indexer smoke}))) 1)))))

(def tb-inject-1 (tb-loader/load 'inject-1))

(deftest test-poslenght-tb-inject-1
  (testing "PosLength query should return expected results for TrainBenchmark inject-1"
    (let [inject-1 (tb-loader/load 'inject-1)
          results (into [] (pattern/run poslength-query [:segment :length] {:indexer inject-1}))]
      (is (= (count results) 12)))))
