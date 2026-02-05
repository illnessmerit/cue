(ns core
  (:require
   [libpython-clj2.python :refer [from-import initialize!]]))

(initialize!)

(from-import transformers AutoTokenizer)
