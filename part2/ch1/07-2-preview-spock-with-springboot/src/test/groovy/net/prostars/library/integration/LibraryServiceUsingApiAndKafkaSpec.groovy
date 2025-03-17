package net.prostars.library.integration

import net.prostars.library.entity.Book
import net.prostars.library.repository.BookRepository
import net.prostars.library.service.PushService
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.spockframework.spring.SpringBean
import org.spockframework.spring.SpringSpy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spock.lang.Specification

import java.time.Duration

@SpringBootTest
@AutoConfigureMockMvc
@EmbeddedKafka(topics = ["push.notification"],
        ports = [9092])
class LibraryServiceUsingApiAndKafkaSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker

    @SpringSpy
    private PushService pushService

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

        and:
        0 * pushService.notification(_)
    }

    def '대출 요청 시 도서 상태에 따른 처리 결과를 확인한다'() {
        given:
        bookRepository.findBookByIsbn(_ as String) >> { bookExists ? Optional.of(new Book(isbn, title, available)) : Optional.empty() }
        def testTopic = "push.notification"
        Map<String, Object> configs = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafkaBroker)
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer)
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer)
        def consumer = new DefaultKafkaConsumerFactory<String, String>(configs).createConsumer()
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, true, testTopic)

        when:
        def response = mockMvc.perform(MockMvcRequestBuilders.post("/api/books/${isbn}/borrow"))

        then:
        response.andExpect(MockMvcResultMatchers.status().is(expectedStatus))
                .andExpect(MockMvcResultMatchers.content().string(expectedBody))

        ConsumerRecord<String, String> consumerRecord = null
        try {
            consumerRecord = KafkaTestUtils.getSingleRecord(consumer, testTopic, Duration.ofSeconds(1))
        } catch (IllegalStateException ignored) {
        }

        consumerRecord?.value() == expectedMessage

        cleanup:
        consumer.close()

        where:
        bookExists | isbn   | title      | available | expectedStatus | expectedBody   | expectedMessage
        true       | '1234' | 'Spock'    | true      | 200            | "1234 : Spock" | "대출 완료 : Spock"
        true       | '5678' | 'Mockito'  | false     | 200            | "5678 : 대출 불가" | null
        false      | '9999' | 'Not Used' | true      | 200            | "9999 : 대출 불가" | null
    }
}
