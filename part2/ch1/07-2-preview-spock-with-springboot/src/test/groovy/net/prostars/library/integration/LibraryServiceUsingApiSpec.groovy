package net.prostars.library.integration

import net.prostars.library.entity.Book
import net.prostars.library.repository.BookRepository
import net.prostars.library.service.PushService
import org.spockframework.spring.SpringBean
import org.spockframework.spring.StubBeans
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spock.lang.Specification

@SpringBootTest
@AutoConfigureMockMvc
@StubBeans([PushService])
class LibraryServiceUsingApiSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    @SpringBean
    private final BookRepository bookRepository = Stub()

    def '도서 이용 가능 여부를 확인한다'() {
        given: 'isbn이 1234인 책을 있다.'
        bookRepository.findBookByIsbn(_ as String) >> Optional.of(new Book('1234', 'single', true))

        when: '존재하는 도서의 이용 가능 여부를 확인하면'
        def response = mockMvc.perform(MockMvcRequestBuilders.get("/api/books/1234/availability"))

        then: '이용이 가능해야 한다'
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("1234 : 대출 가능"))
    }

    def '대출 요청 시 도서 상태에 따른 처리 결과를 확인한다'() {
        given:
        bookRepository.findBookByIsbn(_ as String) >> { bookExists ? Optional.of(new Book(isbn, title, available)) : Optional.empty() }

        when:
        def response = mockMvc.perform(MockMvcRequestBuilders.post("/api/books/${isbn}/borrow"))

        then:
        response.andExpect(MockMvcResultMatchers.status().is(expectedStatus))
                .andExpect(MockMvcResultMatchers.content().string(expectedBody))

        where:
        bookExists | isbn   | title      | available | expectedStatus | expectedBody
        true       | '1234' | 'Spock'    | true      | 200            | "1234 : Spock"
        true       | '5678' | 'Mockito'  | false     | 200            | "5678 : 대출 불가"
        false      | '9999' | 'Not Used' | true      | 200            | "9999 : 대출 불가"
    }
}
