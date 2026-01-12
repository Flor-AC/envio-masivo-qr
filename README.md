# QR Code Email Distribution System

## Overview

This project is a **backend system for sending personalized bulk emails with QR codes**, designed to handle large volumes reliably while respecting SMTP rate limits and security best practices.

It automates data processing from Excel files, validates active records, and sends individualized emails with attached QR/PDF files, ensuring traceability and delivery stability.

---

## Key Features

- **Bulk Email Delivery**
  - Sequential sending of personalized emails
  - Individual QR/PDF attachments per recipient
  - Clear and standardized email body

- **Excel Data Processing**
  - Reads `.xlsx` files using Apache POI
  - Flexible column mapping
  - Validation of active records before sending

- **Rate Limiting & Stability**
  - Configurable delay between emails
  - Prevents SMTP throttling and blocking
  - Designed for high-volume scenarios

- **Logging & Traceability**
  - CSV-based delivery logs
  - Tracks success and failure per email

- **Security & Configuration**
  - Credentials managed via environment variables
  - External configuration using `.properties` files
  - No sensitive data stored in source code

---

## Tech Stack

- **Language:** Java 21  
- **Build Tool:** Maven  
- **Email:** Jakarta Mail (SMTP)  
- **Excel Processing:** Apache POI  

---

## Architecture Highlights

- Clean separation of concerns
- Immutable data models (POJOs)
- Config-driven behavior
- Sequential processing for reliability and auditing

---

## How to Run

```bash
mvn clean compile
mvn exec:java
```

---

## Environment variables required

```bash
MAIL_USER=your_email
MAIL_PASS=your_password
```
