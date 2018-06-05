(ns barchart.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.reader :as reader]
            [reagent.core :as reagent :refer [atom]]
            [cljsjs/d3]
            [ezd3.core :as ezd3]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            ))

(enable-console-print!)

(def data
  [{:country "USA" :gdp  40}
   {:country "UK" :gdp  90}
   {:country "Mexico" :gdp  30}
   {:country "Germany" :gdp  60}])

(js/console.log "start")

  (go (let [response (<! (http/get "https://api.myjson.com/bins/15q4p6"
                                 {:with-credentials? false
                                  }))]
        (prn (map :country (:body response)))
      )
  )

        ;;(js/console.log "end: " + response)))

;;this could be a static file or an endpoint that generates the JSON
;;(make-remote-call "https://api.myjson.com/bins/16dhqy")

(defn d3-render
  [svg]
  (js/console.log "d3-render")

  (let [width      (reader/read-string (.attr svg "width"))
        height     (reader/read-string (.attr svg "height"))
        array-len  (count data)
        max-value  (.max js/d3 (into-array data) (fn [d] (get d :gdp )))
        x-axis-len 200
        y-axis-len 200
        y-scale    (-> js/d3
                       (.scaleLinear)
                       (.domain #js [0 max-value])
                       (.range #js [0 y-axis-len]))
        tooltip    (-> js/d3
                       (.select "body")
                       (.append "div")
                       (ezd3/styles {"position"         "absolute"
                                     "padding"          "2px 5px"
                                     "background-color" "white"
                                     "opacity"          "0.8"
                                     "font-family"      "'Open Sans', sans-serif"
                                     "font-size"        "12px"
                                     "z-index"          "10"
                                     "visibility"       "hidden"}))]
    (js/console.log "width" width)
    (js/console.log "height" height)
    (js/console.log "array-len" array-len)
    (js/console.log "max-value" max-value)

    ;; draw bars
    (-> svg
        (.selectAll "rect")
        (.data (into-array data))
        (.enter)
        (.append "rect")
        (ezd3/attrs {:x      (fn [d i] (+ (* i (/ x-axis-len array-len)) 100))
                     :y      (fn [d] (- height (y-scale (get d :gdp)) 75))
                     :width  (dec (/ x-axis-len array-len))
                     :height (fn [d] (y-scale (get d :gdp)))
                     :fill   "red"})
        (.on "mouseover"
             (fn [d]
               (-> tooltip
                   (.style "visibility" "visible")
                   (.text (str (get d :country) ": " (get d :gdp))))))
        (.on "mousemove"
             (fn [d]
               (let [y (.. js/d3 -event -pageY)
                     x (.. js/d3 -event -pageX)]
                 (-> tooltip
                     (.style "top" (str (- y 10) "px"))
                     (.style "left" (str (+ x 10) "px"))
                     (.text (str (get d :country) ": " (get d :gdp)))))))
        (.on "mouseout"
             (fn [d]
               (.style tooltip "visibility" "hidden")))
        )

    ;; manually draw y-axis (not optimal, but builds character)
    (-> svg
        (.append "line")
        (ezd3/attrs {:x1           100 :y1 50 :x2 100 :y2 300
                     :stroke-width 2
                     :stroke       "black"}))

    ;; manually draw x-axis (not optimal, but builds character)
    (-> svg
        (.append "line")
        (ezd3/attrs {:x1           100 :y1 300 :x2 350 :y2 300
                     :stroke-width 2
                     :stroke       "black"}))

    ;; x-axis label(s)
    (-> svg
        (.append "text")
        (ezd3/attrs {:class       "x label" :text-anchor "end"
                     :font-family "'Open Sans', sans-serif"
                     :font-size   "18px"
                     })
        (.text "Blah Blah Blah")
        (.attr :transform (ezd3/transform 230 325 0))
    )

    ;; y-axis label
    (-> svg
        (.append "text")
        (ezd3/attrs {:class       "y label" :text-anchor "end"
                     :font-family "'Open Sans', sans-serif"
                     :font-size   "18px"
                     })
        (.text "Blah Blah Blah")
        (.attr :transform (ezd3/transform 75 150 -90)))
    ))


(defn hello-world []
  [:div
   [:h1 "ATA Mini-Project Bar Graph Example"]
   [:p "Based heavily on examples and the ezd3 library written by Hitesh Jasani"]
   [ezd3/d3svg {:width 400 :height 375 :on-render d3-render
                 }]
   [:p "See https://github.com/hiteshjasani/cljs-ezd3 for the source."]
   ])

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
