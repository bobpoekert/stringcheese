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

(stringcheese.core/deftemplate resource-template
  "resource test"
  [title rows]
  (slurp (clojure.java.io/resource "test.html")))

(render-string resource-template "foo" (range 10))

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
    (is (= (render-string test-template ["foo" "bar"]) "<ul> <li> foo </li>  <li> bar </li> </ul>")))
  (testing "file template renders"
    (is (= (render-string resource-template "foo" (range 10)) "<html>\n    <head>\n        <title>foo</title>\n    </head>\n    <body>\n        <ul>\n             <li> 0 </li>  <li> 1 </li>  <li> 2 </li>  <li> 3 </li>  <li> 4 </li>  <li> 5 </li>  <li> 6 </li>  <li> 7 </li>  <li> 8 </li>  <li> 9 </li> \n        </ul>\n    </body>\n</html>\n"))))
