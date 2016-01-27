(defproject kramberry "0.1.0"
  :description ""
  :url ""
  :license {:name "Mozilla Public License 2.0"
            :url "https://www.mozilla.org/en-US/MPL/2.0/"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.7.228"]
                 [org.clojure/core.async "0.2.374"]]

  :plugins [[lein-cljsbuild "1.1.1"]
            [lein-doo "0.1.6"]]

  :source-paths ["src"]

  :test-paths ["test/clj"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"]

  :doo {:build "test"}

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src"]
                        :compiler
                        {:output-to "resources/public/js/compiled/kramberry.js"
                         :output-dir "resources/public/js/compiled/out"
                         :source-map-timestamp true
                         :optimizations :none}}

                       {:id "test"
                        :source-paths ["src" "test/cljs"]
                        :compiler
                        {:output-to "resources/public/js/compiled/testable.js"
                         :main kramberry.test-runner
                         :optimizations :none}}]})
