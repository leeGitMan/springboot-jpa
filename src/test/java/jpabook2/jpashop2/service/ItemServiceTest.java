package jpabook2.jpashop2.service;

import jpabook2.jpashop2.domain.Item.Album;
import jpabook2.jpashop2.domain.Item.Book;
import jpabook2.jpashop2.domain.Item.Item;
import jpabook2.jpashop2.exception.NotEnoughStockException;
import jpabook2.jpashop2.repository.ItemRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
class ItemServiceTest {

    @Autowired
    ItemRepository ir;

    @Autowired
    ItemService is;


    @Test
    @Rollback(value = false)
    public void 아이템_등록() throws Exception{
        // given
        Book item = new Book();
        item.setName("item1");

        // when
        is.saveItem(item);

        // then
        if(item.getId() != 0){
            System.out.println("성공");
        }else{
            System.out.println("실패");
        }
    }

    @Test
    @Rollback(value = false)
    public void 아이템_조회() throws Exception{
        // given
        Book book = new Book();
        book.setName("이현경책");
        is.saveItem(book);

        // when
        ir.findOne(book.getId());

        // then
        if(book.getId() != 0){
            System.out.println("단건 조회 성공");
        }else{
            System.out.println("단건 조회 실패");
        }
    }

    @Test
    @Rollback(value = false)
    public void 여러건_조회() throws Exception{
        // given
        Album a = new Album();
        a.setName("BTS");

        Album b = new Album();
        b.setName("STB");

        // when
        List<Item> findAll = ir.findAll();

        // then
        if (!findAll.isEmpty()) {
            System.out.println("여러 건 조회 성공");
        }else{
            System.out.println("여러 건 조회 실패");
        }
    }


    @Test
    public void 재고_증가() throws Exception{
        // given
        Book b = new Book();
        b.setName("이현경 책입니다.");
        is.saveItem(b);

        // when
        b.addStock(1);

        // then
        if (b.getStockQuantity() > 0) {
            System.out.println("재고 증가 성공");
        }else{
            System.out.println("재고 증가 실패");
        }
    }

    @Test
    public void 재고_감소() throws Exception{
        // given
        Book b = new Book();
        b.setName("이현경 책입니다.");
        is.saveItem(b);
        b.addStock(1);
        // when

        try{
            b.removeStock(2);
        }catch (NotEnoughStockException e){
            System.out.println("NotEnoughStockException !!!!!");
            return;
        }

        // then
        Assert.fail("여기오면 안돼");
    }






}