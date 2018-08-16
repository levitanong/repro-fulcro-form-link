(ns repro-fulcro-form-link.intro
  (:require [devcards.core :as rc :refer-macros [defcard]]
            [repro-fulcro-form-link.ui.components :as comp]))

(defcard SVGPlaceholder
  "# SVG Placeholder"
  (comp/ui-placeholder {:w 200 :h 200}))
