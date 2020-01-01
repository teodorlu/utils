(ns com.teodorheggelund.utils.genmac
  (:require [clojure.string :as str]))

;; Tools for generating mac addresses
;;
;; Example of valid MAC address:
;;
;;   00:e1:8c:8b:58:62
;;
;; Number size: 0-16*2. How much space is this? Does it fit anywhere?

(defn gen-mac
  "Generate a 6-tuple of numbers between 0 and 255"
  []
  (repeatedly 6 #(rand-int 256)))

(defn format-mac-hex-component
  "Format component for linux ip link mac address format"
  [n]
  (format "%02x" (bit-and 0xff n)))

(defn show-mac
  "Show a mac address compatible with linux `ip link` format"
  [mac]
  (->> mac
       (map #'format-mac-hex-component)
       (str/join ":")))

(comment
  (-main)
  )

(defn -main []
  (-> (gen-mac)
      show-mac
      println))
