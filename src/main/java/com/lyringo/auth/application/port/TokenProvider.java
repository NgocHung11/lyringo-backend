package com.lyringo.auth.application.port;

import com.lyringo.auth.domain.valueobject.AuthSessionId;
import com.lyringo.shared.domain.valueobject.UserId;

public interface TokenProvider {
  TokenPair issueTokens(UserId userId, AuthSessionId sessionId);
}
