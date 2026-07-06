# 🔌 OpenAPI - Besoin N°2 : Inscrire un Étudiant à un Cours

## Explication ligne par ligne du university-api.yaml

Voici UNIQUEMENT la section du YAML qui correspond au Besoin N°2 :

```yaml
# ============================================================================
# 🎯 ENDPOINT: POST /api/v1/enrollments
# Description: Créer une nouvelle inscription d'étudiant
# ============================================================================

paths:
  /enrollments:
    post:
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      # LIGNE 1: summary
      # Explication: Titre court de l'endpoint (affiché dans Swagger UI)
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      summary: Inscrire un étudiant à un cours
      
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      # LIGNE 2: description
      # Explication: Description détaillée du processus d'inscription
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      description: |
        Crée une nouvelle inscription d'étudiant à un cours.
        Applique les règles métier d'acceptation (statut du cours, capacité).
        Retourne une confirmation avec un ticket unique.
      
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      # LIGNE 3: operationId
      # Explication: Identifiant unique pour générer la méthode Java
      #              Génère: EnrollmentsApi.createEnrollment()
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      operationId: createEnrollment
      
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      # LIGNE 4: tags
      # Explication: Catégorie de regroupement dans Swagger UI
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      tags:
        - Inscriptions
      
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      # SECTION: requestBody
      # Explication: Définit le corps de la requête POST
      #              (données envoyées par le client)
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      requestBody:
        # Explication: Le corps est obligatoire pour cette requête
        required: true
        
        # Explication: Format des données entrantes
        content:
          application/json:
            # Explication: Référence au DTO de demande
            #              (voir section components.schemas ci-dessous)
            schema:
              $ref: '#/components/schemas/EnrollmentRequestDto'
            
            # Explication: Exemple concret de requête
            example:
              studentNumber: "ETU-2026-045"
              courseId: "CS-301"
              academicYear: "2025-2026"

      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      # SECTION: responses
      # Explication: Tous les codes de réponse possibles
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      responses:
        # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        # CODE: 201 Created
        # Explication: Inscription créée avec succès
        #              Code HTTP standard pour création de ressource
        # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        '201':
          description: Inscription créée avec succès
          
          # Explication: Headers spéciaux de réponse
          headers:
            Location:
              schema:
                type: string
              # Explication: L'en-tête Location indique où trouver la ressource créée
              #              Exemple: /api/v1/enrollments/E-2026-001
              description: URL de la ressource créée
          
          # Explication: Le corps de la réponse contient la confirmation
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/EnrollmentResponseDto'
              
              # Explication: Exemple de réponse avec confirmation
              example:
                id: "E-2026-001"
                studentNumber: "ETU-2026-045"
                courseId: "CS-301"
                academicYear: "2025-2026"
                ticketNumber: "TKT-2025-156789"
                enrollmentDate: "2025-01-15T10:30:45Z"
                status: "CONFIRMED"

        # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        # CODE: 400 Bad Request
        # Explication: Données invalides (format incorrect, champs manquants)
        #              Exemple: studentNumber "invalid" au lieu de "ETU-YYYY-NNN"
        # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
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

        # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        # CODE: 404 Not Found
        # Explication: Le cours demandé n'existe pas en base de données
        #              Exemple: courseId "CS-999" inexistant
        # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
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

        # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        # CODE: 409 Conflict
        # Explication: Conflit métier - le cours est plein ou fermé
        #              Ne peut pas accepter l'inscription
        #              Code HTTP standard pour conflit d'affaires
        # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
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

        # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        # CODE: 500 Internal Server Error
        # Explication: Erreur serveur (problème technique non prévu)
        # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        '500':
          description: Erreur serveur
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponseDto'
```

---

## 📦 Composants utilisés (DTOs d'entrée/sortie)

### EnrollmentRequestDto (Données envoyées par le client)

```yaml
components:
  schemas:
    EnrollmentRequestDto:
      type: object
      description: Demande d'inscription d'un étudiant
      
      # Explication: Ces 3 champs DOIVENT être présents
      required:
        - studentNumber
        - courseId
        - academicYear
      
      properties:
        # ┌─────────────────────────────────────────────────────────────┐
        # │ CHAMP: studentNumber                                        │
        # │ Explication: Matricule unique de l'étudiant                 │
        # │ Format: ETU-YYYY-NNN (ex: ETU-2026-045)                     │
        # └─────────────────────────────────────────────────────────────┘
        studentNumber:
          type: string
          description: Matricule de l'étudiant
          # Explication: Regex pour valider le format
          #              ^ = début
          #              \\d{4} = 4 chiffres (année)
          #              \\d{3} = 3 chiffres (numéro)
          #              $ = fin
          pattern: "^ETU-\\d{4}-\\d{3}$"
          minLength: 12      # "ETU-2026-045" = 12 caractères
          maxLength: 12      # Doit être exactement 12
          example: "ETU-2026-045"
        
        # ┌─────────────────────────────────────────────────────────────┐
        # │ CHAMP: courseId                                             │
        # │ Explication: Identifiant du cours (ex: CS-301)              │
        # └─────────────────────────────────────────────────────────────┘
        courseId:
          type: string
          description: Identifiant unique du cours
          minLength: 1       # Au minimum 1 caractère
          maxLength: 50      # Au maximum 50 caractères
          example: "CS-301"
        
        # ┌─────────────────────────────────────────────────────────────┐
        # │ CHAMP: academicYear                                         │
        # │ Explication: Année académique (ex: 2025-2026)               │
        # │ Format: YYYY-YYYY                                           │
        # └─────────────────────────────────────────────────────────────┘
        academicYear:
          type: string
          description: Année académique au format YYYY-YYYY
          # Explication: Regex pour valider l'année
          #              \\d{4} = 4 chiffres (année début)
          #              \\d{4} = 4 chiffres (année fin)
          pattern: "^\\d{4}-\\d{4}$"
          minLength: 9       # "2025-2026" = 9 caractères
          maxLength: 9       # Doit être exactement 9
          example: "2025-2026"
```

### EnrollmentResponseDto (Réponse du serveur)

```yaml
    EnrollmentResponseDto:
      type: object
      description: Confirmation d'inscription
      
      # Explication: Tous ces champs sont retournés au client
      required:
        - id
        - studentNumber
        - courseId
        - academicYear
        - ticketNumber
        - enrollmentDate
        - status
      
      properties:
        # ┌─────────────────────────────────────────────────────────────┐
        # │ CHAMP: id                                                   │
        # │ Explication: Identifiant unique de l'inscription            │
        # └─────────────────────────────────────────────────────────────┘
        id:
          type: string
          description: Identifiant unique de l'inscription
          example: "E-2026-001"
        
        # ┌─────────────────────────────────────────────────────────────┐
        # │ CHAMP: studentNumber                                        │
        # │ Explication: Reproduit le matricule fourni                  │
        # └─────────────────────────────────────────────────────────────┘
        studentNumber:
          type: string
          description: Matricule de l'étudiant
          example: "ETU-2026-045"
        
        # ┌─────────────────────────────────────────────────────────────┐
        # │ CHAMP: courseId                                             │
        # │ Explication: Reproduit l'ID du cours fourni                 │
        # └─────────────────────────────────────────────────────────────┘
        courseId:
          type: string
          description: Identifiant du cours
          example: "CS-301"
        
        # ┌─────────────────────────────────────────────────────────────┐
        # │ CHAMP: academicYear                                         │
        # │ Explication: Reproduit l'année académique fournie           │
        # └─────────────────────────────────────────────────────────────┘
        academicYear:
          type: string
          description: Année académique
          example: "2025-2026"
        
        # ┌─────────────────────────────────────────────────────────────┐
        # │ CHAMP: ticketNumber                                         │
        # │ Explication: Numéro de ticket GÉNÉRÉ par le serveur         │
        # │ Format: TKT-YYYY-XXXXXX                                     │
        # │ Utilisé pour tracer l'inscription                           │
        # └─────────────────────────────────────────────────────────────┘
        ticketNumber:
          type: string
          description: Numéro de ticket unique pour la confirmation
          example: "TKT-2025-156789"
        
        # ┌─────────────────────────────────────────────────────────────┐
        # │ CHAMP: enrollmentDate                                       │
        # │ Explication: Date/heure d'inscription GÉNÉRÉE par serveur   │
        # │ Format ISO 8601: YYYY-MM-DDTHH:MM:SSZ                       │
        # └─────────────────────────────────────────────────────────────┘
        enrollmentDate:
          type: string
          format: date-time
          description: Date et heure d'inscription
          example: "2025-01-15T10:30:45Z"
        
        # ┌─────────────────────────────────────────────────────────────┐
        # │ CHAMP: status                                               │
        # │ Explication: Statut de l'inscription                        │
        # │ CONFIRMED = inscription validée et confirmée                │
        # └─────────────────────────────────────────────────────────────┘
        status:
          type: string
          description: Statut de l'inscription
          enum:
            - CONFIRMED    # Inscription confirmée et valide
            - PENDING      # Inscription en attente de confirmation
            - CANCELLED    # Inscription annulée
          example: "CONFIRMED"
```

---

## 🎯 Résumé du Besoin N°2 en YAML

| Aspect | Définition YAML | Explication |
|--------|-----------------|------------|
| **Endpoint** | `paths: /enrollments:` | Ressource "enrollments" |
| **Méthode HTTP** | `post:` | Création d'une ressource |
| **Données d'entrée** | `EnrollmentRequestDto` (3 champs) | Matricule + Cours + Année |
| **Réponse réussie** | `201 Created` | Inscription créée |
| **Réponse réussie (corps)** | `EnrollmentResponseDto` | Confirmation avec ticket |
| **Erreur validation** | `400 Bad Request` | Données invalides |
| **Erreur ressource** | `404 Not Found` | Cours inexistant |
| **Erreur métier** | `409 Conflict` | Cours plein/fermé |
| **Erreur serveur** | `500 Internal Server Error` | Problème technique |

---

## 📊 Flux de données en YAML

```
CLIENT ENVOIE (requestBody)
│
└─> POST /enrollments
    └─> EnrollmentRequestDto
        ├─ studentNumber: "ETU-2026-045"
        ├─ courseId: "CS-301"
        └─ academicYear: "2025-2026"

SERVEUR RETOURNE (response body + status)
│
├─ Status 201 Created (réussi)
│  └─> EnrollmentResponseDto
│      ├─ id: "E-2026-001"  ← Généré
│      ├─ ticketNumber: "TKT-2025-156789"  ← Généré
│      ├─ enrollmentDate: "2025-01-15T10:30:45Z"  ← Généré
│      └─ status: "CONFIRMED"
│
├─ Status 400 Bad Request (validation échouée)
│  └─> ErrorResponseDto
│      ├─ errorCode: "INVALID_REQUEST"
│      └─ message: "..."
│
├─ Status 404 Not Found (cours inexistant)
│  └─> ErrorResponseDto
│      ├─ errorCode: "COURSE_NOT_FOUND"
│      └─ message: "..."
│
├─ Status 409 Conflict (cours plein)
│  └─> ErrorResponseDto
│      ├─ errorCode: "COURSE_FULL"
│      └─ message: "..."
│
└─ Status 500 Internal Server Error
   └─> ErrorResponseDto
       ├─ errorCode: "INTERNAL_ERROR"
       └─ message: "..."
```

---

**✅ Besoin N°2 complètement expliqué au niveau du YAML!**
