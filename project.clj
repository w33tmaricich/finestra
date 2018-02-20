(defproject widgets "0.0.1-SNAPSHOT"
  :description "Useful widgets for everyday life."
  :url "https://github.com/w33tmaricich/widgets"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.cli "0.3.5"]
                 [org.clojure/tools.logging "0.4.0"]
                 [clj-http "3.5.0"]
                 [org.clojure/data.json "0.2.6"]
                 [clojure-lanterna "0.9.7"]]
  :main ^:skip-aot widgets.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
