# stringcheese

Stringcheese is a string template library for clojure. Libraries like Hiccup are excellent when you want to generate standard HTML from scratch, but sometimes you need to do something cheesy. Maybe you got a blob of HTML from a WYSIWYG tool that you need to templatize. Maybe the HTML you want to geneate needs to have things in it that aren't quite HTML (like edge-side-include tags). Maybe you're generating somehting that isn't HTML at all (like nginx configuration files). In these cases and many more, Stringcheese can help.

## Usage

```clojure
(cheese/deftemplate confirmation-email
    "This is the email that we send to people right after they create an account."
    [user-name creation-date]
    (clojure.java.io/resource "confirmation_email.html"))
```

The template language has one kind if tag: `{% ... %}`.

Anything inside those `{%` `%}` blocks gets interpreted as a clojure expression, and the results go in the output.

The expression is expected to return one of three things:

* A string, or something that can be turned into a string (with `str`)
* Something that implements java.util.List (this includes clojure sequences). In this case we iterate over the elements and write them out one by one.
* Somehting that you can call `deref` on. When we hit one of these we evaluate the rest of the templtae in two passes. On the first pass we evaluate all the expressions, and on the second we deref everything that can be deref'd and write everythng out. This allows for fetching multiple data dependencies in parallel.

## License

Copyright © 2015 Bob Poekert

Distributed under the MIT License.
