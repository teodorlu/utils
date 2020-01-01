(ns eu.teod.utils.fill-month
  (:require [clojure.spec.alpha :as s]
            [java-time :as time]
            [java-time.repl]
            [clojure.string :as string]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def day-order [:mon :tue :wed :thu :fri :sat :sun])

(def day (into #{} day-order))

(def norwegian-day-name
  {:mon "Mandag"
   :tue "Tirsdag"
   :wed "Onsdag"
   :thu "Torsdag"
   :fri "Fredag"
   :sat "Lørdag"
   :sun "Søndag"})

(def english-day-name
  {:mon "Monday"
   :tue "Tuesday"
   :wed "Wednesday"
   :thu "Thursday"
   :fri "Friday"
   :sat "Saturday"
   :sun "Sunday"})

(def java-day-order ["MONDAY" "TUESDAY" "WEDNESDAY" "THURSDAY" "FRIDAY" "SATURDAY" "SUNDAY"])

(def day->java-day
  (zipmap day-order java-day-order))

(def java-day->day
  (zipmap java-day-order day-order))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn month-start-weekday
  "Get the first weekday in the month of a given date"
  {:test #(do
            (assert (= :wed
                       (month-start-weekday (time/local-date 2020 1 5)))))}
  [date]
  (let [month-start-date (time/adjust date :first-day-of-month)]
    (-> month-start-date
        time/day-of-week
        str
        java-day->day)))

(comment
  (test #'month-start-weekday)
  )

(defn month-days
  "Get the number of days in a month"
  {:test #(do
            (assert (= 31 (month-days (time/local-date 2020 1 5)))))}
  [date]
  (-> date
      (time/adjust :last-day-of-month)
      (time/as :day-of-month)))

(comment
  (test #'month-days)
  )

(defn iterate-month [starting-day month-days]
  (assert (day starting-day))
  (assert (<= 1 month-days 31))
  (take month-days
        (let [days (drop-while #(not= starting-day %)
                               (cycle day-order))]
          (map (fn [nr day]
                 [nr day])
               (drop 1 (range))
               days))))

(comment
  (let [today (time/local-date)]
    (iterate-month (month-start-weekday today)
                   (month-days today)))
  )


(defn fill-org-log-month-english
  "Generate \"** 01 Thursday\n**02 Friday\n ... for a month"
  [date]
  (let [starting-day (month-start-weekday date)
        month-days (month-days date)]
    (string/join "\n"
                 (for [[nr day] (iterate-month starting-day month-days)]
                   (format "** %02d %s" nr (english-day-name day))))))

(comment
  (fill-org-log-month-english (time/local-date))
  )

(defn -main []
  (let [today (time/local-date)]
    (println (fill-org-log-month-english today))))

(comment
  (-main)
 )

