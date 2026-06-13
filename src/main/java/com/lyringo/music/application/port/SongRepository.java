package com.lyringo.music.application.port;

import com.lyringo.music.domain.model.Song;
import com.lyringo.music.domain.valueobject.SongId;
import java.util.List;
import java.util.Optional;

public interface SongRepository {
  Song save(Song song);

  Optional<Song> findById(SongId id);

  List<Song> search(String keyword, int limit, int offset);
}
