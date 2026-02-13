# Object-Oriented Programming Course Project (Java)

This is a university course project for the Object-Oriented Programming class at the Hebrew University of Jerusalem.

The main goal of this project was to design and implement a small Java application using clean object-oriented design:
- Separate responsibilities into classes and packages
- Use inheritance, interfaces, and composition appropriately
- Write code that is readable and easy to extend

## Overview

The application is a small simulation-style program written in Java.  
It models several types of objects, their state, and how they interact with each other over time.

Examples of what this project focuses on:
- Defining clear domain classes (for example: manager classes, entity classes, utility classes, etc.)
- Encapsulating state and behavior inside each class
- Using interfaces / abstract classes to avoid duplicated logic
- Handling input / configuration and updating the internal state step by step

*(If you have a concrete theme — like “world simulator”, “game-like grid”, “bank accounts”, etc. — you can briefly describe it here.)*

## Main Concepts and Techniques

- Language: **Java**
- Paradigm: **Object-Oriented Programming**
- Topics practiced:
  - Class design and separation of concerns
  - Inheritance and polymorphism
  - Interfaces and abstract classes
  - Basic error handling and input validation

## Project Structure

- `src/` – main Java source files
  - `...` – domain classes (core logic)
  - `...` – helper / utility classes
  - `Main.java` (or similar) – entry point for the program
- `resources/` (if any) – configuration or input files used by the program

*(Adjust the folder and file names above to match your actual project.)*

## How to Run

1. Make sure you have **Java 8+** installed.
2. Compile the project:

   ```bash
   javac -d out src/*.java
