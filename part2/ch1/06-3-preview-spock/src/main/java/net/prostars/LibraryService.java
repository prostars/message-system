package net.prostars;

import java.util.Optional;

public class LibraryService {
  private final BookRepository bookRepository;
  private final PushService pushService;

  public LibraryService(BookRepository bookRepository, PushService pushService) {
    this.bookRepository = bookRepository;
    this.pushService = pushService;
  }

  public boolean isBookAvailable(String isbn) {
    Optional<Book> book = bookRepository.findBookByIsbn(isbn);
    return book.map(Book::available).orElse(false);
  }

  public Optional<String> borrowBook(String isbn) {
    return bookRepository
        .findBookByIsbn(isbn)
        .filter(Book::available)
        .map(
            book -> {
              pushService.notification("대출 완료: " + book.title());
              return book.title();
            });
  }
}
