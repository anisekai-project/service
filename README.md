> This is the README file for the v3 version of the bot. More on that later. I used ChatGPT because English is not my
> native language and I wanted to be as clear as possible.

# The Anisekai Project

The Anisekai Project aims to provide a set of tools and commands for small communities to organize multiple anime lists
based on their current status (e.g., currently watching, simulcast, downloaded) in a semi-automated manner. Think of it
as a shared Anilist account, but on Discord.

### Internationalization

Internationalization support is planned for the future, but it will not be implemented until the codebase reaches a
satisfactory level. For now, all messages sent through Discord will be in French, while code comments, exceptions, and
messages intended for developers/administrators will remain in English.

### Requirements

- Docker
- Docker Compose
- JDK 17+

This README will be updated to include the installation process and contribution guidelines once the project is ready to
accept contributions. However, at this stage, contributions are limited to ensure stability and consistency in
development. Feel free to clone the repository and explore the `.env.example` file. If you are familiar with Spring
Boot and Java, it should not be too challenging to understand how to get things started, even without Docker.

### Why v3 ?

With every major version of this application comes a big change in codebase (hence the major version bump), and this
time around, a lot of feature were added during v2 which made its codebase a complete mess. The v3 aims to provide a
better environment for the application evolution.

- Discord Version: `v1.1`
- Library Version: `v2.0`
- Backend Version: `v2.0`

# Contributing

Thank you for your interest in contributing to this project! Contributions are welcome, but at this stage, the codebase
is still undergoing significant changes. To avoid unnecessary effort on both sides:

- **Open an Issue First**: Before submitting a pull request, please open an issue to describe the problem or
  feature you'd like to address. This applies to all types of contributions, including bug fixes.
- **Await Approval to work on an issue**: I prefer to give the green light on issues before they are worked on. This
  helps ensure your efforts align with the project's current priorities and focus.

Thank you for your understanding and for helping make this project better!
