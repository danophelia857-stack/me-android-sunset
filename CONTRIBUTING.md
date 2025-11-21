# Contributing to ME Android Sunset

Thank you for your interest in contributing to ME Android Sunset! This document provides guidelines and instructions for contributing to the project.

## Code of Conduct

By participating in this project, you agree to maintain a respectful and inclusive environment for all contributors.

## How to Contribute

### Reporting Bugs

When reporting bugs, please include the following information in your issue:

- **Device Information**: Model and Android version
- **App Version**: Version number from About screen
- **Steps to Reproduce**: Clear steps to reproduce the issue
- **Expected Behavior**: What you expected to happen
- **Actual Behavior**: What actually happened
- **Screenshots**: If applicable, add screenshots
- **Logs**: Any relevant error messages or logs

### Suggesting Features

Feature requests are welcome! Please provide:

- **Clear Description**: What feature you want to add
- **Use Case**: Why this feature would be useful
- **Implementation Ideas**: Any thoughts on how it could be implemented
- **Alternatives**: Any alternative solutions you've considered

### Pull Requests

We actively welcome pull requests! Here's the process:

#### 1. Fork the Repository

```bash
# Fork on GitHub, then clone your fork
git clone https://github.com/YOUR_USERNAME/me-android-sunset.git
cd me-android-sunset
```

#### 2. Create a Branch

```bash
# Create a new branch for your feature
git checkout -b feature/your-feature-name

# Or for bug fixes
git checkout -b fix/bug-description
```

#### 3. Make Your Changes

Follow the coding standards and best practices outlined below.

#### 4. Test Your Changes

- Build the app successfully
- Test on multiple Android versions if possible
- Ensure no new warnings or errors
- Test edge cases

#### 5. Commit Your Changes

```bash
# Add your changes
git add .

# Commit with a descriptive message
git commit -m "Add feature: your feature description"
```

Use clear commit messages following this format:

- `Add feature: description` - for new features
- `Fix: description` - for bug fixes
- `Update: description` - for updates to existing features
- `Refactor: description` - for code refactoring
- `Docs: description` - for documentation changes

#### 6. Push to Your Fork

```bash
git push origin feature/your-feature-name
```

#### 7. Create Pull Request

- Go to the original repository on GitHub
- Click "New Pull Request"
- Select your branch
- Fill in the PR template with:
  - Description of changes
  - Related issues (if any)
  - Screenshots (if UI changes)
  - Testing done

## Development Setup

### Prerequisites

- **Android Studio**: Hedgehog (2023.1.1) or newer
- **JDK**: Version 17
- **Android SDK**: API 34
- **Gradle**: 8.2 (included via wrapper)

### Setting Up Development Environment

1. **Clone the repository**

```bash
git clone https://github.com/danophelia857-stack/me-android-sunset.git
cd me-android-sunset
```

2. **Open in Android Studio**

- Open Android Studio
- Select "Open an Existing Project"
- Navigate to the cloned directory
- Wait for Gradle sync to complete

3. **Build the project**

```bash
./gradlew build
```

4. **Run on emulator or device**

- Connect device or start emulator
- Click "Run" in Android Studio
- Or use: `./gradlew installDebug`

## Project Structure

Understanding the project structure will help you navigate and contribute effectively:

```
me-android-sunset/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/mashu/mesunset/
│   │       │   ├── crypto/          # Encryption & cryptography
│   │       │   │   └── CryptoHelper.kt
│   │       │   ├── data/            # Data layer
│   │       │   │   ├── api/         # API services
│   │       │   │   ├── models/      # Data models
│   │       │   │   └── repository/  # Repository pattern
│   │       │   ├── ui/              # UI layer
│   │       │   │   ├── navigation/  # Navigation setup
│   │       │   │   ├── screens/     # UI screens
│   │       │   │   ├── theme/       # Material 3 theme
│   │       │   │   └── viewmodels/  # ViewModels
│   │       │   ├── MainActivity.kt
│   │       │   └── MESunsetApplication.kt
│   │       ├── res/                 # Resources
│   │       │   ├── drawable/        # Icons & images
│   │       │   ├── values/          # Strings, colors, themes
│   │       │   └── xml/             # XML configs
│   │       └── AndroidManifest.xml
│   ├── build.gradle.kts             # App-level Gradle
│   └── proguard-rules.pro           # ProGuard rules
├── gradle/                          # Gradle wrapper
├── .github/
│   └── workflows/
│       └── android-build.yml        # CI/CD workflow
├── build.gradle.kts                 # Project-level Gradle
├── settings.gradle.kts              # Gradle settings
├── README.md
├── USAGE_GUIDE.md
├── CONTRIBUTING.md
└── LICENSE
```

## Coding Standards

### Kotlin Style Guide

Follow the official [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html):

- Use 4 spaces for indentation
- Use camelCase for function and variable names
- Use PascalCase for class names
- Use UPPER_SNAKE_CASE for constants
- Maximum line length: 120 characters

### Example Code Style

```kotlin
// Good
class UserRepository(private val apiService: ApiService) {
    suspend fun getUser(userId: String): Result<User> {
        return try {
            val response = apiService.getUser(userId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Avoid
class userRepository(val apiService:ApiService){
fun getUser(userId:String):Result<User>{
try{val response=apiService.getUser(userId)
return Result.success(response)}catch(e:Exception){
return Result.failure(e)}}}
```

### Compose Guidelines

- Use `@Composable` functions for UI components
- Keep composables small and focused
- Extract reusable components
- Use `remember` for state that survives recomposition
- Use `LaunchedEffect` for side effects

```kotlin
@Composable
fun UserProfile(
    user: User,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = user.name, style = MaterialTheme.typography.titleLarge)
            Text(text = user.email, style = MaterialTheme.typography.bodyMedium)
            Button(onClick = onEditClick) {
                Text("Edit Profile")
            }
        }
    }
}
```

### Architecture Guidelines

The app follows **MVVM (Model-View-ViewModel)** architecture:

- **Model**: Data classes and repository
- **View**: Composable UI functions
- **ViewModel**: Business logic and state management

**Key Principles:**

- Separation of concerns
- Unidirectional data flow
- Single source of truth
- Repository pattern for data access

### Security Best Practices

When working with cryptography and sensitive data:

- Never hardcode sensitive keys (use build config or secure storage)
- Use appropriate encryption algorithms (AES-256, HMAC-SHA512)
- Validate all user inputs
- Handle errors gracefully without exposing sensitive information
- Use HTTPS for all network communications
- Follow Android security best practices

### Testing

While not required for all contributions, tests are highly encouraged:

- **Unit Tests**: For business logic and utilities
- **Integration Tests**: For repository and API interactions
- **UI Tests**: For critical user flows

```kotlin
// Example unit test
@Test
fun `encryptXData should return valid base64 string`() {
    val plaintext = "test data"
    val xtime = System.currentTimeMillis()
    val encrypted = CryptoHelper.encryptXData(plaintext, xtime)
    
    assertTrue(encrypted.isNotEmpty())
    assertTrue(encrypted.matches(Regex("^[A-Za-z0-9_-]+$")))
}
```

## Documentation

### Code Comments

- Add comments for complex logic
- Use KDoc for public APIs
- Explain "why" not "what"

```kotlin
/**
 * Encrypts data using AES-256 CBC with time-based IV derivation.
 * 
 * @param plaintext The data to encrypt
 * @param xtimeMs Timestamp in milliseconds used for IV derivation
 * @return Base64-encoded encrypted data
 */
fun encryptXData(plaintext: String, xtimeMs: Long): String {
    // Derive IV from timestamp to ensure uniqueness
    val iv = deriveIv(xtimeMs)
    // ... encryption logic
}
```

### README Updates

If your changes affect usage or setup, update the README.md accordingly.

### USAGE_GUIDE Updates

For new features that users will interact with, add documentation to USAGE_GUIDE.md.

## CI/CD

The project uses GitHub Actions for continuous integration:

- **Automatic builds** on push to main/master
- **APK artifacts** generated for each build
- **Releases** created automatically

Your PR will trigger a build. Ensure it passes before requesting review.

## Review Process

After submitting a PR:

1. **Automated Checks**: CI/CD will run automatically
2. **Code Review**: Maintainers will review your code
3. **Feedback**: Address any requested changes
4. **Approval**: Once approved, your PR will be merged
5. **Release**: Changes will be included in the next release

## Getting Help

If you need help:

- **GitHub Discussions**: For general questions
- **GitHub Issues**: For specific problems
- **Code Comments**: Ask questions in PR comments

## Recognition

Contributors will be recognized in:

- GitHub contributors list
- Release notes (for significant contributions)
- README acknowledgments (for major features)

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

---

Thank you for contributing to ME Android Sunset! Your efforts help make this project better for everyone. 🙏
