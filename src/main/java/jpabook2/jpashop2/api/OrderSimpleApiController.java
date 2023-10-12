package jpabook2.jpashop2.api;

import jpabook2.jpashop2.domain.Address;
import jpabook2.jpashop2.domain.Order;
import jpabook2.jpashop2.domain.OrderStatus;
import jpabook2.jpashop2.repository.OrderRepository;
import jpabook2.jpashop2.repository.OrderSearch;
import jpabook2.jpashop2.repository.OrderSimpleQueryDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


/**
 * xToOne(ManyToOne, OneToOne) 관계 최적화 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository or;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> findAll = or.findAll(new OrderSearch());
        for (Order order : findAll) {
            order.getMember().getName(); // lazy 모드 강제 초기화
            order.getDelivery().getAddress(); // lazy 모드 강제 초기화
        }
        return findAll;
    }


    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        // 주문이 2개가 있으면 order 쿼리가 2번 나가는데
        // 그 주문에 맞는 멤버, 딜리버리까지 조회 쿼리가 나가기에
        // 총 5개의 쿼리가 호출이 된다.
        // N + 1의 문제 -> 1+ 회원(N) -> 배송(N) 1+N+N
        List<Order> orders = or.findAll(new OrderSearch());
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return result;
    }




    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {

        // fetch join으로 Lazy모드 여도 한 번에 쿼리를 보내서 쿼리를 한개로 줄엿다.
        List<Order> orders = or.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return result;

    }

    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {

        // 위 v3 페치조인은 select 절에서 데이터를 더 많이 가져오는데,
        // 엔티티에 있는 필드들을 다 조회해서 가져오기에 네트워크를 더 많이 사용한다
        // 근데 Dto를 통해서 조회하는 것은 내가 원하는 것만 조회하기에
        // 성능을 더 최적화 시킬 수 있다.
        // 근데 해당 DTO를 사용할 떄만 사용이 가능 v3보단 재사용성이 낮다
        // DTO를 조회한 것은 데이터를 변경할 수 없고 엔티티로 데이터를 조회한 것은 데이터를 변환할 수 있따.
        // 그리고 코드상에서 DTO가 좀 더..지저분하다.
        // 레파지토리는 엔티티 객체 그래프를 탐색하는데 사용되어야한다
        // 그러나 DTO를 탐색하는 코드를 추가시켰고, api스펙을 레파지토리에 맞췄기에,
        // api스펙이 바뀌면 레파지토리를 변경시켜야한다.


        return or.findOrdersDto();
    }


    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // lazy 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // lazy 초기화
        }
    }


}
