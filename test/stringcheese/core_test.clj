(ns stringcheese.core-test
  (:use clojure.test
        stringcheese.core))

(def inline-template "<ul>{% (for [row rows] %{ <li> {% row %} </li> }%) %}</ul>")

;; (clojure.pprint/pprint (inner-compile-template 'res (template-parser inline-template)))
;; (clojure.pprint/pprint (template-parser inline-template))

(stringcheese.core/deftemplate test-template
  "this is a test"
  [rows]
  "<ul>{% (for [row rows] %{ <li> {% row %} </li> }%) %}</ul>")

(deftest parse-template-test
  (testing "template-parses"
    (is (=
      (template-parser inline-template)
      [:EXPR
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
       [:LITERAL "</ul>"]]))))

(deftest test-render
  (testing "template renders"
    (is (= (render-string test-template ["foo" "bar"]) "<ul> <li> foo </li>  <li> bar </li> </ul>"))))
