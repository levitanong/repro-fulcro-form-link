(ns ^:dev/always repro-fulcro-form-link.client-test-main
  (:require [fulcro-spec.selectors :as sel]
            [fulcro-spec.suite :as suite]))

(suite/def-test-suite client-tests {:ns-regex #"repro-fulcro-form-link.*-spec"}
  {:default   #{::sel/none :focused}
   :available #{:focused}})

(client-tests)
