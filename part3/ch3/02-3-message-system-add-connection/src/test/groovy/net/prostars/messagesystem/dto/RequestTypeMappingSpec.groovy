package net.prostars.messagesystem.dto

import com.fasterxml.jackson.databind.ObjectMapper
import net.prostars.messagesystem.dto.websocket.inbound.BaseRequest
import net.prostars.messagesystem.dto.websocket.inbound.InviteRequest
import net.prostars.messagesystem.dto.websocket.inbound.KeepAliveRequest
import net.prostars.messagesystem.dto.websocket.inbound.WriteMessageRequest
import net.prostars.messagesystem.json.JsonUtil
import spock.lang.Specification

class RequestTypeMappingSpec extends Specification {

    JsonUtil jsonUtil = new JsonUtil(new ObjectMapper())

    def "DTO 형식의 JSON 문자열을 해당 타입의 DTO로 변환할 수 있다."() {
        given:
        String jsonBody = payload

        when:
        BaseRequest request = jsonUtil.fromJson(jsonBody, BaseRequest).get()

        then:
        request.getClass() == expectedClass
        validate(request)

        where:
        payload                                                                        | expectedClass       | validate
        '{"type": "INVITE_REQUEST", "userInviteCode": "TestInviteCode123"}'            | InviteRequest       | { req -> (req as InviteRequest).userInviteCode.code() == 'TestInviteCode123' }
        '{"type": "WRITE_MESSAGE", "username": "testuser", "content": "test message"}' | WriteMessageRequest | { req -> (req as WriteMessageRequest).getContent() == 'test message' }
        '{"type": "KEEP_ALIVE"}'                                                       | KeepAliveRequest    | { req -> (req as KeepAliveRequest).getType() == 'KEEP_ALIVE' }
    }
}
