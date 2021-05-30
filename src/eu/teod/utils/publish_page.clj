(ns eu.teod.utils.publish-page
  (:require [datascript.core :as d]
            [clojure.java.shell :refer [sh with-sh-dir]]
            [clojure.string :as string]
            [clojure.edn :as edn]
            [clojure.java.io :as io]))

;; Find the download

(defn ^:private latest-roam-export []
  (let [downloads-dir (str (System/getenv "HOME")
                           "/Downloads/")
        bash-cmd "ls Roam-Export-*.zip | sort | tail -n 1"
        candidate
        (with-sh-dir downloads-dir
          (-> (sh "bash" "-c" bash-cmd)
              :out
              string/trim))]
    (when-not (string/blank? candidate)
      (str downloads-dir "/" candidate))))

;; Target: Load an in-memory datascript database

(defn ^:private temp-dir []
  (-> (sh "mktemp" "-d")
      :out
      string/trim))

(def ^:private db-name "teod")

(defn roam-db []
  (when-let [download (latest-roam-export)]
    (let [dir (temp-dir)]
      (with-sh-dir dir
        (sh "unzip" download))
      (with-open [r (io/reader (str dir "/" db-name ".edn"))]
        (edn/read {:readers d/data-readers}
                  (java.io.PushbackReader. r))))))

(def ^:private article-page-id "1odcmTECT")

(def teod-db (roam-db))

;; can we query this thing?

(comment
  (d/schema teod-db)

  (d/pull teod-db '[*] [:block/uid article-page-id])
  {:block/children [#:db{:id 36296} #:db{:id 36317} #:db{:id 36342}],
   :block/uid "1odcmTECT",
   :node/title "teod/Descriptive truth, prescriptive truth"})

;; give me a single entity with

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; LOL I don't know Datascript
;;
;; This is why we wanted to do this, I think.

;; let's try by title instead. Or .. by UID?

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; so what ... we need to resolve ...
;; how many layers deep?

(defn explore-1 [db]
  (d/pull db '[:node/title
                {:block/children [:block/string
                                  {:block/children [:block/string]}]}]
          [:block/uid article-page-id]))

;; find the root node!

(d/q [:find '?article '?teod-content
      :where
      ['?article :block/uid article-page-id]
      ['?article :block/children '?teod-content]
      ]
     teod-db)

(defn article-section [db article-id section-string]
  (d/q `[:find ?outline .
         :where
         [?article :block/uid ~article-id]
         [?article :block/children ?outline]
         [?outline :block/string ~section-string]]
       db))

(article-section teod-db article-page-id "[[teod/outline]]")
