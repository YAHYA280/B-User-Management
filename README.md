# User Management Microservice

## Objectif / Description du service
Le microservice de gestion des utilisateurs est conçu pour gérer les utilisateurs, leurs rôles, permissions, et adresses au sein du projet BrainBoost. Il offre des fonctionnalités pour l'authentification, la gestion des profils utilisateurs, et la gestion des rôles et permissions.

## Fonctionnalités principales
- Gestion des utilisateurs (création, mise à jour, suppression, recherche)
- Gestion des rôles et permissions
- Gestion des adresses utilisateurs
- Authentification et gestion des sessions utilisateurs

## Architecture technique du service
- **Langage de programmation** : Java
- **Framework** : Spring Boot
- **Base de données** : PostgreSQL
- **ORM** : Spring Data JPA
- **Sécurité** : Spring Security

## Ports utilisés
- **Port du serveur** : 7071

## Configuration
Le service nécessite une configuration de la base de données PostgreSQL. Les détails de configuration peuvent être spécifiés dans le fichier `application.yml`.

## Endpoints API
### UserController
- `GET /api/users/{userId}` : Récupère un utilisateur par son identifiant.
- `POST /api/users/login` : Gère la connexion d'un utilisateur.
- `PUT /api/users/{userId}/status` : Change le statut d'un utilisateur.
- `PUT /api/users/{userId}/profile-image` : Met à jour l'image de profil d'un utilisateur.

### AdminController
- `GET /api/admins` : Liste tous les administrateurs.
- `GET /api/admins/search` : Recherche dynamique des administrateurs.
- `GET /api/admins/{adminId}` : Visualise les données complètes d'un administrateur.
- `POST /api/admins` : Ajoute un nouvel administrateur.
- `PUT /api/admins/{adminId}` : Modifie les informations d'un administrateur.
- `DELETE /api/admins/{adminId}` : Supprime un administrateur.
- `GET /api/admins/export/csv` : Exporte la liste des administrateurs au format CSV.
- `GET /api/admins/export/pdf` : Exporte la liste des administrateurs au format PDF.

### ChildController
- `GET /api/children` : Liste tous les enfants.
- `GET /api/children/{childId}` : Visualise les données complètes d'un enfant.
- `POST /api/children` : Ajoute un nouvel enfant.
- `PUT /api/children/{childId}` : Modifie les informations d'un enfant.
- `DELETE /api/children/{childId}` : Supprime un enfant.
- `GET /api/children/{childId}/success-rate` : Récupère le taux de réussite global d'un enfant.
- `POST /api/children/{childId}/learning-time` : Enregistre le temps d'apprentissage d'un enfant.

### TimeScreenController
- `POST /api/time-screens` : Ajoute un temps d'écran pour un enfant.
- `PUT /api/time-screens/{id}` : Met à jour un temps d'écran existant.
- `DELETE /api/time-screens/{id}` : Supprime un temps d'écran.
- `GET /api/time-screens/{id}` : Récupère un temps d'écran par son identifiant.
- `GET /api/time-screens/child/{childId}` : Récupère tous les temps d'écran pour un enfant.
- `GET /api/time-screens/child/{childId}/date/{date}` : Récupère le temps d'écran pour un enfant à une date spécifique.
- `GET /api/time-screens/child/{childId}/period` : Récupère les temps d'écran pour un enfant dans une période spécifique.
- `GET /api/time-screens/child/{childId}/total` : Calcule la durée totale du temps d'écran pour un enfant dans une période spécifique.

### ParentController
- `GET /api/parents` : Liste tous les parents.
- `GET /api/parents/search` : Recherche dynamique des parents.
- `GET /api/parents/{parentId}` : Visualise les données complètes d'un parent.
- `POST /api/parents` : Ajoute un nouveau parent.
- `PUT /api/parents/{parentId}` : Modifie les informations d'un parent.
- `DELETE /api/parents/{parentId}` : Supprime un parent.

### AddressController
- `POST /api/users/{userId}/addresses` : Ajoute une nouvelle adresse pour un utilisateur.
- `PUT /api/addresses/{addressId}` : Met à jour une adresse existante.
- `DELETE /api/addresses/{addressId}` : Supprime une adresse.
- `GET /api/users/{userId}/addresses` : Récupère toutes les adresses d'un utilisateur.
- `GET /api/addresses/{addressId}` : Récupère une adresse par son identifiant.

### RoleController
- `GET /api/roles` : Liste tous les rôles.
- `GET /api/roles/{roleId}` : Récupère un rôle par son identifiant.
- `POST /api/roles` : Ajoute un nouveau rôle.
- `PUT /api/roles/{roleId}` : Modifie les informations d'un rôle.
- `DELETE /api/roles/{roleId}` : Supprime un rôle.
- `POST /api/admins/{adminId}/roles/{roleId}` : Attribue un rôle à un administrateur.
- `DELETE /api/admins/{adminId}/roles/{roleId}` : Retire un rôle à un administrateur.
- `GET /api/admins/{adminId}/roles` : Récupère tous les rôles attribués à un administrateur.

### RolePermissionController
- `POST /api/roles/{roleId}/permissions` : Ajoute une nouvelle permission à un rôle.
- `PUT /api/permissions/{permissionId}` : Met à jour une permission existante.
- `DELETE /api/permissions/{permissionId}` : Supprime une permission.
- `GET /api/roles/{roleId}/permissions` : Récupère toutes les permissions d'un rôle.
- `GET /api/admins/{adminId}/check-permission` : Vérifie si un administrateur a une permission spécifique.
- `GET /api/admins/{adminId}/permissions` : Récupère toutes les permissions d'un administrateur.

## Base de données utilisée
- **PostgreSQL** : Base de données relationnelle utilisée pour stocker les informations des utilisateurs, rôles, permissions, et adresses.
