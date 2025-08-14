# HR Connect – Module de Gestion des Quiz

## Présentation

Ce module web développé avec **JavaFx** a été réalisé dans le cadre du projet **PIDEV 3A** à **Esprit**.  
Il constitue un système complet de gestion des ressources humaines incluant la gestion des formations, des séminaires, des employés, des réclamations et du recrutement.  
Le projet repose sur une architecture MVC, exploite Doctrine ORM et interagit avec une base de données relationnelle.

## Fonctionnalités

- Gestion des employés
- Gestion des formations
- Gestion des séminaires
- Gestion des réclamations
- Gestion du recrutement et des candidatures
- Création et gestion des quiz

## Technologies utilisées

### Frontend
- JavaFx

## Structure du project

- `controllers/` :  Contient les classes gérant la logique de l'interface utilisateur (UI).
- `models/` Contient les classes représentant les données de l'application.
- `services/` : Contient les classes qui encapsulent la logique métier.
- `utils/` : Contient des classes utilitaires (helpers, gestionnaires de ressources, etc.).
- `resources/` : ontient les fichiers de ressources (images, fichiers FXML, CSS, etc..)

## Démarrage rapide

1. Cloner le dépôt :
   ```bash
   git clone https://github.com/dridihaythem/hrconnect-javafx.git
   cd hrconnect-javafx


2. Importer la base de données fournie (`db.sql`) dans MySQL :
   ```bash
   mysql -u root -p hrconnect < db.sql
   ```

3. Configurer le fichier `config.properties` :
 