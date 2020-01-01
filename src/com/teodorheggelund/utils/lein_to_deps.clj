(ns com.teodorheggelund.utils.lein-to-deps)

(defn lein->mvn-deps [data]
  (into {}
        (for [[artifact version] data]
          [artifact {:mvn/version version}])))
