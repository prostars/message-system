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
}
