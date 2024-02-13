# QuizApp

QuizApp est une application Android de quiz trivia qui permet aux utilisateurs de tester leurs connaissances dans divers domaines. L'application récupère les questions trivia depuis une API en ligne, affiche une question à la fois avec plusieurs choix de réponses, et permet aux utilisateurs de sélectionner la réponse qu'ils estiment être correcte.

## Fonctionnalités

- Système d'authentification basé sur Firebase.
- Affichage de questions triviales à partir d'une API en ligne.
- Compte à rebours pour chaque question pour limiter le temps de réponse.
- Possibilité de gagner des "limcoins" virtuels en répondant correctement à des questions.
- Suivi du score du joueur et du temps écoulé.
- Intégration avec Firebase pour la gestion des données utilisateur telles que les limcoins, le score et le temps.

## Prérequis

Avant de lancer l'application, assurez-vous d'avoir les éléments suivants configurés :

- Un appareil ou un émulateur Android compatible.
- Connexion Internet active pour récupérer les questions triviales depuis l'API.

## Installation

1. Clonez ce dépôt sur votre machine locale :

```bash
git clone https://github.com/votre-utilisateur/QuizApp.git
```
## Utilisation
Lorsque vous lancez l'application, vous êtes accueilli par l'écran principal où vous choisissez de crée un compte ou de vous connecter, une fois authentifier vous choisiez le mode de jeu que vous voulez (Classique(solo), ou Défi(multijoueur qui devrais venir prochainement) et de choisir une difficulté des questions) ou bien de voir le Leaderboard. 

-  Création d'un compte
-  Identification avec le compte crée
-  Chosir le mode du jeu ( le mode défi (ou en ligne est a venir) et choisir la difficulté des question ou bien voir les joueurs et leur temps écoulé avec le score en cliquant sur Leaderboard.
-  Sélectionnez la réponse que vous pensez être correcte en appuyant sur le bouton correspondant.
-  Un compte à rebours indique le temps restant pour répondre à la question.
-  Après avoir répondu à une question, la suivante s'affiche automatiquement.
-  Si vous répondez correctement, vous gagnez des limcoins virtuels.
-  Le quiz se termine après un certain nombre de questions (11) ou lorsque le temps imparti est écoulé ou lorsque vous vous tromper de réponse
-  À la fin du quiz, votre score et le temps écoulé sont enregistrés et vous êtes redirigé vers l'écran principal.
-  vous pouvez voir votre score et celui des autres joueur dans le Leaderboard.

## Contribuer 
Si vous souhaitez contribuer à ce projet, vous pouvez :

-  Faire un fork du dépôt.
-  Ajouter des fonctionnalités ou corriger des bugs.
-  Soumettre une demande d'extraction avec vos modifications.

## Technologies utilisées
-  kotlin : Langage de programmation utilisé pour développer l'application Android.
-  Android SDK : Kit de développement logiciel pour créer des applications Android.
-  Retrofit : Bibliothèque HTTP pour l'accès à une API RESTful.
-  Firebase : Plateforme de développement d'applications mobiles de Google pour la gestion des données utilisateur.
-  Gson : Bibliothèque pour la sérialisation/désérialisation des objets JSON.
-  ViewModel : Composant d'architecture Jetpack pour gérer et conserver les données liées à l'interface utilisateur.
-  CountDownTimer : Classe Android pour implémenter un compte à rebours.


© 2024
