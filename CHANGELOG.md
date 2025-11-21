# Changelog

All notable changes to ME Android Sunset will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-11-21

### Added

#### Core Features
- **Full Offline Encryption**: Complete AES-256 CBC encryption implemented locally without external server dependencies
- **User Authentication**: Secure login with OTP verification
- **Balance Management**: Real-time balance checking and monitoring
- **Package Management**: Browse, search, and purchase data packages
- **Transaction History**: View complete transaction history with status tracking
- **Family Plan Support**: Manage family plan/Akrab Organizer features
- **Circle Management**: Handle circle groups and benefits
- **Bookmark System**: Save and quickly access favorite packages

#### UI/UX
- **Material 3 Design**: Modern UI with Material Design 3 components
- **Jetpack Compose**: Fully declarative UI built with Compose
- **Dark Mode Support**: Automatic theme switching based on system settings
- **Responsive Layout**: Adaptive layouts for different screen sizes
- **Smooth Navigation**: Seamless navigation between screens
- **Pull to Refresh**: Intuitive gesture to refresh data
- **Loading States**: Clear loading indicators for async operations
- **Error Handling**: User-friendly error messages and retry options

#### Security
- **AES-256 CBC Encryption**: Industry-standard encryption for data payloads
- **HMAC-SHA512 Signatures**: Secure signature generation for API requests
- **SHA-256 Hashing**: Secure IV derivation from timestamps
- **Local Key Storage**: All encryption keys stored securely in app
- **No Third-Party Servers**: Zero dependency on external encryption services
- **Secure Data Storage**: Encrypted SharedPreferences for sensitive data

#### Architecture
- **MVVM Pattern**: Clean architecture with separation of concerns
- **Repository Pattern**: Abstracted data layer for easier testing
- **Kotlin Coroutines**: Efficient asynchronous programming
- **StateFlow**: Reactive state management
- **Retrofit**: Type-safe HTTP client for API communication
- **Gson**: JSON serialization/deserialization

#### Developer Experience
- **GitHub Actions CI/CD**: Automated build and release pipeline
- **Gradle Build System**: Modern Kotlin DSL build configuration
- **ProGuard Rules**: Code obfuscation for release builds
- **Comprehensive Documentation**: README, USAGE_GUIDE, and CONTRIBUTING docs
- **Code Quality**: Kotlin coding conventions and best practices
- **Version Control**: Git with semantic commit messages

### Technical Details

#### Cryptography Implementation
```kotlin
- AES-256 CBC for payload encryption
- HMAC-SHA512 for request signatures
- HMAC-SHA256 for API signatures
- SHA-256 for IV derivation
- Base64 URL-safe encoding
```

#### API Integration
```kotlin
- RESTful API communication
- Encrypted request/response handling
- Token-based authentication
- Automatic token refresh
- Error handling and retry logic
```

#### Data Persistence
```kotlin
- SharedPreferences for user data
- Encrypted token storage
- Bookmark management
- Multi-account support
- Secure data backup exclusion
```

### Dependencies

#### Core
- Kotlin 1.9.20
- Android Gradle Plugin 8.2.0
- Gradle 8.2

#### AndroidX
- Core KTX 1.12.0
- Lifecycle Runtime KTX 2.7.0
- Activity Compose 1.8.2
- Navigation Compose 2.7.7
- DataStore Preferences 1.0.0
- Security Crypto 1.1.0-alpha06

#### Compose
- Compose BOM 2024.02.00
- Material 3
- Material Icons Extended
- UI Tooling

#### Networking
- Retrofit 2.9.0
- OkHttp 4.12.0
- Gson 2.10.1

#### Other
- Kotlinx Coroutines 1.7.3
- Kotlinx Serialization 1.6.2
- ZXing Core 3.5.2 (QR Code)

### Build Configuration

#### Minimum Requirements
- Min SDK: 24 (Android 7.0)
- Target SDK: 34 (Android 14)
- Compile SDK: 34

#### Build Types
- **Debug**: Development build with logging
- **Release**: Production build with ProGuard optimization

### CI/CD Pipeline

#### Automated Workflows
- Build on push to main/master/develop
- Build on pull requests
- Manual workflow dispatch
- Artifact upload (APK files)
- Automatic release creation

#### Build Artifacts
- app-debug.apk: Debug build for testing
- app-release.apk: Production-ready build

### Documentation

#### User Documentation
- **README.md**: Project overview and quick start
- **USAGE_GUIDE.md**: Comprehensive user guide with screenshots
- **FAQ**: Common questions and troubleshooting

#### Developer Documentation
- **CONTRIBUTING.md**: Contribution guidelines and coding standards
- **CHANGELOG.md**: Version history and changes
- **Code Comments**: Inline documentation for complex logic
- **KDoc**: API documentation for public functions

### Known Limitations

- Manual APK installation required (not on Play Store)
- No automatic update mechanism
- Requires Android 7.0 or higher
- Internet connection required for API calls
- No offline mode for package browsing

### Future Roadmap

#### Planned Features (v1.1.0)
- [ ] Package search with filters
- [ ] Advanced bookmark management
- [ ] Push notifications for package expiry
- [ ] Widget for quick balance check
- [ ] Export transaction history
- [ ] Multiple language support

#### Planned Features (v1.2.0)
- [ ] Biometric authentication
- [ ] Package recommendation engine
- [ ] Usage statistics and charts
- [ ] Auto-renewal for packages
- [ ] Referral system
- [ ] In-app chat support

#### Long-term Goals
- [ ] Play Store distribution
- [ ] Automatic updates
- [ ] Offline package catalog
- [ ] Advanced analytics
- [ ] Integration with other services

### Migration Guide

This is the initial release, no migration needed.

### Breaking Changes

None (initial release).

### Deprecations

None (initial release).

### Security Fixes

None (initial release).

### Performance Improvements

- Optimized encryption algorithms
- Efficient state management with StateFlow
- Lazy loading for package lists
- Image caching for package icons
- Minimal APK size (~5MB)

### Bug Fixes

None (initial release).

### Contributors

- **Initial Development**: Full-stack implementation of Android app
- **Crypto Implementation**: Offline encryption system
- **UI/UX Design**: Material 3 design system
- **CI/CD Setup**: GitHub Actions workflow
- **Documentation**: Comprehensive user and developer docs

### Acknowledgments

- Original CLI version: [me-cli-sunset](https://github.com/purplemashu/me-cli-sunset)
- Inspired by the need for transparent offline encryption
- Built with ❤️ using Kotlin & Jetpack Compose

### License

MIT License - See LICENSE file for details.

---

## How to Read This Changelog

- **Added**: New features
- **Changed**: Changes in existing functionality
- **Deprecated**: Soon-to-be removed features
- **Removed**: Removed features
- **Fixed**: Bug fixes
- **Security**: Security fixes

## Version Numbering

We follow [Semantic Versioning](https://semver.org/):
- **MAJOR**: Incompatible API changes
- **MINOR**: Backward-compatible new features
- **PATCH**: Backward-compatible bug fixes

---

[1.0.0]: https://github.com/danophelia857-stack/me-android-sunset/releases/tag/v1.0.0
