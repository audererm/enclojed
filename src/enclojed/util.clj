(ns enclojed.util
  (:require [clojure.string :as string]
            [clojure.java.io :as io])
  (:import [java.io FileInputStream]
           [java.io BufferedInputStream]
           [javazoom.jl.player Player]))

(defn play-file [filename & opts]
  (let [fis (new FileInputStream (io/file (io/resource filename)))
        bis (new BufferedInputStream fis)
        player (new Player bis)]
    (if-let [synchronously (first opts)]
      (doto player
        (.play)
        (.close))
      (.start (Thread. #(doto player (.play) (.close)))))))

(defn clear
  [columns]
  (doseq [i (range columns)]
    (println))
  (println))

(defn slow-print
  ([text]
   (slow-print text 30))
  ([text speed]
   (doall (map (fn [%]
                 (if (= % \_)
                   (Thread/sleep (* speed 8))
                   (do
                     (play-file "click.mp3")
                     (print %)
                     (flush)
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