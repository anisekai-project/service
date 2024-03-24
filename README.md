> As English is not my native language, I wrote a first version with "my" english and asked ChatGPT to adapt it.

# The Anisekai Project

The Anisekai Project aims to provide a set of tools and commands for small communities to organize multiple anime lists
based on their current status (e.g., currently watching, simulcast, downloaded) in a semi-automated manner. Think of it
as a shared Anilist account, but on Discord.

## Internationalization

Internationalization support is planned for the future, but it will not be implemented until the codebase reaches a
satisfactory level. For now, all messages sent through Discord will be in French, while code comments, exceptions, and
messages intended for developers/administrators will remain in English.

## Requirements

- Docker
- Docker Compose
- JDK 17+

This README will be updated to include the installation process and contribution guidelines once the project is ready to
accept contributions. However, at this stage, contributions are not being accepted to ensure stability and consistency
in development. Feel free to clone the repository and explore the `.env.example` file. If you are familiar with Spring
Boot and Java, it should not be too challenging to understand.

## About "Character" Packages in Modules

Each set of features is separated into different packages named after characters. While these packages could potentially
be developed into separate projects or services, this would be overkill for a small personal project. Nevertheless, this
may be considered in the future for the sake of experience.

- **Freya:** Library Management & Video Import
- **Linn:** Anime and Episode Database
- **Shizue:** Event Planner & Watchlist Manager
- **Toshiko:** Discord Bot
- **Chiya:** Web Application

# Contributing

At this stage, the project is not open for contributions due to frequent changes in the codebase and structure. However,
you're welcome to open issues for feature requests, which may be considered for implementation when time permits.

# Library File Structure

```
──/
 ├── automation/            # Root of Freya's import folder
 │   └── <anime>/           @ Folder of the anime to import
 │       └── <season>/      @ Folder of the season to import (containing MKV files)
 ├── content                # Root of Freya's imported content
 │   ├── animes/            # Folder containing all imported animes
 │   │   └── <anime>/       @ Folder of the imported anime
 │   │       └── <season>/  @ Folder of the imported season
 │   └── subtitles/         # Folder containing all imported subtitles
 │       └── <anime>/       @ Folder of the imported anime's subtitles
 │           └── <season>/  @ Folder of the imported season's subtitles
 └── torrents               # Temporary folder storing transmission downloads (before being moved to automation)
```
*`@` means that the folder is repeated in relation to the database/disk content*

```
content/<anime>/<season>/<episode>/<file>

content/0082/0001/0001/video.webm
content/0082/0001/0001/audio-fra.mp3
content/0082/0001/0001/audio-jpn.mp3
content/0082/0001/0001/subs-fra.ssa
content/0082/0001/0001/subs-fra.ssa
```