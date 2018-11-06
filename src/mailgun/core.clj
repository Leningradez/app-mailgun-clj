(ns mailgun.core
  (:require [clojure.tools.logging :as log]
            [aidbox.sdk.core :as aidbox]
            [mailgun.email :as email]
            [clostache.parser :as cp]))

(def manifest
  {:id "mailgun-app"
   :type "app"
   :env {:box {:host "localhost"
               :scheme "http"
               :port 8888}
         :app {;;:host "docker.for.mac.localhost"  ;; for Mac Os
               :host "localhost"
               :scheme "http"
               :port 8989
               :id "root"
               :secret "secret"}

         :mailgun-user "api"
         :mailgun-password "..."
         :mailgun-url      "..."
         :mailgun-from     "..."}
   :entities
   {:EmailTemplate {:attrs {:template {:type "string"}}}
    :Email         {:attrs {:template {:type "Reference"}
                            :from     {:type "string"}
                            :to       {:type "string"}
                            :subject  {:type "string"}
                            :arg      {:isOpen true}
                            :staus    {:type "string"}}}}
   :operations
   {:send-email {:method "POST"
                 :path ["$send-email"]}}})

(defn -main [] (aidbox/start manifest))


(defmethod aidbox/endpoint
  :send-email
  [ctx {res :resource  :as req}]
  (println req)
  (let [res (email/send-email ctx res)]
    {:body res
     :status 200}))


(defn render [tpl arg]
  {:body (cp/render tpl  arg)})


(defn  send-template-email [arg]
  (-> arg
      load-template
      render
      send-email))


(-main)

(comment
  (slurp (:body (email/send-email manifest
                            {:addr "hello@gmail.com"
                             :subject "hello from aidbox app"
                             :body "hello again"})))

  ( -main ))
