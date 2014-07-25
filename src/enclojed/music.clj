(ns enclojed.music
  (:require [overtone.live :refer :all]))

(defn ctlinst
  [instr param value]
  (ctl instr param value))

(defn makenoise
  []
  (definst noisey [freq 440 attack 2 sustain 5 release 2 vol 1.0]
  (* (env-gen (lin-env attack sustain release) 1 1 0 1 FREE)
     (pink-noise)
     vol)))

(defn makebeep
  []
  (definst keystroke [freq 440 attack 0.01 sustain 0.1 release 0.01 vol 1.0]
    (* (env-gen (lin-env attack sustain release) 1 1 0 1 FREE)
       (sin-osc)
       vol)))