(ns mailgun.email
  (:require [clojure.tools.logging :as log]
            [org.httpkit.client :as client]))

(defn send-email
  [{{:keys [mailgun-url mailgun-from mailgun-user mailgun-password]} :env :as ctx}
   {:keys [addr subject body] :as email}]

  (println mailgun-url)
  (if-not (and mailgun-url mailgun-from mailgun-user mailgun-password)
    (log/error "You need to specify mail service parameters through environment variables MAILGUN_URL, MAILGUN_PASSWORD, MAILGUN_USER, MAILGUN_FROM")
    (try
      @(client/post (str mailgun-url "/messages")
                   {:form-params {:from mailgun-from
                                  :to addr
                                  :subject subject
                                  :html body}
                    :basic-auth [mailgun-user mailgun-password]
                    :socket-timeout 5000
                    :conn-timeout 5000
                    :accept :json})
      (catch Exception e e))))
