package com.lyringo.music.application.usecase;

import com.lyringo.music.application.dto.SongDto;
import com.lyringo.music.application.port.SongRepository;
import com.lyringo.music.domain.exception.SongNotFoundException;
import com.lyringo.music.domain.valueobject.SongId;

public class GetSongDetailUseCase {
  private final SongRepository songRepository;

  public GetSongDetailUseCase(SongRepository songRepository) {
    this.songRepository = songRepository;
  }

  public SongDto execute(SongId songId) {
    return songRepository
        .findById(songId)
        .map(SongDto::fromDomain)
        .orElseThrow(SongNotFoundException::new);
  }
}
