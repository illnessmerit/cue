(ns core
  (:require
   [clojure.string :refer [includes?]]
   [com.rpl.specter :refer [BEGINNING setval]]
   [libpython-clj2.python :refer [$a ->py-list from-import initialize! py..]]))

(initialize!)

(from-import torch tensor)

(from-import transformers AutoModelForCausalLM AutoTokenizer)

(def model-name
  "Qwen/Qwen3-0.6B-Base")

(def tokenizer
  ($a AutoTokenizer from_pretrained model-name))

(def model
  ($a AutoModelForCausalLM from_pretrained model-name))

(def prompt
  "She's like, \"")

(defn pop-n
  [n coll]
  (if (or (zero? n) (empty? coll))
    coll
    (recur (dec n) (pop coll))))

(def pad-token-id
  (py.. tokenizer -pad_token_id))

(defn prepare-batch-tensor
  [token-sequences]
  (let [target (apply max (map count token-sequences))]
    (->> token-sequences
         (map #(setval BEGINNING (repeat (- target (count %)) pad-token-id) %))
         ->py-list
         tensor)))

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
