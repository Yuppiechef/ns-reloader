(ns ns-reloader.core
  (:require [ns-tracker.core :as trk]))

(defn check-namespace-changes
  "Use a tracker to check for namespace changes, reloading them and calls reload-fn if there were any namespaces, passing a list of them as its single arg"
  [track delay reload-fn]
  (try
    (if-let [t (track)]
      (when t
        (doseq [ns-sym t]
          (require ns-sym :reload))
        (when reload-fn
          (reload-fn t))))
    (catch Throwable e (.printStackTrace e)))
  (Thread/sleep (or delay 500)))

(defn start-nstracker
  "Fire off a thread to keep checking for namespaces that need reloading. Optionally provide a delay in ms between checks - defaults to 500ms"
  [dirs & [delay reload-fn]]
  (let [track (trk/ns-tracker dirs)]
    (doto
        (Thread.
         #(while true
            (check-namespace-changes track delay reload-fn)))
      (.setDaemon true)
      (.start))))
