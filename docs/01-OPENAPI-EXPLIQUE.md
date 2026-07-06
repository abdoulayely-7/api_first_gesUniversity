# 🔌 OpenAPI - Besoin N°1 : Consulter le Catalogue des Cours

## Explication ligne par ligne du university-api.yaml

Voici UNIQUEMENT la section du YAML qui correspond au Besoin N°1 :

```yaml
# ============================================================================
# 🎯 ENDPOINT: GET /api/v1/courses
# Description: Récupérer la liste des cours avec filtrage optionnel par niveau
# ============================================================================

paths:
  /courses:
    get:
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      # LIGNE 1: summary
      # Explication: Titre court de l'endpoint (affiché dans Swagger UI)
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      summary: Lister tous les cours disponibles
      
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      # LIGNE 2: description
      # Explication: Description détaillée de ce que l'endpoint fait
      #              Utilise le format pipe (|) pour du multi-ligne
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      description: |
        Récupère la liste complète des cours avec la possibilité 
        de filtrer par niveau. Les étudiants peuvent consulter le 
        catalogue pour voir les cours disponibles.
      
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      # LIGNE 3: operationId
      # Explication: Identifiant unique de l'opération
      #              Utilisé pour générer le nom de la méthode Java
      #              Génère: CoursesApi.listCourses()
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      operationId: listCourses
      
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      # LIGNE 4: tags
      # Explication: Catégorie de l'endpoint dans Swagger UI
      #              Groupe tous les endpoints avec le même tag
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      tags:
        - Cours
      
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      # SECTION: parameters
      # Explication: Tous les paramètres d'entrée possibles
      #              Pour GET, généralement en query ou path
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      parameters:
        - name: level
          # Explication: Paramètre dans la query string (?level=...)
          in: query
          
          # Explication: Description du paramètre
          description: |
            Filtrer les cours par niveau académique.
            Si non fourni, retourne tous les cours.
          
          # Explication: Ce paramètre n'est PAS obligatoire
          #              (contrairement à required: true)
          required: false
          
          # Explication: Type et énumération du paramètre
          schema:
            type: string
            # Seules ces valeurs sont acceptées
            enum:
              - LICENCE_1
              - LICENCE_2
              - LICENCE_3
              - MASTER_1
              - MASTER_2
          
          # Explication: Exemple de valeur (affiché dans Swagger)
          example: LICENCE_3

      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      # SECTION: responses
      # Explication: Tous les codes de réponse possibles (200, 400, 500, etc.)
      # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
      responses:
        # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        # CODE: 200 OK
        # Explication: Réponse réussie. La liste des cours est retournée
        # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        '200':
          description: Liste des cours récupérée avec succès
          
          # Explication: Le corps de la réponse est en JSON
          content:
            application/json:
              # Explication: Référence à un type défini dans components.schemas
              #              (voir section components ci-dessous)
              schema:
                type: array  # C'est un tableau
                items:       # Chaque item est un CourseResponseDto
                  $ref: '#/components/schemas/CourseResponseDto'
              
              # Explication: Exemple concret de la réponse
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

        # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        # CODE: 400 Bad Request
        # Explication: Erreur de validation (ex: niveau invalide)
        # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
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

        # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        # CODE: 500 Internal Server Error
        # Explication: Erreur serveur (problème base de données, etc.)
        # ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        '500':
          description: Erreur serveur
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponseDto'
```

---

## 📦 Composants utilisés (definis dans components.schemas)

### CourseResponseDto

```yaml
components:
  schemas:
    CourseResponseDto:
      type: object
      description: Représentation d'un cours pour la consultation
      
      # Explication: Tous ces champs DOIVENT être présents dans la réponse
      required:
        - id
        - title
        - credits
        - professor
        - level
        - status
        - enrolledCount
        - maxEnrollments
      
      # Explication: Définition détaillée de chaque champ
      properties:
        # ┌─────────────────────────────────────────────────────────────┐
        # │ CHAMP: id                                                   │
        # └─────────────────────────────────────────────────────────────┘
        id:
          type: string
          description: Identifiant unique du cours (ex: CS-301)
          example: CS-301
        
        # ┌─────────────────────────────────────────────────────────────┐
        # │ CHAMP: title                                                │
        # └─────────────────────────────────────────────────────────────┘
        title:
          type: string
          description: Titre/Intitulé du cours
          minLength: 5        # Au minimum 5 caractères
          maxLength: 255      # Au maximum 255 caractères
          example: Architecture Logicielle
        
        # ┌─────────────────────────────────────────────────────────────┐
        # │ CHAMP: credits                                              │
        # └─────────────────────────────────────────────────────────────┘
        credits:
          type: integer
          description: Nombre de crédits ECTS
          minimum: 1          # Au minimum 1 crédit
          maximum: 30         # Au maximum 30 crédits
          example: 6
        
        # ┌─────────────────────────────────────────────────────────────┐
        # │ CHAMP: professor                                            │
        # └─────────────────────────────────────────────────────────────┘
        professor:
          type: string
          description: Nom du professeur responsable
          minLength: 3
          maxLength: 255
          example: Dr. Martin
        
        # ┌─────────────────────────────────────────────────────────────┐
        # │ CHAMP: level                                                │
        # │ Explication: Niveau académique (Licence 1-3, Master 1-2)    │
        # └─────────────────────────────────────────────────────────────┘
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
        
        # ┌─────────────────────────────────────────────────────────────┐
        # │ CHAMP: status                                               │
        # │ Explication: Statut obligatoirement OUVERT, COMPLET, FERME  │
        # └─────────────────────────────────────────────────────────────┘
        status:
          type: string
          description: Statut du cours
          enum:
            - OUVERT    # Accepte des inscriptions
            - COMPLET   # Capacité maximale atteinte
            - FERME     # N'accepte plus d'inscriptions
          example: OUVERT
        
        # ┌─────────────────────────────────────────────────────────────┐
        # │ CHAMP: enrolledCount                                        │
        # │ Explication: Nombre d'étudiants actuellement inscrits       │
        # └─────────────────────────────────────────────────────────────┘
        enrolledCount:
          type: integer
          description: Nombre d'étudiants actuellement inscrits
          minimum: 0
          example: 15
        
        # ┌─────────────────────────────────────────────────────────────┐
        # │ CHAMP: maxEnrollments                                       │
        # │ Explication: Capacité maximale du cours                     │
        # └─────────────────────────────────────────────────────────────┘
        maxEnrollments:
          type: integer
          description: Capacité maximale d'inscrits
          minimum: 1
          example: 30
```

### ErrorResponseDto

```yaml
    ErrorResponseDto:
      type: object
      description: Réponse d'erreur standard
      required:
        - errorCode
        - message
        - timestamp
      properties:
        # ┌─────────────────────────────────────────────────────────────┐
        # │ CHAMP: errorCode                                            │
        # │ Explication: Code d'erreur machine (pas de message humain)  │
        # └─────────────────────────────────────────────────────────────┘
        errorCode:
          type: string
          description: Code d'erreur spécifique
          enum:
            - INVALID_LEVEL       # Le niveau fourni n'existe pas
            - INTERNAL_ERROR      # Erreur serveur
          example: INVALID_LEVEL
        
        # ┌─────────────────────────────────────────────────────────────┐
        # │ CHAMP: message                                              │
        # │ Explication: Description lisible par l'humain               │
        # └─────────────────────────────────────────────────────────────┘
        message:
          type: string
          description: Description détaillée de l'erreur
          example: "Le niveau spécifié n'existe pas"
        
        # ┌─────────────────────────────────────────────────────────────┐
        # │ CHAMP: timestamp                                            │
        # │ Explication: Quand l'erreur s'est produite (UTC)            │
        # └─────────────────────────────────────────────────────────────┘
        timestamp:
          type: string
          format: date-time
          description: Date/heure de l'erreur (format ISO 8601)
          example: "2025-01-15T10:30:45Z"
```

---

## 📝 Annotations YAML expliquées

### type: object
Explication: Représente un objet JSON (clés-valeurs)

### type: array
Explication: Représente un tableau JSON (liste d'items)

### type: string
Explication: Texte (ex: "CS-301")

### type: integer
Explication: Nombre entier (ex: 6, 30)

### required: [...]
Explication: Champs obligatoires (doivent être présents)

### minLength / maxLength
Explication: Contraintes sur la longueur d'une chaîne

### minimum / maximum
Explication: Contraintes sur la valeur d'un nombre

### enum: [...]
Explication: Liste des valeurs possibles

### $ref: '#/components/schemas/...'
Explication: Référence à une définition réutilisable

### example:
Explication: Exemple concret pour la documentation Swagger

---

## 🎯 Résumé du Besoin N°1 en YAML

| Aspect | Définition YAML |
|--------|-----------------|
| **Endpoint** | `paths: /courses:` |
| **Méthode HTTP** | `get:` |
| **Paramètre d'entrée** | `parameters: level (optionnel, query)` |
| **Réponse réussie** | `200: array[CourseResponseDto]` |
| **Erreur validation** | `400: ErrorResponseDto` |
| **Erreur serveur** | `500: ErrorResponseDto` |
| **Structure retournée** | `CourseResponseDto` (8 champs) |

---

**✅ Besoin N°1 complètement expliqué au niveau du YAML!** 
