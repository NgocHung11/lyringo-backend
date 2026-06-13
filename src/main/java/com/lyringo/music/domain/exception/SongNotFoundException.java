package com.lyringo.music.domain.exception;

public class SongNotFoundException extends RuntimeException {
  public SongNotFoundException() {
    super("Song not found");
  }
}
