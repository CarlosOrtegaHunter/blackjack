# Blackjack Game - Java Reactive API

## Overview
This project is a **Reactive Blackjack Game** built using **Spring Boot, WebFlux, and MongoDB**. 
The application demonstrates Java Spring skills, including **Reactive Programming**, **R2DBC**, **WebFlux Controllers**, and **Asynchronous Data Handling**. 

## Features
- **Reactive Game Flow**: Asynchronous handling using **Spring WebFlux**.
- **MongoDB Integration**: Stores game data in **MongoDB** using **ReactiveMongoRepository**.
- **Reactive CRUD Operations**: Implements non-blocking repositories with **R2DBC** for `Player` data.
- **REST API**: Exposes endpoints to create games, make moves, check results, and manage players.

---

## **API Endpoints**
### **Game API**
| Method | Endpoint | Description |
|--------|---------|-------------|
| `POST` | `/games/new` | Creates a new Blackjack game |
| `GET` | `/games/{gameId}` | Retrieves game details |
| `POST` | `/games/{gameId}/move` | Makes a move (`HIT` or `STAND`) |
| `GET` | `/games/{gameId}/result` | Returns the game winner |
| `DELETE` | `/games/{gameId}/delete` | Deletes a game |

### **Player API**
| Method | Endpoint | Description |
|--------|---------|-------------|
| `POST` | `/players/new` | Creates a new player |
| `GET` | `/players/{playerId}` | Retrieves a player |
| `PUT` | `/players/{playerId}/name` | Updates a player's name |
| `GET` | `/players/ranking` | Retrieves player rankings |
