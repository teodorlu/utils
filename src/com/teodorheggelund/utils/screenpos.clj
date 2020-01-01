(ns com.teodorheggelund.utils.screenpos
  (:require
   [clojure.string :as string]))

;; Helpers for configuring screen position with xrandr
;;
;;

;; O
;; +------------------+
;; |                  |
;; |                  |
;; |    O'            |
;; +----+--------+----+
;;      |        |
;;      |        |
;;      +--------+
;;
;; Point O (global origin) is at (0,0). I need to find the position of O' (local
;;                                                                         laptop display origin).
;;
;; X coordinate is half of big display minus half of small display. Y coordinate is
;; big display total height. Piece of cake.

(string/split "100x200" #"x")

(defn pos [x y]
  {:x x :y y})

(defn descr->pos [description]
  (let [[xstr ystr & rem] (string/split description #"x")
        x (Integer/parseInt xstr)
        y (Integer/parseInt ystr)]
    (assert (nat-int? x) "Positive x required")
    (assert (nat-int? y) "Positive y required")
    {:x x
     :y y}))

(def wide (pos 3440 1440))
(def laptop (pos 1920 1080))

(defn pos->descr [{:keys [x y]}]
  (str x "x" y))

(do
  (defn position-below-center
    "Position a display below the first one, centered in width"
    [above below]
    (let [{x0 :x y :y} above
          {dx :x} below]
      (pos (- (/ x0 2)
              (/ dx 2))
           y
       )))
  (-> (position-below-center wide laptop)
      pos->descr
      println)
  )

;; $ xrandr --output eDP-1 --auto --output DP-1 --auto --above eDP-1
(do
  (defn unwords [ws]
    (string/join " " ws))

  (defn preferred-xrandr-string []
    (unwords
     ;; '(xrandr --output eDP-1 --auto --output DP-1 --auto --above eDP-1)
     `(~@'(xrandr --output DP-1 --pos "0x0")
       ~@'(--output eDP-1 --pos) ~(-> (position-below-center wide laptop)
                                      pos->descr)
       )
     ))

  (println (preferred-xrandr-string))
  )
