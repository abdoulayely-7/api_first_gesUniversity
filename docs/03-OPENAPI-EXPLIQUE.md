# 🔌 OpenAPI - Besoin N°3 : Saisir la Note d'Examen

## Explication ligne par ligne du university-api.yaml

Voici UNIQUEMENT la section du YAML qui correspond au Besoin N°3 :

```yaml
# ============================================================================
# 🎯 ENDPOINT: PUT /api/v1/enrollments/{enrollmentId}/grades
# Description: Enregistrer la note d'un étudiant pour une inscription
# ============================================================================

paths:
  /enrollments/{enrollmentId}/grades:
    put:
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      # LIGNE 1: summary
      # Explication: Titre court de l'endpoint (affiché dans Swagger UI)
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      summary: Enregistrer la note d'un étudiant
      
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      # LIGNE 2: description
      # Explication: Description détaillée du processus de notation
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      description: |
        Met à jour l'inscription avec la note d'examen et l'appréciation optionnelle.
        Seul un professeur autorisé peut enregistrer une note.
        Une inscription ne peut être notée qu'une seule fois.
      
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      # LIGNE 3: operationId
      # Explication: Identifiant unique pour générer la méthode Java
      #              Génère: EnrollmentsApi.recordGrade()
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      operationId: recordGrade
      
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      # LIGNE 4: tags
      # Explication: Catégorie de regroupement dans Swagger UI
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      tags:
        - Inscriptions
      
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      # SECTION: parameters
      # Explication: Les paramètres de l'URL (path parameters)
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      parameters:
        - name: enrollmentId
          # Explication: Paramètre dans l'URL (pas query string)
          #              Exemple: /enrollments/E-2026-001/grades
          #              {enrollmentId} = E-2026-001
          in: path
          
          # Explication: Description du paramètre
          description: Identifiant unique de l'inscription
          
          # Explication: Ce paramètre est OBLIGATOIRE
          #              Contrairement aux query parameters qui peuvent être optionnels
          required: true
          
          # Explication: Type et format du paramètre
          schema:
            type: string
          
          # Explication: Exemple de valeur
          example: "E-2026-001"

      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      # SECTION: requestBody
      # Explication: Définit le corps de la requête PUT
      #              (données envoyées par le professeur)
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      requestBody:
        # Explication: Le corps est obligatoire pour cette requête
        required: true
        
        # Explication: Format des données entrantes
        content:
          application/json:
            # Explication: Référence au DTO de la requête de notation
            schema:
              $ref: '#/components/schemas/RecordGradeRequestDto'
            
            # Explication: Exemple concret de requête
            example:
              score: 15.5
              feedback: "Très bonne compréhension des concepts"

      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      # SECTION: responses
      # Explication: Tous les codes de réponse possibles
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      responses:
        # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        # CODE: 204 No Content
        # Explication: Note enregistrée avec succès
        #              IMPORTANT: PAS de corps de réponse
        #              Économise la bande passante
        #              Le client sait juste que c'est OK
        # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        '204':
          description: Note enregistrée avec succès (pas de contenu)
          # ATTENTION: Pas de "content:" pour 204
          # Le corps est vide par définition

        # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        # CODE: 400 Bad Request
        # Explication: Erreur de validation
        #              Exemples:
        #              - score > 20
        #              - score < 0
        #              - feedback trop long (> 500 caractères)
        #              - score manquant
        # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
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

        # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        # CODE: 404 Not Found
        # Explication: L'inscription avec cet ID n'existe pas
        #              Exemple: enrollmentId "E-2026-999" inexistant
        # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
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

        # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        # CODE: 409 Conflict
        # Explication: Conflit métier
        #              Exemples:
        #              - L'inscription a déjà une note
        #              - L'inscription n'est pas CONFIRMED
        #              Code HTTP standard pour conflits d'affaires
        # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        '409':
          description: Conflit (inscription déjà notée ou statut invalide)
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponseDto'
              example:
                errorCode: ENROLLMENT_ALREADY_GRADED
                message: "L'inscription E-2026-001 a déjà une note (15.5)"
                timestamp: "2025-01-15T10:50:45Z"

        # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        # CODE: 500 Internal Server Error
        # Explication: Erreur serveur (problème technique)
        # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        '500':
          description: Erreur serveur
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponseDto'
```

---

## 📦 Composants utilisés (DTOs)

### RecordGradeRequestDto (Données envoyées par le professeur)

```yaml
components:
  schemas:
    RecordGradeRequestDto:
      type: object
      description: Demande d'enregistrement de note
      
      # Explication: Seul "score" est obligatoire
      #              "feedback" est OPTIONNEL
      required:
        - score
      
      properties:
        # ┌─────────────────────────────────────────────────────────────┐
        # │ CHAMP: score                                                │
        # │ Explication: La note sur 20                                 │
        # │ Obligatoire: OUI                                            │
        # │ Type: nombre décimal (peut avoir des décimales)             │
        # │ Exemples: 15, 15.5, 20.0, 0                                │
        # └─────────────────────────────────────────────────────────────┘
        score:
          type: number
          format: decimal
          description: Note sur 20 (0.0 à 20.0)
          # Explication: La note minimale est 0.0 (inclus)
          minimum: 0.0
          # Explication: La note maximale est 20.0 (inclus)
          maximum: 20.0
          example: 15.5
        
        # ┌─────────────────────────────────────────────────────────────┐
        # │ CHAMP: feedback                                             │
        # │ Explication: Appréciation textuelle du professeur           │
        # │ Obligatoire: NON (optionnel)                                │
        # │ Type: texte libre (max 500 caractères)                      │
        # │ Exemples: "Bien", "Excellent travail"                       │
        # └─────────────────────────────────────────────────────────────┘
        feedback:
          type: string
          description: Appréciation textuelle (optionnel)
          maxLength: 500
          example: "Très bonne compréhension des concepts"
```

---

## 🎯 Points clés du Besoin N°3 en YAML

### Code HTTP 204 vs 200

| Code | Utilisation | Quand l'utiliser |
|------|-------------|------------------|
| `200 OK` | ✅ Requête réussie avec réponse | GET, POST avec retour de données |
| `204 No Content` | ✅ Requête réussie SANS réponse | PUT, DELETE, PATCH sans retour |

```yaml
# AVEC CONTENU (200)
'200':
  description: Succès avec données
  content:
    application/json:
      schema: {...}

# SANS CONTENU (204)
'204':
  description: Succès sans données
  # Pas de "content:" car pas de corps de réponse
```

---

## 📊 Différence entre les 3 besoins en YAML

### Besoin N°1 : GET /courses
```yaml
paths:
  /courses:
    get:
      parameters:
        - name: level
          in: query      # ← Paramètre optionnel en query string
          required: false
      responses:
        '200':
          content:
            application/json:
              schema:
                type: array  # ← Retourne une liste
                items: {...}
```

### Besoin N°2 : POST /enrollments
```yaml
paths:
  /enrollments:
    post:
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: EnrollmentRequestDto  # ← Un objet
      responses:
        '201':           # ← Created (not OK)
          content:
            application/json:
              schema:
                $ref: EnrollmentResponseDto  # ← Retourne un objet
```

### Besoin N°3 : PUT /enrollments/{id}/grades
```yaml
paths:
  /enrollments/{enrollmentId}/grades:
    put:
      parameters:
        - name: enrollmentId
          in: path       # ← Paramètre obligatoire en path
          required: true
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: RecordGradeRequestDto
      responses:
        '204':           # ← No Content (pas de corps)
          description: Success (pas de "content:")
```

---

## 🔍 Explication du Path Parameter vs Query Parameter

### Path Parameter (Besoin N°3)
```yaml
# URL: /enrollments/{enrollmentId}/grades
# Exemple réel: /enrollments/E-2026-001/grades

parameters:
  - name: enrollmentId
    in: path
    required: true  # ← TOUJOURS requis pour path
```

### Query Parameter (Besoin N°1)
```yaml
# URL: /courses?level=LICENCE_3
# Exemple réel: /courses?level=LICENCE_3

parameters:
  - name: level
    in: query
    required: false  # ← Peut être optionnel
```

---

## 📋 Résumé du Besoin N°3 en YAML

| Aspect | Définition YAML | Explication |
|--------|-----------------|------------|
| **Endpoint** | `paths: /enrollments/{enrollmentId}/grades:` | Ressource imbriquée |
| **Méthode HTTP** | `put:` | Mise à jour d'une ressource |
| **Paramètre URL** | `in: path, required: true` | ID de l'inscription |
| **Données d'entrée** | `RecordGradeRequestDto` | Note + feedback optionnel |
| **Réponse réussie** | `204 No Content` | ✅ Succès silencieux |
| **Réponse réussie (corps)** | Aucun | Économise bande passante |
| **Erreur validation** | `400 Bad Request` | Note invalide/feedback long |
| **Erreur ressource** | `404 Not Found` | Inscription inexistante |
| **Erreur métier** | `409 Conflict` | Déjà notée ou statut invalide |
| **Erreur serveur** | `500 Internal Server Error` | Problème technique |

---

## 📊 Flux de données en YAML

```
CLIENT ENVOIE (requestBody)
│
└─> PUT /enrollments/E-2026-001/grades
    └─> RecordGradeRequestDto
        ├─ score: 15.5
        └─ feedback: "Très bonne..."

SERVEUR RETOURNE (response status + NO body)
│
├─ Status 204 No Content (réussi)
│  └─ (Aucun corps de réponse)
│
├─ Status 400 Bad Request (validation échouée)
│  └─> ErrorResponseDto
│      ├─ errorCode: "INVALID_GRADE"
│      └─ message: "La note doit être entre 0 et 20"
│
├─ Status 404 Not Found (inscription inexistante)
│  └─> ErrorResponseDto
│      ├─ errorCode: "ENROLLMENT_NOT_FOUND"
│      └─ message: "..."
│
├─ Status 409 Conflict (déjà notée)
│  └─> ErrorResponseDto
│      ├─ errorCode: "ENROLLMENT_ALREADY_GRADED"
│      └─ message: "..."
│
└─ Status 500 Internal Server Error
   └─> ErrorResponseDto
```

---

## 🎓 Comparaison des 3 besoins

| Aspect | Besoin 1 | Besoin 2 | Besoin 3 |
|--------|----------|----------|----------|
| **HTTP Method** | GET | POST | PUT |
| **Opération** | Lecture | Création | Modification |
| **Paramètres** | Query (optionnel) | Body (obligatoire) | Path + Body |
| **Succès HTTP** | 200 OK | 201 Created | 204 No Content |
| **Réponse corps** | Array + objets | 1 objet | Rien |
| **Erreurs possibles** | 400, 500 | 400, 404, 409, 500 | 400, 404, 409, 500 |

---

**✅ Besoin N°3 complètement expliqué au niveau du YAML!**
