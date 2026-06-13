package com.lyringo.music.application.port;

import com.lyringo.music.domain.model.Album;
import com.lyringo.music.domain.valueobject.AlbumId;
import java.util.Optional;

public interface AlbumRepository {
  Album save(Album album);

  Optional<Album> findById(AlbumId id);
}
