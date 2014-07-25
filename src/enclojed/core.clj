(ns enclojed.core
  (:gen-class)
  (:require [enclojed.util :refer :all]
            [clojure.string :as string]
            [lanterna.terminal :as t])
  (:import [com.eleet.dragonconsole DragonConsoleFrame]))

(declare cmd-quit cmd-unknown cmd-look cmd-help room-map help intro prison-look room-cmd)

(def columns 80)
(def lines 25)

(defn get-room
  ([data] (get room-map (:room data)))
  ([room data] (get room-map room)))

(defn get-description
  ([data] (get (get-room data) (:phase data)))
  ([phase data] (get (get-room data) phase))
  ([room phase data] (get (get-room room) phase)))

(defn get-flag
  [flag data]
  (get (:flags data) flag))

(defn set-flag
  ([flag data]
   (set-flag flag true data))
  ([flag value data]
   (assoc data :flags (assoc (:flags data) flag value))))

(defn has-item?
  [item data]
  (get (:inventory data) item))

(defn set-item
  ([item data]
   (set-item item true data))
  ([item has data]
   (assoc data :inventory (assoc (:inventory data) item has))))

(defn parse-cmd
  [command args data]
  (case command
    ("quit") (cmd-quit data)
    ("look" "view" "examine") (cmd-look args data)
    ("help" "?") (cmd-help args data)
    (room-cmd command args data)))

(defn input
  [prefix]
  (let [width (first (t/get-size term))
        height (second (t/get-size term))
        whitespace (loop [i width
                          space ""]
                     (if (>= i 0)
                       (recur (dec i)
                              (str space " "))
                       space))]
    (t/put-character term prefix 0 height)
    (loop [keypress (t/get-key term)
           command ""
           clear false
           update false]
      (when clear
        (t/put-string term whitespace 1 height))
      (when update
        (t/put-string term command 1 height))
      (Thread/sleep 1)
      (case keypress
        :enter command
        :backspace (recur (t/get-key term)
                          (if (> (.length command) 0)
                            (subs command 0 (dec (.length command)))
                            commadnd) 
                          true
                          true)
        [:escape :end] (cmd-quit)
        [:delete :left :right :up :down :insert :home :tab 
         :reverse-tab :page-up :page-down] (recur (t/get-key term)
                                                  command
                                                  false
                                                  false)
        nil (recur (t/get-key term)
                   command
                   false
                   false)
        (recur (t/get-key term)
               (str command keypress)
               false
               true)))))

(defn ask-do
  [data]
  (slow-print "What would you like to do?" 20)
  (Thread/sleep 150)
  (println)
  (Thread/sleep 50)
  (let [args (string/split (input \>) #" ")
        command (string/lower-case (first args))]
    (Thread/sleep 50)
    (parse-cmd command args data)))

(defn game-repl
  [data]
  (if (not= (:phase data) :waiting)
    (slow-print-all (get (get room-map (:room data)) (:phase data)) #"\n"))
  (Thread/sleep 400)
  (case (:phase data)
    :intro (do (Thread/sleep 600) (game-repl (assoc data :phase :pathway)))
    :pathway (ask-do (assoc data :phase :waiting))
    :waiting (ask-do data)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ;(t/start term)
  (def frame (new DragonConsoleFrame))
  (.setVisible frame true)
  (t/in-terminal term
                 (printstr intro)
                 (loop [keypress (t/get-key-blocking term)] ; Waits for enter key
                   (when-not (= keypress :enter)
                     (recur (t/get-key-blocking term))))
                 (t/clear term)
                 (game-repl {:room :prison, :phase :intro, :inventory {}, :flags {}})))

(load "commands")
(load "rooms")