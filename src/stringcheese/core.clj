(ns stringcheese.core
  (require [clojure.string :as s]
           [instaparse.core :as insta])
  (import [stringcheese ArrayListWriter TemplateException]))

(def template-parser
  (insta/parser
    "EXPR = (EXPR_TAG | LITERAL)+
     EXPR_TAG = '{%' EXPR_INSIDE '%}'
     LITERAL = #'(?is)^(?:(?!(\\{%)|(%\\})|(%\\{)|(\\}%)).)+'
     EXPR_INSIDE = (LITERAL | UNEXPR)+
     UNEXPR = '%{' UNEXPR_INSIDE '}%'
     UNEXPR_INSIDE = (LITERAL | EXPR)+")) 

(declare inner-compile-template)

(defn gen-unexpr
  [args]
  (let [writer-sym (gensym "writer")]
    `(let [^stringcheese.ArrayListWriter ~writer-sym (stringcheese.ArrayListWriter.)]
      ~@(map (partial inner-compile-template writer-sym) args)
      (.getContent ~writer-sym))))

(defn gen-expr-inside
  [[tag & args]]
  (case tag
    :LITERAL (first args)
    :UNEXPR (recur (second args))
    :UNEXPR_INSIDE (prn-str (gen-unexpr args))))

(defn inner-compile-template
  [writer-sym [tag & args]]
  (case tag
    :EXPR (cons 'do (map (partial inner-compile-template writer-sym) args))
    :LITERAL `(.write ~writer-sym ~(first args))
    :EXPR_TAG (recur writer-sym (second args))
    :EXPR_INSIDE `(stringcheese.core/write-value!
                  ~(read-string (apply str (map gen-expr-inside args)))
                  ~writer-sym)))

(defmacro deftemplate
  [nom docstring arglist template]
  (let [outp-sym (with-meta (gensym "outp") {:tag 'java.io.Writer})]
    `(defn ~nom ~docstring
      ~(into [outp-sym] arglist)
      ~(inner-compile-template outp-sym (template-parser (eval template))))))

(defprotocol WriteValue
  (write-value! [v target]))

(extend-protocol WriteValue
  java.util.List
  (write-value! 
    [v ^java.io.Writer target]
    (let [^java.util.Iterator it (.iterator v)]
      (while (.hasNext it)
        (write-value! (.next it) target))))
  Object
  (write-value!
    [v ^java.io.Writer target]
    (.write target (str v))))

(defn render-string
  [thunk & args]
  (let [outp (java.io.StringWriter.)]
    (apply thunk outp args)
    (.toString outp)))

(defn render-list
  [thunk & args]
  (let [^ArrayListWriter outp (ArrayListWriter.)]
    (apply thunk outp args)
    (.getContent outp)))
