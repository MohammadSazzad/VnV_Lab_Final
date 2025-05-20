import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.Book;
import com.Library;
import com.Member;

import java.util.Arrays;

public class LibraryTests {

    @Nested
    @DisplayName("Book Tests")
    class BookTests {
        private Book book;

        @BeforeEach
        void setUp() {
            book = new Book("1984", "Orwell");
        }

        @Test
        @DisplayName("Borrowing a Book sets isBorrowed to true")
        void testBorrowBook() {
            assertFalse(book.isBorrowed(), "Book should not be borrowed initially");
            book.borrow();
            assertTrue(book.isBorrowed(), "Book should be borrowed after calling borrow()");
        }

        @Test
        @DisplayName("Borrowing an already borrowed book throws")
        void testBorrowAlreadyBorrowed() {
            book.borrow();
            IllegalStateException ex = assertThrows(IllegalStateException.class, () -> book.borrow());
            assertEquals("Book already borrowed", ex.getMessage());
        }

        @Test
        @DisplayName("Returning a borrowed book sets isBorrowed to false")
        void testReturnBook() {
            book.borrow();
            assertTrue(book.isBorrowed());
            book.returnBook();
            assertFalse(book.isBorrowed(), "Book should not be borrowed after returning");
        }

        @Test
        @DisplayName("Returning a book that was not borrowed throws")
        void testReturnNotBorrowed() {
            IllegalStateException ex = assertThrows(IllegalStateException.class, () -> book.returnBook());
            assertEquals("Book was not borrowed", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Member Tests")
    class MemberTests {
        private Member member;

        @BeforeEach
        void setUp() {
            member = new Member("Alice");
        }

        @Test
        @DisplayName("Member can borrow up to 3 books")
        void testBorrowLimit() {
            for (int i = 1; i <= 3; i++) {
                member.borrowBook();
                assertEquals(i, member.getBorrowedBooks(), "Borrowed books count should match");
            }
        }

        @Test
        @DisplayName("Member exceeding borrow limit throws")
        void testExceedBorrowLimit() {
            for (int i = 0; i < 3; i++) {
                member.borrowBook();
            }
            IllegalStateException ex = assertThrows(IllegalStateException.class, () -> member.borrowBook());
            assertEquals("Cannot borrow more than 3 books", ex.getMessage());
        }

        @Test
        @DisplayName("Returning a book decreases count")
        void testReturnBookByMember() {
            member.borrowBook();
            assertEquals(1, member.getBorrowedBooks());
            member.returnBook();
            assertEquals(0, member.getBorrowedBooks());
        }

        @Test
        @DisplayName("Returning with none borrowed throws")
        void testReturnWhenNoneBorrowed() {
            IllegalStateException ex = assertThrows(IllegalStateException.class, () -> member.returnBook());
            assertEquals("No books to return", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Library Tests")
    class LibraryAndUtilsTests {
        private Library library;
        private Book book1, book2;

        @BeforeEach
        void setUp() {
            library = new Library();
            book1 = new Book("1984", "Orwell");
            book2 = new Book("Brave New World", "Huxley");
            library.addBook(book1);
            library.addBook(book2);
            library.registerMember(new Member("Alice"));
        }

        @Test
        @DisplayName("findBook returns correct book or null")
        void testFindBook() {
            assertEquals(book1, library.findBook("1984"));
            assertEquals(book1, library.findBook("1984".toLowerCase()));
            assertNull(library.findBook("Nonexistent"));
        }

        @Test
        @DisplayName("isBookAvailable returns true only when not borrowed and exists")
        void testIsBookAvailable() {
            assertTrue(library.isBookAvailable("1984"));
            book1.borrow();
            assertFalse(library.isBookAvailable("1984"));
            assertFalse(library.isBookAvailable("Unknown"));
        }

        @Test
        @DisplayName("countAvailableBooks accurately counts non-borrowed books")
        void testCountAvailableBooks() {
            assertEquals(2, LibraryUtils.countAvailableBooks(library));
            book1.borrow();
            assertEquals(1, LibraryUtils.countAvailableBooks(library));
            book2.borrow();
            assertEquals(0, LibraryUtils.countAvailableBooks(library));
        }
    }
}
