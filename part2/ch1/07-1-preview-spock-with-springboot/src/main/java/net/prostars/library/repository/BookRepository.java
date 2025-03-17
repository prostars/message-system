package net.prostars.library.repository;

import java.util.Optional;
import net.prostars.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
  Optional<Book> findBookByIsbn(@NonNull String isbn);
}
