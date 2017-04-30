(ns milo.server.main
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [milo.server.system :as system]
            [taoensso.timbre :as log])
  (:gen-class :main true))

(defn -main
  [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (log/info (str "Using port " port "."))
    (let [system (system/system {:id "milo" :port port})]
      (log/info "Starting system.")
      (component/start-system system))
    (log/info "Waiting forever.")
    @(promise)))
