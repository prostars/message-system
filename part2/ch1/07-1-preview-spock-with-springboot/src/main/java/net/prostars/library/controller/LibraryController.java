package net.prostars.library.controller;

import java.util.Optional;
import net.prostars.library.service.LibraryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@SuppressWarnings("unused")
public class LibraryController {

  private final LibraryService libraryService;

  public LibraryController(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  @GetMapping("/books/{isbn}/availability")
  public ResponseEntity<String> isBookAvailable(@PathVariable String isbn) {
    return ResponseEntity.ok(
        String.format("%s : 대출 %s", isbn, libraryService.isBookAvailable(isbn) ? "가능" : "불가"));
  }

  @PostMapping("/books/{isbn}/borrow")
  public ResponseEntity<String> borrowBook(@PathVariable String isbn) {
    Optional<String> borrowedBook = libraryService.borrowBook(isbn);
    return borrowedBook
        .map(title -> ResponseEntity.ok(String.format("%s : %s", isbn, title)))
        .orElse(ResponseEntity.ok(String.format("%s : 대출 불가", isbn)));
  }
}
