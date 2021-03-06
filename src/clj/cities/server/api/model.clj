(ns cities.server.api.model
  (:require [cities.game :as game]
            [cities.player :as player]))

(defn game-for
  [player game]
  (if (game/game-over? game)
    game
    (let [{:keys [::game/players ::game/round ::game/previous-rounds]}  game
          {:keys [::game/draw-pile ::game/discard-piles]} round
          opponent (game/opponent game player)
          available-discards (into [] (comp (map val)
                                            (map last)
                                            (filter identity))
                                   discard-piles)
          round (-> round
                    (dissoc ::game/draw-pile ::game/discard-piles)
                    (update-in [::game/player-data opponent] dissoc ::player/hand)
                    (assoc ::game/available-discards available-discards)
                    (assoc ::game/draw-count (count draw-pile)))]
      (assoc game ::game/round round ::game/opponent opponent))))

(defn summarize-game-for
  [player game]
  (let [id (:cities.game/id game)
        opponent (game/opponent game player)
        base #:cities.game{:id id
                         :opponent opponent
                         :loaded? false}]
    (if (game/game-over? game)
      (assoc base :cities.game/over? true)
      (assoc base
             :cities.game/over? false
             :cities.game/round (select-keys (:cities.game/round game) [::game/turn])
             :cities.game/round-number (inc (count (:cities.game/past-rounds game)))))))
