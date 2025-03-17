package net.prostars

import spock.lang.Specification

class LibraryServiceSpec extends Specification {
    PushService pushService = Stub()

    def "도서 이용 가능 여부를 확인한다."() {
        given:
        def bookRepository = Stub(BookRepository)
        bookRepository.findBookByIsbn(_ as String) >> Optional.of(new Book("1234", "Stub", true))

        def libraryService = new LibraryService(bookRepository, pushService)

        when:
        def isBookAvailable = libraryService.isBookAvailable("1234")

        then:
        isBookAvailable
    }

    def "대출 요청 시 도서 상태에 따른 처리 결과를 확인한다."() {
        given:
        def bookRepository = Stub(BookRepository)
        bookRepository.findBookByIsbn(_ as String) >> {
            bookExists ? Optional.of(new Book(isbn, title, available)) : Optional.empty()
        }
        def libraryService = new LibraryService(bookRepository, pushService)

        when:
        def borrowBook = libraryService.borrowBook(isbn)

        then:
        expected == borrowBook

        where:
        bookExists | isbn   | title      | available | expected
        true       | "1234" | "JUnit"    | true      | Optional.of("JUnit")
        true       | "5678" | "Mockito"  | false     | Optional.empty()
        false      | "9999" | "Not used" | true      | Optional.empty()
    }

    def "대출에 성공하면 알림이 발송되어야 한다."() {
        given:
        PushService pushServiceMock = Mock()

        def bookRepository = Mock(BookRepository)
        def libraryService = new LibraryService(bookRepository, pushServiceMock)

        when:
        def borrowBook = libraryService.borrowBook("1234")

        then:
        Optional.of("Stub") == borrowBook
        1 * pushServiceMock.notification(_ as String)
        1 * bookRepository.findBookByIsbn(_ as String) >> Optional.of(new Book("1234", "Stub", true))
    }

    def "도서 조회 중에 예외가 발생하면 대출 요청 시 예외를 던지다."() {
        def bookRepository = Stub(BookRepository)
        bookRepository.findBookByIsbn(_ as String) >> {
            throw new RuntimeException("Database error")
        }

        def libraryService = new LibraryService(bookRepository, pushService)

        when:
        libraryService.borrowBook("1234")

        then:
        thrown(RuntimeException)
    }

    def "Spy 테스트"() {
        given:
        def bookRepository = Stub(BookRepository)
        bookRepository.findBookByIsbn(_ as String) >> Optional.of(new Book("1234", "Spy", true))

        LibraryService libraryService = Spy(constructorArgs: [bookRepository, pushService]) {
            borrowBook(_ as String) >> Optional.of("Overridden Spy")
        }

        when:
        def isBookAvailable = libraryService.isBookAvailable("1234")
        def borrowBook = libraryService.borrowBook("1234")

        then:
        isBookAvailable
        Optional.of("Overridden Spy") == borrowBook
    }
}
