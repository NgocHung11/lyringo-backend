package com.lyringo.music.application.command;

import java.util.UUID;

public record CreateSongCommand(
    String title, UUID artistId, UUID albumId, String language, UUID createdBy) {}
