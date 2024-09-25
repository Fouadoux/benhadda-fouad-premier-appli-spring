# SafetyNet Alerts

## Description

SafetyNet Alerts est une application qui fournit des alertes de sécurité aux habitants desservis par les casernes de pompiers. Elle expose plusieurs endpoints REST pour récupérer des informations sur les habitants, les casernes de pompiers et les dossiers médicaux.

## Stack Technique

- **Framework** : Spring Boot
- **Gestionnaire de dépendances** : Maven (ou Gradle)
- **Versionnement du code** : Git
- **Parser JSON** : Jackson ou Gson
- **Tests unitaires** : JUnit
- **Couverture de code** : JaCoCo
- **Logging** : Logback

## Fonctionnalités

- Démarrage du serveur d'alertes SafetyNet.
- Tous les endpoints URL sont fonctionnels et tracés via des logs.
  - Les réponses réussies sont tracées au niveau `INFO`.
  - Les erreurs sont tracées au niveau `ERROR`.
  - Les étapes informatives ou de débogage sont tracées au niveau `DEBUG`.
- Tests unitaires avec un rapport de couverture de code généré par JaCoCo et Surefire


## Endpoints

### 1. **Obtenir les personnes par caserne de pompiers**
   - **URL** : `http://localhost:8080/firestation?stationNumber=<station_number>`
   - **Description** : Retourne une liste des personnes couvertes par la caserne spécifiée.
   - **Détails** : Nom, prénom, adresse, téléphone, nombre d'adultes et d'enfants.

### 2. **Alerte enfant par adresse**
   - **URL** : `http://localhost:8080/childAlert?address=<address>`
   - **Description** : Retourne une liste d'enfants (moins de 18 ans) à une adresse donnée, avec les autres membres du foyer.

### 3. **Alerte téléphonique par caserne de pompiers**
   - **URL** : `http://localhost:8080/phoneAlert?firestation=<firestation_number>`
   - **Description** : Retourne une liste de numéros de téléphone pour une caserne donnée.

### 4. **Information sur les foyers par adresse**
   - **URL** : `http://localhost:8080/fire?address=<address>`
   - **Description** : Retourne les habitants d'une adresse avec leurs informations médicales.

### 5. **Alerte inondation par caserne de pompiers**
   - **URL** : `http://localhost:8080/flood/stations?stations=<list_of_station_numbers>`
   - **Description** : Retourne une liste des foyers desservis par les casernes avec les informations médicales des habitants.

### 6. **Informations sur une personne par nom de famille**
   - **URL** : `http://localhost:8080/personInfo?lastName=<lastName>`
   - **Description** : Retourne des informations complètes sur une personne, incluant les antécédents médicaux.

### 7. **Emails des habitants par ville**
   - **URL** : `http://localhost:8080/communityEmail?city=<city>`
   - **Description** : Retourne les adresses email de tous les habitants d'une ville.

### 8. **Gestion des personnes**
   - **URL** : `http://localhost:8080/person`
   - **Actions** :
     - Ajouter une personne (POST)
     - Mettre à jour une personne existante (PUT)
     - Supprimer une personne (DELETE)

### 9. **Gestion des casernes de pompiers**
   - **URL** : `http://localhost:8080/firestation`
   - **Actions** :
     - Ajouter un mapping caserne/adresse (POST)
     - Mettre à jour un mapping existant (PUT)
     - Supprimer un mapping (DELETE)

### 10. **Gestion des dossiers médicaux**
   - **URL** : `http://localhost:8080/medicalRecord`
   - **Actions** :
     - Ajouter un dossier médical (POST)
     - Mettre à jour un dossier médical (PUT)
     - Supprimer un dossier médical (DELETE)

### Pré-requis

- Java 11 ou supérieur
- Maven ou Gradle
- Lombok
- 
