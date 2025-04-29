package net.prostars.messagesystem.integration

import net.prostars.messagesystem.MessageSystemApplication
import net.prostars.messagesystem.dto.domain.UserId
import net.prostars.messagesystem.repository.UserRepository
import net.prostars.messagesystem.service.UserConnectionLimitService
import net.prostars.messagesystem.service.UserConnectionService
import net.prostars.messagesystem.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import static java.util.Collections.synchronizedList

@SpringBootTest(classes = MessageSystemApplication)
class UserConnectionServiceSpec extends Specification {

    @Autowired
    UserService userService

    @Autowired
    UserConnectionService userConnectionService

    @Autowired
    UserConnectionLimitService userConnectionLimitService

    @Autowired
    UserRepository userRepository

    def "연결 요청 수락은 연결 제한 수를 넘을 수 없다."() {
        given:
        userConnectionLimitService.setLimitConnections(10)
        (0..19).collect { userService.addUser("testuser${it}", "testpass${it}") }
        def userIdA = userService.getUserId("testuser0").get()
        def inviteCodeA = userService.getInviteCode(userIdA).get()
        (1..9).collect {
            userConnectionService.invite(userService.getUserId("testuser${it}").get(), inviteCodeA)
            userConnectionService.accept(userIdA, "testuser${it}")
        }
        def inviteCodes = (10..19).collect {
            userService.getInviteCode(userService.getUserId("testuser${it}").get()).get()
        }
        inviteCodes.each { userConnectionService.invite(userIdA, it) }
        def results = synchronizedList(new ArrayList<Optional<UserId>>())

        when:
        def threads = (10..19).collect { idx ->
            Thread.start {
                def userId = userService.getUserId("testuser${idx}")
                results << userConnectionService.accept(userId.get(), "testuser0").getFirst()
            }
        }
        threads*.join()

        then:
        results.count { it.isPresent() } == 1

        cleanup:
        (0..19).each {
            def userId = userService.getUserId("testuser${it}").get()
            userRepository.deleteById(userId.id())
        }
    }
}
