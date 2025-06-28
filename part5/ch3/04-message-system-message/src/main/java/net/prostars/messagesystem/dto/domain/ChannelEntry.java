package net.prostars.messagesystem.dto.domain;

public record ChannelEntry(
    String title, MessageSeqId lastReadMessageSeqId, MessageSeqId lastChannelMessageSeqId) {}
