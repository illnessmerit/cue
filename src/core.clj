(ns core
  (:require
   [clojure.string :refer [includes?]]
   [libpython-clj2.python :refer [$a from-import initialize! py..]]))

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

(def vocab
  (->> (py.. tokenizer -vocab_size)
       inc
       range
       (map (juxt decode* identity))))

(defn stop?
  [s]
  (or (includes? s ".")
      (includes? s "?")
      (includes? s "!")))

(defn fragment?
  [s]
  (and (not (stop? s))
       (includes? s "\"")))

(def stop-tokens
  (map last (filter (comp stop? first) vocab)))

(def fragment-tokens
  (map last (filter (comp fragment? first) vocab)))
