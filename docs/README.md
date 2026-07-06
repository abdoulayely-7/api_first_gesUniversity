# 📚 Documentation des Besoins - Plateforme de Gestion Universitaire

## Vue d'ensemble

Cette documentation détaille les **3 besoins principaux** de la plateforme de gestion universitaire, implémentés selon l'approche **API First**.

Chaque besoin est documenté avec :
- 🎯 **Description du besoin** : Quoi et pourquoi
- 📋 **Flux d'exécution** : Étapes détaillées
- 🔌 **API OpenAPI** : La spécification exacte
- 💾 **Données** : DTOs et structures
- ✅ **Codes HTTP** : Réponses possibles
- 🧪 **Exemples** : Requêtes et réponses réelles

---

## 📋 Les 3 Besoins

### [Besoin N°1 : 📖 Consulter le Catalogue des Cours](./01-BESOIN-CONSULTATION-CATALOGUE.md)

**Contexte :** Les étudiants doivent accéder au catalogue complet des cours disponibles, avec la possibilité de filtrer par niveau académique.

- **Endpoint** : `GET /api/v1/courses`
- **Opération** : Lecture (consultation)
- **Filtre** : Niveau (Licence 1-3, Master 1-2)
- **Retour** : Liste complète des cours avec détails

[👉 Lire la documentation du Besoin N°1](./01-BESOIN-CONSULTATION-CATALOGUE.md)

---

### [Besoin N°2 : 📝 Inscrire un Étudiant à un Cours](./02-BESOIN-INSCRIPTION-ETUDIANT.md)

**Contexte :** Quand un étudiant clique sur "S'inscrire", le système traite sa demande d'inscription en appliquant des règles métier strictes (validation, capacité du cours, etc.).

- **Endpoint** : `POST /api/v1/enrollments`
- **Opération** : Écriture (création)
- **Données** : Matricule étudiant, ID cours, année académique
- **Retour** : Confirmation avec ticket et date

[👉 Lire la documentation du Besoin N°2](./02-BESOIN-INSCRIPTION-ETUDIANT.md)

---

### [Besoin N°3 : 📊 Saisir la Note d'Examen](./03-BESOIN-NOTATION-EXAMEN.md)

**Contexte :** Un professeur met à jour le dossier d'un étudiant pour ajouter sa note d'examen avec appréciation optionnelle.

- **Endpoint** : `PUT /api/v1/enrollments/{enrollmentId}/grades`
- **Opération** : Mise à jour partielle
- **Données** : Note (0-20), appréciation textuelle
- **Retour** : 204 No Content (succès silencieux)

[👉 Lire la documentation du Besoin N°3](./03-BESOIN-NOTATION-EXAMEN.md)

---

## 🏗️ Architecture Globale

```
                    ┌─────────────────────────┐
                    │   Client (Frontend)     │
                    │  (Web, Mobile, etc.)    │
                    └────────────┬────────────┘
                                 │
                    ┌────────────┴────────────┐
                    │                         │
        GET /courses  POST /enrollments  PUT /enrollments/.../grades
                    │                         │
                    └────────────┬────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │  REST Controllers       │
                    │  (HTTP Adapters)        │
                    └────────────┬────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │  Use Cases / Services   │
                    │  (Orchestration)        │
                    └────────────┬────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │  Domain Model (DDD)     │
                    │  • Entities (Course,    │
                    │    Enrollment)          │
                    │  • Value Objects        │
                    │  • Business Logic       │
                    └────────────┬────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │  Repositories & DB      │
                    │  (Data Persistence)     │
                    └────────────────────────┘
```

---

## 🔄 Approche API First

### Processus d'Implémentation

```
1️⃣  SPÉCIFICATION OpenAPI (university-api.yaml)
    ↓
    Définition du contrat API
    (endpoints, DTOs, codes d'erreur)

2️⃣  GÉNÉRATION DE CODE
    ↓
    mvn clean compile
    Génère les interfaces et classes
    (NE JAMAIS MODIFIER)

3️⃣  IMPLÉMENTATION MÉTIER (DDD)
    ↓
    Domain Model : Entities, Value Objects
    Use Cases : Orchestration
    Repositories : Persistence

4️⃣  ADAPTERS (Controllers)
    ↓
    Implémentation des interfaces générées
    Conversion DTO ↔ Domain Objects

5️⃣  TESTS & DOCUMENTATION
    ↓
    Vérification du comportement
    Documentation des cas d'usage
```

---

## 📊 Flux de Données : Exemple Besoin N°2

```
CLIENT HTTP REQUEST
│
├─ Method: POST
├─ URL: /api/v1/enrollments
├─ Body: {
│    "studentNumber": "ETU-2026-045",
│    "courseId": "CS-301",
│    "academicYear": "2025-2026"
│  }
│
▼
┌─────────────────────────────────┐
│  REST Controller                │
│  (EnrollmentController.java)    │
│  • Parse la requête HTTP        │
│  • Valide le format JSON        │
│  • Appelle le Use Case          │
└─────────────────┬───────────────┘
                  │
▼
┌─────────────────────────────────┐
│  Use Case                       │
│  (EnrollStudentUseCase.java)    │
│  • Charge le Course             │
│  • Vérifie acceptsEnrollments() │
│  • Crée Enrollment              │
│  • Persiste dans DB             │
└─────────────────┬───────────────┘
                  │
▼
┌─────────────────────────────────┐
│  Domain Model                   │
│  • Course.acceptsEnrollments()  │
│  • Enrollment.create()          │
│  • Logique métier              │
└─────────────────┬───────────────┘
                  │
▼
┌─────────────────────────────────┐
│  Repository + Database          │
│  • Sauvegarde en H2             │
│  • Récupère les données         │
└─────────────────┬───────────────┘
                  │
▼
CLIENT HTTP RESPONSE
│
├─ Status: 201 Created
├─ Headers: Location: /enrollments/E-2026-001
├─ Body: {
│    "id": "E-2026-001",
│    "studentNumber": "ETU-2026-045",
│    "courseId": "CS-301",
│    "enrollmentDate": "2025-01-15",
│    "ticketNumber": "TKT-2025-156789",
│    "status": "CONFIRMED"
│  }
```

---

## 🛠️ Fichiers Clés du Projet

### OpenAPI Specification
- **Chemin** : `src/main/resources/openapi/university-api.yaml`
- **Rôle** : Source de vérité pour le contrat API
- **Contient** : Endpoints, DTOs, codes d'erreur, exemples

### Domain Model (DDD)
- **Chemin** : `src/main/java/com/university/domain/`
- **Entités** : `Course.java`, `Enrollment.java`
- **Value Objects** : `CourseId`, `StudentNumber`, `AcademicYear`
- **Logique métier** : Immuable, pas de setters

### Use Cases
- **Chemin** : `src/main/java/com/university/application/`
- **Classes** : `ListCoursesUseCase`, `EnrollStudentUseCase`, `RecordGradeUseCase`
- **Rôle** : Orchestrer les opérations métier

### REST Controllers
- **Chemin** : `src/main/java/com/university/infrastructure/adapter/`
- **Classes** : `CourseController`, `EnrollmentController`
- **Rôle** : Adapter les requêtes HTTP aux use cases

---

## 📖 Guide de Lecture

### Pour les débutants API First
1. Lire cette page (vue d'ensemble)
2. Lire chaque besoin numéroté (01, 02, 03)
3. Consulter `API-FIRST-GUIDE.md` pour théorie

### Pour les développeurs implémentant
1. Consulter le besoin concerné
2. Chercher la section OpenAPI YAML
3. Localiser les fichiers correspondants
4. Voir les exemples cURL

### Pour les testeurs
1. Lire le flux d'exécution
2. Voir les exemples
3. Utiliser les commandes cURL
4. Consulter `EXAMPLES.md` pour tous les cas

---

## ✅ Checklist de Compréhension

Avant de commencer à développer, vérifiez :

- [ ] J'ai lu le README principal
- [ ] Je comprends ce que c'est qu'API First
- [ ] Je sais ce que fait chaque besoin
- [ ] Je comprends le flow des données
- [ ] Je connais les 3 endpoints principaux
- [ ] Je peux tester l'API avec cURL
- [ ] Je sais où trouver le domain model
- [ ] Je sais où trouver les Use Cases
- [ ] Je comprends pourquoi pas de setters
- [ ] Je peux contribuer au projet

---

## 🔗 Ressources Supplémentaires

| Ressource | Description |
|-----------|-------------|
| `API-FIRST-GUIDE.md` | Guide complet du processus API First |
| `README.md` | Architecture du projet |
| `SUMMARY.md` | Checklist des implémentations |
| `EXAMPLES.md` | Exemples d'utilisation cURL |
| `pom.xml` | Configuration Maven |
| `university-api.yaml` | Spécification OpenAPI |

---

## 💬 Questions Fréquentes

**Q: Pourquoi pas de setters dans le domain model ?**
R: Pour garantir l'immuabilité. Tous les changements d'état passent par des factory methods ou business methods, ce qui facilite la traçabilité et prévient les modifications accidentelles.

**Q: Qu'est-ce qu'une Value Object ?**
R: Un objet immuable qui encapsule une valeur métier (ex: `CourseId`, `StudentNumber`). Il valide sa valeur à la création et peut être utilisé partout où cette valeur est requise.

**Q: Pourquoi générer le code plutôt que l'écrire manuellement ?**
R: Parce que la spécification OpenAPI est la source de vérité. Si le contrat change, le code se régénère automatiquement. Cela garantit la cohérence.

**Q: Puis-je modifier les fichiers générés ?**
R: **NON**. Jamais. Les fichiers générés sont des contrats. Les modifications vont dans les implémentations (Controllers, Use Cases, etc.).

---

## 📞 Support

Pour toute question ou clarification :
1. Consulter la documentation du besoin spécifique
2. Vérifier `API-FIRST-GUIDE.md`
3. Regarder les tests d'intégration (`src/test/`)
4. Examiner les exemples en `EXAMPLES.md`

---

**🎓 Bon apprentissage ! Vous allez maîtriser API First et DDD.** ✨
