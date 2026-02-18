<#
Script PowerShell d'aide pour lancer le backend en mode développement (H2 en mémoire).
Usage: Exécuter depuis la racine du projet:
  .\scripts\run-dev.ps1

Ce script vérifie Java et Maven. S'il manque Maven, il affiche les commandes d'installation (winget ou Chocolatey).
Ensuite il construit et lance le service Spring Boot avec le profil 'dev'.
#>

Write-Host "=== Run Dev Script: Cognivita Backend ===`n"

# Vérifier Java
Write-Host "Vérification de Java..." -NoNewline
try {
    $javaVersion = & java -version 2>&1
    Write-Host " OK"
    Write-Host $javaVersion
} catch {
    Write-Host " ERREUR: Java non trouvé. Installez un JDK (17+) et configurez JAVA_HOME."
    Write-Host "Exemple (winget): winget install --id Oracle.OpenJDK.17 -e"
    exit 1
}

# Vérifier Maven
Write-Host "Vérification de Maven (mvn)..." -NoNewline
$mvnAvailable = $false
try {
    $mvnVersion = & mvn -v 2>&1
    $mvnAvailable = $true
    Write-Host " OK"
    Write-Host $mvnVersion
} catch {
    Write-Host " PAS INSTALLE"
}

if (-not $mvnAvailable) {
    Write-Host "
Maven n'est pas installé sur ce système. Choix d'installation :"
    Write-Host " - Avec winget (Windows 10/11) : winget install Apache.Maven -e"
    Write-Host " - Avec Chocolatey      : choco install maven -y"
    Write-Host " - Ou installer manuellement depuis https://maven.apache.org/download.cgi"
    Write-Host "
Après installation, fermez et rouvrez la session PowerShell pour que PATH soit mis à jour."
    Write-Host "
Si vous préférez utiliser un IDE (IntelliJ/Eclipse), ouvrez le projet Maven et exécutez l'application avec le profil 'dev'."
    exit 1
}

# Construire le backend
Write-Host "
Construction du backend (skip tests)..."
$buildCmd = "mvn -f backend/pom.xml clean package -DskipTests"
Write-Host "Commande: $buildCmd"
$buildResult = & mvn -f backend/pom.xml clean package -DskipTests 2>&1
Write-Host $buildResult
if ($LASTEXITCODE -ne 0) { Write-Host "Build échoué. Voir la sortie ci-dessus."; exit 1 }

# Lancer l'application avec profil 'dev' (utilise H2 en mémoire)
Write-Host "
Lancement du backend en profil 'dev' (H2)..."
$runCmd = "mvn -f backend/pom.xml spring-boot:run -Dspring-boot.run.profiles=dev"
Write-Host "Commande: $runCmd`n"
& mvn -f backend/pom.xml spring-boot:run -Dspring-boot.run.profiles=dev

# Fin
Write-Host "
Script terminé (processus Spring Boot terminé ou interrompu)."
