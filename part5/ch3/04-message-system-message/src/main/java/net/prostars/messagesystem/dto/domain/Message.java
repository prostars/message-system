package net.prostars.messagesystem.dto.domain;

public record Message(
    ChannelId channelId, MessageSeqId messageSeqId, String username, String content) {}
