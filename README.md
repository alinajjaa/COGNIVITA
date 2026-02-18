# 🧠 Cognivita - CRUD Activités Cognitives

**Projet PIDEV - ESPRIT 4SAE4**  
**Module**: Gestion des Activités Cognitives  
**Stack**: Spring Boot 3.2.2 + Angular 17 + MySQL 8

---

## 📋 Table des Matières

- [Vue d'ensemble](#vue-densemble)
- [Prérequis](#prérequis)
- [Installation Backend](#installation-backend)
- [Installation Frontend](#installation-frontend)
- [Base de Données](#base-de-données)
- [Tests API](#tests-api)
- [Documentation](#documentation)

---

## 🎯 Vue d'ensemble

Ce projet implémente un **CRUD complet** pour le module **Activités Cognitives** de la plateforme Cognivita.

### Fonctionnalités

✅ **CRUD Activités**
- Créer, Lire, Mettre à jour, Supprimer des activités
- Filtrage par type (MEMORY, ATTENTION, LOGIC, CREATIVITY)
- Filtrage par difficulté (EASY, MEDIUM, HARD)
- Recherche par titre
- Désactivation (soft delete)

✅ **Participations**
- Démarrer une activité
- Compléter avec score et temps
- Abandonner une activité
- Historique des participations

✅ **Statistiques**
- Statistiques globales de la plateforme
- Statistiques personnalisées par utilisateur
- Calcul automatique du niveau
- Recommandations personnalisées

---

## 🛠️ Prérequis

### Obligatoires
- ☕ **Java 17** ou supérieur
- 📦 **Maven 3.8+**
- 🗄️ **MySQL 8.0+**
- 🅰️ **Node.js 18+** et npm
- 🅰️ **Angular CLI 17**

### Vérification
```bash
java -version        # Java 17+
mvn -version         # Maven 3.8+
mysql --version      # MySQL 8.0+
node --version       # Node 18+
ng version          # Angular CLI 17+
```

---

## 💻 Installation Backend

### Étape 1: Base de Données

```bash
# Se connecter à MySQL
mysql -u root -p

# Exécuter le script
source database/schema.sql

# Vérifier
USE cognivita_db;
SHOW TABLES;
SELECT COUNT(*) FROM cognitive_activities;
```

### Étape 2: Configuration

Modifier `backend/src/main/resources/application.properties` :

```properties
# Modifier le mot de passe si nécessaire
spring.datasource.password=VOTRE_MOT_DE_PASSE
```

### Étape 3: Compilation et Lancement

```bash
cd backend

# Compiler
mvn clean install

# Lancer l'application
mvn spring-boot:run
```

### Étape 4: Vérification

✅ Backend démarré : `http://localhost:8080/api`  
✅ Swagger UI : `http://localhost:8080/api/swagger-ui.html`  
✅ API Docs : `http://localhost:8080/api/api-docs`

---

## 🅰️ Installation Frontend

### Étape 1: Installation des dépendances

```bash
cd frontend

# Installer les dépendances
npm install
```

### Étape 2: Lancement

```bash
# Mode développement
ng serve

# Ou avec port personnalisé
ng serve --port 4200
```

### Étape 3: Vérification

✅ Frontend : `http://localhost:4200`

---

## 🗄️ Base de Données

### Structure

```
cognivita_db
├── users                        # Utilisateurs
├── cognitive_activities         # Activités cognitives (CRUD principal)
└── activity_participations      # Participations aux activités
```

### Données de Test

Le script `schema.sql` insère automatiquement :
- 4 utilisateurs (patients, médecin, admin)
- 12 activités cognitives (3 par type)
- 11 participations d'exemple

---

## 🧪 Tests API

### Avec cURL

```bash
# GET - Toutes les activités
curl http://localhost:8080/api/activities

# GET - Une activité par ID
curl http://localhost:8080/api/activities/1

# GET - Filtrer par type
curl http://localhost:8080/api/activities/type/MEMORY

# POST - Créer une activité
curl -X POST http://localhost:8080/api/activities \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Nouvelle Activité",
    "description": "Description",
    "activityType": "MEMORY",
    "difficulty": "EASY",
    "durationMinutes": 10,
    "instructions": "Instructions...",
    "points": 15,
    "isActive": true
  }'

# PUT - Mettre à jour
curl -X PUT http://localhost:8080/api/activities/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Titre Modifié",
    ...
  }'

# DELETE - Supprimer
curl -X DELETE http://localhost:8080/api/activities/1
```

### Avec Postman

1. Importer la collection : `docs/Cognivita.postman_collection.json`
2. Tester tous les endpoints

### Avec Swagger

1. Aller sur `http://localhost:8080/api/swagger-ui.html`
2. Tester directement depuis l'interface

---

## 📚 Documentation API

### Endpoints Principaux

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/activities` | Liste toutes les activités |
| `GET` | `/api/activities/{id}` | Détails d'une activité |
| `POST` | `/api/activities` | Créer une activité |
| `PUT` | `/api/activities/{id}` | Mettre à jour une activité |
| `DELETE` | `/api/activities/{id}` | Supprimer une activité |
| `GET` | `/api/activities/type/{type}` | Filtrer par type |
| `GET` | `/api/activities/difficulty/{difficulty}` | Filtrer par difficulté |
| `POST` | `/api/activities/{id}/start` | Démarrer une activité |
| `GET` | `/api/activities/statistics/user/{userId}` | Stats utilisateur |

📖 **Documentation complète** : Voir `docs/API_Documentation.md`

---

## 🚀 Déploiement

### Backend (JAR)

```bash
cd backend
mvn clean package
java -jar target/cognivita-activities-1.0.0.jar
```

### Frontend (Production)

```bash
cd frontend
ng build --configuration production
# Les fichiers sont dans dist/
```

---

## 🧪 Tests

### Backend (JUnit)

```bash
cd backend
mvn test
```

### Frontend (Jasmine/Karma)

```bash
cd frontend
ng test
```

---

## 📝 Structure du Projet

```
crud-activites-cognitives/
├── backend/
│   ├── src/main/java/tn/esprit/cognivita/
│   │   ├── entity/               # Entités JPA
│   │   ├── repository/           # Repositories
│   │   ├── service/              # Services métier
│   │   ├── controller/           # REST Controllers
│   │   ├── config/               # Configurations
│   │   └── CognivitaApplication.java
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
│
├── frontend/
│   └── src/app/
│       ├── models/               # Interfaces TypeScript
│       ├── services/             # Services HTTP
│       └── components/           # Composants Angular
│
├── database/
│   └── schema.sql               # Script SQL complet
│
└── docs/
    ├── API_Documentation.md     # Doc API complète
    └── README.md                # Ce fichier
```

---

## ⚠️ Troubleshooting

### Backend ne démarre pas

```bash
# Vérifier MySQL
sudo systemctl status mysql

# Vérifier le port 8080
lsof -i :8080

# Vérifier les logs
tail -f logs/spring.log
```

### Frontend erreur CORS

Vérifier que le backend est lancé et que la configuration CORS dans `CorsConfig.java` autorise `http://localhost:4200`

### Base de données vide

```bash
mysql -u root -p
source database/schema.sql
```

---

## 👥 Auteurs

**NeuroTech Innovators**  
ESPRIT - Cycle Ingénieur - 4SAE4  
Année Académique 2025-2026

---

## 📄 License

Ce projet est réalisé dans le cadre du PIDEV à ESPRIT.

---

## 🎯 Prochaines Étapes

- [ ] Ajouter l'authentification JWT
- [ ] Implémenter le système de recommandations avancé
- [ ] Ajouter des tests unitaires
- [ ] Déployer sur un serveur de production
- [ ] Intégrer avec les autres modules (Journal, Tests, etc.)

---

**🧠 Cognivita** - Prenez soin de votre santé cognitive !
