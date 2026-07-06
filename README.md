# API Gestion Universitaire - API First + DDD

## Architecture

Ce projet suit une approche **API First** avec **Domain-Driven Design (DDD)**, avec une structure en couches hexagonales (Clean Architecture).

### Principes respectés

✅ **API First** : Spec OpenAPI définie en premier (`openapi.yaml`)  
✅ **Code généré intouché** : Les fichiers générés par OpenAPI Generator ne sont jamais modifiés  
✅ **DDD** : Entités avec logique métier, Value Objects, Agrégats  
✅ **Immutabilité** : Pas de setters, seules les factory methods et méthodes métier modifient l'état  
✅ **Séparation des préoccupations** : Application → Domain → Infrastructure  

### Structure du projet

```
src/main/java/com/university/
├── UniversityApplication.java                    # Spring Boot Main
├── domain/                                       # Cœur métier
│   ├── shared/                                   # Objets partagés
│   │   ├── CourseId, StudentNumber, AcademicYear
│   │   ├── CourseStatus, CourseLevel, EnrollmentStatus  # Enums
│   ├── course/                                   # Agrégat Course
│   │   └── Course                               # Entité racine (immutable)
│   └── enrollment/                               # Agrégat Enrollment
│       ├── Enrollment                            # Entité racine (immutable)
│       ├── EnrollmentId, TicketNumber, Grade     # Value Objects
│
├── application/                                  # Logique applicative (Use Cases)
│   ├── course/
│   │   └── ListCoursesUseCase
│   └── enrollment/
│       ├── EnrollStudentUseCase
│       └── RecordGradeUseCase
│
├── infrastructure/                               # Adaptateurs
│   ├── adapter/                                  # REST Controllers
│   │   ├── CatalogueAdapter
│   │   ├── InscriptionsAdapter
│   │   ├── CourseController
│   │   └── EnrollmentController
│   └── persistence/                              # Repositories (JPA)
│       ├── CourseRepository
│       └── EnrollmentRepository
│
└── config/                                       # Configurations Spring

src/main/resources/
├── openapi/
│   └── university-api.yaml                       # Contrat OpenAPI (source de vérité)
└── application.yml                               # Config Spring

src/test/java/com/university/
└── UniversityApiIntegrationTest.java             # Tests d'intégration
```

## Flux de traitement

### 1. Lister les cours
```
GET /api/v1/courses?level=Licence%203
    ↓
CourseController.listCourses()
    ↓
CatalogueAdapter.listCourses() → ListCoursesUseCase.execute()
    ↓
CourseRepository.findByLevel() → Database
    ↓
Course entities (immutable)
    ↓
DTO mapping → JSON response
```

### 2. Inscrire un étudiant
```
POST /api/v1/enrollments
{
  "studentNumber": "ETU-2026-045",
  "courseId": "CS-301",
  "academicYear": "2025-2026"
}
    ↓
EnrollmentController.enrollStudent()
    ↓
InscriptionsAdapter.enrollStudent() → EnrollStudentUseCase.execute()
    ↓
1. Course.acceptsEnrollments() [domaine]
2. Course.addEnrollment() [domaine] → nouveau Course avec enrolledCount+1
3. Enrollment.create() [domaine] → factory method
4. Save à la DB
    ↓
201 CREATED avec EnrollmentConfirmation
```

### 3. Enregistrer une note
```
PUT /api/v1/enrollments/{enrollmentId}/grades
{
  "score": 15.5,
  "feedback": "Excellent"
}
    ↓
EnrollmentController.updateGrade()
    ↓
InscriptionsAdapter.updateGrade() → RecordGradeUseCase.execute()
    ↓
Enrollment.recordGrade() [domaine] → nouveau Enrollment avec score et feedback
    ↓
Save à la DB
    ↓
204 NO CONTENT
```

## Règles métier (dans le Domain)

### Course
- `isFull()` : Vérifie si le cours atteint la capacité max
- `acceptsEnrollments()` : Statut == OUVERT ET pas complet
- `addEnrollment()` : Retourne un nouveau Course avec enrolledCount incrémenté
- `markFull()` : Ferme le cours si complet
- `close()` : Ferme le cours (arrête les inscriptions)

### Enrollment
- `recordGrade()` : Valide la note (0-20) et retourne nouveau Enrollment
- `isConfirmed()` : Vérifie le statut
- `hasGrade()` : Vérifie si une note existe
- `cancel()` : Annule l'inscription

## Lancement du projet

```bash
# Compiler (génère le code OpenAPI)
mvn clean compile

# Lancer les tests
mvn test

# Démarrer l'application
mvn spring-boot:run

# Swagger UI
http://localhost:8080/swagger-ui.html

# API Docs
http://localhost:8080/api-docs
```

## Points clés du design

1. **Value Objects Immuables**
   - `CourseId`, `StudentNumber`, `AcademicYear`
   - Validation au sein du VO
   - Pas de setters

2. **Agrégats**
   - `Course` et `Enrollment` sont des racines d'agrégats
   - Logique métier = méthodes de l'agrégat
   - Pas de setters, seules des factory methods et méthodes métier

3. **Ports & Adaptateurs**
   - Infrastructure n'dépend pas du Domain
   - Repositories = interfaces dans le port
   - Adapters (Controllers) convertissent les DTO et appellent Use Cases

4. **Immutabilité**
   - Les entités ne changent pas d'état
   - Les méthodes métier retournent de nouvellas instances
   - Facilite le testing et la prédictabilité

## Tests

Les tests d'intégration couvrent :
- ✅ Lister les cours (tous, filtré par niveau)
- ✅ Inscrire un étudiant (succès, données invalides)
- ✅ Cours complet (erreur 409)
- ✅ Enregistrer une note
