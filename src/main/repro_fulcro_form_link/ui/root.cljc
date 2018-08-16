(ns repro-fulcro-form-link.ui.root
  (:require
    [fulcro.client.mutations :as m]
    [fulcro.client.data-fetch :as df]
    #?(:cljs [fulcro.client.dom :as dom] :clj [fulcro.client.dom-server :as dom])
    [repro-fulcro-form-link.api.mutations :as api]
    [fulcro.client.primitives :as prim :refer [defsc]]
    [fulcro.client.routing :as r]
    [fulcro.ui.form-state :as fs]
    [fulcro.i18n :as i18n :refer [tr trf]]))

;; The main UI of your application

(defsc Bar [this props]
  {:query         [:bar/id
                   :bar/name]
   :ident         [:bar/by-id :bar/id]
   :initial-state (fn [{:keys [bar/id]}]
                    {:bar/id id})}
  )

(defsc FooForm [this {:keys [foo/id]}]
  {:ident         [:foo/by-id :foo/id]
   :query         [:foo/id
                   :foo/name
                   fs/form-config-join
                   {[:root/bar '_] (prim/get-query Bar)}]
   :form-fields   #{:foo/name}}
  (dom/div "foo" (str id)))

(r/defrouter FooRouter :foo-router
  (fn [_ {:keys [foo/id]}] [:foo/by-id id])
  :foo/by-id FooForm)

(def ui-foo-router (prim/factory FooRouter))

(defsc FooFormWrapper [this {:keys [foo-router]}]
  {:ident         (fn [] [:foo-form :root])
   :query         [:page
                   {:foo-router (prim/get-query FooRouter)}]
   :initial-state (fn [_]
                    {:page       :foo-form
                     :foo-router (prim/get-initial-state FooRouter {})})}
  (ui-foo-router foo-router))

(defsc Main [this props]
  {:query         [:page]
   :ident         (fn [] [:main :root])
   :initial-state {:page :main}}
  (dom/div
    "main"
    (dom/button {:onClick (fn []
                            (let [foo-id   (prim/tempid)
                                  foo      {:foo/id foo-id}
                                  foo-tree (fs/add-form-config FooForm foo)]
                              (prim/merge-component! (prim/get-reconciler this)
                                                     FooForm
                                                     foo-tree)
                              (prim/transact! this
                                `[(r/route-to ~{:handler      :foo-form
                                                :route-params {:foo-id foo-id}})])))}
      "New foo form")))

(r/defrouter RootRouter :root-router
  (fn [this {:keys [page]}] [page :root])
  :main Main
  :foo-form FooFormWrapper)

(def ui-root-router (prim/factory RootRouter))

(def routing-tree
  (r/routing-tree
   (r/make-route :main [(r/router-instruction :root-router [:main :root])])
   (r/make-route :foo-form [(r/router-instruction :root-router [:foo-form :root])
                            (r/router-instruction :foo-router [:foo/by-id :param/foo-id])])))

(defsc Root [this {:keys [root-router]}]
  {:query         [{:root-router (prim/get-query RootRouter)}
                   {:root/bar (prim/get-query Bar)}]
   :initial-state (fn [p]
                    (merge routing-tree
                           {:root-router (prim/get-initial-state RootRouter {})
                            :root/bar    (prim/get-initial-state Bar {:bar/id 0})}))}
  (ui-root-router root-router))
