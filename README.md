# DAG Navigation Flow Manager

## Overview

This project implements a Directed Acyclic Graph (DAG) based navigation flow manager for Android applications. It provides a structured way to manage complex navigation flows with conditional transitions between screens.

## Features

- DAG-based navigation flow management
- Conditional screen transitions based on user state
- DSL-style API for creating navigation flows
- Automatic detection of the next appropriate screen
- Cycle detection to prevent infinite navigation loops

## Core Components

### DagNode

Represents a single node in the navigation flow graph. Each node:
- Has a unique identifier
- Contains screen route data
- Maintains dependencies on other nodes
- Has condition checks for determining if the node is satisfied

```kotlin
val loginNode = DagNode("login", Screen.Login.route) {
    +privacyNode  // Login depends on privacy policy agreement
    condition { userPreferences.isLoggedIn() }
}
```

### DagManager

Manages the entire DAG structure:
- Adds/removes nodes
- Detects circular dependencies
- Provides topological ordering of nodes
- Finds executable nodes based on conditions
- Visualizes the DAG structure

### FlowManager

Integrates the DAG system with Android navigation:
- Determines the current navigation step
- Checks if navigation to specific screens is allowed
- Handles navigation to the next appropriate screen
- Maintains user state through preferences

## Usage Example

```kotlin
// Initialize the flow manager with user preferences
val flowManager = FlowManager(userPreferences)

// Get the current screen based on user state
val currentScreen = flowManager.getCurrentStep()

// Navigate to next screen
flowManager.navToNext(navController)
```

## Navigation Flow

The application implements a simple flow:
1. Privacy Policy Screen - User must agree to privacy policy
2. Login Screen - User must log in
3. Main Screen - Main application content

Each transition is controlled by conditions and dependencies in the DAG structure.

## Implementation Details

The project uses:
- Jetpack Compose for UI components
- Kotlin DSL for creating readable flow definitions
- AndroidX Navigation for screen transitions
- Custom DAG implementation for flow control

## Future Improvements

- Support for parallel paths in the navigation flow
- Saving and restoring flow state across app restarts
- Integration with ViewModel architecture
- Advanced visualization of the navigation graph
- Support for deep linking within the DAG structure
