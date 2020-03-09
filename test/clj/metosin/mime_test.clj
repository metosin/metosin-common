(ns metosin.mime-test
  (:require [clojure.test :refer [deftest testing is]]
            [metosin.mime :as mime]
            [clojure.java.io :as io]))

(deftest mime-type-of-test
  (is (= "text/plain" (mime/mime-type-of (io/resource "foobar.txt"))))
  (is (= "text/x-clojure" (mime/mime-type-of (io/resource "metosin/mime.clj"))))

  (is (= "image/png" (mime/mime-type-of (io/file "dev-resources/test.png"))))
  (is (= "image/png" (mime/mime-type-of (io/file "dev-resources/test.pdf"))))
  (with-open [iss (io/input-stream (io/file "dev-resources/test.png.pdf"))]
    (is (= "image/png" (mime/mime-type-of iss)))))

(deftest extension-for-name-test
  (is (= ".jpg" (mime/extension-for-name "image/jpeg")))
  (is (= ".clj" (mime/extension-for-name "text/x-clojure")))
  (is (= "" (mime/extension-for-name "foo/bar"))))
