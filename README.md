# AI Recipe Platform

---

## Overview

AI Recipe Platform is a full-stack web application that combines a traditional recipe management system with AI-powered functionality.

The platform allows users to:

* store and organize personal recipes
* generate new recipes with the help of AI
* quickly find relevant recipes using semantic search

The main goal of the project is to explore modern backend architecture by integrating a Java backend, a Python AI service and a modern Angular frontend.

---

## Key Features

* Recipe storage and management
* AI-assisted recipe generation
* Semantic recipe search using embeddings
* Modular service architecture
* REST API communication between services

---

## System Architecture

The system is designed as a **multi-service architecture** where each component has a specific responsibility.

```
                 +---------------------+
                 |     Angular UI      |
                 |  (Frontend Client)  |
                 +----------+----------+
                            |
                            | HTTP REST API
                            |
                 +----------v----------+
                 |     Spring Boot     |
                 |       Backend       |
                 |   Business Logic    |
                 +----------+----------+
                            |
                            | Service communication
                            |
                 +----------v----------+
                 |    Python Service   |
                 |  Embeddings / AI    |
                 +----------+----------+
                            |
                            |
                 +----------v----------+
                 |   Vector Database   |
                 |   Semantic Search   |
                 +---------------------+
```

---

## Tech Stack

### Backend

* Java
* Spring Boot
* REST API
* JWT Authentication
* Docker

### AI Service

* Python
* Embedding generation
* Vector database integration
* Semantic search

### Frontend

* Angular
* TypeScript
* REST API integration

---

## Responsibilities

### Spring Boot Backend

Responsible for:

* application business logic
* REST API endpoints
* authentication and authorization
* recipe management
* communication with the AI service

---

### Python AI Service

Responsible for:

* generating text embeddings
* semantic search processing
* interaction with the vector database

---

### Angular Frontend

Responsible for:

* user interface
* recipe management interface
* AI recipe generation interface
* search and filtering

---

## Project Goals

This project was created to gain practical experience with:

* full-stack application architecture
* service communication via REST APIs
* integration of AI components into web applications
* modern frontend and backend development

---

## Future Improvements

* improved AI prompt generation
* recipe recommendation system
* user profiles and personalization
* deployment to cloud infrastructure

---

## Author

Anna Padun

---

## Repository

GitHub: https://github.com/AnikRoan/RPS
