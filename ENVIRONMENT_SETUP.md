# Configuration des Variables d'Environnement

## Problème Résolu

Ce projet utilisait auparavant des secrets (clés API, tokens) codés en dur dans le code source, ce qui posait des problèmes de sécurité. GitHub a bloqué le push à cause de la détection de secrets.

## Solution Implémentée

Les secrets ont été déplacés vers un fichier `.env` qui n'est pas versionné (ajouté au `.gitignore`).

## Configuration Requise

### 1. Créer le fichier .env

Copiez le fichier `.env.example` vers `.env` :

```bash
cp .env.example .env
```

### 2. Remplir les valeurs réelles

Éditez le fichier `.env` et remplacez les valeurs d'exemple par vos vraies clés API :

```env
# Configuration SMTP
HAYTHEM_STMP_HOST=smtp.mailersend.net
HAYTHEM_STMP_FROM=votre_email@example.com
HAYTHEM_STMP_PORT=587
HAYTHEM_STMP_PASSWORD=votre_mot_de_passe_smtp

# API Gemini
HAYTHEM_GEMINI_API=votre_cle_api_gemini

# Mail JS
HAYTHEM_MAIL_JS_USER_ID=votre_user_id_mailjs
HAYTHEM_MAIL_JS_ACCESS_TOKEN=votre_access_token_mailjs

# Google Maps API
HAYTHEM_GOOGLE_MAPS=votre_cle_api_google_maps

# Twilio - Haythem
HAYTHEM_TWILIO_SID=votre_twilio_sid
HAYTHEM_TWILIO_TOKEN=votre_twilio_token

# ImgBB API
HAYTHEM_IMGBB=votre_cle_api_imgbb

# Twilio - Ala
ALA_TWILIO_SID=votre_twilio_sid
ALA_TWILIO_AUTH=votre_twilio_auth_token

# Twilio - Amine
AMINE_TWILIO_SID=votre_twilio_sid
AMINE_TWILIO_AUTH=votre_twilio_auth_token

# Google OAuth
GOOGLE_CLIENT_ID=votre_google_client_id
GOOGLE_CLIENT_SECRET=votre_google_client_secret
GOOGLE_REDIRECT_URI=urn:ietf:wg:oauth:2.0:oob
```

### 3. Sécurité

- ⚠️ **IMPORTANT** : Ne jamais commiter le fichier `.env`
- Le fichier `.env` est déjà ajouté au `.gitignore`
- Partagez uniquement le fichier `.env.example` avec l'équipe
- Chaque développeur doit créer son propre fichier `.env` avec ses propres clés

### 4. Utilisation

La classe `ConfigReader` a été modifiée pour :
1. Charger automatiquement les variables depuis le fichier `.env`
2. Utiliser les variables d'environnement système comme fallback
3. Maintenir la compatibilité avec l'API existante

## Résolution du Problème GitHub

Après avoir configuré les variables d'environnement :

1. Les secrets ont été supprimés du code source
2. Le fichier `config.properties` a été vidé
3. Les contrôleurs utilisent maintenant `System.getenv()` ou `ConfigReader.get()`
4. Le push vers GitHub devrait maintenant fonctionner

## Prochaines Étapes

1. Configurez votre fichier `.env` avec vos vraies clés
2. Testez l'application pour vous assurer que tout fonctionne
3. Commitez et poussez les changements vers GitHub
