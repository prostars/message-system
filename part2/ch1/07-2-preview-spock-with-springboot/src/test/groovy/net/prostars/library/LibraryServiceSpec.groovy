package net.prostars.library

import net.prostars.library.entity.Book
import net.prostars.library.repository.BookRepository
import net.prostars.library.service.LibraryService
import net.prostars.library.service.PushService
import spock.lang.Specification

class LibraryServiceSpec extends Specification {

    private final PushService pushService = Stub()

    def '도서 이용 가능 여부를 확인한다'() {
        given: 'isbn이 1234인 책을 있다.'
        BookRepository bookRepository = Stub()
        bookRepository.findBookByIsbn(_ as String) >> Optional.of(new Book('1234', 'single', true))
        def library = new LibraryService(bookRepository, pushService)

        when: '존재하는 도서의 이용 가능 여부를 확인하면'
        def result = library.isBookAvailable('1234')

        then: '이용이 가능해야 한다'
        result
    }

    def '대출 요청 시 도서 상태에 따른 처리 결과를 확인한다'() {
        given:
        BookRepository bookRepository = Stub() {
            findBookByIsbn(_ as String) >> {
                bookExists ? Optional.of(new Book(isbn, title, available)) : Optional.empty()
            }
        }
        def library = new LibraryService(bookRepository, pushService)

        when:
        def result = library.borrowBook(isbn)

        then:
        result == expected

        where:
        bookExists | isbn   | title      | available | expected
        true       | '1234' | 'Spock'    | true      | Optional.of('Spock')
        true       | '5678' | 'Mockito'  | false     | Optional.empty()
        false      | '9999' | 'Not Used' | true      | Optional.empty()
    }

    def '대출에 성공하면 알림이 발송되어야 한다.'() {
        given:
        PushService pushServiceMock = Mock()
        BookRepository bookRepository = Mock(BookRepository)
        bookRepository.findBookByIsbn(_ as String) >> Optional.of(new Book('1234', 'single', true))
        def library = new LibraryService(bookRepository, pushServiceMock)

        when:
        def result = library.borrowBook('1234')

        then:
        'single' == result.get()
        1 * pushServiceMock.notification(_ as String)
    }


    def '도서 조회 중 예외가 발생하면 대출 요청 시 예외를 던진다'() {
        given:
        BookRepository bookRepository = Stub() {
            findBookByIsbn('error') >> { throw new RuntimeException('Repository error') }
        }
        def library = new LibraryService(bookRepository, pushService)

        when:
        library.borrowBook('error')

        then:
        thrown(RuntimeException)
    }

    def 'spy 테스트'() {
        given:
        BookRepository repo = Stub(BookRepository) {
            findBookByIsbn('1234') >> Optional.of(new Book('1234', 'Spock Spy', true))
        }
        LibraryService libraryService = Spy(constructorArgs: [repo, pushService]) {
            borrowBook(_ as String) >> Optional.of('Overridden borrowBook')
        }

        when:
        def isAvailable = libraryService.isBookAvailable('1234')
        def result = libraryService.borrowBook('1234')

        then:
        isAvailable
        result.get() == 'Overridden borrowBook'
    }
}
