# Systeme de gestion de bibliotheque

Application Spring Boot pour gerer :

- les livres
- les utilisateurs
- les emprunts

L'application contient :

- une interface web simple
- une connexion / inscription utilisateur
- un formulaire pour ajouter un livre avec date debut et date fin d'emprunt
- un tableau des livres
- un tableau des emprunts actifs
- des actions `Edit` et `Delete` sur les livres

## Technologies

- Java 17+
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- MySQL
- HTML / CSS / JavaScript

## Modeles

### Livre

- `id`
- `titre`
- `auteur`
- `categorie`
- `isbn`

### Utilisateur

- `id`
- `nom`
- `email`
- `adresse`
- `telephone`
- `motDePasse`

### Emprunt

- `id`
- `utilisateurId`
- `livreId`
- `dateEmprunt`
- `dateRetour`

## Fonctionnalites

- inscription utilisateur
- connexion utilisateur
- ajout d'un livre avec emprunt
- affichage des livres sous forme de liste
- affichage des emprunts actifs
- modification d'un livre avec son emprunt
- suppression d'un livre et de ses emprunts
- suppression automatique des emprunts expires quand la date de fin est atteinte

## Configuration

Le fichier de configuration principal est :

- [application.properties](C:/Users/khali/Desktop/poo%20java/Systeme-gestion-bibliotheue/src/main/resources/application.properties)

Configuration actuelle :

```properties
spring.application.name=gestion_livres
spring.datasource.url=jdbc:mysql://localhost:3306/gestion_livres?createDatabaseIfNotExist=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.show-sql=true
server.port=8083
```

## Prerequis

- JDK installe
- MySQL demarre
- utilisateur MySQL `root`
- Maven Wrapper du projet (`mvnw.cmd`)

## Lancer le projet

Depuis la racine du projet :

```powershell
.\mvnw.cmd spring-boot:run
```

L'application sera disponible sur :

```text
http://localhost:8083
```

## Lancer les tests

```powershell
.\mvnw.cmd test
```

## Utilisation de l'interface

1. Ouvrir `http://localhost:8083`
2. Creer un compte avec l'onglet `Inscription`
3. Se connecter
4. Ajouter un livre avec :
   - titre
   - auteur
   - categorie
   - isbn
   - date debut emprunt
   - date fin emprunt
5. Voir le livre dans le tableau des livres
6. Voir l'emprunt dans le tableau des emprunts
7. Utiliser `Edit` pour modifier
8. Utiliser `Delete` pour supprimer

## Endpoints principaux

### Authentification

- `POST /api/auth/register`
- `POST /api/auth/login`

### Livres

- `GET /api/livres`
- `GET /api/livres/{id}`
- `POST /api/livres`
- `POST /api/livres/avec-emprunt`
- `PUT /api/livres/{id}/avec-emprunt`
- `DELETE /api/livres/{id}`

### Utilisateurs

- `GET /api/utilisateurs`
- `GET /api/utilisateurs/{id}`
- `POST /api/utilisateurs`

### Emprunts

- `GET /api/emprunts`
- `GET /api/emprunts/{id}`
- `POST /api/emprunts`
- `PUT /api/emprunts/{id}/retour`

## Structure utile

- [AuthController.java](C:/Users/khali/Desktop/poo%20java/Systeme-gestion-bibliotheue/src/main/java/com/market01/systemegestionbibliotheue/controller/AuthController.java)
- [LivreController.java](C:/Users/khali/Desktop/poo%20java/Systeme-gestion-bibliotheue/src/main/java/com/market01/systemegestionbibliotheue/controller/LivreController.java)
- [EmpruntService.java](C:/Users/khali/Desktop/poo%20java/Systeme-gestion-bibliotheue/src/main/java/com/market01/systemegestionbibliotheue/service/EmpruntService.java)
- [BibliothequeService.java](C:/Users/khali/Desktop/poo%20java/Systeme-gestion-bibliotheue/src/main/java/com/market01/systemegestionbibliotheue/service/BibliothequeService.java)
- [index.html](C:/Users/khali/Desktop/poo%20java/Systeme-gestion-bibliotheue/src/main/resources/static/index.html)
- [app.js](C:/Users/khali/Desktop/poo%20java/Systeme-gestion-bibliotheue/src/main/resources/static/app.js)
- [styles.css](C:/Users/khali/Desktop/poo%20java/Systeme-gestion-bibliotheue/src/main/resources/static/styles.css)

## Remarque

Si le port `8083` est deja occupe, changez `server.port` dans `application.properties` ou fermez le processus qui utilise ce port.
