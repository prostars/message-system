package net.prostars.messagesystem.dto.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public record MessageSeqId(@JsonValue Long id) {

  public MessageSeqId {
    if (id == null || id <= 0) {
      throw new IllegalArgumentException("Invalid MessageSeqId");
    }
  }
}
