(ns core
  (:require
   [libpython-clj2.python :refer [$a from-import initialize!]]))

(initialize!)

(from-import transformers AutoTokenizer)

(def tokenizer
  ($a AutoTokenizer from-pretrained "Qwen/Qwen3-0.6B-Base"))