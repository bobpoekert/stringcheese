# stringcheese

Stringcheese is a string template library for clojure. Libraries like [Hiccup](https://github.com/weavejester/hiccup) are excellent when you want to generate standard HTML from scratch, but sometimes you need to do something cheesy. Maybe you got a blob of HTML from a WYSIWYG tool that you need to templatize. Maybe the HTML you want to geneate needs to have things in it that aren't quite HTML (like [edge-side-include tags](https://www.varnish-cache.org/docs/3.0/tutorial/esi.html)). Maybe you're generating somehting that isn't HTML at all (like nginx configuration files). In these cases and many more, Stringcheese can help.

## Usage

```clojure
(require '[stringcheese.core :as cheese])

(cheese/deftemplate confirmation-email
    "This is the email that we send to people right after they create an account."
    [user-name creation-date]
    (slurp (clojure.java.io/resource "confirmation-email.html")))

(cheese/deftemplate results
    [rows]
    "<html><body><ul>{% (for [row rows] %{ <li> {% row %} </li> }%) %}</ul></body></html>")

(cheese/render-string confirmation-email "Jane Doe" (java.util.Date.))

(cheese/render-string email-templates/direct-message (User. 12345) (User. 56789) "howdy")
```

The template language has two kinds if tag: `{% ... %}` and `%{ ... }%`.

Anything inside `{%` `%}` blocks gets interpreted as a clojure expression, and the results go in the output.

An expression in a `{% ... %}` is expected to return one of two things:

* A string, or something that can be turned into a string (with `str`)
* Something that implements java.util.List (this includes clojure sequences). In this case we iterate over the elements and write them out one by one.

`%{ ... }%` blocks go inside `{% ... %}` blocks and mode-switch back to literal mode. This allows you to embed sub-expressions that are themselves templates inside the expressions in your templates (like the `<li>` tag in the previous example).. 

## License

Copyright © 2015 Bob Poekert

Distributed under the MIT License.
