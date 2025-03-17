package net.prostars.library.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class Book {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String isbn;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private boolean available;

  public Book() {}

  public Book(String isbn, String title, boolean available) {
    this.isbn = isbn;
    this.title = title;
    this.available = available;
  }

  public Long getId() {
    return id;
  }

  public String getIsbn() {
    return isbn;
  }

  public String getTitle() {
    return title;
  }

  public boolean isAvailable() {
    return available;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Book book = (Book) o;
    return Objects.equals(id, book.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    return "Book{id=%d, isbn='%s', title='%s', available=%s}".formatted(id, isbn, title, available);
  }
}
