(ns core
  (:require
   [clojure.data.priority-map :refer [priority-map-by]]
   [clojure.math :refer [log]]
   [clojure.string :refer [includes?]]
   [com.rpl.specter :refer [AFTER-ELEM ALL BEGINNING setval]]
   [libpython-clj2.python :refer [$a ->py-list from-import get-item initialize! py.. with]]))

(initialize!)

(from-import builtins slice)

(from-import torch nn no_grad nonzero tensor)

(from-import transformers AutoModelForCausalLM AutoTokenizer)

(def model-name
  "Qwen/Qwen3-0.6B-Base")

(def tokenizer
  ($a AutoTokenizer from_pretrained model-name))

(def model
  ($a AutoModelForCausalLM from_pretrained model-name))

(def prompt
  "She's like, \"")

(def exponent
  1)

(def threshold
  (- (* exponent (log 10))))

(defn pop-n
  [n coll]
  (if (or (zero? n) (empty? coll))
    coll
    (recur (dec n) (pop coll))))

(def pad-token-id
  (py.. tokenizer -pad_token_id))

(def max-count
  (comp (partial apply max)
        (partial map count)))

(def tensor*
  (comp tensor ->py-list))

(defn prepare-batch-tensor
  [token-sequences]
  (let [target (max-count token-sequences)]
    (tensor* (map #(setval BEGINNING (repeat (- target (count %)) pad-token-id) %) token-sequences))))

(defn prepare-mask-tensor
  [token-sequences]
  (let [target (max-count token-sequences)]
    (->> token-sequences
         (setval [ALL ALL] 1)
         (map #(setval BEGINNING (repeat (- target (count %)) 0) %))
         tensor*)))

(defn predict
  [token-sequences]
  (py.. nn
        -functional
        (log_softmax (get-item (py.. (with [_ (no_grad)]
                                           (model (prepare-batch-tensor token-sequences) (prepare-mask-tensor token-sequences)))
                                     -logits)
                               [(slice nil) -1 (slice nil)]))))

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

(def batch-size
  2)

(defn expand-node
  [predictions [prefix-sequence prefix-likelihood]]
  (let [surviving-tokens (-> predictions
                             ($a ge (- threshold prefix-likelihood))
                             nonzero
                             (py.. flatten)
                             (py.. tolist))]
    (map (fn [token likelihood]
           [(setval AFTER-ELEM token prefix-sequence) (+ prefix-likelihood likelihood)])
         surviving-tokens
         (py.. (get-item predictions surviving-tokens) tolist))))

(defn search-step
  [m]
  (into (pop-n batch-size m) (mapcat expand-node
                                     (predict (map first (take batch-size m)))
                                     (take batch-size m))))

(defn -main
  []
  (search-step (priority-map-by > ($a tokenizer encode prompt) 0)))