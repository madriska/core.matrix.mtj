(ns com.madriska.matrix.mtj
  (:require [clojure.core.matrix.protocols :as mp]
            [clojure.core.matrix.implementations :as imp]
            [clojure.core.matrix.compliance-tester :as compliance]
            [clojure.core.matrix.utils :refer :all])
  (:import [no.uib.cipr.matrix DenseMatrix DenseVector]))

(set! *warn-on-reflection* true)
(set! *unchecked-math* true)

(extend-type DenseMatrix
  mp/PImplementation
    (implementation-key [m] :mtj)
    (meta-info [m]
      {:doc "Core.matrix implementation for matrix-toolkits-java"})
    (new-vector [m ^long length]
      (DenseVector. length))
    (new-matrix [m ^long rows ^long columns]
      (DenseMatrix. rows columns))
    (new-matrix-nd [m dims]
      (case (count dims)
        1 (apply mp/new-vector m dims)
        2 (apply mp/new-matrix m dims)
        :else (error "MTJ new-matrix-nd only supports vectors and matrices!")))
    (construct-matrix [m data]
      (cond
        (instance? DenseMatrix data)
          (.copy ^DenseMatrix data)
        (instance? DenseVector data)
          (.copy ^DenseVector data)
        (mp/is-scalar? data)
          (double data)
        (or (mp/is-vector? data)
            (mp/is-scalar? (first data)))
          (doto (mp/new-vector m (count data))
            (#(doseq [[i x] (map-indexed vector data)]
                (.set ^DenseVector % ^long i ^double x))))
        (empty? data)
          (DenseMatrix. 0 0)
        (seq data)
          (let [m (count (seq data)),
                n (count (seq (first data)))]
            (doto (DenseMatrix. m n)
              (#(doseq [[i row] (map-indexed vector data),
                        [j val] (map-indexed vector row)]
                  (.set ^DenseMatrix % i j val)))))
        :else (error "Can't construct matrix from: " data)))
    (supports-dimensionality? [m dims]
      (#{1 2} dims))

  mp/PDimensionInfo
    (dimensionality [m] 2)
    (get-shape [m] [(.numRows m) (.numColumns m)])
    (is-scalar? [m] false)
    (is-vector? [m] false)
    (dimension-count [m dimension-number]
      (case dimension-number
        0 (.numRows m)
        1 (.numColumns m)
        :else (error "Illegal dimension number")))

  mp/PIndexedAccess
    (get-1d [m x] (error "get-1d only supported on vectors"))
    (get-2d [m ^long x ^long y] (.get m x y))
    (get-nd [m dims] (if (= 2 (count dims))
                       (apply mp/get-2d m dims)
                       (error "Only 2 dimensions are supported!")))

  mp/PIndexedSetting
    (set-1d [m x] (error "set-1d not supported"))
    (set-2d [m row column v]
      (doto ^DenseMatrix (mp/clone m)
        (.set row column v)))
    (set-nd [m dims v]
      (if (= 2 (count dims))
        (mp/set-2d m (first dims) (second dims) v)
        (error "Only 2 dimensions are supported!")))
    (is-mutable? [m] true)

  mp/PIndexedSettingMutable
    (set-1d! [m x] (error "set-1d! not supported"))
    (set-2d! [m row column v]
      (doto m (.set row column v)))
    (set-nd! [m dims v]
      (if (= 2 (count dims))
        (mp/set-2d! m (first dims) (second dims) v)
        (error "Only 2 dimensions are supported!")))

  mp/PMatrixCloning
    (clone [m] (.copy m))

  mp/PImmutableMatrixConstruction
    (immutable-matrix [m] (mp/convert-to-nested-vectors m)))

(extend-type DenseVector
  mp/PDimensionInfo
    (dimensionality [m] 1)
    (get-shape [v] [(.size v)])
    (is-scalar? [v] false)
    (is-vector? [v] true)
    (dimension-count [v dimension-number]
      (case dimension-number
        0 (.size v)
        :else (error "Illegal dimension number")))

  mp/PIndexedAccess
    (get-1d [v ^long x] (.get v x))
    (get-2d [v x y] (error "get-2d only supported on matrices"))
    (get-nd [v dims] (if (= 1 (count dims))
                       (apply mp/get-1d v dims)
                       (error "Only 1 dimension is supported!")))

  mp/PIndexedSetting
    (set-1d [v ^long x ^double val]
      (doto ^DenseVector (mp/clone v)
        (.set x val)))
    (set-2d [v row column x] (error "set-2d only supported on matrices"))
    (set-nd [v dims val]
      (if (= 1 (count dims))
        (mp/set-1d v (first dims) val)
        (error "Only 1 dimension is supported!")))
    (is-mutable? [m] true)

  mp/PIndexedSettingMutable
    (set-1d! [v ^long x ^double val] (doto v (.set x val)))
    (set-2d! [v row column val] (error "set-2d! only supported on matrices"))
    (set-nd! [v dims val]
      (if (= 1 (count dims))
        (mp/set-1d! v (first dims) val)
        (error "Only 1 dimension is supported!")))

  mp/PMatrixCloning
    (clone [v] (.copy v))

  mp/PImmutableMatrixConstruction
    (immutable-matrix [v] (mp/convert-to-nested-vectors v)))

(imp/register-implementation (DenseMatrix. 1 1))

;; Run compliance checks with:
;;
;;   lein run -m com.madriska.matrix.mtj
;;
(defn -main []
  (compliance/compliance-test :mtj)
  (println "Everything looks good!"))
