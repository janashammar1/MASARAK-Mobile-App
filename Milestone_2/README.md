# MASARAK — Milestone 2

This folder contains the complete Milestone 2 design, navigation, architecture, and implementation-planning documentation for the MASARAK mobile application.

## Milestone 2 Objective

The goal of Milestone 2 is to define the application structure, user interface, navigation flows, design system, and planned Android implementation components before development begins.

## Project Overview

MASARAK is an Android mobile application designed to support student field training through task documentation, attendance and training-hour tracking, supervisor review, academic relevance review, weekly evaluation, and in-app communication.

## User Roles

MASARAK supports three main user roles:

- Student
- Field Supervisor
- Academic Supervisor

Each role has its own screens, responsibilities, and navigation flow.

## Deliverables

### 1. App Architecture

Contains the finalized MASARAK application architecture showing:

- UI Layer
- Domain / Business Logic Layer
- Data Layer
- Unidirectional Data Flow

Folder: `App_Architecture/`

---

### 2. Screens and Navigation

Contains the finalized screen designs and detailed navigation flows for:

- Student
- Field Supervisor
- Academic Supervisor
- Supporting screens and UI states

It also includes documentation explaining how to access large screen files if GitHub cannot preview them directly.

Folder: `Screens_and_Navigation/`

---

### 3. Navigation Map

Contains the finalized visual Navigation Map showing the main navigation flow across all three MASARAK user roles.

Folder: `Navigation_Map/`

Main file:

`navigation-map.png`

---

### 4. Interactive Prototype

Contains access information for the finalized interactive MASARAK prototype in Figma.

The prototype includes flows for:

- Student
- Field Supervisor
- Academic Supervisor

Folder: `Prototype/`

---

### 5. Components and Libraries

Contains the planned Jetpack Compose components and Android libraries required for implementation.

Folder: `Components_and_Libraries/`

---

### 6. Style Guide

The MASARAK design system and implementation reference is documented in:

`STYLE_GUIDE.md`

It includes:

- Brand and visual identity
- Colors and Material 3 color roles
- Typography
- Spacing
- Shapes and corner radii
- Borders and elevation
- Icons
- Reusable UI components
- UI states
- Jetpack Compose / Material 3 mappings

---

## Implementation Context

MASARAK is planned for Android development using:

- Kotlin
- Jetpack Compose
- Material 3
- Single-activity application structure
- Compose-based navigation

## File Structure

```text
Milestone_2/
├── README.md
├── STYLE_GUIDE.md
├── App_Architecture/
├── Screens_and_Navigation/
├── Navigation_Map/
├── Components_and_Libraries/
└── Prototype/

Submission Note

The files in this folder represent the finalized Milestone 2 design and architecture documentation prepared for implementation.

Some large PDF files may not preview directly inside GitHub because of file size or GitHub rendering limitations. If this occurs, the files can still be accessed using the Raw or Download option.
