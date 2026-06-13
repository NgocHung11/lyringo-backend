package com.lyringo.music.domain.exception;

public class SongNotReadyException extends RuntimeException {
  public SongNotReadyException() {
    super("Song is not ready for playback");
  }
}
