# stringcheese

Stringcheese is a string template library for clojure. Libraries like [Hiccup](https://github.com/weavejester/hiccup) are excellent when you want to generate standard HTML from scratch, but sometimes you need to do something cheesy. Maybe you got a blob of HTML from a WYSIWYG tool that you need to templatize. Maybe the HTML you want to geneate needs to have things in it that aren't quite HTML (like [edge-side-include tags](https://www.varnish-cache.org/docs/3.0/tutorial/esi.html)). Maybe you're generating somehting that isn't HTML at all (like nginx configuration files). In these cases and many more, Stringcheese can help.

## Usage

```clojure
(require '[stringcheese.core :as cheese])

(cheese/deftemplate confirmation-email
    "This is the email that we send to people right after they create an account."
    [user-name creation-date]
    (clojure.java.io/resource "confirmation-email.html"))


(cheese/require-templates
    email-templates
    "resources/email-templates/")

(cheese/render-string confirmation-email "Jane Doe" (java.util.Date.))

(cheese/render-string email-templates/direct-message (User. 12345) (User. 56789) "howdy")
```

The template language has two kinds if tag: `{% ... %}` and `{%* ... *%}`.

Anything inside `{%` `%}` blocks gets interpreted as a clojure expression, and the results go in the output.

Anything inside `{%*` `*}` blocks gets interpreted as metadata for the clojure function that this template represents. If the thing inside the brackets is a vector, it's interpreted as an arglist. If it's a string, it's interpreted as a docstring. If it's a map, it's interpreted as metadata. Anything else is an error.

When you call `require-templates` and pass it a directory, it generates a clojure namespace for that directory with a funciton for each template file, whose name is the filename. This is why `{%* ... *%}` tags exist.

An expression in a `{% ... %}` is expected to return one of three things:

* A string, or something that can be turned into a string (with `str`)
* Something that implements java.util.List (this includes clojure sequences). In this case we iterate over the elements and write them out one by one.
* Something that you can call `deref` on. When we hit one of these we evaluate the rest of the templtae in two passes. On the first pass we evaluate all the expressions, and on the second we deref everything that can be deref'd and write everythng out. This allows for fetching multiple data dependencies in parallel.

## License

Copyright © 2015 Bob Poekert

Distributed under the MIT License.
