package net.prostars;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LibraryServiceTest {

  private final PushService pushService = mock(PushService.class);

  @Test
  @DisplayName("도서 이용 가능 여부를 확인한다.")
  void isBookAvailable() {
    BookRepository bookRepository = mock(BookRepository.class);
    when(bookRepository.findBookByIsbn(anyString()))
        .thenReturn(Optional.of(new Book("1234", "Stub", true)));

    LibraryService libraryService = new LibraryService(bookRepository, pushService);

    boolean bookAvailable = libraryService.isBookAvailable("1234");

    assertTrue(bookAvailable);
  }
}
