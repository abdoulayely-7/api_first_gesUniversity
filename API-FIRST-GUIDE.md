# 📚 GUIDE COMPLET : API FIRST AVEC SPRING BOOT

## Table des matières
1. [Qu'est-ce que l'API First ?](#quest-ce-que-lapi-first)
2. [Pourquoi API First ?](#pourquoi-api-first)
3. [Étapes du processus API First](#étapes-du-processus-api-first)
4. [Exemple pratique : Notre projet](#exemple-pratique--notre-projet)
5. [Outils et technologies](#outils-et-technologies)
6. [Best practices](#best-practices)
7. [Différence Code-First vs API-First](#différence-code-first-vs-api-first)
8. [Cas d'usage réels](#cas-dusage-réels)

---

## Qu'est-ce que l'API First ?

### Définition
**API First** est une approche de développement où :
- **L'API (le contrat)** est définie en premier
- **Le code** est ensuite implémenté pour respecter ce contrat
- **La spécification OpenAPI** est la source de vérité

### Concept clé
```
┌─────────────────────────────────────────────────────┐
│  1. Spécification OpenAPI (YAML/JSON)               │
│     ↓                                               │
│  2. Accord client-serveur sur le contrat            │
│     ↓                                               │
│  3. Génération automatique du code de base          │
│     ↓                                               │
│  4. Implémentation de la logique métier             │
│     ↓                                               │
│  5. Vérification conformité = teste rapidement      │
└─────────────────────────────────────────────────────┘
```

---

## Pourquoi API First ?

### ✅ Avantages

#### 1. **Contrat clair dès le départ**
```
AVANT (Code-First) :
Developer écrit le code → Client découvre l'API

APRÈS (API-First) :
Client et Developer s'accordent → Code suit
```

#### 2. **Génération automatique**
- Plus de boilerplate à écrire
- OpenAPI Generator crée les interfaces, DTOs, etc.
- Moins d'erreurs humaines

#### 3. **Tests parallèles**
- QA peut écrire les tests AVANT le code
- Frontend peut mocquer l'API immédiatement
- Pas de blocage

#### 4. **Documentation vivante**
- La spec = la documentation
- Swagger UI auto-généré et toujours à jour
- Pas de documentation obsolète

#### 5. **Évolutivité et maintenance**
- Changements de spec = changements de code
- Versioning clair (v1, v2, v3)
- Migrations faciles

#### 6. **Réutilisabilité**
- La spec peut être utilisée par plusieurs implémentations
- Mobile, web, backend tous basés sur le même contrat

### ❌ Inconvénients (et solutions)

| Problème | Solution |
|----------|----------|
| Courbe d'apprentissage OpenAPI | Utiliser des éditeurs visuels (Swagger Editor, Stoplight) |
| Effort initial pour la spec | Outils comme API Designer simplifient |
| Synchronisation spec-code | Ci/CD vérifie la conformité |

---

## Étapes du processus API First

### Phase 1 : Design de l'API

#### Étape 1.1 : Identifier les ressources
```
Ressources = Nouns (noms)

Exemple Plateforme Universitaire:
  /courses        → Ressource "Cours"
  /enrollments    → Ressource "Inscriptions"
  /students       → Ressource "Étudiants"
```

#### Étape 1.2 : Définir les opérations HTTP
```
Opérations = Verbes HTTP

GET     → Lire une ressource
POST    → Créer une ressource
PUT     → Mettre à jour
DELETE  → Supprimer
PATCH   → Modification partielle

Exemple:
  GET    /courses              → Lister
  POST   /courses              → Créer
  GET    /courses/{id}         → Détail
  PUT    /courses/{id}         → Mettre à jour
  DELETE /courses/{id}         → Supprimer
```

#### Étape 1.3 : Spécifier les DTOs (Data Transfer Objects)
```yaml
CourseDTO:
  type: object
  properties:
    courseId:
      type: string
      example: "CS-301"
    title:
      type: string
      example: "Architecture Logicielle"
    credits:
      type: integer
      minimum: 1
      maximum: 30
    status:
      type: string
      enum: [OUVERT, COMPLET, FERME]
```

#### Étape 1.4 : Définir les codes de réponse
```yaml
Succès:
  200 OK           → Requête réussie (GET, PUT)
  201 CREATED      → Ressource créée (POST)
  204 NO CONTENT   → Succès sans corps (DELETE)

Erreurs:
  400 BAD REQUEST  → Données invalides
  401 UNAUTHORIZED → Authentification requise
  403 FORBIDDEN    → Autorisé mais pas de permission
  404 NOT FOUND    → Ressource inexistante
  409 CONFLICT     → Conflit métier (ex: cours complet)
  500 ERROR        → Erreur serveur
```

### Phase 2 : Écriture de la spécification OpenAPI

#### Structure minimale OpenAPI 3.0
```yaml
openapi: 3.0.0

info:
  title: Nom de l'API
  version: 1.0.0
  description: Description

servers:
  - url: http://localhost:8080
    description: Développement

paths:
  /resources:
    get:
      summary: Lister
      operationId: listResources
      responses:
        '200':
          description: Liste reçue
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/ResourceDTO'
    
    post:
      summary: Créer
      operationId: createResource
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateResourceRequest'
      responses:
        '201':
          description: Créé
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ResourceDTO'

components:
  schemas:
    ResourceDTO:
      type: object
      properties:
        id:
          type: string
        name:
          type: string
```

### Phase 3 : Génération du code

#### Outil : OpenAPI Generator

```bash
# Installation (Maven)
<plugin>
  <groupId>org.openapitools</groupId>
  <artifactId>openapi-generator-maven-plugin</artifactId>
  <version>7.14.0</version>
  <executions>
    <execution>
      <phase>generate-sources</phase>
      <goals>
        <goal>generate</goal>
      </goals>
      <configuration>
        <inputSpec>${project.basedir}/src/main/resources/openapi/api.yaml</inputSpec>
        <generatorName>spring</generatorName>
        <library>spring-boot</library>
        <apiPackage>com.example.api</apiPackage>
        <modelPackage>com.example.model</modelPackage>
        <interfaceOnly>true</interfaceOnly>
      </configuration>
    </execution>
  </executions>
</plugin>

# Compilation
mvn clean compile
```

#### Qu'est-ce qui est généré ?
```
target/generated-sources/openapi/
├── src/main/java/
│   └── com/example/
│       ├── api/
│       │   ├── CoursesApi.java         # Interface HTTP
│       │   └── EnrollmentsApi.java     # Interface HTTP
│       └── model/
│           ├── CourseDTO.java          # DTO pour Course
│           └── EnrollmentDTO.java      # DTO pour Enrollment
```

**Important** : Ces fichiers générés ne doivent JAMAIS être modifiés !

### Phase 4 : Implémentation

#### Étape 4.1 : Créer le Domain Model (DDD)

**Value Objects** (immuables) :
```java
public final class CourseId {
    private final String value;
    
    private CourseId(String value) {
        this.value = Objects.requireNonNull(value);
    }
    
    public static CourseId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CourseId cannot be blank");
        }
        return new CourseId(value);
    }
    
    public String getValue() { return value; }
}
```

**Entités** (avec logique métier) :
```java
@Entity
public class Course {
    @Id
    private String courseId;
    private Integer enrolledCount;
    private Integer maxStudents;
    private String status;  // OUVERT, COMPLET, FERME
    
    // Pas de setters !
    // Factory method pour créer
    public static Course create(String courseId, Integer maxStudents) {
        Course course = new Course();
        course.courseId = courseId;
        course.maxStudents = maxStudents;
        course.enrolledCount = 0;
        course.status = "OUVERT";
        return course;
    }
    
    // Logique métier dans l'entité
    public boolean acceptsEnrollments() {
        return "OUVERT".equals(status) && enrolledCount < maxStudents;
    }
    
    // Méthode qui retourne un nouvel objet (immutabilité)
    public Course addEnrollment() {
        if (!acceptsEnrollments()) {
            throw new CourseFullException("Cours complet");
        }
        Course updated = Course.create(this.courseId, this.maxStudents);
        updated.enrolledCount = this.enrolledCount + 1;
        return updated;
    }
    
    // Getters uniquement
    public String getCourseId() { return courseId; }
    public Integer getEnrolledCount() { return enrolledCount; }
}
```

#### Étape 4.2 : Créer les Repositories
```java
@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    List<Course> findByStatus(String status);
}
```

#### Étape 4.3 : Créer les Use Cases
```java
@Service
public class EnrollStudentUseCase {
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    
    public Enrollment execute(String studentNumber, String courseId) {
        // 1. Récupérer le cours
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new CourseNotFoundException());
        
        // 2. Appeler la logique métier du domaine
        if (!course.acceptsEnrollments()) {
            throw new CourseFullException();
        }
        
        Course updatedCourse = course.addEnrollment();
        
        // 3. Créer l'inscription
        Enrollment enrollment = Enrollment.create(studentNumber, courseId);
        
        // 4. Persister
        enrollmentRepository.save(enrollment);
        courseRepository.save(updatedCourse);
        
        return enrollment;
    }
}
```

#### Étape 4.4 : Implémenter les Interfaces Générées
```java
@RestController
@RequestMapping("/api/v1")
public class CoursesApiImpl implements CoursesApi {
    private final EnrollStudentUseCase enrollStudentUseCase;
    
    @Override
    @PostMapping("/enrollments")
    public ResponseEntity<EnrollmentDTO> enrollStudent(
            @RequestBody EnrollmentRequest request) {
        
        // Appeler le use case
        Enrollment enrollment = enrollStudentUseCase.execute(
            request.getStudentNumber(),
            request.getCourseId()
        );
        
        // Convertir en DTO (généré)
        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setEnrollmentId(enrollment.getEnrollmentId());
        dto.setStudentNumber(enrollment.getStudentNumber());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}
```

### Phase 5 : Tests et Validation

#### Tests Unitaires (Domain)
```java
@Test
public void testCourseAcceptsEnrollments() {
    Course course = Course.create("CS-301", 30);
    
    assertTrue(course.acceptsEnrollments()); // Nouveau cours = accepte
    
    Course full = course.addEnrollment()
                       .addEnrollment()
                       // ... répéter 28 fois
                       .addEnrollment(); // 30 inscrits
    
    assertFalse(full.acceptsEnrollments()); // Complet = refuse
}
```

#### Tests d'Intégration (API)
```java
@Test
public void testEnrollStudent() throws Exception {
    mockMvc.perform(post("/api/v1/enrollments")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"studentNumber\":\"ETU-001\",\"courseId\":\"CS-301\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.enrollmentId").exists());
}
```

#### Tests de Conformité (OpenAPI)
```bash
# Utiliser Schemathesis ou des outils similaires
schemathesis run http://localhost:8080/api-docs
```

---

## Exemple pratique : Notre projet

### Récapitulatif du flux

```
1. SPÉCIFICATION OPENAPI
   ↓ (src/main/resources/openapi/university-api.yaml)
   
2. GÉNÉRATION
   ↓ (mvn clean compile)
   → target/generated-sources/openapi/
   → CoursesApi.java (interface)
   → EnrollmentsApi.java (interface)
   → CourseDTO.java (modèle)
   
3. DOMAIN MODEL (DDD)
   ↓ (src/main/java/com/university/domain/)
   → Course.java (Agrégat + logique métier)
   → Enrollment.java (Agrégat + logique métier)
   → CourseId, StudentNumber, AcademicYear (Value Objects)
   
4. USE CASES
   ↓ (src/main/java/com/university/application/)
   → EnrollStudentUseCase
   → RecordGradeUseCase
   → ListCoursesUseCase
   
5. ADAPTERS (REST)
   ↓ (src/main/java/com/university/infrastructure/adapter/)
   → EnrollmentController implémente EnrollmentsApi
   → CourseController implémente CoursesApi
   
6. PERSISTENCE
   ↓ (src/main/java/com/university/infrastructure/persistence/)
   → CourseRepository (JPA)
   → EnrollmentRepository (JPA)
   
7. TESTS
   ↓ (src/test/java/com/university/)
   → UniversityApiIntegrationTest
   
8. RÉSULTAT
   ↓
   API fonctionnelle conforme à la spec OpenAPI
```

### Commandes clés

```bash
# 1. Compiler (génère le code OpenAPI)
mvn clean compile

# 2. Démarrer l'application
mvn spring-boot:run

# 3. Accéder à Swagger UI (doc auto)
http://localhost:8080/swagger-ui.html

# 4. Appeler l'API
curl -X POST http://localhost:8080/api/v1/enrollments \
  -H "Content-Type: application/json" \
  -d '{"studentNumber":"ETU-001","courseId":"CS-301","academicYear":"2025-2026"}'
```

---

## Outils et technologies

### Essentiels pour API First

#### 1. **OpenAPI Specification (3.0+)**
- Standard industrie pour décrire les APIs REST
- Format YAML ou JSON
- Supporte le versioning

#### 2. **Éditeurs OpenAPI**
- **Swagger Editor** : https://editor.swagger.io (gratuit)
- **Stoplight Studio** : https://stoplight.io (pro)
- **VS Code Extensions** : OpenAPI Preview

#### 3. **Générateurs de code**
- **OpenAPI Generator** (Maven plugin) - Le plus populaire
- **Swagger Codegen** - Alternative
- **API Generator** - Pour Java/Spring

#### 4. **Mock servers**
- **Prism** : Mock automatique basé sur spec
- **Swagger Inspector** : Test et mock
- **PostMan** : Test et mock

#### 5. **Validation et tests**
- **Schemathesis** : Tests de conformité OpenAPI
- **Spectacle** : Validation de spec
- **SwaggerHub** : Plateforme complète

#### 6. **Documentation**
- **Springdoc** : Swagger UI auto (Spring)
- **Swagger UI** : Interface web interactive
- **ReDoc** : Documentation élégante

### Stack du projet actuel

```
├── OpenAPI 3.0.0              (Spec)
├── OpenAPI Generator 7.14.0   (Code generation)
├── Spring Boot 3.3.3          (Framework)
├── Spring Data JPA            (Persistence)
├── H2 Database                (Test database)
├── Springdoc 2.6.0            (Swagger UI)
└── JUnit 5 + Mockito          (Tests)
```

---

## Best practices

### ✅ À faire

#### 1. **Versionner l'API**
```yaml
info:
  version: 1.0.0

servers:
  - url: /api/v1
  - url: /api/v2
```

#### 2. **Utiliser les bons codes HTTP**
```
201 → Créé
204 → Pas de contenu (pour PUT/DELETE)
400 → Validation échouée
409 → Conflit métier
```

#### 3. **Documenter les erreurs**
```yaml
responses:
  '400':
    description: Validation error
    content:
      application/json:
        schema:
          $ref: '#/components/schemas/ErrorResponse'
        example:
          error: VALIDATION_ERROR
          message: "Le numéro d'étudiant est obligatoire"
          timestamp: "2026-01-15T10:30:00Z"
```

#### 4. **Utiliser les tags pour organiser**
```yaml
paths:
  /courses:
    get:
      tags:
        - Courses
  /enrollments:
    post:
      tags:
        - Enrollments
```

#### 5. **Respecter les conventions REST**
```
GET    /courses              # Lister
POST   /courses              # Créer
GET    /courses/{id}         # Détail
PUT    /courses/{id}         # Remplacer
PATCH  /courses/{id}         # Modification partielle
DELETE /courses/{id}         # Supprimer
```

### ❌ À éviter

#### 1. **Verbes dans les URLs**
```
❌ GET /getCourses
✅ GET /courses

❌ POST /createCourse
✅ POST /courses
```

#### 2. **Mélanger les ressources**
```
❌ /student/courses/123/enrollment/456
✅ /enrollments/456
✅ GET /enrollments?courseId=123&studentNumber=ETU-001
```

#### 3. **Oublier la pagination**
```
❌ GET /courses              # Retourne TOUT
✅ GET /courses?page=0&size=20
```

#### 4. **Oublier les validations**
```yaml
❌ name:
     type: string

✅ name:
     type: string
     minLength: 1
     maxLength: 100
     pattern: ^[a-zA-Z\s]+$
```

#### 5. **Modifier la spec sans versioning**
```
❌ Changer directement la spec en production
✅ Créer /api/v2 pour les changements majeurs
```

---

## Différence Code-First vs API-First

### Code-First (Ancienne approche)

```
1. Écrire le code Spring
   ↓
2. Ajouter les annotations Swagger
   ↓
3. Générer la documentation
   ↓
4. Client découvre l'API
   ↓
5. Problèmes discovered → Réécrire le code

PROBLÈMES:
- Documentation souvent obsolète
- Client et serveur pas synchronisés
- Tests écrits tardivement
- QA dépend du code fini
```

### API-First (Nouvelle approche) ← RECOMMANDÉ

```
1. Écrire la spec OpenAPI
   ↓
2. Valider avec client/QA
   ↓
3. Générer le code (interfaces, DTOs)
   ↓
4. Implémenter la logique métier
   ↓
5. Code et spec TOUJOURS synchronisés

AVANTAGES:
✅ Spec = source de vérité
✅ Client peut mocquer immédiatement
✅ QA peut écrire les tests en parallèle
✅ Documentation automatiquement à jour
✅ Changements visibles immédiatement
```

### Tableau comparatif

| Aspect | Code-First | API-First |
|--------|-----------|-----------|
| Qui décide ? | Développeur | Client + Dev (accord) |
| Documentation | Après le code | Avant le code |
| Tests | Tardivement | Parallèles |
| Changements | Code → Spec | Spec → Code |
| Synchronisation | Manuelle | Automatique |
| Erreurs découvertes | Tôt (en test) | Très tôt (design) |
| Effort initial | Faible | Moyen |
| Maintenance | Difficile | Facile |

---

## Cas d'usage réels

### 1. **Plateforme e-commerce**

Spec définit :
- Catalogue `/products`
- Panier `/cart`
- Commandes `/orders`
- Paiement `/payments`

Frontend, mobile, et backend services utilisent tous la même spec.

```yaml
/products:
  get:
    parameters:
      - name: category
        in: query
      - name: priceMin
        in: query
      - name: priceMax
        in: query
```

### 2. **Système bancaire**

Spec définit :
- Comptes `/accounts`
- Transactions `/transactions`
- Virements `/transfers`

Toutes les règles métier (limites de transfert, taux de change) documentées dans la spec.

```yaml
/transfers:
  post:
    responses:
      '400':
        description: Montant dépasse limite
      '403':
        description: Compte gelé
      '409':
        description: Solde insuffisant
```

### 3. **API publique (SaaS)**

Spec versionnée `/api/v1` et `/api/v2` :
- Clients utilisent la version qui leur convient
- Migration progressive sans perte de compatibilité
- Swagger UI pour onboarding des nouveaux clients

```yaml
servers:
  - url: https://api.saas.com/v1
    description: Version stable
  - url: https://api.saas.com/v2
    description: Version bêta
```

---

## Checklist : Passer à l'API First

### Phase de design
- [ ] Identifier les ressources principales
- [ ] Lister les opérations (GET, POST, PUT, DELETE)
- [ ] Définir les DTOs avec validation
- [ ] Lister les codes d'erreur
- [ ] Documenter les cas métier

### Phase de spécification
- [ ] Écrire la spec OpenAPI 3.0
- [ ] Valider la syntaxe YAML/JSON
- [ ] Valider avec le client
- [ ] Passer avec Swagger Editor
- [ ] Ajouter des exemples

### Phase de génération
- [ ] Configurer OpenAPI Generator en Maven
- [ ] Générer les interfaces et DTOs
- [ ] Vérifier le code généré
- [ ] Ajouter au .gitignore

### Phase d'implémentation
- [ ] Créer le domain model (DDD)
- [ ] Créer les repositories
- [ ] Implémenter les use cases
- [ ] Créer les controllers (implémenter les interfaces)
- [ ] Gérer les erreurs correctement

### Phase de tests
- [ ] Tests unitaires (domain)
- [ ] Tests d'intégration (API)
- [ ] Tests de conformité OpenAPI
- [ ] Tester tous les codes d'erreur

### Phase de documentation
- [ ] Swagger UI généré automatiquement
- [ ] README avec exemples cURL
- [ ] Documentation des erreurs
- [ ] Guide d'intégration pour clients

---

## Ressources et références

### Officiel
- https://www.openapis.org/ - Spec OpenAPI
- https://swagger.io/ - Swagger tools
- https://openapi-generator.tech/ - OpenAPI Generator

### Tutoriels
- Swagger Editor : https://editor.swagger.io
- Springdoc : https://springdoc.org
- OpenAPI Guide : https://learn.openapis.org

### Exemples
- GitHub Openapi-samples
- Swagger Petstore : https://petstore.swagger.io

---

## Conclusion

**API First n'est pas juste une tendance, c'est une meilleure façon de développer.**

### Résumé des bénéfices
1. ✅ Contrat clair avant le code
2. ✅ Documentation automatique
3. ✅ Tests parallèles
4. ✅ Génération de code
5. ✅ Synchronisation garantie
6. ✅ Maintenance facile
7. ✅ Évolution sans breaking changes

### Prochaines étapes
1. Commencer par une spec simple
2. Valider avec un pair
3. Générer le code
4. Implémenter progressivement
5. Ajouter des tests
6. Itérer en fonction des retours

**Le futur du développement API est API First. Commencez dès maintenant !** 🚀
