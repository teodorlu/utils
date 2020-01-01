(ns com.teodorheggelund.utils.calendar
  (:require
   [clojure.string :as string]))

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

(defn iterate-month
  "Create a lazy seq of [day-nr day] starting at starting-day"
  [starting-day]
  (if-not (day starting-day)
    nil
    (let [days (drop-while #(not= starting-day %)
                         (cycle day-order))]
      (map (fn [nr day]
             [nr day])
           (drop 1 (range))
           days))))

(defn fill-org-log-month-english
  "Generate \"** 01 Thursday\n**02 Friday\n ... for a month"
  [starting-day]
  (string/join "\n"
               (for [[nr day] (take 31 (iterate-month starting-day))]
                 (format "** %02d %s" nr (english-day-name day)))))

(defn -main [starting-day]
  (assert (day (keyword starting-day)))
  (println (fill-org-log-month-english (keyword starting-day))))
