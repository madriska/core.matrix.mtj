# core.matrix.mtj

A [core.matrix](https://github.com/mikera/core.matrix) backend for 
[matrix-toolkits-java](https://github.com/fommil/matrix-toolkits-java).


## Usage

```clojure
(require '[clojure.core.matrix :refer :all])
(require '[com.madriska.matrix.mtj])
(set-current-implementation :mtj)

(matrix [[1 2] [3 4]])
;; #<DenseMatrix   1.00  2.00
;;   3.00  4.00
;; >
```


## Compliance Testing

core.matrix includes a wonderful compliance-testing suite that verifies
compatibility with the core.matrix API. To run the suite:

```
lein run -m com.madriska.matrix.mtj
```


## Releases and Dependency Information

Latest stable release: 0.0.1

[Leiningen](https://github.com/technomancy/leiningen) dependency information:

```clojure
[madriska/core.matrix.mtj "0.0.1"]
```

[Maven](http://maven.apache.org/) dependency information:

```xml
<dependency>
  <groupId>madriska</groupId>
  <artifactId>core.matrix.mtj</artifactId>
  <version>0.0.1</version>
</dependency>
```


## License

Copyright © 2014 Madriska, Inc.

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
