# ktor-casino-leaderboard-app


This project was created using the [Ktor Project Generator](https://start.ktor.io).

Here are some useful links to get you started:

- [Ktor Documentation](https://ktor.io/docs/home.html)
- [Ktor GitHub page](https://github.com/ktorio/ktor)
- The [Ktor Slack chat](https://app.slack.com/client/T09229ZC6/C0A974TJ9). You'll need to [request an invite](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up) to join.

## Features

Here's a list of features included in this project:

| Name                                                                   | Description                                                                        |
| ------------------------------------------------------------------------|------------------------------------------------------------------------------------ |
| [Content Negotiation](https://start.ktor.io/p/content-negotiation)     | Provides automatic content conversion according to Content-Type and Accept headers |
| [Routing](https://start.ktor.io/p/routing)                             | Provides a structured routing DSL                                                  |
| [kotlinx.serialization](https://start.ktor.io/p/kotlinx-serialization) | Handles JSON serialization using kotlinx.serialization library                     |
| [Koin](https://start.ktor.io/p/koin)                                   | Provides dependency injection                                                      |
| [Static Content](https://start.ktor.io/p/static-content)               | Serves static files from defined locations                                         |

## Building & Running

To build or run the project, use one of the following tasks:

| Task                          | Description                                                          |
| -------------------------------|---------------------------------------------------------------------- |
| `./gradlew test`              | Run the tests                                                        |
| `./gradlew build`             | Build everything                                                     |
| `buildFatJar`                 | Build an executable JAR of the server with all dependencies included |
| `buildImage`                  | Build the docker image to use with the fat JAR                       |
| `publishImageToLocalRegistry` | Publish the docker image locally                                     |
| `run`                         | Run the server                                                       |
| `runDocker`                   | Run using the local docker image                                     |

If the server starts successfully, you'll see the following output:

```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080

-------------------------------------------------------------------

[feat] Ajouter une nouvelle fonctionnalité
[fix] Corriger un bug
[refactor] Refactorisation de code
[test] Ajout ou mise à jour de tests
[doc] Documentation ou README
[build] Configuration build / dépendances
[style] Formatage / lint
[chore] Tâches diverses (nettoyage, renommage...)

-------------------------------------------------------------------
TODO
1. Gestion des erreur
2. Commit
3. Test
4. Commit
5. Koin
commit
6. DynamoDb
commit
7. Test
commit
7. Tournois
commit
8. define
    ranking alors que meme nombre de points
    CI CD ?
     

Autres : 
- Ajouter la notion de tournois
- Pour l'update des points faire quelque chose d'intelligent. par exemple un player peut perdre des points. Ou peut se faire réinitialiser des points
- Koin
- DynamoDb




```

