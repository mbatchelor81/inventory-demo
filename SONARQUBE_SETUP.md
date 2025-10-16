# SonarQube Cloud Integration Guide

This document describes the SonarQube Cloud integration for the Inventory Service Demo project.

## Overview

SonarQube Cloud is configured to analyze both the backend (Spring Boot/Java) and frontend (React) components of this application. The analysis runs automatically on every push to `main` or `develop` branches and on pull requests.

## Configuration Files

### 1. `sonar-project.properties`
Main configuration file at the root of the project containing:
- Organization key: `mbatchelor81`
- Project key: `mbatchelor81_inventory-service-demo`
- Source and test paths for both backend and frontend
- Coverage and exclusion settings

### 2. `backend/pom.xml`
Maven configuration includes:
- **SonarQube Maven Plugin** (v4.0.0.4121): Integrates SonarQube analysis with Maven
- **JaCoCo Plugin** (v0.8.12): Generates code coverage reports for Java code

### 3. `.github/workflows/sonarqube.yml`
GitHub Actions workflow that:
- Triggers on pushes to main/develop and on pull requests
- Builds and tests both backend and frontend
- Generates coverage reports
- Runs SonarQube analysis

## Prerequisites

### 1. SonarQube Cloud Token
You need to create a token in SonarQube Cloud:

1. Go to [SonarCloud.io](https://sonarcloud.io)
2. Log in with your account
3. Navigate to **My Account** → **Security** → **Generate Tokens**
4. Create a token with a descriptive name (e.g., "inventory-service-github-actions")
5. Copy the token (you won't be able to see it again)

### 2. GitHub Secret Configuration
Add the SonarQube token to your GitHub repository:

1. Go to your GitHub repository
2. Navigate to **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**
4. Name: `SONAR_TOKEN`
5. Value: Paste your SonarQube Cloud token
6. Click **Add secret**

### 3. SonarQube Cloud Project Setup
Ensure your project exists in SonarQube Cloud:

1. Go to [SonarCloud.io](https://sonarcloud.io)
2. Navigate to your organization: `mbatchelor81`
3. If the project doesn't exist, create it:
   - Click **+** → **Analyze new project**
   - Select your GitHub repository
   - Follow the setup wizard

## Running Analysis

### Automatic Analysis (Recommended)
The analysis runs automatically via GitHub Actions when you:
- Push to `main` or `develop` branches
- Open or update a pull request

### Manual Local Analysis

#### Backend Only
```bash
cd backend
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=mbatchelor81_inventory-service-demo \
  -Dsonar.organization=mbatchelor81 \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.token=YOUR_SONAR_TOKEN
```

#### Full Project Analysis
```bash
# Build and test backend
cd backend
mvn clean verify
cd ..

# Install and test frontend
cd frontend
npm ci
npm test -- --coverage --watchAll=false
cd ..

# Run SonarQube analysis
cd backend
mvn sonar:sonar \
  -Dsonar.projectKey=mbatchelor81_inventory-service-demo \
  -Dsonar.organization=mbatchelor81 \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.token=YOUR_SONAR_TOKEN
```

**Note:** Replace `YOUR_SONAR_TOKEN` with your actual SonarQube token.

## What Gets Analyzed

### Backend (Java/Spring Boot)
- **Source code:** `backend/src/main/java`
- **Test code:** `backend/src/test/java`
- **Coverage:** JaCoCo reports from `backend/target/site/jacoco`
- **Java version:** 21

### Frontend (React)
- **Source code:** `frontend/src`
- **Test code:** `frontend/src` (test files)
- **Coverage:** Jest coverage reports from `frontend/coverage/lcov.info`
- **Exclusions:** node_modules, test files, setupTests.js, reportWebVitals.js

## Viewing Results

1. Go to [SonarCloud.io](https://sonarcloud.io)
2. Navigate to your organization: `mbatchelor81`
3. Select the project: **Inventory Service Demo**
4. View:
   - **Overview:** Quality gate status, coverage, code smells, bugs, vulnerabilities
   - **Issues:** Detailed list of all detected issues
   - **Measures:** Code metrics and technical debt
   - **Code:** Browse code with inline issue annotations
   - **Activity:** Historical analysis data

## Quality Gates

The default SonarQube quality gate checks:
- **New Code Coverage:** ≥ 80%
- **New Duplicated Lines:** ≤ 3%
- **New Maintainability Rating:** A
- **New Reliability Rating:** A
- **New Security Rating:** A
- **New Security Hotspots Reviewed:** 100%

If the quality gate fails, the GitHub Actions workflow will fail, blocking merges if branch protection is enabled.

## Troubleshooting

### Analysis Fails in GitHub Actions
1. Check that `SONAR_TOKEN` secret is properly configured
2. Verify the token hasn't expired
3. Check GitHub Actions logs for specific error messages

### Coverage Not Showing
1. Ensure tests are running successfully
2. For backend: Check that JaCoCo reports are generated in `backend/target/site/jacoco`
3. For frontend: Check that Jest coverage is generated in `frontend/coverage`

### Project Not Found
1. Verify the project exists in SonarQube Cloud
2. Check that organization key and project key match in all configuration files
3. Ensure your token has access to the organization

## Best Practices

1. **Fix issues early:** Address SonarQube issues as they appear
2. **Monitor coverage:** Aim to maintain or improve code coverage
3. **Review security hotspots:** Always review and address security findings
4. **Don't ignore issues:** Avoid marking issues as "won't fix" without good reason
5. **Use quality gate:** Enable branch protection to enforce quality standards

## Additional Resources

- [SonarQube Cloud Documentation](https://docs.sonarcloud.io/)
- [SonarQube Java Rules](https://rules.sonarsource.com/java/)
- [SonarQube JavaScript Rules](https://rules.sonarsource.com/javascript/)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)

## Support

For issues or questions:
1. Check SonarQube Cloud status: [status.sonarqube.com](https://status.sonarqube.com/)
2. Review SonarQube Community: [community.sonarsource.com](https://community.sonarsource.com/)
3. Check GitHub Actions logs for detailed error messages
