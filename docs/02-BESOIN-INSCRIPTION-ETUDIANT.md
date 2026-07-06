# 📝 Besoin N°2 : Inscrire un Étudiant à un Cours

## 🎯 Vue d'ensemble

**Besoin métier :** Quand un étudiant clique sur "S'inscrire" dans l'interface, le système traite sa demande d'inscription en appliquant des règles métier strictes. Une confirmation est générée avec ticket et date.

**Type d'opération :** 📝 **ÉCRITURE** (Création)
**Endpoint HTTP :** `POST /api/v1/enrollments`
**Codes possibles :** 
- `201 Created` ✅ Succès
- `400 Bad Request` ❌ Validation échouée
- `409 Conflict` ⚠️ Cours plein

---

## 📋 Flux d'exécution détaillé

### Étape 1️⃣  : Étudiant déclenche l'inscription

L'étudiant clique sur "S'inscrire" pour un cours. Le frontend (web/mobile) envoie une requête POST :

```bash
POST /api/v1/enrollments HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "studentNumber": "ETU-2026-045",
  "courseId": "CS-301",
  "academicYear": "2025-2026"
}
```

**Données obligatoires :**
- `studentNumber` : Matricule unique de l'étudiant (format: ETU-YYYY-NNN)
- `courseId` : Identifiant du cours (format: XX-NNN)
- `academicYear` : Année académique (format: YYYY-YYYY)

---

### Étape 2️⃣  : REST Controller valide la requête

Le contrôleur reçoit et valide les données :

```java
// Fichier: EnrollmentController.java
@PostMapping("/enrollments")
public ResponseEntity<EnrollmentResponseDto> createEnrollment(
    @RequestBody @Valid EnrollmentRequestDto request
) {
    // 1. Spring valide automatiquement le format JSON et les @Valid
    //    (studentNumber, courseId, academicYear not null, format valide)
    
    // 2. Créer les Value Objects pour le domaine
    StudentNumber studentNumber = StudentNumber.of(request.getStudentNumber());
    CourseId courseId = CourseId.of(request.getCourseId());
    AcademicYear academicYear = AcademicYear.of(request.getAcademicYear());
    
    // 3. Appeler le Use Case
    EnrollmentResponseDto result = enrollStudentUseCase.execute(
        studentNumber, courseId, academicYear
    );
    
    // 4. Retourner 201 Created avec le ticket
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .location(URI.create("/enrollments/" + result.getId()))
        .body(result);
}
```

**Validations appliquées automatiquement :**
- ✅ Champs requis (not null)
- ✅ Format JSON valide
- ✅ Longueurs min/max
- ✅ Patterns regex

---

### Étape 3️⃣  : Validation des Value Objects

Les Value Objects valident leurs propres formats :

```java
// Fichier: StudentNumber.java
public static StudentNumber of(String value) {
    if (!value.matches("ETU-\\d{4}-\\d{3}")) {
        throw new IllegalArgumentException(
            "Format invalide: " + value
        );
    }
    return new StudentNumber(value);
}

// Fichier: CourseId.java
public static CourseId of(String value) {
    if (value == null || value.trim().isEmpty()) {
        throw new IllegalArgumentException("CourseId cannot be empty");
    }
    return new CourseId(value);
}

// Fichier: AcademicYear.java
public static AcademicYear of(String value) {
    if (!value.matches("\\d{4}-\\d{4}")) {
        throw new IllegalArgumentException(
            "Format invalid. Expected: YYYY-YYYY, got: " + value
        );
    }
    return new AcademicYear(value);
}
```

**Si validation échoue :** Retourne `400 Bad Request` avec message d'erreur

---

### Étape 4️⃣  : Use Case charge le cours

Le Use Case cherche le cours dans la base de données :

```java
// Fichier: EnrollStudentUseCase.java
public EnrollmentResponseDto execute(
    StudentNumber studentNumber,
    CourseId courseId,
    AcademicYear academicYear
) throws CourseNotFoundException, CourseFullException {
    
    // 1. Charger le cours depuis la base de données
    Course course = courseRepository.findById(courseId.getValue())
        .orElseThrow(() -> new CourseNotFoundException(
            "Cours non trouvé: " + courseId
        ));
    
    // 2. Appeler la logique métier du cours
    // Voir Étape 5️⃣  ci-dessous
}
```

**Si le cours n'existe pas :** Retourne `404 Not Found`

---

### Étape 5️⃣  : Domain Model applique les règles métier

La Course encapsule la logique d'acceptation d'inscriptions :

```java
// Fichier: Course.java
public class Course {
    private CourseStatus status;  // OUVERT, COMPLET, FERME
    private int enrolledCount;
    private int maxEnrollments;
    
    // Règles métier encapsulées
    public boolean acceptsEnrollments() {
        // Règle 1: Le cours doit être OUVERT
        if (!status.equals(CourseStatus.OUVERT)) {
            return false;
        }
        
        // Règle 2: Le cours ne doit pas être plein
        if (enrolledCount >= maxEnrollments) {
            return false;
        }
        
        return true;
    }
    
    public Course addEnrollment(StudentNumber studentNumber) {
        // Vérifier les règles
        if (!acceptsEnrollments()) {
            throw new IllegalStateException(
                "Cours " + id + " n'accepte pas d'inscriptions"
            );
        }
        
        // Créer une nouvelle instance (immuabilité)
        return new Course(
            this.id,
            this.title,
            this.credits,
            this.professor,
            this.level,
            this.status,
            this.enrolledCount + 1,  // Incrémenter
            this.maxEnrollments
        );
    }
}
```

---

### Étape 6️⃣  : Use Case crée l'Enrollment

Si les règles métier sont satisfaites, créer l'inscription :

```java
// Continuation: EnrollStudentUseCase.java
public EnrollmentResponseDto execute(
    StudentNumber studentNumber,
    CourseId courseId,
    AcademicYear academicYear
) {
    Course course = courseRepository.findById(courseId.getValue())
        .orElseThrow(/* ... */);
    
    // Vérifier si le cours accepte des inscriptions
    if (!course.acceptsEnrollments()) {
        throw new CourseFullException(
            "Le cours " + courseId + " est plein ou fermé"
        );
    }
    
    // ✅ Créer l'inscription
    Enrollment enrollment = Enrollment.create(
        studentNumber,
        courseId,
        academicYear
    );
    
    // ✅ Incrémenter le nombre d'inscrits dans le cours
    Course updatedCourse = course.addEnrollment(studentNumber);
    
    // Marquer comme plein si atteint la capacité
    if (updatedCourse.isFull()) {
        updatedCourse = updatedCourse.markFull();
    }
    
    // Persister les modifications
    enrollmentRepository.save(enrollment);
    courseRepository.save(updatedCourse);
    
    return toEnrollmentResponseDto(enrollment);
}
```

**Si le cours est plein :** Retourne `409 Conflict` avec message

---

### Étape 7️⃣  : Génération du ticket

L'Enrollment génère automatiquement un ticket unique :

```java
// Fichier: Enrollment.java
public class Enrollment {
    private EnrollmentId id;
    private TicketNumber ticketNumber;  // Généré automatiquement
    private LocalDateTime enrollmentDate;  // Date actuelle
    
    public static Enrollment create(
        StudentNumber studentNumber,
        CourseId courseId,
        AcademicYear academicYear
    ) {
        return new Enrollment(
            EnrollmentId.generate(),           // Générer ID unique
            TicketNumber.generate(),           // Générer ticket unique
            studentNumber,
            courseId,
            academicYear,
            EnrollmentStatus.CONFIRMED,
            LocalDateTime.now(),               // Date actuelle
            null, null, null                   // Pas de grade pour l'instant
        );
    }
}

// Fichier: TicketNumber.java
public class TicketNumber {
    private String value;  // Format: TKT-YYYY-XXXXXX
    
    public static TicketNumber generate() {
        String timestamp = String.valueOf(System.currentTimeMillis() % 1000000);
        String ticket = String.format("TKT-%d-%s",
            Year.now().getValue(),
            timestamp
        );
        return new TicketNumber(ticket);
    }
}
```

---

### Étape 8️⃣  : Réponse HTTP retournée

Le serveur retourne un code 201 avec les détails de l'inscription :

```json
HTTP/1.1 201 Created
Location: /api/v1/enrollments/E-2026-001
Content-Type: application/json

{
  "id": "E-2026-001",
  "studentNumber": "ETU-2026-045",
  "courseId": "CS-301",
  "academicYear": "2025-2026",
  "ticketNumber": "TKT-2025-156789",
  "enrollmentDate": "2025-01-15T10:30:45.123Z",
  "status": "CONFIRMED"
}
```

---

## 🔌 Spécification OpenAPI

Voici la section du `university-api.yaml` qui définit ce besoin :

```yaml
# ====== BESOIN N°2 ======
# Inscrire un étudiant à un cours
# ===========================

paths:
  /enrollments:
    post:
      summary: Inscrire un étudiant à un cours
      description: |
        Crée une nouvelle inscription d'étudiant à un cours.
        Applique les règles métier d'acceptation (statut du cours, capacité).
        Retourne une confirmation avec un ticket unique.
      operationId: createEnrollment
      tags:
        - Inscriptions
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/EnrollmentRequestDto'
            example:
              studentNumber: "ETU-2026-045"
              courseId: "CS-301"
              academicYear: "2025-2026"
      
      responses:
        '201':
          description: Inscription créée avec succès
          headers:
            Location:
              schema:
                type: string
              description: URL de la ressource créée
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/EnrollmentResponseDto'
              example:
                id: "E-2026-001"
                studentNumber: "ETU-2026-045"
                courseId: "CS-301"
                academicYear: "2025-2026"
                ticketNumber: "TKT-2025-156789"
                enrollmentDate: "2025-01-15T10:30:45Z"
                status: "CONFIRMED"

        '400':
          description: Erreur de validation (données manquantes ou invalides)
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponseDto'
              example:
                errorCode: INVALID_REQUEST
                message: "Données invalides: studentNumber au mauvais format"
                timestamp: "2025-01-15T10:30:45Z"

        '409':
          description: Conflit métier (cours plein ou fermé)
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponseDto'
              example:
                errorCode: COURSE_FULL
                message: "Le cours CS-301 est complet"
                timestamp: "2025-01-15T10:30:45Z"

        '404':
          description: Cours non trouvé
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponseDto'
              example:
                errorCode: COURSE_NOT_FOUND
                message: "Cours CS-999 non trouvé"
                timestamp: "2025-01-15T10:30:45Z"

        '500':
          description: Erreur serveur
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponseDto'
```

---

## 💾 Structures de Données (DTOs)

### EnrollmentRequestDto (Entrée)

```yaml
components:
  schemas:
    EnrollmentRequestDto:
      type: object
      description: Demande d'inscription d'un étudiant
      required:
        - studentNumber
        - courseId
        - academicYear
      properties:
        studentNumber:
          type: string
          description: Matricule de l'étudiant
          pattern: "^ETU-\\d{4}-\\d{3}$"
          minLength: 12
          maxLength: 12
          example: "ETU-2026-045"
        
        courseId:
          type: string
          description: Identifiant unique du cours
          minLength: 1
          maxLength: 50
          example: "CS-301"
        
        academicYear:
          type: string
          description: Année académique au format YYYY-YYYY
          pattern: "^\\d{4}-\\d{4}$"
          minLength: 9
          maxLength: 9
          example: "2025-2026"
```

### EnrollmentResponseDto (Sortie)

```yaml
    EnrollmentResponseDto:
      type: object
      description: Confirmation d'inscription
      required:
        - id
        - studentNumber
        - courseId
        - academicYear
        - ticketNumber
        - enrollmentDate
        - status
      properties:
        id:
          type: string
          description: Identifiant unique de l'inscription
          example: "E-2026-001"
        
        studentNumber:
          type: string
          description: Matricule de l'étudiant
          example: "ETU-2026-045"
        
        courseId:
          type: string
          description: Identifiant du cours
          example: "CS-301"
        
        academicYear:
          type: string
          description: Année académique
          example: "2025-2026"
        
        ticketNumber:
          type: string
          description: Numéro de ticket unique pour la confirmation
          example: "TKT-2025-156789"
        
        enrollmentDate:
          type: string
          format: date-time
          description: Date et heure d'inscription
          example: "2025-01-15T10:30:45Z"
        
        status:
          type: string
          description: Statut de l'inscription
          enum:
            - CONFIRMED
            - PENDING
            - CANCELLED
          example: "CONFIRMED"

    ErrorResponseDto:
      type: object
      description: Réponse d'erreur standard
      required:
        - errorCode
        - message
        - timestamp
      properties:
        errorCode:
          type: string
          description: Code d'erreur spécifique
          enum:
            - INVALID_REQUEST
            - COURSE_NOT_FOUND
            - COURSE_FULL
            - STUDENT_ALREADY_ENROLLED
            - INVALID_ACADEMIC_YEAR
        
        message:
          type: string
          description: Description détaillée de l'erreur
          example: "Le cours CS-301 est complet"
        
        timestamp:
          type: string
          format: date-time
          description: Date/heure de l'erreur
          example: "2025-01-15T10:30:45Z"
```

---

## ✅ Codes de réponse HTTP

| Code | Signification | Quand ? | Exemple |
|------|---------------|--------|---------|
| `201 Created` | ✅ Succès | Inscription créée | `{"id": "E-2026-001", ...}` |
| `400 Bad Request` | ❌ Validation échouée | Format invalide | `studentNumber` mal formé |
| `404 Not Found` | ❌ Ressource manquante | Cours inexistant | `courseId` n'existe pas |
| `409 Conflict` | ⚠️ Conflit métier | Cours plein ou fermé | Capacité atteinte |
| `500 Internal Error` | ❌ Erreur serveur | Problème serveur | Exception non gérée |

---

## 🧪 Exemples d'utilisation (cURL)

### Exemple 1️⃣  : Inscription réussie

```bash
curl -X POST "http://localhost:8080/api/v1/enrollments" \
  -H "Content-Type: application/json" \
  -d '{
    "studentNumber": "ETU-2026-045",
    "courseId": "CS-301",
    "academicYear": "2025-2026"
  }'
```

**Réponse (201 Created) :**
```json
{
  "id": "E-2026-001",
  "studentNumber": "ETU-2026-045",
  "courseId": "CS-301",
  "academicYear": "2025-2026",
  "ticketNumber": "TKT-2025-156789",
  "enrollmentDate": "2025-01-15T10:30:45.123Z",
  "status": "CONFIRMED"
}
```

---

### Exemple 2️⃣  : Validation échouée (studentNumber invalide)

```bash
curl -X POST "http://localhost:8080/api/v1/enrollments" \
  -H "Content-Type: application/json" \
  -d '{
    "studentNumber": "invalid",
    "courseId": "CS-301",
    "academicYear": "2025-2026"
  }'
```

**Réponse (400 Bad Request) :**
```json
{
  "errorCode": "INVALID_REQUEST",
  "message": "Le format du studentNumber est invalide. Attendu: ETU-YYYY-NNN",
  "timestamp": "2025-01-15T10:35:12.456Z"
}
```

---

### Exemple 3️⃣  : Cours inexistant

```bash
curl -X POST "http://localhost:8080/api/v1/enrollments" \
  -H "Content-Type: application/json" \
  -d '{
    "studentNumber": "ETU-2026-045",
    "courseId": "CS-999",
    "academicYear": "2025-2026"
  }'
```

**Réponse (404 Not Found) :**
```json
{
  "errorCode": "COURSE_NOT_FOUND",
  "message": "Le cours CS-999 n'existe pas",
  "timestamp": "2025-01-15T10:40:20.789Z"
}
```

---

### Exemple 4️⃣  : Cours plein ou fermé

```bash
curl -X POST "http://localhost:8080/api/v1/enrollments" \
  -H "Content-Type: application/json" \
  -d '{
    "studentNumber": "ETU-2026-100",
    "courseId": "CS-303",
    "academicYear": "2025-2026"
  }'
```

**Réponse (409 Conflict) :**
```json
{
  "errorCode": "COURSE_FULL",
  "message": "Le cours CS-303 (Réseau et Protocoles) est complet (30/30)",
  "timestamp": "2025-01-15T10:45:33.012Z"
}
```

---

## 🛠️ Fichiers impactés

### 1. OpenAPI Spécification
**Fichier :** `src/main/resources/openapi/university-api.yaml`
**Rôle :** Définit le contrat du endpoint `POST /enrollments`

### 2. REST Controller
**Fichier :** `src/main/java/com/university/infrastructure/adapter/EnrollmentController.java`
**Rôle :** Reçoit les requêtes POST et valide les données

### 3. Use Case
**Fichier :** `src/main/java/com/university/application/enrollment/EnrollStudentUseCase.java`
**Rôle :** Orchestre l'inscription (charge cours, valide règles, crée enrollment)

### 4. Domain Entities
**Fichiers :**
- `src/main/java/com/university/domain/enrollment/Enrollment.java`
- `src/main/java/com/university/domain/course/Course.java`
**Rôle :** Encapsulent la logique métier d'inscription

### 5. Value Objects
**Fichiers :**
- `src/main/java/com/university/domain/shared/StudentNumber.java`
- `src/main/java/com/university/domain/shared/CourseId.java`
- `src/main/java/com/university/domain/shared/AcademicYear.java`
- `src/main/java/com/university/domain/shared/EnrollmentId.java`
- `src/main/java/com/university/domain/shared/TicketNumber.java`
**Rôle :** Valident les formats et encapsulent les valeurs

### 6. Repositories
**Fichiers :**
- `src/main/java/com/university/domain/enrollment/EnrollmentRepository.java`
- `src/main/java/com/university/domain/course/CourseRepository.java`
**Rôle :** Persistance des données

### 7. Tests
**Fichier :** `src/test/java/com/university/application/EnrollStudentUseCaseTest.java`
**Rôle :** Tests des 4 scénarios (succès, validation échouée, cours introuvable, cours plein)

---

## 🔍 Détails de la Logique Métier

### Règles d'Acceptation d'Inscription

```
IF cours.status = FERMÉ
  THEN rejeter avec erreur 409
  
IF cours.status = COMPLET
  THEN rejeter avec erreur 409
  
IF cours.status = OUVERT AND cours.enrolledCount < cours.maxEnrollments
  THEN accepter et créer l'inscription
  
IF inscription.count = cours.maxEnrollments
  THEN marquer le cours comme COMPLET
```

### Immuabilité et Transactions

```java
// Les objets sont immuables - chaque changement crée une nouvelle instance

// AVANT
Course {
  id: CS-301,
  status: OUVERT,
  enrolledCount: 15,
  maxEnrollments: 30
}

// APRÈS addEnrollment()
Course {
  id: CS-301,
  status: OUVERT,
  enrolledCount: 16,  // Nouveau nombre
  maxEnrollments: 30
}

// Les deux instances existent en mémoire
// La seconde est persistée, la première peut être discardée
```

---

## 💡 Points clés à retenir

✅ **Validation en couches :**
- HTTP/JSON (Spring) → Value Objects (Domaine) → Rules (Business)

✅ **Codes HTTP significatifs :**
- `201` : Création réussie
- `400` : Données invalides
- `404` : Ressource manquante
- `409` : Conflit métier

✅ **Immuabilité :**
- Course n'a pas de setter
- Chaque modification retourne une nouvelle instance

✅ **Logique métier dans le Domaine :**
- `Course.acceptsEnrollments()` encapsule les règles
- `Enrollment.create()` génère les valeurs

✅ **Séparation des responsabilités :**
- Controller : HTTP
- Use Case : Orchestration
- Domain : Logique métier
- Repository : Persistance

---

## 🎓 Prochaines étapes

1. Lire le **Besoin N°3** pour voir la mise à jour
2. Consulter les tests : `EnrollStudentUseCaseTest`
3. Lancer le projet : `mvn spring-boot:run`
4. Tester l'endpoint : `POST http://localhost:8080/api/v1/enrollments`
5. Essayer les 4 scénarios avec les exemples cURL

---

**👉 Besoin N°2 complet et documenté !** ✨
