(in-ns 'enclojed.core)

(defn cmd-quit 
  [data]
  (slow-print "Are you sure you wish to quit enclojed?" 20)
  (Thread/sleep 150)
  (println)
  (Thread/sleep 50)
  (case (string/lower-case (input ">"))
    ("y" "ye" "yes") (do 
                       (slow-print "Thank you for playing enclojed." 20)
                       (Thread/sleep 300)
                       (println))
    (game-repl data)))

(defn cmd-help
  [args data]
  (if (> (count args) 1)
    (do
      )
    (do
      (slow-print-all help)
      (game-repl data))))

(defn room-cmd
  [command args data]
  (eval-string (str "(enclojed.core/" (name (:room data)) "-cmd " (esc-string command) " " args " " data ")")))

(defn cmd-look
  [args data]
  (if (> (count args) 1)
    (do
      (eval-string (str "(enclojed.core/" (name (:room data)) "-look " (esc-string (second args)) " " data ")")))
    (do
      (slow-print-all (get-description :pathway data) #"/n")
      (game-repl data))))

(defn cmd-unknown 
  [command args data]
  (if (> (count args) 1)
    (slow-print (str "You don't know how to " command " a " (second args) ".") 20)
    (slow-print (str "You don't know how to " command ".") 20))
  (Thread/sleep 400)
  (println)
  (game-repl data))