# Changelog

All notable changes to the MeetNote project will be documented in this file.

## [1.0.0] - 2026-06-27

### Added
- Core Application Framework: Implemented Clean Architecture with MVVM and Koin DI.
- Dual Profile Management: Support for distinct Work and Social identities.
- Digital Business Card: QR code generation and sharing for both profile types.
- Contact "Catching": Integrated QR scanner to quickly add new contacts.
- Event-Based Organization: Ability to group contacts by the specific event where they were met.
- Interactive Contact Actions: Functional Call, Email, and Message buttons integrated with system intents.
- Documentation: Comprehensive README.md for project overview and tech stack.

### Changed
- Refined UI on QR Share Screen: The header now correctly displays the user's profile picture if available, with an initials fallback.
- Optimized Events Screen: Removed redundant menu icons from the top bar for a cleaner look.
- Streamlined Details Screens: Removed redundant edit buttons from the Notes component in favor of the primary edit action in the TopAppBar.

### Fixed
- Navigation Bug: Corrected the "Edit" action in Contact Details to navigate to a comprehensive Edit Screen instead of the "Add More Details" step.
- Data Persistence: Updated the DAO and Repository to ensure all basic info (Name, Phone, Email, Tag) is correctly saved during an update.
- Compilation: Resolved "No value passed for parameter 'tag'" error in the data mapping layer.

---
*Note: This project follows Semantic Versioning.*
