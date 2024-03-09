# Airbnb Database Manager

<img src="repo.jpg" alt="Project Logo" width="300" height="300"/>

## Badges

![Java Version](https://img.shields.io/badge/java-v1.8+-blue.svg)
![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)
![Contributions Welcome](https://img.shields.io/badge/contributions-welcome-orange.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)

## Table of Contents

- [Features](#features)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Running the Application](#running-the-application)
- [Usage](#usage)
- [License](#license)

## Features

- Operations with a MySQL database.
- GUI for managing vacation site information, including add, edit, delete, and search functionalities.
- Data validation and implementation of sorting algorithms for displaying sites.
- Comprehensive Javadocs and comments for better understanding and maintenance.

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 1.8 or higher
- MySQL Server and JDBC driver installed and configured

### Installation

1. Clone the repository:
   ```
   git clone https://github.com/wldeh/airbnb-jdbc.git
   ```
2. Navigate to the project directory:
   ```
   cd airbnb-jdbc
   ```

### Running the Application

1. Ensure your MySQL server is running and accessible.
2. Update the `db.properties` file with your MySQL server details (username, password, and database URL).
1. pen IntelliJ IDEA, select "File > Open," and choose your project directory.
2. In "File > Project Structure > Project," set your Java SDK version.
3. Adjust GUI designer settings under "File > Settings > Editor > GUI Designer" to "Java source code."
4. Add a new run configuration in "Run > Edit Configurations" selecting the main class.
5. Click the "Run" button to start the application.

## Usage

Use the GUI to interact with the vacation sites database:
- **Add New Site:** Fill in the details in the provided form and submit.
- **Edit Site:** Select a site from the list, modify details, and save changes.
- **Delete Site:** Select a site and confirm deletion.
- **Search Site:** Use the search functionality to find sites by name, country, or capital.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
