import net.prostars.messagesystem.dto.domain.ChannelId
import net.prostars.messagesystem.dto.domain.Message
import net.prostars.messagesystem.dto.domain.MessageSeqId
import net.prostars.messagesystem.dto.domain.User
import net.prostars.messagesystem.dto.domain.UserId
import net.prostars.messagesystem.dto.websocket.inbound.MessageNotification
import net.prostars.messagesystem.dto.websocket.outbound.FetchMessagesRequest
import net.prostars.messagesystem.dto.websocket.outbound.ReadMessageAck
import net.prostars.messagesystem.service.MessageService
import net.prostars.messagesystem.service.TerminalService
import net.prostars.messagesystem.service.UserService
import net.prostars.messagesystem.service.WebSocketService
import spock.lang.Specification

class ProcessMessageNotificationSpec extends Specification {

    UserService userService
    MessageService messageService
    WebSocketService webSocketService = Mock()
    TerminalService terminalService = Mock()

    def setup() {
        userService = new UserService()
        messageService = new MessageService(userService, terminalService)
        messageService.setWebSocketService(webSocketService)
    }

    def '채널에 입장하지 않은 상태에서 받은 메시지는 무시한다.'() {
        given:
        def channelId = new ChannelId(5)
        userService.moveToLobby()

        when:
        messageService.receiveMessage(new MessageNotification(channelId, new MessageSeqId(100), 'bob', 'hello.'))

        then:
        0 * terminalService.printMessage(_)
        0 * webSocketService.sendMessage(_)
    }

    def 'UserService는 참여 중인 채널에서 마지막으로 받은 메시지의 MessageSeqId를 가지고 있다.'() {
        given:
        def channelId = new ChannelId(5)
        def messageSeqId = new MessageSeqId(100)
        userService.moveToChannel(channelId)

        when:
        messageService.receiveMessage(new MessageNotification(channelId, messageSeqId, 'bob', 'hello.'))

        then:
        userService.getLastReadMessageSeqId() == messageSeqId
    }
    
    def '새로 받은 메시지의 MessageSeqId가 이전에 받은 메시지의 MessageSeqId보다 정확히 1이 크다면, ack를 보내야 한다.'() {
        given:
        def channelId = new ChannelId(5)
        userService.moveToChannel(channelId)
        userService.setLastReadMessageSeqId(new MessageSeqId(100))
        def messageSeqId = new MessageSeqId(101)
        def expectMessageReadAckRequest = new ReadMessageAck(channelId, messageSeqId)
        
        when:
        messageService.receiveMessage(new MessageNotification(channelId, messageSeqId, 'bob', 'hello.'))
        
        then:
        1 * webSocketService.sendMessage(expectMessageReadAckRequest)
    }
    
    def '새로 받은 메시지의 MessageSeqId가 이전에 받은 메시지의 MessageSeqId보다 2 이상 크다면 누락된 메시지를 요청해야 한다.'() {
        given:
        def channelId = new ChannelId(5)
        userService.moveToChannel(channelId)
        userService.setLastReadMessageSeqId(new MessageSeqId(100))
        def messageSeqId = new MessageSeqId(102)
        def expectFetchMessagesRequest = null
        
        when:
        messageService.receiveMessage(new MessageNotification(channelId, messageSeqId, 'bob', 'hello.'))
        def fetchMessagesRequest = new FetchMessagesRequest(channelId, new MessageSeqId(userService.getLastReadMessageSeqId().id() + 1), new MessageSeqId(messageSeqId.id() - 1))
        sleep(200)
        
        then:
        1 * webSocketService.sendMessage(_) >> { List args -> expectFetchMessagesRequest = args[0] }
        expectFetchMessagesRequest == fetchMessagesRequest
        
        and:
        userService.peekMessage().messageSeqId() == messageSeqId
    }
    
    def '새로 받은 메시지의 MessageSeqId가 이전에 받은 메시지의 MessageSeqId와 같거나 작으면 무시한다.'() {
        given:
        def channelId = new ChannelId(5)
        userService.moveToChannel(channelId)
        userService.setLastReadMessageSeqId(new MessageSeqId(100))
        def messageSeqId = new MessageSeqId(100)
        
        when:
        messageService.receiveMessage(new MessageNotification(channelId, messageSeqId, 'bob', 'hello.'))
        
        then:
        1 * terminalService.printSystemMessage(_)
        0 * terminalService.printMessage(_)
        0 * webSocketService.sendMessage(_)
    }
    
    def '버퍼에 저장된 메시지가 있으면 처리한다.'() {
        given:
        def channelId = new ChannelId(5)
        def user = new User(new UserId(3), 'bob')
        userService.moveToChannel(channelId)
        userService.setLastReadMessageSeqId(new MessageSeqId(8))
        userService.addMessage(new Message(channelId, new MessageSeqId(10), user.username(), '10 hi'))
        userService.addMessage(new Message(channelId, new MessageSeqId(11), user.username(), '11 hello'))
        userService.addMessage(new Message(channelId, new MessageSeqId(13), user.username(), '13 good'))

        when:
        messageService.receiveMessage(new MessageNotification(channelId, new MessageSeqId(9), 'bob', '9 hello.'))

        then:
        1 * terminalService.printMessage(_, '9 hello.')
        1 * terminalService.printMessage(_, '10 hi')
        1 * terminalService.printMessage(_, '11 hello')
    }
}
