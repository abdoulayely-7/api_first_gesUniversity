# Exemples d'utilisation de l'API Gestion Universitaire

## Base URL
```
http://localhost:8080/api/v1
```

## 1️⃣ Lister tous les cours

### Requête
```bash
curl -X GET http://localhost:8080/api/v1/courses \
  -H "Content-Type: application/json"
```

### Réponse (200 OK)
```json
{
  "courses": [
    {
      "courseId": "CS-301",
      "title": "Architecture Logicielle",
      "credits": 6,
      "professorName": "Dr. Jean Dupont",
      "status": "OUVERT",
      "level": "Licence 3",
      "maxStudents": 30,
      "enrolledCount": 0
    },
    {
      "courseId": "MATH-201",
      "title": "Algèbre Avancée",
      "credits": 4,
      "professorName": "Pr. Marie Martin",
      "status": "OUVERT",
      "level": "Licence 2",
      "maxStudents": 25,
      "enrolledCount": 0
    }
  ],
  "totalElements": 4,
  "currentPage": 0
}
```

---

## 2️⃣ Filtrer les cours par niveau

### Requête
```bash
curl -X GET "http://localhost:8080/api/v1/courses?level=Licence%203" \
  -H "Content-Type: application/json"
```

### Réponse (200 OK)
```json
{
  "courses": [
    {
      "courseId": "CS-301",
      "title": "Architecture Logicielle",
      "credits": 6,
      "professorName": "Dr. Jean Dupont",
      "status": "OUVERT",
      "level": "Licence 3",
      "maxStudents": 30,
      "enrolledCount": 2
    }
  ],
  "totalElements": 2,
  "currentPage": 0
}
```

---

## 3️⃣ Inscrire un étudiant à un cours

### Requête
```bash
curl -X POST http://localhost:8080/api/v1/enrollments \
  -H "Content-Type: application/json" \
  -d '{
    "studentNumber": "ETU-2026-045",
    "courseId": "CS-301",
    "academicYear": "2025-2026"
  }'
```

### Réponse (201 CREATED)
```json
{
  "enrollmentId": "ENR-1736172805123-4567",
  "studentNumber": "ETU-2026-045",
  "courseId": "CS-301",
  "confirmationDate": "2026-01-07T10:30:15.123Z",
  "ticketNumber": "TKT-2026-54321",
  "academicYear": "2025-2026",
  "status": "CONFIRMED"
}
```

### Erreurs possibles

#### Données invalides (400 BAD REQUEST)
```bash
curl -X POST http://localhost:8080/api/v1/enrollments \
  -H "Content-Type: application/json" \
  -d '{
    "studentNumber": "ETU-2026-045"
    # Missing courseId et academicYear
  }'
```

Réponse :
```json
{
  "timestamp": "2026-01-07T10:30:15.123Z",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "L'identifiant du cours est obligatoire"
}
```

#### Cours complet (409 CONFLICT)
```bash
curl -X POST http://localhost:8080/api/v1/enrollments \
  -H "Content-Type: application/json" \
  -d '{
    "studentNumber": "ETU-2026-999",
    "courseId": "CS-301",  # Ce cours est maintenant complet
    "academicYear": "2025-2026"
  }'
```

Réponse :
```json
{
  "timestamp": "2026-01-07T10:30:15.123Z",
  "status": 409,
  "error": "COURSE_FULL",
  "message": "Cannot enroll: course is COMPLET"
}
```

---

## 4️⃣ Enregistrer la note d'un étudiant

### Requête
```bash
curl -X PUT http://localhost:8080/api/v1/enrollments/ENR-1736172805123-4567/grades \
  -H "Content-Type: application/json" \
  -d '{
    "score": 15.5,
    "feedback": "Excellente compréhension des principes d'\''architecture\''."
  }'
```

### Réponse (204 NO CONTENT)
```
(Pas de corps de réponse)
```

### Erreurs possibles

#### Note invalide (400 BAD REQUEST)
```bash
curl -X PUT http://localhost:8080/api/v1/enrollments/ENR-1736172805123-4567/grades \
  -H "Content-Type: application/json" \
  -d '{
    "score": 25  # Doit être <= 20
  }'
```

Réponse :
```json
{
  "timestamp": "2026-01-07T10:30:15.123Z",
  "status": 400,
  "error": "INVALID_SCORE",
  "message": "La note doit être entre 0 et 20"
}
```

#### Inscription non trouvée (404 NOT FOUND)
```bash
curl -X PUT http://localhost:8080/api/v1/enrollments/ENR-INVALID/grades \
  -H "Content-Type: application/json" \
  -d '{
    "score": 15.5
  }'
```

Réponse :
```json
{
  "timestamp": "2026-01-07T10:30:15.123Z",
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Enrollment not found: ENR-INVALID"
}
```

---

## 📊 Swagger UI

Accédez à la documentation interactive :
```
http://localhost:8080/swagger-ui.html
```

Tous les endpoints sont documentés avec :
- Descriptions
- Paramètres d'entrée
- Codes de réponse
- Exemples de request/response

---

## 🔄 Flux complet d'un cas d'usage

```
1. Récupérer les cours
   GET /api/v1/courses?level=Licence%203
   ↓
2. Sélectionner un cours disponible
   (status = "OUVERT")
   ↓
3. Inscrire l'étudiant
   POST /api/v1/enrollments
   {
     "studentNumber": "ETU-2026-045",
     "courseId": "CS-301",
     "academicYear": "2025-2026"
   }
   ↓
4. Mémoriser l'enrollmentId et ticketNumber
   ↓
5. À la fin du cours, enregistrer la note
   PUT /api/v1/enrollments/{enrollmentId}/grades
   {
     "score": 15.5,
     "feedback": "Excellent travail"
   }
```

---

## 🧪 Avec cURL en script bash

```bash
#!/bin/bash

API="http://localhost:8080/api/v1"

# 1. Lister les cours
echo "📚 Fetching courses..."
COURSES=$(curl -s "$API/courses?level=Licence%203")
echo "$COURSES" | jq .

# 2. Inscrire un étudiant
echo "✏️  Enrolling student..."
ENROLLMENT=$(curl -s -X POST "$API/enrollments" \
  -H "Content-Type: application/json" \
  -d '{
    "studentNumber": "ETU-2026-045",
    "courseId": "CS-301",
    "academicYear": "2025-2026"
  }')
echo "$ENROLLMENT" | jq .

ENROLLMENT_ID=$(echo "$ENROLLMENT" | jq -r '.enrollmentId')

# 3. Enregistrer une note
echo "📝 Recording grade for enrollment $ENROLLMENT_ID..."
curl -s -X PUT "$API/enrollments/$ENROLLMENT_ID/grades" \
  -H "Content-Type: application/json" \
  -d '{
    "score": 15.5,
    "feedback": "Excellent"
  }' | jq .

echo "✅ Done!"
```

Exécution :
```bash
bash examples.sh
```
