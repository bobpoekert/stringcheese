(ns stringcheese.core-test
  (:use clojure.test
        stringcheese.core))

(def inline-template "<ul>{% (for [row rows] %{ <li> {% row %} </li> }%) %}</ul>")

;; (clojure.pprint/pprint (inner-compile-template 'foo (template-parser inline-template)))
;; (clojure.pprint/pprint (template-parser inline-template))

'[:EXPR
   [:LITERAL "<ul>"]
   [:EXPR_TAG
    "{%"
    [:EXPR_INSIDE
     [:LITERAL " (for [row rows] "]
     [:UNEXPR
      "%{"
      [:UNEXPR_INSIDE
       [:EXPR [:LITERAL " <li> "]]
       [:EXPR [:EXPR_TAG "{%" [:EXPR_INSIDE [:LITERAL " row "]] "%}"]]
       [:LITERAL " </li> "]]
      "}%"]
     [:LITERAL ") "]]
    "%}"]
   [:LITERAL "</ul>"]]



'(do
  (.write res "<ul>")
  (write-value! res
    (for [row rows]
      (let [agg (ArrayListWriter.)]
        (.write agg "<li>")
        (.write agg row)
        (.write agg "</li>")
        (.getValue agg))))
  (.write res "</ul>"))

(deftest parse-template-test
  (testing "template-parses"
    (let [[start-tag body end-tag] (parse-template inline-template)]
      (is (= start-tag {:type :string :value "<ul>"}))
      (is (= body {:type :expr :value '(for [row rows] (format "<li>%s</li>"))}))
      (is (= end-tag {:type :string :value "</ul>"})))))
