(defproject widgets "0.1.0-develop-SNAPSHOT"
  :description "Useful widgets for everyday life."
  :url "https://github.com/w33tmaricich/widgets"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clojure-lanterna "0.9.7"]]
  :main ^:skip-aot widgets.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
