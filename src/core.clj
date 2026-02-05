(ns core
  (:require
   [libpython-clj2.python :refer [$a from-import initialize!]]))

(initialize!)

(from-import transformers AutoModelForCausalLM AutoTokenizer)

(def model-name
  "Qwen/Qwen3-0.6B-Base")

(def tokenizer
  ($a AutoTokenizer from_pretrained model-name))

(def model
  ($a AutoModelForCausalLM from_pretrained model-name))

(defn decode*
  [x]
  ($a tokenizer decode x))