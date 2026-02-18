# 🚀 GUIDE DE DÉMARRAGE RAPIDE

## Pour votre validation CRUD cette semaine

### ⏱️ Temps estimé : 15 minutes

---

## 📋 Checklist Pré-Validation

- [ ] Java 17 installé
- [ ] MySQL installé et démarré
- [ ] Maven installé
- [ ] Node.js et Angular CLI installés
- [ ] IDE prêt (IntelliJ IDEA / VS Code)

---

## 🎯 ÉTAPE 1 : Base de Données (5 min)

```bash
# 1. Se connecter à MySQL
mysql -u root -p

# 2. Créer la base et les données
source database/schema.sql

# 3. Vérifier
USE cognivita_db;
SELECT COUNT(*) FROM cognitive_activities;
# Résultat attendu: 12 activités

SHOW TABLES;
# Résultat attendu: users, cognitive_activities, activity_participations
```

✅ **Checkpoint** : Vous devez avoir 12 activités et 11 participations

---

## 🎯 ÉTAPE 2 : Backend Spring Boot (5 min)

```bash
# 1. Aller dans le dossier backend
cd backend

# 2. Compiler le projet
mvn clean install
# ⏳ Patience, première compilation peut prendre 2-3 min

# 3. Lancer l'application
mvn spring-boot:run

# Vous devriez voir:
# ✅ Cognivita Backend Started Successfully!
# 📖 Swagger UI: http://localhost:8080/api/swagger-ui.html
```

### Tester le Backend

Ouvrir dans le navigateur : `http://localhost:8080/api/activities`

Vous devez voir un JSON avec la liste des 12 activités ! 🎉

---

## 🎯 ÉTAPE 3 : Tester avec Swagger (5 min)

1. Ouvrir : `http://localhost:8080/api/swagger-ui.html`

2. Tester les endpoints dans l'ordre :

### ✅ **GET** `/api/activities` 
- Click "Try it out" → "Execute"
- Vous voyez les 12 activités

### ✅ **GET** `/api/activities/{id}`
- ID: `1`
- Execute → Vous voyez l'activité "Mémorisation de Séquence"

### ✅ **POST** `/api/activities`
- Click "Try it out"
- Copier ce JSON :
```json
{
  "title": "Ma Nouvelle Activité",
  "description": "Test de création",
  "activityType": "MEMORY",
  "difficulty": "EASY",
  "durationMinutes": 10,
  "instructions": "Instructions de test",
  "points": 20,
  "isActive": true
}
```
- Execute → Status 201 Created ✅

### ✅ **PUT** `/api/activities/13`
- ID: `13` (l'activité qu'on vient de créer)
- Modifier le titre dans le JSON
- Execute → Activité modifiée ✅

### ✅ **DELETE** `/api/activities/13`
- ID: `13`
- Execute → Status 204 No Content ✅

### ✅ **GET** `/api/activities/type/MEMORY`
- Execute → Toutes les activités de mémoire

### ✅ **GET** `/api/activities/statistics/user/1`
- Execute → Statistiques de l'utilisateur 1

---

## 🎯 BONUS : Frontend Angular (Optionnel)

Si vous avez le temps :

```bash
cd frontend

# Installer les dépendances
npm install

# Lancer
ng serve
```

Ouvrir : `http://localhost:4200`

---

## 📝 Pour la Présentation de Validation

### Ce que vous devez montrer :

1. **Base de Données** ✅
   - Montrer les tables dans MySQL Workbench ou CLI
   - Montrer les 12 activités de test

2. **Backend** ✅
   - Montrer le code source :
     - Entity (`CognitiveActivity.java`)
     - Repository (`CognitiveActivityRepository.java`)
     - Service (`CognitiveActivityService.java`)
     - Controller (`CognitiveActivityController.java`)

3. **Tests API** ✅
   - Faire une démo live avec Swagger :
     - GET all
     - GET by ID
     - POST create
     - PUT update
     - DELETE
     - GET avec filtres
     - GET statistiques

4. **Documentation** ✅
   - Montrer la documentation Swagger auto-générée
   - Expliquer les différents endpoints

### Structure de Présentation (10-15 min)

1. **Introduction** (2 min)
   - Présenter le module Activités Cognitives
   - Expliquer l'utilité dans Cognivita

2. **Architecture** (3 min)
   - Montrer le schéma de la base de données
   - Expliquer Spring Boot + JPA + MySQL

3. **Démo CRUD** (8 min)
   - CREATE → Créer une activité
   - READ → Lister, filtrer
   - UPDATE → Modifier une activité
   - DELETE → Supprimer
   - BONUS → Stats, participations

4. **Questions** (2 min)

---

## 🐛 Troubleshooting Rapide

### MySQL refuse de se connecter
```bash
# Vérifier que MySQL tourne
sudo systemctl status mysql

# Ou sur Windows :
# Services → MySQL → Start
```

### Port 8080 déjà utilisé
```properties
# Dans application.properties, changer :
server.port=8081
```

### Maven build error
```bash
# Nettoyer et reconstruire
mvn clean
mvn install -DskipTests
```

---

## ✅ Checklist Finale Avant Validation

- [ ] MySQL contient les données de test
- [ ] Backend démarre sans erreur
- [ ] Swagger UI accessible
- [ ] Tests API fonctionnent (GET, POST, PUT, DELETE)
- [ ] Code propre et commenté
- [ ] Présentation prête (PowerPoint optionnel)

---

## 🎓 Points Clés à Retenir

1. **CRUD Complet** : Create, Read, Update, Delete ✅
2. **JPA/Hibernate** : Mapping automatique entre objet et BD
3. **REST API** : Endpoints suivent les conventions REST
4. **Validation** : Bean Validation avec annotations
5. **Documentation** : Auto-générée avec Swagger
6. **Transaction** : `@Transactional` pour l'intégrité des données

---

## 🌟 Fonctionnalités Avancées à Mentionner

- ✨ Soft Delete (isActive)
- 📊 Statistiques en temps réel
- 🔍 Recherche et filtrage avancés
- 📈 Calcul automatique du niveau utilisateur
- 🎯 Système de recommandations
- 🏆 Participations avec score et temps

---

**Bonne chance pour votre validation ! 🚀**

*En cas de problème, relire ce guide étape par étape.*
