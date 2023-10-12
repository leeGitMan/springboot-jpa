package jpabook2.jpashop2.api;

import jpabook2.jpashop2.domain.Address;
import jpabook2.jpashop2.domain.Order;
import jpabook2.jpashop2.domain.OrderItem;
import jpabook2.jpashop2.domain.OrderStatus;
import jpabook2.jpashop2.repository.OrderRepository;
import jpabook2.jpashop2.repository.OrderSearch;
import jpabook2.jpashop2.repository.order.query.OrderFlatDto;
import jpabook2.jpashop2.repository.order.query.OrderItemQueryDto;
import jpabook2.jpashop2.repository.order.query.OrderQueryDto;
import jpabook2.jpashop2.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;



/*
    V1. 엔티티 직접 노출
        * - 엔티티가 변하면 API 스펙이 변한다.
        * - 트랜잭션 안에서 지연 로딩 필요
        * - 양방향 연관관계 문제 *
    V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용X)
        * - 트랜잭션 안에서 지연 로딩 필요
    V3. 엔티티를 조회해서 DTO로 변환(fetch join 사용O)
        * - 페이징 시에는 N 부분을 포기해야함(대신에 batch fetch size? 옵션 주면 N -> 1 쿼리로 변경
        가능) *
    V4.JPA에서 DTO로 바로 조회, 컬렉션 N 조회 (1+NQuery)
        * - 페이징 가능
    V5.JPA에서 DTO로 바로 조회, 컬렉션 1 조회 최적화 버전 (1+1Query)
        * - 페이징 가능
     V6. JPA에서 DTO로 바로 조회, 플랫 데이터(1Query) (1 Query)
        * - 페이징 불가능... *
*/
@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository or;
    private final OrderQueryRepository oqr;


    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = or.findAll(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {

        List<Order> orders = or.findAll(new OrderSearch());

        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return collect;
    }


    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = or.findAllWithItem();

        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return collect;
    }

    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value="limit", defaultValue = "100") int limit)
    {
        List<Order> orders = or.findAllWithMemberDelivery(offset, limit);

        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return collect;
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return oqr.findOrderQueryDtos();
    }

    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return oqr.findAllByDto_optimization();
    }

    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6() {
        List<OrderFlatDto> flats = oqr.findAllByDto_flat();

        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
                                o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                                o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
                        e.getKey().getAddress(), e.getValue()))
                .collect(toList());
    }


    @Data
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address; // 값타입은 노출되어도 괜찮다.
        private List<OrderItemsDto> orderItems; // DTO 속에있는 것들도 엔티티를 노출 하면 안된다. 그래서 orderItems도 DTO로 덮어씌움

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();

            orderItems = order.getOrderItems().stream()
                    .map(orderItems -> new OrderItemsDto(orderItems))
                    .collect(Collectors.toList());
        }

        @Data
        static class OrderItemsDto {

            private String itemName; // 상품명
            private int orderPrice; // 주문 가격
            private int count; // 주문 수량


            public OrderItemsDto(OrderItem orderItem) {
                itemName = orderItem.getItem().getName();
                orderPrice = orderItem.getOrderPrice();
                count = orderItem.getCount();
            }
        }
    }




}
