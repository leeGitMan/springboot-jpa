package jpabook2.jpashop2.service;

import jpabook2.jpashop2.domain.Delivery;
import jpabook2.jpashop2.domain.Item.Item;
import jpabook2.jpashop2.domain.Member;
import jpabook2.jpashop2.domain.Order;
import jpabook2.jpashop2.domain.OrderItem;
import jpabook2.jpashop2.repository.ItemRepository;
import jpabook2.jpashop2.repository.MemberRepository;
import jpabook2.jpashop2.repository.OrderRepository;
import jpabook2.jpashop2.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository or;
    private final MemberRepository mr;
    private final ItemRepository ir;
    

    // 주문
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        Member findMemberId = mr.findOne(memberId);
        Item findItemId = ir.findOne(itemId);

        // 배송 정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(findMemberId.getAddress());

        // 주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(findItemId, findItemId.getPrice(), count);

        // 주문 생성
        Order order = Order.createOrder(findMemberId, delivery, orderItem);

        // 주문 저장
        or.save(order);
        // order만 save하는 이유는 Order 엔티티 내부에 delivery, orderItem 이 전부 cascadeType.ALL로
        // 조건이 걸려있다.
        // 근데 여기서 delivery나 혹은 orderItem이 Order뿐만이 아니라 다른 엔티티에서도 참조가 된다면
        // cascadeType.ALL은 안 쓰는것이 좋다
        return order.getId();
    }

    // 주문 취소
    @Transactional
    public void cancelOrder(Long orderId) {
        Order findOrder = or.findOne(orderId);
        findOrder.cancel();
    }

    // 주문 검색
    public List<Order> findOrders(OrderSearch orderSearch) {
        return or.findAll(orderSearch);
    }
}
