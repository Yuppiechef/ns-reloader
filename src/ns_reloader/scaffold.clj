(ns ns-reloader.scaffold
  (:require [ns-reloader.core :as core]))

(defonce scaffolds (atom {}))

(defn set-scaffold! [key fn]
  (swap! scaffolds assoc key fn))

(defmacro scaffold!
  "Mark a section of code to rerun when run-scaffolds is called - This also implicitly runs the body, example: (scaffold (println \"Hello\")) (run-scaffolds) will output 'Hello' twice.

This means that state is automatically preserved when a scaffold is setup.

Using the same key twice will override the old scaffold - this makes for more sane state management as old scaffolds will be torn down"
  [key & body]
  (let [f (cons 'fn (cons [] body))]
    `(let [f# ~f]
       (set-scaffold! ~key f#)
       (f#))))

(defn clean-scaffolds!
  "Clear out any currently set scaffolds"
  []
  (reset! scaffolds {}))

(defn run-scaffolds
  "Run any scaffolds previously setup"
  []
  (doseq [[key scaffold] @scaffolds]
    (scaffold)))

(defn start-tracker-with-scaffold
  [dirs & [delay reload-fn]]
  (core/start-nstracker dirs delay (fn [n] (run-scaffolds) (reload-fn))))