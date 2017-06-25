[Le README sera complété prochainement]

En attendant, quelques infos rapides :
- Le rapport est à la racine ;
- La documentation dans doc/ ;
- Le lancement de l'application se fait via `PicSimulatorMain` du package `Main` ;
- La date de la simulation est paramétrable via la constante `DATE` de `Util.Constant` et a pour valeur valide une de celles données dans le rapport (27/02/27 -> 30/03/17 à l'exception de certains jours) ;
- Les bibliothèques à lier dans le build path sont dans lib/.
- Si la simulation est lente, préférer un autre jour que celui du 27 février qui contient énormément d'étudiants.

# PicSimulator

Simulation du fonctionnement d'un bar (nommé le Pic'Asso, aussi connue comme le foyer de l'UTC). L'objectif est de partir de données réelles, obtenues auprès d'étudiants par le biais d'un questionnaire, de pouvoir retrouver les données (consommations, mouvements de foules, etc.) réelles, récupérées auprès du Pic'Asso.

## Technologie

Ce projet est développé avec MASON, un environnement multiagent en JAVA permettant de mettre en place des simulations.
