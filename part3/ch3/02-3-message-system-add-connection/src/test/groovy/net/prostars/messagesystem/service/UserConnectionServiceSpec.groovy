package net.prostars.messagesystem.service

import net.prostars.messagesystem.constant.UserConnectionStatus
import net.prostars.messagesystem.dto.domain.InviteCode
import net.prostars.messagesystem.dto.domain.User
import net.prostars.messagesystem.dto.domain.UserId
import net.prostars.messagesystem.dto.projection.UserConnectionStatusProjection
import net.prostars.messagesystem.repository.UserConnectionRepository
import org.springframework.data.util.Pair
import spock.lang.Specification

class UserConnectionServiceSpec extends Specification {

    UserConnectionService userConnectionService
    UserService userService = Stub()
    UserConnectionRepository userConnectionRepository = Stub()

    def setup() {
        userConnectionService = new UserConnectionService(userService, userConnectionRepository)
    }

    def "사용자 연결 신청에 대한 테스트."() {
        given:
        userService.getUser(inviteCodeOfTargetUser) >> Optional.of(new User(targetUserId, targetUsername))
        userService.getUsername(senderUserId) >> Optional.of(senderUsername)
        userConnectionRepository.findByPartnerAUserIdAndPartnerBUserId(_ as Long, _ as Long) >> {
            Optional.of(Stub(UserConnectionStatusProjection) {
                getStatus() >> beforeConnectionStatus.name()
            })
        }

        when:
        def result = userConnectionService.invite(senderUserId, usedInviteCode)

        then:
        result == expectedResult

        where:
        scenario              | senderUserId  | senderUsername | targetUserId  | targetUsername | inviteCodeOfTargetUser      | usedInviteCode                | beforeConnectionStatus            | expectedResult
        'Valid invite code'   | new UserId(1) | 'userA'        | new UserId(2) | 'userB'        | new InviteCode('user2code') | new InviteCode('user2code')   | UserConnectionStatus.NONE         | Pair.of(Optional.of(new UserId(2)), 'userA')
        'Already connected'   | new UserId(1) | 'userA'        | new UserId(2) | 'userB'        | new InviteCode('user2code') | new InviteCode('user2code')   | UserConnectionStatus.ACCEPTED     | Pair.of(Optional.of(new UserId(2)), 'Already connected with ' + targetUsername)
        'Already invited'     | new UserId(1) | 'userA'        | new UserId(2) | 'userB'        | new InviteCode('user2code') | new InviteCode('user2code')   | UserConnectionStatus.PENDING      | Pair.of(Optional.of(new UserId(2)), 'Already invited to ' + targetUsername)
        'Already rejected'    | new UserId(1) | 'userA'        | new UserId(2) | 'userB'        | new InviteCode('user2code') | new InviteCode('user2code')   | UserConnectionStatus.REJECTED     | Pair.of(Optional.of(new UserId(2)), 'Already invited to ' + targetUsername)
        'After disconnected'  | new UserId(1) | 'userA'        | new UserId(2) | 'userB'        | new InviteCode('user2code') | new InviteCode('user2code')   | UserConnectionStatus.DISCONNECTED | Pair.of(Optional.of(new UserId(2)), 'userA')
        'Invalid invite code' | new UserId(1) | 'userA'        | new UserId(2) | 'userB'        | new InviteCode('user2code') | new InviteCode('nobody code') | UserConnectionStatus.DISCONNECTED | Pair.of(Optional.empty(), 'Invalid invite code.')
        'Self invite'         | new UserId(1) | 'userA'        | new UserId(1) | 'userA'        | new InviteCode('user1code') | new InviteCode('user1code')   | UserConnectionStatus.DISCONNECTED | Pair.of(Optional.empty(), "Can't self invite.")
    }
}
