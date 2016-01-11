# Metosin-common

Random collection is various namespaces used in multiple Metosin projects

## Use

You need S3 credentials to use this. Save them to *~/.lein/credentials.clj.gpg*.
(You need GPG setup...).

```clj
{#"s3p://metosinmaven/releases/"
 {:username "<access key id>"
  :passphrase "<secret key>"}}
```

### Leiningen

*project.clj:*
```clj
:plugins [[s3-wagon-private "1.2.0"]]
:repositories [["private" {:url "s3p://metosinmaven/releases/" :creds :gpg}]]
```
### Boot

*build.boot:*
```clj
(set-env!
 :wagons       '[[s3-wagon-private "1.1.2"]]
 :repositories #(conj % '["private" {:url "s3p://metosinmaven/releases/"}])
 :dependencies '[[acme/s3library "1.0.0"]])
```

*~/.boot/profile.boot:*
```clj
(configure-repositories!
 (fn [m]
   (merge m (some (fn [[regex cred]] (if (re-find regex (:url m)) cred))
                  (gpg-decrypt
                   (clojure.java.io/file
                    (System/getProperty "user.home") ".lein/credentials.clj.gpg")
                   :as :edn)))))
```

## TODO

- [ ] Test everything
- [ ] Deploy to clojars or private maven repo?
- [ ] Start using

## Ideas

- [ ] Static resources handler and middleware
- [ ] Common users, groups and logic backend and UI
- [ ] Changelog UI and generation mechanism...?
