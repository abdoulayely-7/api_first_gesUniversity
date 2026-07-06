# 📑 INDEX - Documentation Complète des Besoins

## 🎯 Tous les fichiers de documentation

La documentation est organisée en **3 besoins** + **explications OpenAPI**.

---

## 📚 Structure de la documentation

```
docs/
├── README.md                          # 👈 COMMENCER ICI
│   └── Vue d'ensemble des 3 besoins
│
├── BESOINS (Exécution pas à pas)
├── ├── 01-BESOIN-CONSULTATION-CATALOGUE.md
│   │   └── GET /courses
│   │   └── Flux complet étape par étape
│   │   └── Exemples cURL
│   │
├── ├── 02-BESOIN-INSCRIPTION-ETUDIANT.md
│   │   └── POST /enrollments
│   │   └── Flux complet étape par étape
│   │   └── Exemples cURL
│   │
└── └── 03-BESOIN-NOTATION-EXAMEN.md
    └── PUT /enrollments/{id}/grades
    └── Flux complet étape par étape
    └── Exemples cURL

├── OPENAPI EXPLIQUÉES (Ligne par ligne du YAML)
├── ├── 01-OPENAPI-EXPLIQUE.md
│   │   └── Explication du YAML pour GET /courses
│   │   └── Chaque ligne commentée
│   │   └── DTOs détaillés
│   │
├── ├── 02-OPENAPI-EXPLIQUE.md
│   │   └── Explication du YAML pour POST /enrollments
│   │   └── Chaque ligne commentée
│   │   └── DTOs détaillés
│   │
└── └── 03-OPENAPI-EXPLIQUE.md
    └── Explication du YAML pour PUT /enrollments/{id}/grades
    └── Chaque ligne commentée
    └── DTOs détaillés
```

---

## 🗺️ Guide de navigation

### Pour COMPRENDRE les besoins (Approche métier)
1. Lire **README.md** (vue d'ensemble)
2. Lire **01-BESOIN-CONSULTATION-CATALOGUE.md**
3. Lire **02-BESOIN-INSCRIPTION-ETUDIANT.md**
4. Lire **03-BESOIN-NOTATION-EXAMEN.md**

### Pour COMPRENDRE le YAML (Approche technique)
1. Lire **01-OPENAPI-EXPLIQUE.md** (GET)
2. Lire **02-OPENAPI-EXPLIQUE.md** (POST)
3. Lire **03-OPENAPI-EXPLIQUE.md** (PUT)

### Pour TESTER l'API (Approche pratique)
1. Lancer: `mvn spring-boot:run`
2. Utiliser les exemples cURL dans chaque fichier BESOIN-*
3. Ou accéder à Swagger: `http://localhost:8080/swagger-ui.html`

---

## 📋 Les 3 Besoins en Résumé

### Besoin N°1 : 📖 Consulter le Catalogue des Cours

| Aspect | Valeur |
|--------|--------|
| **Endpoint** | `GET /api/v1/courses` |
| **Type d'opération** | Lecture (Consultation) |
| **Paramètres** | `level` (optionnel, query) |
| **Réponse réussie** | `200 OK` + Liste de CourseResponseDto |
| **Erreurs** | 400 Bad Request, 500 Internal Server Error |
| **Documentation** | [`01-BESOIN-CONSULTATION-CATALOGUE.md`](./01-BESOIN-CONSULTATION-CATALOGUE.md) |
| **Explication YAML** | [`01-OPENAPI-EXPLIQUE.md`](./01-OPENAPI-EXPLIQUE.md) |

**Cas d'usage :** L'étudiant consulte le catalogue des cours disponibles

---

### Besoin N°2 : 📝 Inscrire un Étudiant à un Cours

| Aspect | Valeur |
|--------|--------|
| **Endpoint** | `POST /api/v1/enrollments` |
| **Type d'opération** | Création (Écriture) |
| **Paramètres** | studentNumber, courseId, academicYear (body) |
| **Réponse réussie** | `201 Created` + EnrollmentResponseDto (avec ticket) |
| **Erreurs** | 400 Bad Request, 404 Not Found, 409 Conflict, 500 Internal Server Error |
| **Documentation** | [`02-BESOIN-INSCRIPTION-ETUDIANT.md`](./02-BESOIN-INSCRIPTION-ETUDIANT.md) |
| **Explication YAML** | [`02-OPENAPI-EXPLIQUE.md`](./02-OPENAPI-EXPLIQUE.md) |

**Cas d'usage :** L'étudiant s'inscrit à un cours (avec validation métier)

---

### Besoin N°3 : 📊 Saisir la Note d'Examen

| Aspect | Valeur |
|--------|--------|
| **Endpoint** | `PUT /api/v1/enrollments/{enrollmentId}/grades` |
| **Type d'opération** | Mise à jour (Update) |
| **Paramètres** | enrollmentId (path) + score, feedback (body) |
| **Réponse réussie** | `204 No Content` (pas de corps) |
| **Erreurs** | 400 Bad Request, 404 Not Found, 409 Conflict, 500 Internal Server Error |
| **Documentation** | [`03-BESOIN-NOTATION-EXAMEN.md`](./03-BESOIN-NOTATION-EXAMEN.md) |
| **Explication YAML** | [`03-OPENAPI-EXPLIQUE.md`](./03-OPENAPI-EXPLIQUE.md) |

**Cas d'usage :** Le professeur enregistre la note d'un étudiant

---

## 🔄 Flux complet de l'application

```
┌─────────────────────────────────────────────────────────────────┐
│                      PROCESSUS COMPLET                          │
└─────────────────────────────────────────────────────────────────┘

JOUR 1: AVANT INSCRIPTION
───────────────────────────
Étudiant
   │
   └─> Besoin N°1: GET /courses?level=LICENCE_3
       │
       └─> Voir les cours disponibles
           • Architecture Logicielle (CS-301) - OUVERT
           • Bases de Données (CS-302) - OUVERT
           • Réseau et Protocoles (CS-303) - COMPLET

JOUR 2: INSCRIPTION
───────────────────
Étudiant
   │
   └─> Besoin N°2: POST /enrollments
       {
         "studentNumber": "ETU-2026-045",
         "courseId": "CS-301",
         "academicYear": "2025-2026"
       }
       │
       └─> Confirmation:
           • ID Inscription: E-2026-001
           • Ticket: TKT-2025-156789
           • Status: CONFIRMED

APRÈS EXAMEN: NOTATION
──────────────────────
Professeur
   │
   └─> Besoin N°3: PUT /enrollments/E-2026-001/grades
       {
         "score": 15.5,
         "feedback": "Très bonne performance"
       }
       │
       └─> Confirmation: 204 No Content ✅
           (Note enregistrée silencieusement)
```

---

## 🛠️ Fichiers source du projet

### OpenAPI Specification (Source de vérité)
- **Chemin** : `src/main/resources/openapi/university-api.yaml`
- **Rôle** : Définit le contrat API (endpoints, DTOs, codes d'erreur)
- **Consulter pour** : Comprendre la structure technique

### Domain Model (DDD)
- **Chemin** : `src/main/java/com/university/domain/`
- **Fichiers clés** :
  - `course/Course.java` - Entité course (immuable)
  - `enrollment/Enrollment.java` - Entité enrollment (immuable)
  - `shared/CourseId.java`, `StudentNumber.java`, etc. - Value Objects
- **Consulter pour** : Comprendre la logique métier

### Use Cases
- **Chemin** : `src/main/java/com/university/application/`
- **Fichiers clés** :
  - `course/ListCoursesUseCase.java` - Besoin N°1
  - `enrollment/EnrollStudentUseCase.java` - Besoin N°2
  - `enrollment/RecordGradeUseCase.java` - Besoin N°3
- **Consulter pour** : Comprendre l'orchestration

### REST Controllers
- **Chemin** : `src/main/java/com/university/infrastructure/adapter/`
- **Fichiers clés** :
  - `CourseController.java` - Implémente GET /courses
  - `EnrollmentController.java` - Implémente POST /enrollments et PUT /enrollments/.../grades
- **Consulter pour** : Comprendre l'adaptation HTTP

---

## 📊 Tableau comparatif des 3 endpoints

| Aspect | Besoin 1 (GET) | Besoin 2 (POST) | Besoin 3 (PUT) |
|--------|---|---|---|
| **Opération** | Lecture | Création | Mise à jour |
| **Méthode HTTP** | GET | POST | PUT |
| **Paramètres** | Query (optionnel) | Body (obligatoire) | Path + Body |
| **Succès HTTP** | 200 | 201 | 204 |
| **Réponse corps** | Array | Objet | Rien |
| **Validation** | Simple | Complexe (métier) | Complexe (métier) |
| **Cas d'erreur** | 2 (400, 500) | 4 (400, 404, 409, 500) | 4 (400, 404, 409, 500) |
| **Idempotent** | OUI | NON | NON |

---

## ✅ Checklist de compréhension

Avant de développer, vérifiez que vous comprenez :

### Concepts généraux
- [ ] Qu'est-ce qu'API First ?
- [ ] Pourquoi API First est mieux que Code First ?
- [ ] Quel est le contrat API (OpenAPI) ?
- [ ] Quel est le rôle de chaque couche (Controller, Use Case, Domain) ?

### Besoin N°1
- [ ] Qu'est-ce qu'un GET ? (lecture, sans effet de bord)
- [ ] Comment ça marche le paramètre optionnel `level` ?
- [ ] Quand retourner 200 ? 400 ? 500 ?
- [ ] Que contient la réponse ?

### Besoin N°2
- [ ] Qu'est-ce qu'un POST ? (création, réponse 201)
- [ ] Quelles sont les règles métier d'inscription ? (cours ouvert, pas plein)
- [ ] Pourquoi le serveur génère le ticket ?
- [ ] Quand retourner 201 ? 400 ? 404 ? 409 ?

### Besoin N°3
- [ ] Qu'est-ce qu'un PUT ? (mise à jour, réponse 204)
- [ ] Pourquoi 204 et pas 200 ?
- [ ] Pourquoi un enrollment peut pas être noté deux fois ?
- [ ] Comment fonctionnent les validations (0-20) ?

### Technique (YAML)
- [ ] Qu'est-ce qu'un `$ref` ? (référence réutilisable)
- [ ] Différence entre `in: path` et `in: query` ?
- [ ] Qu'est-ce que `required: true/false` ?
- [ ] Qu'est-ce que `enum` ? (liste de valeurs possibles)
- [ ] Différence entre `requestBody` et `responses` ?

---

## 🎓 Ordre de lecture recommandé

### Option 1 : Apprentissage complet (Recommandé)
1. **README.md** - Vue d'ensemble (15 min)
2. **01-BESOIN-CONSULTATION-CATALOGUE.md** - Flux simple (30 min)
3. **01-OPENAPI-EXPLIQUE.md** - Détails YAML (20 min)
4. **02-BESOIN-INSCRIPTION-ETUDIANT.md** - Flux avec validation (40 min)
5. **02-OPENAPI-EXPLIQUE.md** - Détails YAML (25 min)
6. **03-BESOIN-NOTATION-EXAMEN.md** - Flux avec update (35 min)
7. **03-OPENAPI-EXPLIQUE.md** - Détails YAML (20 min)

**Total : ~3 heures** pour maîtriser complètement

### Option 2 : Compréhension rapide (30 min)
1. **README.md** - Vue d'ensemble
2. Survol rapide des 3 fichiers BESOIN-*
3. Tester avec Swagger UI : `http://localhost:8080/swagger-ui.html`

### Option 3 : Référence technique (À la demande)
- Chercher le besoin concerné
- Lire le fichier BESOIN-*
- Lire le fichier OPENAPI-EXPLIQUE-* correspondant
- Consulter les exemples cURL

---

## 🔗 Liens rapides

### Documentation métier (Flux & cas d'usage)
- [Besoin 1: Consulter le catalogue](./01-BESOIN-CONSULTATION-CATALOGUE.md)
- [Besoin 2: Inscrire un étudiant](./02-BESOIN-INSCRIPTION-ETUDIANT.md)
- [Besoin 3: Enregistrer une note](./03-BESOIN-NOTATION-EXAMEN.md)

### Documentation technique (Ligne par ligne du YAML)
- [OpenAPI Besoin 1](./01-OPENAPI-EXPLIQUE.md)
- [OpenAPI Besoin 2](./02-OPENAPI-EXPLIQUE.md)
- [OpenAPI Besoin 3](./03-OPENAPI-EXPLIQUE.md)

### Documentation globale du projet
- [Accueil README.md](./README.md)
- [Guide API First Complet](../API-FIRST-GUIDE.md)
- [Architecture du projet](../README.md)
- [Exemples cURL](../EXAMPLES.md)

---

## 💡 Conseils pour bien comprendre

✅ **Lisez les besoins d'abord** (métier)
- Comprenez QUOI avant COMMENT
- Visualisez le flux de données
- Testez avec les exemples cURL

✅ **Puis lisez les explications OpenAPI** (technique)
- Comprenez chaque ligne du YAML
- Voyez comment le métier se traduit en API
- Comprenez les codes HTTP

✅ **Puis explorez le code source** (implémentation)
- Voyez comment ça s'implémente en Java
- Comprenez DDD et l'architecture
- Exécutez et debuggez

✅ **Testez sur Swagger UI**
- Interface interactive
- Testez les 3 endpoints
- Essayez les cas d'erreur
- Voyez les réponses réelles

---

## ❓ FAQ

**Q: Par où je commence ?**
R: Lisez `README.md` puis `01-BESOIN-CONSULTATION-CATALOGUE.md`

**Q: Pourquoi y a-t-il deux fichiers par besoin ?**
R: Un pour le flux (métier), un pour le YAML (technique)

**Q: Dois-je mémoriser le YAML ?**
R: Non, c'est à titre informatif. Utilisez Swagger UI comme référence.

**Q: Comment je teste l'API ?**
R: Utilisez les exemples cURL dans les fichiers BESOIN-* ou Swagger UI

**Q: Pourquoi pas de corps en 204 ?**
R: Pour économiser la bande passante. Le client sait juste que c'est OK.

**Q: Qu'est-ce qu'une Value Object ?**
R: Un objet immuable qui encapsule validation et logique (ex: StudentNumber)

**Q: Pourquoi pas de setters ?**
R: Pour garantir l'immuabilité et la traçabilité des changements d'état

---

## 📞 Support

Si vous avez des questions :
1. Consulter le fichier documentation correspondant
2. Vérifier les exemples cURL
3. Examiner le code source en `src/main/java/`
4. Consulter les tests en `src/test/java/`

---

**🎓 Vous avez maintenant toute la documentation pour maîtriser ce projet ! Bon apprentissage !** ✨
