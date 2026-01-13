$ErrorActionPreference = "Stop"
Set-Location "E:\2-2\Advance java lab\File for my Medicine Tracker app\Daily_Medicine_Remainder"

$javaHome = "$env:USERPROFILE\.jdks\openjdk-25.0.1"
$javac = "$javaHome\bin\javac.exe"
$java = "$javaHome\bin\java.exe"

if (!(Test-Path $javac)) {
    if ($env:JAVA_HOME) {
        $javaHome = $env:JAVA_HOME
        $javac = "$javaHome\bin\javac.exe"
        $java = "$javaHome\bin\java.exe"
    }
}

if (!(Test-Path $javac)) {
    Write-Error "Java compiler not found at $javac or JAVA_HOME"
    exit 1
}

Write-Host "Using JDK at $javaHome"

$m2Repo = "$env:USERPROFILE\.m2\repository"
$libs = @(
    "org\xerial\sqlite-jdbc\3.44.0.0\sqlite-jdbc-3.44.0.0.jar",
    "com\google\code\gson\gson\2.10.1\gson-2.10.1.jar",
    "org\slf4j\slf4j-api\2.0.11\slf4j-api-2.0.11.jar",
    "ch\qos\logback\logback-classic\1.4.14\logback-classic-1.4.14.jar",
    "ch\qos\logback\logback-core\1.4.14\logback-core-1.4.14.jar",
    "org\apache\commons\commons-csv\1.10.0\commons-csv-1.10.0.jar",
    "org\apache\commons\commons-lang3\3.14.0\commons-lang3-3.14.0.jar"
)

$cpElements = @()
foreach ($lib in $libs) {
    $fullPath = Join-Path $m2Repo $lib
    if (!(Test-Path $fullPath)) {
        Write-Warning "Library not found at expected path: $lib. Attempting to search..."
        $parts = $lib -split "\\"
        if ($parts.Count -gt 3) {
             $base = Join-Path $m2Repo ($parts[0..($parts.Count-3)] -join "\")
             if (Test-Path $base) {
                 $found = Get-ChildItem -Path $base -Filter *.jar -Recurse | Sort-Object LastWriteTime -Descending | Select-Object -First 1
                 if ($found) {
                     $fullPath = $found.FullName
                     Write-Host "Found alternative: $($found.Name)"
                 }
             }
        }
    }
    $cpElements += $fullPath
}
$cpElements += "target\classes"
$classpath = $cpElements -join ";"

Write-Host "Compiling sources..."
if (!(Test-Path "target\classes")) { New-Item -ItemType Directory -Path "target\classes" -Force | Out-Null }

$sources = Get-ChildItem "src\main\java" -Recurse -Filter *.java | Select-Object -ExpandProperty FullName
$sourceFile = "sources_list.txt"
$sources | ForEach-Object { $_ -replace '\\', '/' } | ForEach-Object { "`"$_`"" } | Set-Content $sourceFile

& $javac -d target\classes -cp "$classpath" "@$sourceFile"

if ($LASTEXITCODE -eq 0) {
    if (Test-Path "daily_dose_v2.db") {
        Write-Host "Removing existing database for clean population..."
        Remove-Item "daily_dose_v2.db" -Force
    }

    Write-Host "Compilation Successful."
    Write-Host "Running Database Population..."
    & $java -cp "$classpath" com.example.utils.PopulateDB
} else {
    Write-Error "Compilation Failed"
}
