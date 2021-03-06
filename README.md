Cissa
=====

Cissa is a simple CSS-preprocessor. Any valid CSS-file is in itself valid Cissa-markup,
but Cissa provides some additional features on top of CSS. The features are briefly
introduced here, but see [cissa-core/src/test/java/fi/evident/cissa/CissaTest.java](https://github.com/EvidentSolutions/cissa/blob/master/cissa-core/src/test/java/fi/evident/cissa/CissaTest.java) for details.

Single line comments
====================

You can use `//` to make comments that reach until the end of line.

Nested selectors
================

Normally you have to repeat your selectors several times when writing CSS. This
makes it hard to see the structure of the file and to make changes.

```scss
.foo {
    rules
}

.foo .bar {
    rules
}

.foo .baz {
    rules
}
```

Cissa lets you nest rule-sets inside other rule-sets:

```scss
.foo {
  rules

  .bar {
    rules
  }

  .baz {
    rules
  }
}
```

Variables
=========

Often you need to refer to the same thing in multiple different rules. Cissa let's you
define variables in any scope. The variables are visible to all nested scopes.

```scss
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
```

Expressions
===========

You can use arithmetic expressions when computing values of properties. Cissa will
make sure that you use compatible units.

```scss
$leftColumnWidth: 300px;
$defaultPadding: 10px;
$myColor: #442485;

.center {
  position: absolute;
  left: $leftColumnWidth + $defaultPadding;
  color: $myColor + #111111;
}
```

Cissa vs. Sass
==============

Originally by accident, later by design, Cissa is compatible with a large subset of
the .scss-syntax of [Sass](http://sass-lang.com/). Thus you can mostly consider Cissa
as an implementation of Sass for Java environments. At least you get to take advantage
of editor support for Sass when writing Cissa markup.

Using Cissa with Maven
======================

Cissa is available on the central Maven repository, so just add the following
dependency to your pom.xml to use the programmatic interface:

```xml
<dependency>
    <groupId>fi.evident.cissa</groupId>
    <artifactId>cissa-core</artifactId>
    <version>0.5.0</version>
</dependency>
```

To use the servlet in your web application, add the following instead:

```xml
<dependency>
    <groupId>fi.evident.cissa</groupId>
    <artifactId>cissa-servlet</artifactId>
    <version>0.5.0</version>
</dependency>
```

Attributions
============

Image of cat used on the website is by [Joaquim Alves Gaspar](http://tinyurl.com/alvesgaspar)
and is used by [CC BY-SA 3.0](http://creativecommons.org/licenses/by-sa/3.0/).
