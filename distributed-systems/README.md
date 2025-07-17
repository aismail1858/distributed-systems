# 🏪 Distributed E-Commerce System

Ein hochmodernes, verteiltes E-Commerce-System mit Saga-Pattern, das die Komplexität realer Microservices-Architekturen demonstriert.

## 🚀 Schnellstart

```bash
# System starten
./run.sh

# Logs anzeigen
./logs.sh

# System stoppen
./stop.sh
```

## 📚 Dokumentation

### 📖 **Vollständige Systemdokumentation**
➡️ **[COMPLETE_SYSTEM_DOCUMENTATION.md](COMPLETE_SYSTEM_DOCUMENTATION.md)**
- Umfassende Systemarchitektur
- Technische Komponenten
- Installation und Konfiguration
- Entwickler-Handbuch
- Library-Compliance Details

### 🐳 **Docker-Anleitung**
➡️ **[DOCKER_GUIDE.md](DOCKER_GUIDE.md)**
- Schritt-für-Schritt Docker-Befehle
- Logs anzeigen und überwachen
- Troubleshooting-Tipps
- Service-Management

## ✅ Library-Compliance Status

**Das System ist vollständig library-compliant:**
- ✅ Nur ZeroMQ für Netzwerkkommunikation
- ✅ Keine externen Bibliotheken außer den erlaubten

## 🏗️ System-Architektur

- **5 Seller Services** (seller1-seller5)
- **2 Marketplace Services** (marketplace1-marketplace2)
- **Saga-Pattern** für verteilte Transaktionen
- **Circuit Breaker** für Fehlertoleranz
- **ZeroMQ** für Messaging

## 🔧 Hauptfeatures

- **Distributed Transaction Processing**
- **Fault Tolerance & Recovery**
- **Realistic Failure Simulation**
- **Comprehensive Monitoring**
- **Docker-Containerisierung**
- **Native JSON-Verarbeitung**

## 📊 Systemanforderungen

- **Java 11+**
- **Maven 3.6+**
- **Docker & Docker Compose**
- **4GB RAM** (empfohlen)

---

**System-Version:** 1.0.0 (Library-Compliant)  
**Letzte Aktualisierung:** 16. Juli 2025