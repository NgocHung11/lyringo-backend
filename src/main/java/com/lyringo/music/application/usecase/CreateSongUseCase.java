package com.lyringo.music.application.usecase;

import com.lyringo.music.application.command.CreateSongCommand;
import com.lyringo.music.application.dto.SongDto;
import com.lyringo.music.application.port.ArtistRepository;
import com.lyringo.music.application.port.SongRepository;
import com.lyringo.music.domain.model.Song;
import com.lyringo.music.domain.valueobject.AlbumId;
import com.lyringo.music.domain.valueobject.ArtistId;
import com.lyringo.shared.domain.valueobject.UserId;

public class CreateSongUseCase {
  private final SongRepository songRepository;
  private final ArtistRepository artistRepository;

  public CreateSongUseCase(SongRepository songRepository, ArtistRepository artistRepository) {
    this.songRepository = songRepository;
    this.artistRepository = artistRepository;
  }

  public SongDto execute(CreateSongCommand command) {
    ArtistId artistId = new ArtistId(command.artistId());
    artistRepository
        .findById(artistId)
        .orElseThrow(() -> new IllegalArgumentException("Artist does not exist"));

    AlbumId albumId = command.albumId() == null ? null : new AlbumId(command.albumId());
    UserId createdBy = new UserId(command.createdBy());

    Song song = Song.create(command.title(), artistId, albumId, command.language(), createdBy);
    return SongDto.fromDomain(songRepository.save(song));
  }
}
