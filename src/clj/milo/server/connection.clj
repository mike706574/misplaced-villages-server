(ns milo.server.connection
  (:require [com.stuartsierra.component :as component]
            [manifold.stream :as s]
            [milo.server.util :as util]
            [taoensso.timbre :as log]))

(defprotocol ConnectionManager
  "Manages connections."
  (add! [this user type conn] "Add a connection.")
  (close-all! [this] "Closes all connections."))

(defrecord AtomConnectionManager [conns]
  ConnectionManager
  (add! [this user type conn]
    (let [id (util/uuid)]
      (swap! conns #(update % user conj {:id id
                                         :type type
                                         :conn conn}))
      id))
  (close-all! [this]
    (let [all-conns (flatten (vals @conns))
          conn-count (count all-conns)]
      (when (pos? conn-count)
        (log/debug (str "Closing " conn-count " connections."))
        (doseq [entry all-conns]
          (log/debug entry)
          (s/close! (:conn entry)))))))

(defn manager
  [conn-atom]
  (AtomConnectionManager. conn-atom))
