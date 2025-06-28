import net.prostars.messagesystem.dto.domain.*
import net.prostars.messagesystem.dto.websocket.inbound.FetchMessagesResponse
import net.prostars.messagesystem.dto.websocket.outbound.ReadMessageAck
import net.prostars.messagesystem.service.MessageService
import net.prostars.messagesystem.service.TerminalService
import net.prostars.messagesystem.service.UserService
import net.prostars.messagesystem.service.WebSocketService
import spock.lang.Specification

class ProcessFetchMessagesResponseSpec extends Specification {

    UserService userService
    MessageService messageService
    WebSocketService webSocketService = Mock()
    TerminalService terminalService = Mock()

    def setup() {
        userService = new UserService()
        messageService = new MessageService(userService, terminalService)
        messageService.setWebSocketService(webSocketService)
    }

    def '사용자가 입장한 채널과 다른 채널의 메시지를 받으면 무시한다.'() {
        given:
        userService.moveToChannel(new ChannelId(10))

        when:
        messageService.receiveMessage(new FetchMessagesResponse(new ChannelId(5), Collections.emptyList()))

        then:
        1 * terminalService.printSystemMessage(_)
    }

    def '버퍼에 저장된 메시지의 MessageSeqId가 수신한 누락 메시지들의 마지막 MessageSeqId와 차이가 1인 것을 수신 처리한다.'() {
        given:
        def user = new User(new UserId(3), 'bob')
        def channelId = new ChannelId(5)
        userService.moveToChannel(channelId)
        userService.setLastReadMessageSeqId(new MessageSeqId(7))
        userService.addMessage(new Message(channelId, new MessageSeqId(10), user.username(), '10 hi'))
        userService.addMessage(new Message(channelId, new MessageSeqId(11), user.username(), '11 hello'))
        userService.addMessage(new Message(channelId, new MessageSeqId(13), user.username(), '13 good'))

        when:
        messageService.receiveMessage(new FetchMessagesResponse(channelId, new ArrayList(List.of(
                new Message(channelId, new MessageSeqId(8), user.username(), '8 bad'),
                new Message(channelId, new MessageSeqId(9), user.username(), '9 bye')))))

        then:
        1 * terminalService.printMessage(user.username(), '8 bad')
        1 * terminalService.printMessage(user.username(), '9 bye')
        1 * terminalService.printMessage(user.username(), '10 hi')
        1 * terminalService.printMessage(user.username(), '11 hello')

        and:
        userService.getLastReadMessageSeqId().id() == 11
        userService.getBufferSize() == 1
    }

    def '수신한 누락 메시지가 버퍼에 저장된 메시지의 MessageSeqId와 중복이면 무시한다.'() {
        given:
        def user = new User(new UserId(3), 'bob')
        def channelId = new ChannelId(5)
        userService.moveToChannel(channelId)
        userService.setLastReadMessageSeqId(new MessageSeqId(8))
        userService.addMessage(new Message(channelId, new MessageSeqId(10), user.username(), '10 hi'))
        userService.addMessage(new Message(channelId, new MessageSeqId(11), user.username(), '11 hello'))
        userService.addMessage(new Message(channelId, new MessageSeqId(13), user.username(), '13 good'))

        when:
        messageService.receiveMessage(new FetchMessagesResponse(channelId, new ArrayList(List.of(
                new Message(channelId, new MessageSeqId(9), user.username(), '9 bad'),
                new Message(channelId, new MessageSeqId(10), user.username(), '10 ten')))))

        then:
        1 * terminalService.printMessage(user.username(), '9 bad')
        1 * terminalService.printMessage(user.username(), '10 hi')
        1 * terminalService.printMessage(user.username(), '11 hello')

        and:
        userService.getLastReadMessageSeqId().id() == 11
        userService.getBufferSize() == 1
    }

    def '버퍼에 저장된 메시지가 LastReadMessageSeqId보다 작거나 같으면 버린다.'() {
        given:
        def user = new User(new UserId(3), 'bob')
        def channelId = new ChannelId(5)
        userService.moveToChannel(channelId)
        userService.setLastReadMessageSeqId(new MessageSeqId(13))
        userService.addMessage(new Message(channelId, new MessageSeqId(10), user.username(), '10 hi'))
        userService.addMessage(new Message(channelId, new MessageSeqId(11), user.username(), '11 hello'))
        userService.addMessage(new Message(channelId, new MessageSeqId(13), user.username(), '13 good'))

        when:
        messageService.receiveMessage(new FetchMessagesResponse(channelId, new ArrayList(List.of(
                new Message(channelId, new MessageSeqId(8), user.username(), '9 bad'),
                new Message(channelId, new MessageSeqId(9), user.username(), '10 ten')))))

        then:
        0 * terminalService.printMessage(_)

        and:
        userService.getLastReadMessageSeqId().id() == 13
        userService.isBufferEmpty()
    }

    def '누락 메시지를 처리했으면 ack를 보내야 한다.'() {
        given:
        def user = new User(new UserId(3), 'bob')
        def channelId = new ChannelId(5)
        userService.moveToChannel(channelId)
        userService.setLastReadMessageSeqId(new MessageSeqId(8))
        def expectMessageReadAckRequest = [
                new ReadMessageAck(userService.getChannelId(), new MessageSeqId(9)),
                new ReadMessageAck(userService.getChannelId(), new MessageSeqId(10))]
        def params = []

        when:
        messageService.receiveMessage(new FetchMessagesResponse(channelId, new ArrayList(List.of(
                new Message(channelId, new MessageSeqId(9), user.username(), '9 bad'),
                new Message(channelId, new MessageSeqId(10), user.username(), '10 ten')))))

        then:
        2 * webSocketService.sendMessage(_) >> { List args -> params.add(args[0]) }
        params[0] == expectMessageReadAckRequest[0]
        params[1] == expectMessageReadAckRequest[1]
    }
}
