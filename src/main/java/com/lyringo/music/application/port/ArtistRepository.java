package com.lyringo.music.application.port;

import com.lyringo.music.domain.model.Artist;
import com.lyringo.music.domain.valueobject.ArtistId;
import java.util.Optional;

public interface ArtistRepository {
  Artist save(Artist artist);

  Optional<Artist> findById(ArtistId id);
}
