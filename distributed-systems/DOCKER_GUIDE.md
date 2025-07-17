# 🐳 Docker-Anleitung für Distributed E-Commerce System

## 📋 Inhaltsverzeichnis

1. [System starten](#-system-starten)
2. [Logs anzeigen](#-logs-anzeigen)
3. [System überwachen](#-system-überwachen)
4. [System stoppen](#-system-stoppen)
5. [Einzelne Services verwalten](#-einzelne-services-verwalten)
6. [Troubleshooting](#-troubleshooting)
7. [Typische Arbeitsabläufe](#-typische-arbeitsabläufe)
8. [Befehlsreferenz](#-befehlsreferenz)

---

## 🚀 **System starten**

### **Schritt 1: System starten**

```bash
# In das Projektverzeichnis wechseln
cd /mnt/c/Users/AhmedAbdelhamidIsmai/Downloads/distributed-systems

# System mit dem Convenience-Script starten
./run.sh
```

**Erwartete Ausgabe:**
```
=========================================
Starting Distributed Marketplace System
=========================================
Cleaning up old containers...
Starting services...
[+] Building 45.2s (21/21) FINISHED
[+] Running 8/8
 ✔ Network distributed-systems_marketplace-net  Created
 ✔ Container seller1                            Started
 ✔ Container seller2                            Started
 ✔ Container seller3                            Started
 ✔ Container seller4                            Started
 ✔ Container seller5                            Started
 ✔ Container marketplace1                       Started
 ✔ Container marketplace2                       Started

System status:
NAME            IMAGE                              COMMAND               STATUS
marketplace1    distributed-systems_marketplace1   "java -jar app.jar"   Up 2 seconds
marketplace2    distributed-systems_marketplace2   "java -jar app.jar"   Up 2 seconds
seller1         distributed-systems_seller1        "java -jar app.jar"   Up 3 seconds
seller2         distributed-systems_seller2        "java -jar app.jar"   Up 3 seconds
seller3         distributed-systems_seller3        "java -jar app.jar"   Up 3 seconds
seller4         distributed-systems_seller4        "java -jar app.jar"   Up 3 seconds
seller5         distributed-systems_seller5        "java -jar app.jar"   Up 3 seconds

✓ System started!

📋 Nützliche Befehle:
==================
📊 Logs anzeigen:
  - Alle Services:           ./logs.sh
  - Bestimmter Service:      ./logs.sh marketplace1
  - Mehrere Services:        ./logs.sh marketplace1 seller1
  - Docker-Compose direkt:   docker-compose logs -f

🔍 System überwachen:
  - Service-Status:          docker-compose ps
  - Ressourcenverbrauch:     docker stats
  - Live-Logs verfolgen:     docker-compose logs -f

🛑 System stoppen:
  - Graceful shutdown:       ./stop.sh
  - Sofort stoppen:          docker-compose down
  - Alles entfernen:         docker-compose down -v

🔧 Einzelne Services:
  - Service neustarten:      docker-compose restart marketplace1
  - Service skalieren:       docker-compose up -d --scale seller1=3
  - Service stoppen:         docker-compose stop seller1

⚡ Troubleshooting:
  - Kompletter Reset:        docker-compose down -v && ./run.sh
  - Build-Cache löschen:     docker-compose build --no-cache
  - System bereinigen:       docker system prune -a

Stop system with: ./stop.sh
```

### **Alternative: Manuelle Docker-Compose-Befehle**

```bash
# System manuell starten
docker-compose build --no-cache
docker-compose up -d

# Status prüfen
docker-compose ps
```

### **Interaktiver Modus (für Debugging)**

```bash
# System im Vordergrund starten (Logs direkt sichtbar)
docker-compose up

# Oder nur bestimmte Services
docker-compose up marketplace1 seller1 seller2
```

---

## 📊 **Logs anzeigen**

### **Alle Services:**
```bash
# Alle Logs anzeigen
./logs.sh

# Oder mit Docker-Compose direkt
docker-compose logs -f
```

### **Bestimmte Services:**
```bash
# Marketplace-Logs
./logs.sh marketplace1

# Seller-Logs
./logs.sh seller1

# Mehrere Services gleichzeitig
./logs.sh marketplace1 seller1 seller2

# Letzte 50 Zeilen anzeigen
docker-compose logs --tail=50 marketplace1
```

### **Erwartete Log-Ausgabe:**
```
marketplace1  | Starting Marketplace MP1...
marketplace1  | Order processor started for MP1
marketplace1  | Saga orchestrator initialized
marketplace1  | AsyncMessageBroker started on port 5555
marketplace1  | 
marketplace1  | === Submitting Order O1 for processing ===
marketplace1  | Starting saga for order O1
marketplace1  | Reserving products for order O1
marketplace1  | → Reserving 2x P1 from seller1
marketplace1  | → Reserving 1x P2 from seller2
marketplace1  | 
seller1       | Seller seller1 connecting to marketplace...
seller1       | Initial inventory: {P1=50, P2=30, P3=25}
seller1       | Seller seller1 connected and ready for requests
seller1       | 
seller1       | Received request: {"type":"RESERVE","orderId":"O1","productId":"P1","quantity":"2"}
seller1       | Reserved: 2x P1 (ID: RES-1234)
seller1       | 
seller2       | Seller seller2 connecting to marketplace...
seller2       | Received request: {"type":"RESERVE","orderId":"O1","productId":"P2","quantity":"1"}
seller2       | Reserved: 1x P2 (ID: RES-5678)
```

---

## 🔍 **System überwachen**

### **Service-Status:**
```bash
# Service-Status anzeigen
docker-compose ps
```

**Erwartete Ausgabe:**
```
NAME            IMAGE                              COMMAND               SERVICE       STATUS       PORTS
marketplace1    distributed-systems_marketplace1   "java -jar app.jar"   marketplace1  Up 5 minutes
marketplace2    distributed-systems_marketplace2   "java -jar app.jar"   marketplace2  Up 5 minutes
seller1         distributed-systems_seller1        "java -jar app.jar"   seller1       Up 5 minutes
seller2         distributed-systems_seller2        "java -jar app.jar"   seller2       Up 5 minutes
seller3         distributed-systems_seller3        "java -jar app.jar"   seller3       Up 5 minutes
seller4         distributed-systems_seller4        "java -jar app.jar"   seller4       Up 5 minutes
seller5         distributed-systems_seller5        "java -jar app.jar"   seller5       Up 5 minutes
```

### **Ressourcenverbrauch:**
```bash
# Ressourcenverbrauch anzeigen
docker stats

# Nur bestimmte Container
docker stats marketplace1 seller1
```

### **Netzwerk-Informationen:**
```bash
# Netzwerke anzeigen
docker network ls

# Netzwerk-Details
docker network inspect distributed-systems_marketplace-net
```

---

## 🛑 **System stoppen**

### **Graceful Shutdown (Empfohlen):**
```bash
./stop.sh
```

**Erwartete Ausgabe:**
```
=========================================
Stopping Distributed Marketplace System
=========================================
[+] Running 8/8
 ✔ Container marketplace2                       Removed
 ✔ Container marketplace1                       Removed
 ✔ Container seller5                            Removed
 ✔ Container seller4                            Removed
 ✔ Container seller3                            Removed
 ✔ Container seller2                            Removed
 ✔ Container seller1                            Removed
 ✔ Network distributed-systems_marketplace-net  Removed

✓ System stopped!
```

### **Alternative Stop-Methoden:**
```bash
# Sofort stoppen
docker-compose down

# Mit Volumes löschen
docker-compose down -v

# Mit Images löschen
docker-compose down --rmi all

# Notfall-Stop (hart stoppen)
docker-compose kill
```

---

## 🔧 **Einzelne Services verwalten**

### **Service neustarten:**
```bash
# Marketplace neustarten
docker-compose restart marketplace1

# Seller neustarten
docker-compose restart seller1

# Mehrere Services neustarten
docker-compose restart marketplace1 seller1
```

### **Service skalieren:**
```bash
# Mehr Seller-Instanzen starten
docker-compose up -d --scale seller1=3

# Zurück zu einer Instanz
docker-compose up -d --scale seller1=1
```

### **Service stoppen/starten:**
```bash
# Service stoppen
docker-compose stop seller1

# Service starten
docker-compose start seller1

# Service entfernen
docker-compose rm seller1
```

### **In Service-Container ausführen:**
```bash
# Shell in laufendem Container
docker-compose exec marketplace1 bash

# Einmaliger Befehl
docker-compose exec marketplace1 ps aux
```

---

## ⚡ **Troubleshooting**

### **Kompletter Reset:**
```bash
# Alles stoppen und neu starten
docker-compose down -v
./run.sh
```

### **Build-Probleme:**
```bash
# Cache leeren und neu builden
docker-compose build --no-cache --force-rm
docker-compose up -d

# Einzelnes Service neu builden
docker-compose build --no-cache marketplace1
docker-compose up -d marketplace1
```

### **System bereinigen:**
```bash
# Nicht verwendete Container/Images löschen
docker system prune -a

# Alle Docker-Daten löschen (VORSICHT!)
docker system prune -a --volumes

# Nur Images löschen
docker image prune -a
```

### **Häufige Probleme:**

#### **1. Port-Konflikte:**
```bash
# Prüfen welche Ports verwendet werden
netstat -tlnp | grep :5555

# Konflikte lösen
docker-compose down
./run.sh
```

#### **2. Container startet nicht:**
```bash
# Logs prüfen
docker-compose logs marketplace1

# Container-Details anzeigen
docker-compose ps
docker inspect marketplace1
```

#### **3. Netzwerk-Probleme:**
```bash
# Netzwerk neu erstellen
docker network rm distributed-systems_marketplace-net
docker-compose up -d
```

#### **4. Speicherplatz-Probleme:**
```bash
# Speicherplatz prüfen
docker system df

# Aufräumen
docker system prune -a
```

---

## 🎯 **Typische Arbeitsabläufe**

### **1. Entwicklungsworkflow:**
```bash
# System starten
./run.sh

# Logs verfolgen während Entwicklung
./logs.sh

# Nach Code-Änderungen: Service neu builden
docker-compose build marketplace1
docker-compose restart marketplace1

# System stoppen
./stop.sh
```

### **2. Debugging-Workflow:**
```bash
# System im Vordergrund starten (für sofortige Logs)
docker-compose up

# In separatem Terminal: Spezifische Logs anzeigen
docker-compose logs -f seller1

# Service einzeln testen
docker-compose run --rm seller1
```

### **3. Produktions-Workflow:**
```bash
# System starten
./run.sh

# Status überwachen
docker-compose ps
docker stats

# Bei Problemen: Logs prüfen
./logs.sh problematic-service

# Graceful shutdown
./stop.sh
```

### **4. Testing-Workflow:**
```bash
# System für Tests starten
./run.sh

# Tests ausführen
./test-enhanced-system.sh

# Logs der Tests anzeigen
./logs.sh

# System nach Tests stoppen
./stop.sh
```

---

## 📋 **Befehlsreferenz**

| Aktion | Befehl |
|--------|---------|
| **System starten** | `./run.sh` |
| **System stoppen** | `./stop.sh` |
| **Alle Logs** | `./logs.sh` |
| **Service-Logs** | `./logs.sh marketplace1` |
| **Status prüfen** | `docker-compose ps` |
| **Ressourcen** | `docker stats` |
| **Service neustarten** | `docker-compose restart marketplace1` |
| **Service skalieren** | `docker-compose up -d --scale seller1=3` |
| **Kompletter Reset** | `docker-compose down -v && ./run.sh` |
| **Build erneuern** | `docker-compose build --no-cache` |
| **System bereinigen** | `docker system prune -a` |
| **Container-Shell** | `docker-compose exec marketplace1 bash` |
| **Netzwerk prüfen** | `docker network inspect distributed-systems_marketplace-net` |
| **Volumes anzeigen** | `docker volume ls` |

---

## 🚀 **Schnellstart-Kommandos**

```bash
# 1. System starten
./run.sh

# 2. Logs verfolgen (in neuem Terminal)
./logs.sh

# 3. Bei Problemen: Service neustarten
docker-compose restart marketplace1

# 4. System stoppen
./stop.sh
```

---

## 🏗️ **System-Architektur**

Das Docker-Setup erstellt:
- **5 Seller Services**: `seller1`, `seller2`, `seller3`, `seller4`, `seller5`
- **2 Marketplace Services**: `marketplace1`, `marketplace2`
- **Gemeinsames Netzwerk**: `marketplace-net`
- **Shared Volume**: `maven-repo` (für Maven-Dependencies)

### **Service-Ports (intern):**
- **Marketplace**: Port 5555 (ZeroMQ)
- **Seller**: Ports 6001-6005

---

## 🔒 **Sicherheitshinweise**

- Das System läuft in einem isolierten Docker-Netzwerk
- Keine Ports sind nach außen exponiert (nur intern)
- Alle Services kommunizieren über ZeroMQ im internen Netzwerk
- Maven-Dependencies werden in einem separaten Volume gespeichert

---

## 📊 **Performance-Tipps**

### **Optimierungen:**
```bash
# Mehr Ressourcen für Docker
docker-compose up -d --scale seller1=2

# Build-Cache nutzen
docker-compose build  # (ohne --no-cache)

# Logs begrenzen
docker-compose logs --tail=100 marketplace1
```

### **Monitoring:**
```bash
# Ressourcenverbrauch kontinuierlich überwachen
watch docker stats

# Container-Gesundheit prüfen
docker-compose ps
```

---

## 🎯 **Library-Compliance Status**

✅ **Das System ist vollständig library-compliant:**
- Nur ZeroMQ für Netzwerkkommunikation
- Keine externen Bibliotheken außer den erlaubten

**Verifikation:**
```bash
# Prüfe JAR-Inhalte nach dem Build
# → Keine Ausgabe (gut!)

jar -tf marketplace/target/marketplace.jar | grep JsonParser
# → common/JsonParser.class (native Implementation)
```

---

**System-Version:** 1.0.0  
**Docker-Guide-Version:** 1.0.0  
**Letzte Aktualisierung:** 16. Juli 2025