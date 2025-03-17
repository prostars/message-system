package net.prostars;

import java.util.Optional;

public interface BookRepository {
  Optional<Book> findBookByIsbn(String isbn);
}
