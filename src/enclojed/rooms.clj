(in-ns 'enclojed.core)

(def room-map {:prison {:intro (str "You awaken feeling lost and confused in a poorly lit room.\n"  
                                    "The smell of sweat and body odor fills your nostrils.\n"
                                    "You notice a set of metal bars._._._\n"
                                    "Your memory partially returns. You are in a prison cell.\n"
                                    "You must have been here for a long time. The reason of your "
                                    "imprisonment still escapes your memory._")
                        :pathway (str "A dim light flickers from above, giving off an eerie glow.\n"
                           		 	  "Your body is covered in bruises.\n"
                                 	  "Against the wall is a small cot with a rusty toilet next to it.")
                        :outro ""}})

(def intro (str "Welcome to enclojed! Enclojed is the first ever text-based-game written in clojure.\n"
                       "You can view the game's source code on github after playing, but beware of spoilers!\n"
                       "In enclojed, you play as a prison inmate with a strange case of amnesia.\n"
                       "Type commands in the console to interact with the game. These commands are called 'actions'.\n"
                       "Your goal is to escape the prison and figure out who you are.\n"
                       "There are multiple endings to this game. See if you can find them all!\n"
                       "If you need help, you can type 'help' at any time."))

(def help (str "Type 'look' in the console to examine your surroundings. You can then type 'look [object]' to look at something specific.\n"
                      "Pick up items using 'take [object]'. You can view your inventory by typing 'inventory'.\n"
                      "There are many more actions for you to figure out. Don't hesitate to experiment!\n"
                      "If you need to quit the game, type 'quit' at any time. Progress cannot yet be saved."))

(defn print-return
  "Prints the text, then returns to the game loop"
  [text data]
  (slow-print-all text)
  (game-repl data))

(defn prison-look
  [object data]
  (case (keyword object)
    (:light :lightbulb :bulb) (print-return (str "There is a small flourescent light hanging from the ceiling.\n"
                              "It's flickering on and off.") data)
    :cot (print-return (str "There is a very beat-up looking cot against the wall.\n"
                          "Is this where you've been sleeping? It looks dreadful.\n"
                          "You better find a way out before you get too tired.") data)
    :toilet (if (get-flag :moved-toilet? data)
              (print-return (str "The toilet has been moved aside, revealing a hole in the floor.\n"
                               "You could probably fit through it, but it looks a bit tight.\n"
                               "Not that you have much of a choice...\n") data)
              (print-return (str "The toilet sits on the floor, right next to your 'bed'._\n"
                             "Upon closer inspection, it appears that some bolts are missing from the floor-piece.") data))
    (:hole :floor) (if (get-flag :moved-toilet? data)
            (print-return (str "The toilet has been moved aside, revealing a hole in the floor.\n"
                               "You could probably climb through it, but it looks a bit tight.\n"
                               "Not that you have much of a choice._._._\n") data)
            (print-return "You don't see any holes nearby." data))
    (:bruise :bruises) (print-return "Bruises cover your arms and legs. Whoever locked you here must have beat you up pretty bad.\n" data)
    (:wall :walls) (print-return "The walls loom above you, closing you in. You long for freedom." data)
    (print-return (str "You don't see any " object "s nearby.") data)))

(defn prison-take
  [args data]
  (let [object (second args)]
    (case (keyword object)
      (:light :lightbulb :bulb) (print-return "You reach for the lightbulb, but it's too high up." data)
      :cot (print-return "The cot is bolted to the ground. It won't budge." data)
      (:wall :walls) (print-return "Are you insane? You can't take the walls." data)
      :toilet (print-return "You consider taking the toilet, but decide that might not be too pleasant to carry around." data)
      (print-return (str "You don't see any " object "s nearby.") data))))

(defn prison-move
  [args data]
  (let [object (second args)]
    (case (keyword object)
      (:light :lightbulb :bulb) (print-return "You reach for the lightbulb, but it's too high up." data)
      :cot (print-return "The cot is bolted to the ground. It won't budge." data)
      (:wall :walls) (print-return (str "You " (first args) " on the walls, but it only feels like they're closing in more...") data)
      :toilet (if (get-flag :moved-toilet? data)
                (print-return "You have already moved the toilet over. Beneath it is a narrow hole in the floor." data)
                (do
                  (slow-print-all (str "You " (first args) " hard on the toilet._._._\n"
                                       "It slides forward, revealing an open hole in the ground."))
                  (game-repl (set-flag :moved-toilet? true data))))
      (print-return (str "You don't see any " object "s nearby.") data))))

(defn prison-go
  [args data]
  (let [object (second args)]
    (case (keyword object)
      :toilet (print-return (str "You'd rather not " (first args) " through the toilet...") data)
      :hole (if (get-flag :moved-toilet? data)
              (if (get-flag :has-box? data)
                (do
                  (print-return (str "You have already " (first args) "ed into the hole and found a small box.") data)
                  (slow-print-all (str "You begin to " (first args) " into the hole, but realize it isn't very deep.\n"
                                       "You can feel a small object inside. You pull it out of the hole._\n"
                                       "As the light continues to flicker, you see that the object is a small box."))
                  (game-repl (set-flag :has-box? true data))))
              (print-return "You don't see any holes nearby." data))
      :floor (if (get-flag :moved-toilet? data)
               (print-return (str "Perhaps you should " (first args) " through the newly found hole in the floor!") data)
               (print-return (str "You can't " (first args) " through the floor! Maybe if you could dig...") data))
      (:cot :light :lightbulb :bulb :wall :walls) (print-return "You can't do that." data)
      (print-return "You don't see any " object "s nearby." data))))

(defn prison-cmd
  [command args data]
  (case command
    ("look" "examine" "view" "check") (prison-look (second args) data)
    ("move" "push" "pull" "shove") (prison-move args data)
    ("go" "climb" "enter" "reach" "crawl") (prison-go args data)))