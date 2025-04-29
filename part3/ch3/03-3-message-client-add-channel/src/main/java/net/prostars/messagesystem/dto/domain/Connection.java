package net.prostars.messagesystem.dto.domain;

import net.prostars.messagesystem.constant.UserConnectionStatus;

public record Connection(String username, UserConnectionStatus status) {}
