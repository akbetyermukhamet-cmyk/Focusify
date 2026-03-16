# Testing Strategy - Tomato

This document outlines the testing strategy implemented for the Tomato productivity application.

## Overview
The goal is to ensure application stability, data integrity, and a smooth user experience through a combination of Unit, Integration, and (planned) UI tests.

## 1. Unit Tests
Focus on business logic, data processing, and ViewModel state management.

### ViewModels
- **StatsViewModelTest**: Verifies data aggregation logic for weekly, monthly, and yearly charts. Ensures that daily statistics are correctly processed into chart data models.
- **TimerViewModelTest**: Validates task management actions (Add, Edit, Delete) and their interaction with repositories.

### Repositories
- **TaskRepositoryTest**: Ensures critical data operations like renaming Projects/Tags correctly trigger cascading updates across all associated tasks.
- **AppStatRepositoryTest**: Verifies that focus time is correctly distributed across the four day-quarters (Q1-Q4).

### Utilities
- **UtilsKtTest**: Covers time formatting (`millisecondsToStr`, `millisecondsToHours`) and color conversions.

## 2. Integration Tests
Focus on data persistence and database integrity.

- **Room DAO Tests**: Instrumented tests (e.g., `StatDaoTest`) that use an in-memory database to verify complex SQL queries and Room `@Dao` operations. *Note: These require an Android environment (emulator/device).*

## 3. UI Tests (Strategy)
Focus on critical user flows using Compose Test Rule.

- **Timer Flow**: Verify starting, pausing, and skipping sessions.
- **Task Management**: Verify adding a task, assigning a project/tag, and editing it.
- **Statistics**: Verify that charts are rendered when data is present.

## Testing Tools
- **JUnit 4**: Main testing framework.
- **MockK**: For mocking dependencies in unit tests.
- **Kotlinx Coroutines Test**: For controlling time and dispatchers in suspendable tests.
- **Turbine**: For testing Kotlin Flows.
- **AndroidX Test**: For instrumented and integration tests.

## Running Tests
To run unit tests:
```bash
./gradlew test
```
To run instrumented tests:
```bash
./gradlew connectedAndroidTest
```
