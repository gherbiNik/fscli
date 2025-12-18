# FSCLI - Filesystem Command Interpreter Simulator

**FSCLI** is a Software Engineering project designed to simulate a filesystem command-line interface. It creates a virtual environment where users can execute standard UNIX-like commands to manipulate files and directories within a simulated filesystem structure.

The application is built using a **JavaFX** GUI and follows a strict **MVC (Model-View-Controller)** architecture with separated backend and frontend modules.

## Functional Requirements

### GUI Capabilities
Upon starting the GUI client, the user can:
* **Create New Filesystem**: Initialize a new, empty simulated filesystem.
* **Persist Filesystem**:
    * Save as a new text file (JSON format) on the local machine.
    * Save changes to the currently loaded file.
* **Load Filesystem**: Load a previously persisted filesystem state from a file.
* **Command Interaction**: Write textual commands, have them interpreted, and view textual feedback.
* **Help & Credits**: View a help manual and application credits (Developers, Release, Build Date).
* **Visual Feedback**: Receive relevant feedback for every GUI action and command execution.

### Command Interpreter
The interpreter supports **absolute paths**, **relative paths**, special paths (`.` and `..`), and the `*` wildcard.

**Feedback Behavior:**
* *Success*: No feedback is printed (silent success).
* *Not Found*: `<command>: command not found` if the command does not exist.
* *Syntax Error*: `usage: <command synopsis>` if arguments are invalid.

### Supported Commands
The simulator supports a simplified subset of common UNIX commands:

| Command | Usage | Description |
| :--- | :--- | :--- |
| `pwd` | `pwd` | Print the current working directory. |
| `touch` | `touch FILE...` | Create empty text files. |
| `mkdir` | `mkdir DIRECTORY...` | Create new directories. |
| `cd` | `cd [DIRECTORY]` | Change the current directory. |
| `rm` | `rm FILE...` | Remove files. |
| `rmdir` | `rmdir DIRECTORY...` | Remove empty directories. |
| `mv` | `mv SOURCE DESTINATION` | Move/Rename a file or directory. |
| `ln` | `ln [-s] TARGET LINK_NAME` | Create hard or soft links (hard links to dirs not allowed). |
| `ls` | `ls [-i] [FILE]...` | List directory contents (supports inode flag). |
| `clear` | `clear` | Clear the output area text. |
| `help` | `help` | Show available commands and synopsis. |

## ⚙️ User Preferences
Users can customize the interface. Settings are saved as a human-readable text file in the user's home directory and are **cold reloaded** (require restart).

* **Language**: Interface language selection.
* **Command Line**: Number of visible columns (width) and font style.
* **Output Area**: Number of visible lines (min 3) and font style.
* **Log Area**: Number of visible lines (min 3) and font style.

## Architecture

The project adheres to the **MVC** pattern with a modular design:
* **Backend**: Layered architecture handling business logic and data structures.
* **Frontend**: JavaFX implementation handling the View and Controller aspects.

### Non-Functional Requirements
* **Responsive UI**: Initial frame width is determined by the command line width; components enable/disable based on application state.
* **Persistence**: Filesystem state is saved in **JSON** format.
* **Distribution**: Packaged as a self-contained executable JAR.

## Tech Stack & Tools

* **Language**: Java 17
* **Build Tool**: Maven
* **GUI Framework**: JavaFX 17.0.15
* **Testing**:
    * *Unit Testing*: JUnit 5, Mockito
    * *E2E Testing*: TestFX
    * *Coverage*: JaCoCo

## Getting Started

### Prerequisites
* Java JDK 17
* Maven 3.6+

### Installation

1.  Clone the repository:
    ```bash
    git clone [https://gitlab-edu.supsi.ch/dti-isin/labingsw/labingsw02/2025-2026/fscli/Gruppo_16.git](https://gitlab-edu.supsi.ch/dti-isin/labingsw/labingsw02/2025-2026/fscli/Gruppo_16.git)
    cd fscli
    ```

2.  Build the project using Maven:
    ```bash
    mvn clean install
    ```

### Running the Application
After building, you can run the executable JAR generated in the frontend target folder:

```bash
java -jar frontend/target/fscli-jar-with-dependencies.jar
