package net.prostars.messagesystem.handler.kafka;


import net.prostars.messagesystem.dto.kafka.RecordInterface;

public interface BaseRecordHandler<T extends RecordInterface> {
  void handleRecord(T record);
}
