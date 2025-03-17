package net.prostars.library.integration

import net.prostars.library.entity.Book
import net.prostars.library.repository.BookRepository
import net.prostars.library.service.LibraryService
import net.prostars.library.service.PushService
import org.spockframework.spring.EnableSharedInjection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import spock.lang.Shared
import spock.lang.Specification

@DataJpaTest
@EnableSharedInjection
class LibraryServiceUsingDBSpec extends Specification {

    @Shared
    @Autowired
    private BookRepository bookRepository

    private final PushService pushService = Stub()

    def setupSpec() {
        bookRepository.save(new Book('1234', 'Spock', true))
        bookRepository.save(new Book('5678', 'Mockito', false))
        bookRepository.flush()
    }

    def '도서 이용 가능 여부를 확인한다'() {
        given: 'isbn이 1234인 책을 있다.'
        def library = new LibraryService(bookRepository, pushService)

        when: '존재하는 도서의 이용 가능 여부를 확인하면'
        def result = library.isBookAvailable('1234')

        then: '이용이 가능해야 한다'
        result
    }

    def '대출 요청 시 도서 상태에 따른 처리 결과를 확인한다'() {
        given:
        def library = new LibraryService(bookRepository, pushService)

        when:
        def result = library.borrowBook(isbn)

        then:
        result == expected

        where:
        isbn   | expected
        '1234' | Optional.of('Spock')
        '5678' | Optional.empty()
        '9999' | Optional.empty()
    }
}
