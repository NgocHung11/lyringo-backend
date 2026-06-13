package com.lyringo.music.application.port;

import java.time.Duration;

public interface StorageService {
  SignedStorageUrl createUploadUrl(String storageKey, String contentType, Duration expiresIn);

  SignedStorageUrl createPlaybackUrl(String storageKey, Duration expiresIn);
}
