package com.lyringo.users.application.command;

public record CreateUserCommand(String email, String username, String displayName) {}
