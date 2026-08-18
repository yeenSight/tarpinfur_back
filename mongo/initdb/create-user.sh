#!/bin/bash
set -e

# Ce script est exécuté uniquement lors du premier démarrage de la base (docker-entrypoint-initdb.d)
# Il crée l'utilisateur application avec les variables passées via .env : MONGO_USERNAME, MONGO_PASSWORD, MONGO_DATABASE

if [ -z "$MONGO_USERNAME" ] || [ -z "$MONGO_PASSWORD" ] || [ -z "$MONGO_DATABASE" ]; then
  echo "MONGO_USERNAME, MONGO_PASSWORD ou MONGO_DATABASE manquant. Skip user creation."
  exit 0
fi

cat <<'EOS' > /tmp/create-user.js
(function() {
  const dbName = "%MONGO_DATABASE%";
  const username = "%MONGO_USERNAME%";
  const password = "%MONGO_PASSWORD%";

  const db = db.getSiblingDB(dbName);
  const user = db.getUser(username);
  if (!user) {
    print("Creating user " + username + " on database " + dbName);
    db.createUser({
      user: username,
      pwd: password,
      roles: [{ role: "readWrite", db: dbName }]
    });
  } else {
    print("User already exists: " + username);
  }
})();
EOS

# Remplacement des variables (sécurisé pour ce script initial)
sed -i "s|%MONGO_DATABASE%|${MONGO_DATABASE}|g" /tmp/create-user.js
sed -i "s|%MONGO_USERNAME%|${MONGO_USERNAME}|g" /tmp/create-user.js
sed -i "s|%MONGO_PASSWORD%|${MONGO_PASSWORD}|g" /tmp/create-user.js

# Exécute la création (lors du premier démarrage, mongo n'a pas encore de auth enforced pour init scripts)
mongosh --quiet /tmp/create-user.js || true