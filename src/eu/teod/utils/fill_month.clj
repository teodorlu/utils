(ns eu.teod.utils.fill-month
  (:require [java-time :as time]
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

  (let [date (time/local-date)]
    (.getValue (time/month date)))
  ;; => 4
  )

(def month-nr->month
  {1 :jan
   2 :feb
   3 :mar
   4 :apr
   5 :may
   6 :jun
   7 :jul
   8 :aug
   9 :sep
   10 :oct
   11 :nov
   12 :dec})

(def month->english-name
  {:jan "January"
   :feb "February"
   :mar "March"
   :apr "April"
   :may "May"
   :jun "June"
   :jul "July"
   :aug "August"
   :sep "September"
   :oct "October"
   :nov "November"
   :dec "December"})


(defn fill-org-log-month-english
  "Generate \"** 01 Thursday\n**02 Friday\n ... for a month"
  [date]
  (let [starting-day (month-start-weekday date)
        month-days (month-days date)
        month-nr (-> date time/month .getValue)
        month-name (-> month-nr month-nr->month month->english-name)]
    (str
     (format "* Future\n%02d %s\n" month-nr month-name)
     (string/join "\n"
                  (for [[nr day] (iterate-month starting-day month-days)]
                    (format "** %02d %s" nr (english-day-name day)))))))

(comment
  (fill-org-log-month-english (time/plus (time/local-date) (time/days 5)))
  )

(defn -main
  ([] (-main "0"))
  ([advance-days-str]
   (let [advance-days (Long/parseLong advance-days-str)
         today (time/local-date)
         heuristic-month-gen-date (time/plus today (time/days advance-days))]
     (binding [*out* *err*]
       (println "Today:" (str today))
       (println "Advanced" advance-days-str "days into" (str heuristic-month-gen-date)))
     (println (fill-org-log-month-english heuristic-month-gen-date)))))

(comment
  (-main)

 )

