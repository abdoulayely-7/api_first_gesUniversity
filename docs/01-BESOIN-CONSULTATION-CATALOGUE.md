# 📖 Besoin N°1 : Consulter le Catalogue des Cours

## 🎯 Vue d'ensemble

**Besoin métier :** L'application doit permettre aux étudiants de consulter la liste complète des cours disponibles avec la possibilité de filtrer par niveau académique (Licence 1-3, Master 1-2).

**Type d'opération :** 📖 **LECTURE** (Consultation)
**Endpoint HTTP :** `GET /api/v1/courses`
**Code de succès :** `200 OK`

---

## 📋 Flux d'exécution détaillé

### Étape 1️⃣  : Client envoie la requête HTTP

L'étudiant accède au catalogue des cours. Le frontend (web/mobile) envoie une requête HTTP :

```bash
# SANS filtre - Récupérer TOUS les cours
GET /api/v1/courses HTTP/1.1
Host: localhost:8080
Accept: application/json

# AVEC filtre - Récupérer uniquement les cours de Licence 3
GET /api/v1/courses?level=LICENCE_3 HTTP/1.1
Host: localhost:8080
Accept: application/json
```

**Paramètres possibles :**
- `level` (optionnel) : `LICENCE_1` | `LICENCE_2` | `LICENCE_3` | `MASTER_1` | `MASTER_2`

---

### Étape 2️⃣  : REST Controller reçoit la requête

Le contrôleur HTTP reçoit la requête et l'extrait les paramètres :

```java
// Fichier: CourseController.java
@GetMapping("/courses")
public ResponseEntity<List<CourseResponseDto>> getCourses(
    @RequestParam(required = false) String level
) {
    // 1. Convertir le paramètre optionnel en CourseLevel
    Optional<CourseLevel> courseLevel = level != null 
        ? Optional.of(CourseLevel.of(level)) 
        : Optional.empty();
    
    // 2. Appeler le Use Case
    List<CourseResponseDto> result = listCoursesUseCase.execute(courseLevel);
    
    // 3. Retourner la réponse HTTP
    return ResponseEntity.ok(result);
}
```

---

### Étape 3️⃣  : Use Case orchestre l'opération

Le service métier (Use Case) cherche les cours selon les critères :

```java
// Fichier: ListCoursesUseCase.java
public List<CourseResponseDto> execute(Optional<CourseLevel> level) {
    // 1. Récupérer les cours depuis la base de données
    List<Course> courses = courseRepository.findAll();
    
    // 2. Filtrer par niveau si spécifié
    if (level.isPresent()) {
        courses = courses.stream()
            .filter(course -> course.getLevel().equals(level.get()))
            .collect(Collectors.toList());
    }
    
    // 3. Convertir en DTOs pour la réponse HTTP
    return courses.stream()
        .map(this::toCourseResponseDto)
        .collect(Collectors.toList());
}
```

---

### Étape 4️⃣  : Repository accède à la base de données

Le repository récupère les données persistées :

```java
// Fichier: CourseRepository.java (interface JPA)
@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    // Trouver tous les cours (déjà fourni par JpaRepository)
    List<Course> findAll();
    
    // Trouver les cours par niveau
    List<Course> findByLevel(CourseLevel level);
}
```

**SQL généré par Hibernate :**
```sql
-- Si sans filtre
SELECT * FROM courses;

-- Si avec filtre niveau=LICENCE_3
SELECT * FROM courses WHERE level = 'LICENCE_3';
```

---

### Étape 5️⃣  : Domain Model retourne les Entities

La base de données retourne des objets **Course** (entités du domaine) :

```java
// Exemple de Course chargée
Course {
    id: CourseId("CS-301"),
    title: "Architecture Logicielle",
    credits: 6,
    professor: "Dr. Martin",
    level: CourseLevel.LICENCE_3,
    status: CourseStatus.OUVERT,
    maxEnrollments: 30,
    enrolledCount: 15,
    createdAt: 2025-01-10,
    ...
}
```

---

### Étape 6️⃣  : Conversion DTO pour la réponse HTTP

Les Course sont converties en CourseResponseDto :

```java
private CourseResponseDto toCourseResponseDto(Course course) {
    return new CourseResponseDto()
        .id(course.getId().getValue())
        .title(course.getTitle())
        .credits(course.getCredits())
        .professor(course.getProfessor())
        .level(course.getLevel().getValue())
        .status(course.getStatus().getValue())
        .enrolledCount(course.getEnrolledCount())
        .maxEnrollments(course.getMaxEnrollments());
}
```

---

### Étape 7️⃣  : Réponse HTTP retournée au client

Le serveur retourne un code 200 avec la liste des cours :

```json
HTTP/1.1 200 OK
Content-Type: application/json

[
  {
    "id": "CS-301",
    "title": "Architecture Logicielle",
    "credits": 6,
    "professor": "Dr. Martin",
    "level": "LICENCE_3",
    "status": "OUVERT",
    "enrolledCount": 15,
    "maxEnrollments": 30
  },
  {
    "id": "CS-302",
    "title": "Bases de Données",
    "credits": 5,
    "professor": "Prof. Chen",
    "level": "LICENCE_3",
    "status": "OUVERT",
    "enrolledCount": 28,
    "maxEnrollments": 30
  },
  {
    "id": "CS-303",
    "title": "Réseau et Protocoles",
    "credits": 4,
    "professor": "Prof. Taylor",
    "level": "LICENCE_3",
    "status": "COMPLET",
    "enrolledCount": 30,
    "maxEnrollments": 30
  }
]
```

---

## 🔌 Spécification OpenAPI

Voici la section du `university-api.yaml` qui définit ce besoin :

```yaml
# ====== BESOIN N°1 ======
# Consulter le catalogue des cours
# ========================

paths:
  /courses:
    get:
      summary: Lister tous les cours disponibles
      description: |
        Récupère la liste complète des cours avec la possibilité de filtrer par niveau.
        Les étudiants peuvent consulter le catalogue pour voir les cours disponibles.
      operationId: listCourses
      tags:
        - Cours
      parameters:
        - name: level
          in: query
          description: |
            Filtrer les cours par niveau académique.
            Si non fourni, retourne tous les cours.
          required: false
          schema:
            type: string
            enum:
              - LICENCE_1
              - LICENCE_2
              - LICENCE_3
              - MASTER_1
              - MASTER_2
          example: LICENCE_3
      
      responses:
        '200':
          description: Liste des cours récupérée avec succès
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/CourseResponseDto'
              example:
                - id: CS-301
                  title: Architecture Logicielle
                  credits: 6
                  professor: Dr. Martin
                  level: LICENCE_3
                  status: OUVERT
                  enrolledCount: 15
                  maxEnrollments: 30
                - id: CS-302
                  title: Bases de Données
                  credits: 5
                  professor: Prof. Chen
                  level: LICENCE_3
                  status: OUVERT
                  enrolledCount: 28
                  maxEnrollments: 30

        '400':
          description: Erreur de validation (niveau invalide)
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponseDto'
              example:
                errorCode: INVALID_LEVEL
                message: "Le niveau spécifié n'existe pas"
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

### CourseResponseDto

```yaml
# Dans university-api.yaml
components:
  schemas:
    CourseResponseDto:
      type: object
      description: Représentation d'un cours pour la consultation
      required:
        - id
        - title
        - credits
        - professor
        - level
        - status
        - enrolledCount
        - maxEnrollments
      properties:
        id:
          type: string
          description: Identifiant unique du cours (ex: CS-301)
          example: CS-301
        
        title:
          type: string
          description: Titre/Intitulé du cours
          minLength: 5
          maxLength: 255
          example: Architecture Logicielle
        
        credits:
          type: integer
          description: Nombre de crédits ECTS
          minimum: 1
          maximum: 30
          example: 6
        
        professor:
          type: string
          description: Nom du professeur responsable
          minLength: 3
          maxLength: 255
          example: Dr. Martin
        
        level:
          type: string
          description: Niveau académique
          enum:
            - LICENCE_1
            - LICENCE_2
            - LICENCE_3
            - MASTER_1
            - MASTER_2
          example: LICENCE_3
        
        status:
          type: string
          description: Statut du cours (obligatoirement OUVERT, COMPLET ou FERME)
          enum:
            - OUVERT
            - COMPLET
            - FERME
          example: OUVERT
        
        enrolledCount:
          type: integer
          description: Nombre d'étudiants actuellement inscrits
          minimum: 0
          example: 15
        
        maxEnrollments:
          type: integer
          description: Capacité maximale d'inscrits
          minimum: 1
          example: 30
```

---

## ✅ Codes de réponse HTTP

| Code | Signification | Quand ? |
|------|---------------|--------|
| `200 OK` | ✅ Succès | La liste des cours est retournée |
| `400 Bad Request` | ❌ Erreur client | Le paramètre `level` est invalide |
| `500 Internal Server Error` | ❌ Erreur serveur | Problème base de données ou serveur |

---

## 🧪 Exemples d'utilisation (cURL)

### Exemple 1️⃣  : Récupérer TOUS les cours

```bash
curl -X GET "http://localhost:8080/api/v1/courses" \
  -H "Content-Type: application/json"
```

**Réponse (200 OK) :**
```json
[
  {
    "id": "CS-301",
    "title": "Architecture Logicielle",
    "credits": 6,
    "professor": "Dr. Martin",
    "level": "LICENCE_3",
    "status": "OUVERT",
    "enrolledCount": 15,
    "maxEnrollments": 30
  },
  {
    "id": "CS-302",
    "title": "Bases de Données",
    "credits": 5,
    "professor": "Prof. Chen",
    "level": "LICENCE_3",
    "status": "OUVERT",
    "enrolledCount": 28,
    "maxEnrollments": 30
  },
  {
    "id": "MATH-201",
    "title": "Algèbre Linéaire",
    "credits": 4,
    "professor": "Prof. Dupont",
    "level": "LICENCE_2",
    "status": "OUVERT",
    "enrolledCount": 22,
    "maxEnrollments": 25
  }
]
```

---

### Exemple 2️⃣  : Filtrer par niveau (Licence 3 uniquement)

```bash
curl -X GET "http://localhost:8080/api/v1/courses?level=LICENCE_3" \
  -H "Content-Type: application/json"
```

**Réponse (200 OK) :**
```json
[
  {
    "id": "CS-301",
    "title": "Architecture Logicielle",
    "credits": 6,
    "professor": "Dr. Martin",
    "level": "LICENCE_3",
    "status": "OUVERT",
    "enrolledCount": 15,
    "maxEnrollments": 30
  },
  {
    "id": "CS-302",
    "title": "Bases de Données",
    "credits": 5,
    "professor": "Prof. Chen",
    "level": "LICENCE_3",
    "status": "OUVERT",
    "enrolledCount": 28,
    "maxEnrollments": 30
  },
  {
    "id": "CS-303",
    "title": "Réseau et Protocoles",
    "credits": 4,
    "professor": "Prof. Taylor",
    "level": "LICENCE_3",
    "status": "COMPLET",
    "enrolledCount": 30,
    "maxEnrollments": 30
  }
]
```

---

### Exemple 3️⃣  : Filtre invalide (Master 1)

```bash
curl -X GET "http://localhost:8080/api/v1/courses?level=MASTER_1" \
  -H "Content-Type: application/json"
```

**Réponse (200 OK) :**
```json
[
  {
    "id": "PROG-401",
    "title": "Programmation Avancée",
    "credits": 8,
    "professor": "Prof. Rubin",
    "level": "MASTER_1",
    "status": "OUVERT",
    "enrolledCount": 12,
    "maxEnrollments": 20
  },
  {
    "id": "AI-401",
    "title": "Intelligence Artificielle",
    "credits": 8,
    "professor": "Dr. Leblanc",
    "level": "MASTER_1",
    "status": "OUVERT",
    "enrolledCount": 18,
    "maxEnrollments": 20
  }
]
```

---

## 🛠️ Fichiers impactés

### 1. OpenAPI Spécification
**Fichier :** `src/main/resources/openapi/university-api.yaml`
**Rôle :** Définit le contrat du endpoint `GET /courses`

### 2. REST Controller (généré + implémentation)
**Fichier :** `src/main/java/com/university/infrastructure/adapter/CourseController.java`
**Rôle :** Reçoit les requêtes HTTP et appelle les Use Cases

### 3. Use Case
**Fichier :** `src/main/java/com/university/application/course/ListCoursesUseCase.java`
**Rôle :** Orchestre la récupération et le filtrage des cours

### 4. Repository
**Fichier :** `src/main/java/com/university/domain/course/CourseRepository.java`
**Rôle :** Interface d'accès aux données

### 5. Domain Model
**Fichier :** `src/main/java/com/university/domain/course/Course.java`
**Rôle :** Entité immuable représentant un cours

### 6. Value Objects
**Fichiers :**
- `src/main/java/com/university/domain/shared/CourseId.java`
- `src/main/java/com/university/domain/shared/CourseLevel.java`
- `src/main/java/com/university/domain/shared/CourseStatus.java`
**Rôle :** Encapsulent les valeurs métier avec validation

### 7. Tests
**Fichier :** `src/test/java/com/university/application/ListCoursesUseCaseTest.java`
**Rôle :** Vérifient le bon fonctionnement du besoin

---

## 🔍 Détails d'implémentation

### Où est stockée la logique métier ?

**❌ PAS dans le Controller :** Le controller ne fait que recevoir/envoyer

**❌ PAS dans le Use Case :** Le Use Case orchestre seulement

**✅ DANS le Domain Model :** La Course encapsule sa logique

```java
// MAUVAIS (ne pas faire)
public List<CourseResponseDto> getCourses(String level) {
    // Logique métier ici = MAUVAIS
    List<Course> all = db.findAll();
    if (level != null) {
        all = all.stream().filter(c -> c.getLevel().equals(level)).collect(...);
    }
    return all.stream().map(this::toDto).collect(...);
}

// BON
public List<CourseResponseDto> getCourses(String level) {
    // Appeler le Use Case
    return listCoursesUseCase.execute(
        level != null ? Optional.of(CourseLevel.of(level)) : Optional.empty()
    );
}

// Use Case : orchestration seulement
public List<CourseResponseDto> execute(Optional<CourseLevel> level) {
    List<Course> courses = courseRepository.findAll();
    if (level.isPresent()) {
        courses = courses.stream()
            .filter(c -> c.getLevel().equals(level.get()))
            .collect(...);
    }
    return courses.stream().map(this::toDto).collect(...);
}
```

---

## 💡 Points clés à retenir

✅ **API First :** Le besoin N°1 est complètement défini dans OpenAPI avant toute implémentation

✅ **Code Généré :** Les interfaces `CoursesApi` sont générées et NE JAMAIS modifiées

✅ **DDD :** La logique métier appartient à l'entité `Course`, pas au controller ou au service

✅ **Immuabilité :** Course n'a pas de setters. Les données sont créées via factory methods

✅ **Immutabilité :** Value Objects `CourseId`, `CourseLevel`, etc. sont immuables

✅ **Séparation des couches :**
- Controller : HTTP
- Use Case : Orchestration
- Domain : Logique métier
- Repository : Persistance

✅ **Testabilité :** Chaque couche peut être testée indépendamment

---

## 🎓 Prochaines étapes

1. Lire le **Besoin N°2** pour voir un cas plus complexe (création)
2. Consulter le code en `src/main/java/com/university/`
3. Lancer le projet : `mvn spring-boot:run`
4. Tester l'endpoint : `GET http://localhost:8080/api/v1/courses`
5. Accéder à Swagger UI : `http://localhost:8080/swagger-ui.html`

---

**👉 Besoin N°1 complet et documenté !** ✨
