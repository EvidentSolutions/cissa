Cissa is a simple CSS-preprocessor. Any valid CSS-file is in itself valid Cissa-markup,
but Cissa provides some additional features on top of CSS. The features are briefly
introduced here, but see [cissa-core/src/test/java/fi/evident/cissa/CissaTest.java](https://bitbucket.org/evidentsolutions/cissa/src/default/cissa-core/src/test/java/fi/evident/cissa/CissaTest.java) for details.

Single line comments
====================

You can use // to make comments that reach until the end of line.

Nested selectors
================

Normally you have to repeat your selectors several times when writing CSS. This
makes it hard to see the structure of the file and to make changes.

    :::css
    .foo {
      rules
    }

    .foo .bar {
      rules
    }

    .foo .baz {
      rules
    }

Cissa lets you nest rule-sets inside other rule-sets:

    :::css
    .foo {
      rules

      .bar {
        rules
      }

      .baz {
        rules
      }
    }

Variables
=========

Often you need to refer to the same thing in multiple different rules. Cissa let's you
define variables in any scope. The variables are visible to all nested scopes.

    :::css
    $favoriteColor: green;

    .foo {
      background-color: $favoriteColor;
    }

    .bar {
      $otherColor: red;

      color: $favoriteColor;
      background: $otherColor;

      .baz {
        color: $otherColor;
      }
    }

Expressions
===========

You can use arithmetic expressions when computing values of properties. Cissa will
make sure that you use compatible units.

    :::css
    $leftColumnWidth: 300px;
    $defaultPadding: 10px;
    $myColor: #442485;

    .center {
      position: absolute;
      left: $leftColumnWidth + $defaultPadding;
      color: $myColor + #111111;
    }

Cissa vs. Sass
==============

Originally by accident, later by design, Cissa is compatible with a large subset of
the .scss-syntax of [Sass](http://sass-lang.com/). Thus you can mostly consider Cissa
as an implementation of Sass for Java environments. At least you get to take advantage
of editor support for Sass when writing Cissa markup.
