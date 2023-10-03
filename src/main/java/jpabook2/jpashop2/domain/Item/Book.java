package jpabook2.jpashop2.domain.Item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@DiscriminatorValue("B")
public class Book extends Item{

    private String author;
    private String isbn;


    public Book createBook(String name, int price, int quantity, String author
            , String isbn) {

        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(quantity);
        book.setAuthor(author);
        book.setIsbn(isbn);
        return book;
    }



}
