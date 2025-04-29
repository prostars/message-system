package net.prostars.messagesystem.dto

import com.fasterxml.jackson.databind.ObjectMapper
import net.prostars.messagesystem.dto.websocket.inbound.*
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
        payload                                                                        | expectedClass              | validate
        '{"type": "FETCH_USER_INVITECODE_REQUEST"}'                                    | FetchUserInvitecodeRequest | { req -> (req as FetchUserInvitecodeRequest).type == 'FETCH_USER_INVITECODE_REQUEST' }
        '{"type": "FETCH_CONNECTIONS_REQUEST", "status": "ACCEPTED"}'                  | FetchConnectionsRequest    | { req -> (req as FetchConnectionsRequest).status.name() == 'ACCEPTED' }
        '{"type": "INVITE_REQUEST", "userInviteCode": "TestInviteCode123"}'            | InviteRequest              | { req -> (req as InviteRequest).userInviteCode.code() == 'TestInviteCode123' }
        '{"type": "ACCEPT_REQUEST", "username": "testuser"}'                           | AcceptRequest              | { req -> (req as AcceptRequest).username == 'testuser' }
        '{"type": "REJECT_REQUEST", "username": "testuser"}'                           | RejectRequest              | { req -> (req as RejectRequest).username == 'testuser' }
        '{"type": "DISCONNECT_REQUEST", "username": "testuser"}'                       | DisconnectRequest          | { req -> (req as DisconnectRequest).username == 'testuser' }
        '{"type": "WRITE_MESSAGE", "username": "testuser", "content": "test message"}' | WriteMessage               | { req -> (req as WriteMessage).getContent() == 'test message' }
        '{"type": "KEEP_ALIVE"}'                                                       | KeepAlive                  | { req -> (req as KeepAlive).getType() == 'KEEP_ALIVE' }
    }
}
