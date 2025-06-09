# API Automation Framework

This is the API Automation framework for testing the Bookstore service APIs.

---

## Features
- Full CRUD API automation using RestAssured
- JUnit 5 for test organization
- Allure for user-friendly test reporting
- GitHub Actions CI pipeline for automation
 
---

## Prerequisites

- Java JDK 8 or higher installed
- Maven installed (if using Maven for build)
- Allure commandline installed for report generation ([Installation Guide](https://docs.qameta.io/allure/#_installing_a_commandline))

---

## How to Run Tests locally

1. Make sure the **Bookstore service** is running locally. To run it, execute command

```bash
cd bookstore
uvicorn main:app --reload
```

2. Open a new terminal/command prompt and navigate to the root directory of this project.

3. To run all API tests, execute:

```bash
mvn clean test
```

4. To run on a specific env, execute:

```bash
mvn clean test -Denv=qa
```

---

## 📊 Generate Allure Report locally
```bash
allure serve allure-results
```

## ⚙️ CI/CD
Automatically runs on each push using GitHub Actions. 
Also generates and uploads the Allure report at https://priyankaj250.github.io/api-automation/.

## 🧪 Test Strategy
- Covers Create, Read, Update, Delete endpoints.
- Chained requests for consistent state.
- Validates both status codes and payloads.


