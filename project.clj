(defproject enclojed "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 ;[overtone "0.6.0"]
                 [clojure-lanterna "0.9.4"]
                 [dragonconsole "3.0.0"]]
  :repositories [["local" {:url ~(str (.toURI (java.io.File. "maven_repository")))}]]
  :main ^:skip-aot enclojed.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})