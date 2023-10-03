package jpabook2.jpashop2.service;

import jakarta.persistence.EntityManager;
import jpabook2.jpashop2.domain.Address;
import jpabook2.jpashop2.domain.Item.Book;
import jpabook2.jpashop2.domain.Item.Item;
import jpabook2.jpashop2.domain.Member;
import jpabook2.jpashop2.domain.Order;
import jpabook2.jpashop2.domain.OrderStatus;
import jpabook2.jpashop2.exception.NotEnoughStockException;
import jpabook2.jpashop2.repository.OrderRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.parser.Entity;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired OrderService os;

    @Autowired
    OrderRepository or;





    @Test
    public void 상품주문() throws Exception{
        // given
        Member member = createMember();


        Book book = createOrder();

        int orderCount = 2;


        // when
        Long orderId = os.order(member.getId(), book.getId(), orderCount);


        // then
        Order getOrder = or.findOne(orderId);

        Assertions.assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "상품 주문 시 상태는 ORDER");
        Assertions.assertEquals(1, getOrder.getOrderItems().size(), "주문한 상품 종류 수가 정확해야 한다.");
        Assertions.assertEquals(10000 * orderCount, getOrder.getTotalPrice(), "주문 가격은 가격 * 수량이다");
        Assertions.assertEquals(8, book.getStockQuantity(), "주문 수량만큼 재고가 줄어야 한다.");
    }




    @Test
    public void 주문취소() throws Exception{
        // given
        Member member = createMember();
        Book order = createOrder();
        int orderCount = 2;
        Long orderId = os.order(member.getId(), order.getId(), orderCount);

        // when
        os.cancelOrder(orderId);

        // then
        Order getOrder = or.findOne(orderId);

        Assertions.assertEquals(OrderStatus.CANCEL, getOrder.getStatus());
        Assertions.assertEquals(10, order.getStockQuantity());
    }



//    @Test
//    public void 상품주문_재고수량초과() throws Exception{
//        // given
//        Member member = createMember();
//        Book order = createOrder();
//        int orderCount = 11;
//
//        // when
////        Assertions.assertThrows(NotEnoughStockException.class, () ->{
////            os.order(member.getId(), order.getId(), orderCount);
////        });
////
////        // then
////        Assert.fail("여기 오면 안된다(재고수량 예외가 발생해야함)");

//    }


    private Book createOrder() {
        Book book = new Book();
        book.setName("시골 JPA");
        book.setPrice(10000);
        book.setStockQuantity(10);

        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("member1");
        member.setAddress(new Address("서울", "곰달래로", "123-123"));
        em.persist(member);
        return member;
    }








}