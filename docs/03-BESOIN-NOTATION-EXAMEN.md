# 📊 Besoin N°3 : Saisir la Note d'Examen

## 🎯 Vue d'ensemble

**Besoin métier :** Un professeur met à jour le dossier d'un étudiant pour ajouter sa note d'examen et une appréciation optionnelle. La réponse est silencieuse (204 No Content).

**Type d'opération :** 📊 **MISE À JOUR** (Update partielle)
**Endpoint HTTP :** `PUT /api/v1/enrollments/{enrollmentId}/grades`
**Codes possibles :**
- `204 No Content` ✅ Succès (pas de corps de réponse)
- `400 Bad Request` ❌ Validation échouée
- `404 Not Found` ❌ Inscription inexistante
- `409 Conflict` ⚠️ Inscription déjà notée

---

## 📋 Flux d'exécution détaillé

### Étape 1️⃣  : Professeur envoie la note

Le professeur accède au formulaire de notation et envoie la note et l'appréciation :

```bash
PUT /api/v1/enrollments/E-2026-001/grades HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "score": 15.5,
  "feedback": "Très bonne compréhension des concepts"
}
```

**Données :**
- `{enrollmentId}` : Identifiant de l'inscription (dans l'URL)
- `score` : Note sur 20 (0-20, décimal accepté)
- `feedback` : Appréciation textuelle (optionnel, max 500 caractères)

---

### Étape 2️⃣  : REST Controller valide la requête

Le contrôleur reçoit et valide les données :

```java
// Fichier: EnrollmentController.java
@PutMapping("/enrollments/{enrollmentId}/grades")
public ResponseEntity<Void> recordGrade(
    @PathVariable String enrollmentId,
    @RequestBody @Valid RecordGradeRequestDto request
) {
    // 1. Valider l'ID de l'inscription
    EnrollmentId id = EnrollmentId.of(enrollmentId);
    
    // 2. Valider et créer la Grade
    Grade grade = Grade.of(request.getScore(), request.getFeedback());
    
    // 3. Appeler le Use Case
    recordGradeUseCase.execute(id, grade);
    
    // 4. Retourner 204 No Content (pas de corps)
    return ResponseEntity.noContent().build();
}
```

**Validations appliquées :**
- ✅ `enrollmentId` n'est pas vide
- ✅ `score` est entre 0 et 20 (inclus)
- ✅ `feedback` optionnel, max 500 caractères

---

### Étape 3️⃣  : Value Object Grade valide la note

La Grade encapsule la validation de la note :

```java
// Fichier: Grade.java
public class Grade {
    private BigDecimal score;      // 0.0 à 20.0
    private String feedback;       // Optionnel, max 500 chars
    
    public static Grade of(BigDecimal score, String feedback) {
        // Validation de la note
        if (score == null) {
            throw new IllegalArgumentException("Score cannot be null");
        }
        
        if (score.compareTo(BigDecimal.ZERO) < 0 || 
            score.compareTo(new BigDecimal("20")) > 0) {
            throw new IllegalArgumentException(
                "Score must be between 0 and 20, got: " + score
            );
        }
        
        // Validation du feedback
        if (feedback != null && feedback.length() > 500) {
            throw new IllegalArgumentException(
                "Feedback too long: " + feedback.length() + " characters"
            );
        }
        
        return new Grade(score, feedback);
    }
}
```

**Si validation échoue :** Retourne `400 Bad Request`

---

### Étape 4️⃣  : Use Case charge l'Enrollment

Le Use Case récupère l'inscription depuis la base de données :

```java
// Fichier: RecordGradeUseCase.java
public void execute(EnrollmentId enrollmentId, Grade grade) {
    // 1. Charger l'inscription
    Enrollment enrollment = enrollmentRepository
        .findById(enrollmentId.getValue())
        .orElseThrow(() -> new EnrollmentNotFoundException(
            "Enrollment not found: " + enrollmentId
        ));
    
    // 2. Appeler la logique métier de l'enrollment
    // Voir Étape 5️⃣  ci-dessous
}
```

**Si l'inscription n'existe pas :** Retourne `404 Not Found`

---

### Étape 5️⃣  : Domain Model applique les règles métier

L'Enrollment encapsule la logique de notation :

```java
// Fichier: Enrollment.java
public class Enrollment {
    private EnrollmentId id;
    private StudentNumber studentNumber;
    private CourseId courseId;
    private AcademicYear academicYear;
    private EnrollmentStatus status;  // CONFIRMED, PENDING, CANCELLED
    private Grade grade;              // Null avant notation
    
    // Règles métier encapsulées
    public boolean canBeGraded() {
        // Règle 1: L'inscription doit être CONFIRMED
        if (!status.equals(EnrollmentStatus.CONFIRMED)) {
            return false;
        }
        
        // Règle 2: L'inscription ne doit pas déjà avoir une note
        if (hasGrade()) {
            return false;
        }
        
        return true;
    }
    
    public boolean hasGrade() {
        return grade != null;
    }
    
    // Enregistrer la note (retourne une nouvelle instance)
    public Enrollment recordGrade(Grade newGrade) {
        // Vérifier les règles
        if (!canBeGraded()) {
            throw new IllegalStateException(
                "Enrollment " + id + " cannot be graded"
            );
        }
        
        // Créer une nouvelle instance avec la note (immuabilité)
        return new Enrollment(
            this.id,
            this.studentNumber,
            this.courseId,
            this.academicYear,
            this.status,
            this.enrollmentDate,
            this.ticketNumber,
            newGrade,  // Ajouter la note
            LocalDateTime.now()  // Marquer la date de notation
        );
    }
}
```

---

### Étape 6️⃣  : Use Case met à jour et persiste

Le Use Case met à jour l'enrollment avec la note :

```java
// Continuation: RecordGradeUseCase.java
public void execute(EnrollmentId enrollmentId, Grade grade) {
    // 1. Charger l'inscription
    Enrollment enrollment = enrollmentRepository
        .findById(enrollmentId.getValue())
        .orElseThrow(/* ... */);
    
    // 2. Vérifier si elle peut être notée
    if (!enrollment.canBeGraded()) {
        if (enrollment.hasGrade()) {
            throw new EnrollmentAlreadyGradedException(
                "Enrollment " + enrollmentId + " already has a grade"
            );
        } else {
            throw new InvalidEnrollmentStatusException(
                "Enrollment " + enrollmentId + " is not CONFIRMED"
            );
        }
    }
    
    // 3. Enregistrer la note (crée nouvelle instance)
    Enrollment gradedEnrollment = enrollment.recordGrade(grade);
    
    // 4. Persister la modification
    enrollmentRepository.save(gradedEnrollment);
    
    // 5. Retourner void (la réponse sera 204 No Content)
}
```

**Si l'enrollment a déjà une note :** Retourne `409 Conflict`

---

### Étape 7️⃣  : Réponse HTTP (silencieuse)

Le serveur retourne un code 204 sans corps de réponse :

```
HTTP/1.1 204 No Content
Date: Wed, 15 Jan 2025 10:50:30 GMT
```

**Pourquoi 204 ?**
- L'opération de mise à jour est réussie
- Il n'y a rien à retourner au client
- Le client n'a besoin que de savoir si c'est OK ou en erreur
- Économise la bande passante (pas de corps JSON)

---

## 🔌 Spécification OpenAPI

Voici la section du `university-api.yaml` qui définit ce besoin :

```yaml
# ====== BESOIN N°3 ======
# Saisir la note d'examen
# =======================

paths:
  /enrollments/{enrollmentId}/grades:
    put:
      summary: Enregistrer la note d'un étudiant
      description: |
        Met à jour l'inscription avec la note d'examen et l'appréciation optionnelle.
        Seul un professeur autorisé peut enregistrer une note.
        Une inscription ne peut être notée qu'une seule fois.
      operationId: recordGrade
      tags:
        - Inscriptions
      parameters:
        - name: enrollmentId
          in: path
          description: Identifiant unique de l'inscription
          required: true
          schema:
            type: string
          example: "E-2026-001"
      
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/RecordGradeRequestDto'
            example:
              score: 15.5
              feedback: "Très bonne compréhension des concepts"
      
      responses:
        '204':
          description: Note enregistrée avec succès (pas de contenu)

        '400':
          description: Erreur de validation (note invalide, feedback trop long)
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponseDto'
              example:
                errorCode: INVALID_GRADE
                message: "La note doit être entre 0 et 20"
                timestamp: "2025-01-15T10:50:45Z"

        '404':
          description: Inscription non trouvée
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponseDto'
              example:
                errorCode: ENROLLMENT_NOT_FOUND
                message: "L'inscription E-2026-999 n'existe pas"
                timestamp: "2025-01-15T10:50:45Z"

        '409':
          description: Conflit (inscription déjà notée ou statut invalide)
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponseDto'
              example:
                errorCode: ENROLLMENT_ALREADY_GRADED
                message: "L'inscription E-2026-001 a déjà une note"
                timestamp: "2025-01-15T10:50:45Z"

        '500':
          description: Erreur serveur
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponseDto'
```

---

## 💾 Structures de Données (DTOs)

### RecordGradeRequestDto (Entrée)

```yaml
components:
  schemas:
    RecordGradeRequestDto:
      type: object
      description: Demande d'enregistrement de note
      required:
        - score
      properties:
        score:
          type: number
          format: decimal
          description: Note sur 20 (0.0 à 20.0)
          minimum: 0.0
          maximum: 20.0
          example: 15.5
        
        feedback:
          type: string
          description: Appréciation textuelle (optionnel)
          maxLength: 500
          example: "Très bonne compréhension des concepts"
```

**Notes importantes :**
- `score` : Obligatoire, décimal de 0.0 à 20.0
- `feedback` : Optionnel, max 500 caractères
- Pas de `RecordGradeResponseDto` car réponse = 204 No Content

---

## ✅ Codes de réponse HTTP

| Code | Signification | Quand ? |
|------|---------------|--------|
| `204 No Content` | ✅ Succès | Note enregistrée avec succès |
| `400 Bad Request` | ❌ Validation échouée | Score < 0 ou > 20, feedback trop long |
| `404 Not Found` | ❌ Ressource manquante | Enrollment inexistant |
| `409 Conflict` | ⚠️ Conflit métier | Enrollment déjà noté ou statut invalide |
| `500 Internal Error` | ❌ Erreur serveur | Problème serveur |

---

## 🧪 Exemples d'utilisation (cURL)

### Exemple 1️⃣  : Notation réussie

```bash
curl -X PUT "http://localhost:8080/api/v1/enrollments/E-2026-001/grades" \
  -H "Content-Type: application/json" \
  -d '{
    "score": 15.5,
    "feedback": "Très bonne compréhension des concepts"
  }'
```

**Réponse (204 No Content) :**
```
HTTP/1.1 204 No Content
Date: Wed, 15 Jan 2025 10:50:30 GMT
```

(Aucun corps de réponse)

---

### Exemple 2️⃣  : Notation réussie (sans feedback)

```bash
curl -X PUT "http://localhost:8080/api/v1/enrollments/E-2026-002/grades" \
  -H "Content-Type: application/json" \
  -d '{
    "score": 12.0
  }'
```

**Réponse (204 No Content) :**
```
HTTP/1.1 204 No Content
Date: Wed, 15 Jan 2025 10:52:15 GMT
```

---

### Exemple 3️⃣  : Note invalide (> 20)

```bash
curl -X PUT "http://localhost:8080/api/v1/enrollments/E-2026-001/grades" \
  -H "Content-Type: application/json" \
  -d '{
    "score": 25.0,
    "feedback": "Trop bien"
  }'
```

**Réponse (400 Bad Request) :**
```json
{
  "errorCode": "INVALID_GRADE",
  "message": "La note doit être entre 0 et 20, reçu: 25.0",
  "timestamp": "2025-01-15T10:55:20.123Z"
}
```

---

### Exemple 4️⃣  : Note invalide (< 0)

```bash
curl -X PUT "http://localhost:8080/api/v1/enrollments/E-2026-001/grades" \
  -H "Content-Type: application/json" \
  -d '{
    "score": -5.0
  }'
```

**Réponse (400 Bad Request) :**
```json
{
  "errorCode": "INVALID_GRADE",
  "message": "La note doit être entre 0 et 20, reçu: -5.0",
  "timestamp": "2025-01-15T11:00:45.567Z"
}
```

---

### Exemple 5️⃣  : Enrollment inexistant

```bash
curl -X PUT "http://localhost:8080/api/v1/enrollments/E-2026-999/grades" \
  -H "Content-Type: application/json" \
  -d '{
    "score": 15.0
  }'
```

**Réponse (404 Not Found) :**
```json
{
  "errorCode": "ENROLLMENT_NOT_FOUND",
  "message": "L'inscription E-2026-999 n'existe pas",
  "timestamp": "2025-01-15T11:05:30.890Z"
}
```

---

### Exemple 6️⃣  : Enrollment déjà noté

```bash
curl -X PUT "http://localhost:8080/api/v1/enrollments/E-2026-001/grades" \
  -H "Content-Type: application/json" \
  -d '{
    "score": 18.5,
    "feedback": "Excellente performance"
  }'
```

(Supposant que E-2026-001 a déjà une note)

**Réponse (409 Conflict) :**
```json
{
  "errorCode": "ENROLLMENT_ALREADY_GRADED",
  "message": "L'inscription E-2026-001 a déjà une note (15.5)",
  "timestamp": "2025-01-15T11:10:15.234Z"
}
```

---

### Exemple 7️⃣  : Feedback trop long

```bash
curl -X PUT "http://localhost:8080/api/v1/enrollments/E-2026-003/grades" \
  -H "Content-Type: application/json" \
  -d '{
    "score": 10.0,
    "feedback": "'$(printf 'x%.0s' {1..501})'"
  }'
```

**Réponse (400 Bad Request) :**
```json
{
  "errorCode": "INVALID_FEEDBACK",
  "message": "Le feedback ne peut dépasser 500 caractères (501 reçus)",
  "timestamp": "2025-01-15T11:15:40.456Z"
}
```

---

## 🛠️ Fichiers impactés

### 1. OpenAPI Spécification
**Fichier :** `src/main/resources/openapi/university-api.yaml`
**Rôle :** Définit le contrat du endpoint `PUT /enrollments/{id}/grades`

### 2. REST Controller
**Fichier :** `src/main/java/com/university/infrastructure/adapter/EnrollmentController.java`
**Rôle :** Reçoit les requêtes PUT et valide les données

### 3. Use Case
**Fichier :** `src/main/java/com/university/application/enrollment/RecordGradeUseCase.java`
**Rôle :** Orchestre l'enregistrement de la note

### 4. Domain Entity
**Fichier :** `src/main/java/com/university/domain/enrollment/Enrollment.java`
**Rôle :** Encapsule la logique de notation (validation, immuabilité)

### 5. Value Object Grade
**Fichier :** `src/main/java/com/university/domain/shared/Grade.java`
**Rôle :** Valide et encapsule la note + feedback

### 6. Repository
**Fichier :** `src/main/java/com/university/domain/enrollment/EnrollmentRepository.java`
**Rôle :** Persistance des modifications

### 7. Tests
**Fichier :** `src/test/java/com/university/application/RecordGradeUseCaseTest.java`
**Rôle :** Tests des scénarios (succès, validation échouée, inexistant, déjà noté)

---

## 🔍 Détails de la Logique Métier

### Règles de Notation

```
IF enrollment.status != CONFIRMED
  THEN rejeter avec erreur 409
  
IF enrollment.hasGrade() = true
  THEN rejeter avec erreur 409 (déjà noté)
  
IF score < 0 OR score > 20
  THEN rejeter avec erreur 400 (validation)
  
IF feedback.length() > 500
  THEN rejeter avec erreur 400 (validation)
  
IF toutes les vérifications = OK
  THEN enregistrer la note et retourner 204
```

### Immuabilité de la notation

```java
// AVANT
Enrollment {
  id: E-2026-001,
  studentNumber: ETU-2026-045,
  courseId: CS-301,
  grade: null,
  gradedAt: null
}

// APRÈS recordGrade()
Enrollment {
  id: E-2026-001,
  studentNumber: ETU-2026-045,
  courseId: CS-301,
  grade: Grade(15.5, "Très bonne..."),
  gradedAt: 2025-01-15T10:50:30Z
}
```

---

## 💡 Points clés à retenir

✅ **Code 204 No Content :**
- Utilisé pour les opérations de mise à jour réussie
- Pas de corps de réponse
- Économise la bande passante

✅ **Validation en couches :**
- HTTP/JSON (Spring) → Value Objects (Domaine) → Rules (Business)

✅ **Idempotence (partielle) :**
- Appeler 2x le même endpoint retourne 409 (idempotence = fausse)
- C'est intentionnel : éviter les doublons de notation

✅ **Immuabilité :**
- Enrollment n'a pas de setter pour la note
- La notation crée une nouvelle instance

✅ **Codes HTTP appropriés :**
- `204` : Succès
- `400` : Données invalides
- `404` : Ressource manquante
- `409` : Conflit métier

✅ **Logique métier encapsulée :**
- `Enrollment.canBeGraded()` : Règles d'acceptation
- `Grade.of()` : Validation de la note

---

## 🎓 Prochaines étapes

1. Consulter les **3 besoins** pour avoir une vue globale
2. Consulter les tests : `RecordGradeUseCaseTest`
3. Lancer le projet : `mvn spring-boot:run`
4. Tester l'endpoint : `PUT http://localhost:8080/api/v1/enrollments/{id}/grades`
5. Essayer les 7 scénarios avec les exemples cURL

---

## 📊 Récapitulatif des 3 Besoins

| Besoin | Opération | Endpoint | Code Succès | Cas d'Erreur |
|--------|-----------|----------|-------------|--------------|
| 1️⃣ Catalogue | READ | GET /courses | 200 OK | 400, 500 |
| 2️⃣ Inscription | CREATE | POST /enrollments | 201 Created | 400, 404, 409 |
| 3️⃣ Notation | UPDATE | PUT /enrollments/.../grades | 204 No Content | 400, 404, 409 |

---

**👉 Besoin N°3 complet et documenté !** ✨
