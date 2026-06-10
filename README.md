# Reconnaissance faciale

Application Java/JavaFX de reconnaissance faciale fondée sur l’Analyse en Composantes Principales, construite à partir d’une base d’images de référence.

## Prérequis

- Java
- JavaFX
- Ubuntu/Linux conseillé

## Méthode pour faire fonctionner le fichier .jar exécutable 

### Installation JavaFX

```bash
sudo apt update
sudo apt install openjfx
```

### Lancement

Placer les dossiers `dataset` et `dataReady` dans le même dossier que le `.jar`, puis exécuter :

```bash
java --module-path /usr/share/openjfx/lib --add-modules javafx.controls,javafx.fxml -jar reconnaissance_faciale_exec.jar
```

### Structure

```text
reconnaissance_faciale_exec.jar
dataset/
dataReady/
```

mon-dossier/
├── reconnaissance_faciale_exec.jar
├── dataset/
│   └── reference/
|   └── test/
└── dataReady/
    └── reference/
    └── test/
