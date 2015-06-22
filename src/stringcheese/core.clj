(ns stringcheese.core
  (require [clojure.string :as s]
           [instaparse.core :as insta])
  (import [stringcheese ArrayListWriter TemplateException]))

(def template-parser
  (insta/parser
    "EXPR = (EXPR_TAG | EXPR_LITERAL)+
     EXPR_TAG = '{%' EXPR '%}'
     EXPR_LITERAL = #'(?:(?!(\\{%)|(%\\})).)+'"))


;; template-parser

(defn parse-template
  [template-string]
  (let [pieces (re-seq expr-re template-string)]
    (for [piece pieces]
      (cond
        (= (count piece) 1) {:type :string :value (first piece)}
        (.startsWith (second piece) "*") {:type :meta :value (read-string (.substring (second piece) 1 -1))}
        :else {:type :expr :value (read-string (second piece))}))))

(defn extract-meta
  [template-results]
  (let [[_meta body] (group-by #(= (:type %) :meta) template-results)
        docstring (or (first (map string? _meta)) nil)
        arglist (or (first (map vector? _meta)) nil)]
    {:arglist arglist
     :docstring docstring
     :body body}))

;;TODO
(defn template-error
  [ex-sym fragment]
  `(stringcheese.TemplateException.
      ~ex-sym ~(:line-number fragment) ~(:char-number fragment) ~(:source fragment)))

(defprotocol WriteValue
  java.util.List
  (write-value! 
    [v ^java.io.Writer target]
    (let [^java.util.Iteartor it (.iterator v)]
      (while (.hasNext it)
        (write-value! (.next it)))))
  Object
  (write-value!
    [v ^java.io.Writer target]
    (.write target (str v))))

(defn generate-funciton
  [function-name {:keys [arglist docstring body]}]
  (let [writer-sym (with-meta (gensym "writer") {:tag 'java.io.Writer})
        ex-sym (gensym exception)]
    `(defn ~function-name
      ~docstring
      ~(into [writer-sym] arglist)
      ~@(for [fragment body]
        (case (:type fragment)
          :value `(.write ~writer-sym ~(:value fragment))
          :expr `(try
                  (stringcheese.core/write-value! ~(:value fragment) ~writer-sym)
                  (catch Exception ~ex-sym (throw ~@(template-error ex-sym fragment)))))))))

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
