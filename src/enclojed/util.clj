(ns enclojed.util
  (:require [clojure.string :as string]
            [clojure.java.io :as io]
            [lanterna.terminal :as t]))

(def term (t/get-terminal :swing {:palette :gnome, :font ["Nimbus Mono L" 
                                                          "Courier" 
                                                          "Courier New" 
                                                          "Courier 10"]}))

(defn printchar
  [c]
  (t/put-character term c))

(defn printstr
  [s]
  (dorun (map #(printchar %) s)))

(defn printline
  ([s] 
   (printstr s)
   (printline))
  ([]
   ()))

(defn slow-print
  ([text]
   (slow-print text 30))
  ([text speed]
   (doall (map (fn [%]
                 (if (= % \_)
                   (Thread/sleep (* speed 8))
                   (do
                     (printchar %)
                     (Thread/sleep speed))))
               text))))

(defn slow-print-all
  ([text]
   (slow-print-all text #"\n"))
  ([text split] 
   (let [lines (string/split text split)]
    (doall (map (fn [%]
                  (slow-print % 30)
                  (Thread/sleep 400)
                  (println)
                  (Thread/sleep 200)) lines)))))

(defn eval-string
  [s]
  (eval (read-string s)))

(defn esc-string
  [s]
  (str "\"" s "\""))