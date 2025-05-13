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

1. Tests
2. Deployement / CI CD ?

 [ Questions ]
 1. Concernant DynamoDb. J'utilise une version en local qui run dans un conteneur docker. Est ce que c'est ok pour toi ?
 2. L'update de point pour un player n'est qu'un ajout de point pour l'instant. Je pourrais ajouter un paramètre d'update , qui permet d'enlever des points ou de réinitialiser les points d'un player. Est-ce utile ?
 3. Le rang des joueurs est calculé dynamiquement uniquement lors des appels où cette information est nécessaire. Cela garantit la cohérence sans nécessiter de mises à jour en cascade en base (qui serait, entre autre, très couteux). Mais si le nombre de modifications de scores est faible et que l’API est très sollicitée pour la récupération des classements, il serait envisageable d’étudier une solution de stockage ou de cache du rang. Pour l'instant, je reste sur le calcul du rank à la volée si c'est ok pour toi ? 
 4. Pour les players qui ont le meme nombre de point, ont aussi le meme rank ?
 



```

