# Rebuild the standalone ParkingSystem.exe (needs JDK 21+ on this machine only).
Remove-Item -Recurse -Force build -ErrorAction SilentlyContinue
javac -d build/classes *.java
New-Item -ItemType Directory -Force build/jarin | Out-Null
jar --create --file build/jarin/ParkingSystem.jar --main-class ParkingSystem -C build/classes .
jpackage --type app-image --name ParkingSystem --input build/jarin `
  --main-jar ParkingSystem.jar --main-class ParkingSystem --win-console --dest build/dist `
  --add-modules java.base `
  --jlink-options "--strip-debug --no-header-files --no-man-pages --compress=zip-6"
Compress-Archive -Path build/dist/ParkingSystem -DestinationPath build/ParkingSystem-win64.zip -Force
Write-Host "Done: build/ParkingSystem-win64.zip"
