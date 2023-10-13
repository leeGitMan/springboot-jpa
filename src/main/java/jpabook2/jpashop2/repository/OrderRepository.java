package jpabook2.jpashop2.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jpabook2.jpashop2.domain.Order;
import jpabook2.jpashop2.repository.order.query.OrderQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;
    private final OrderQueryRepository oqr;


    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAll(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건
        return query.getResultList();

    }



    public List<Order> findAllWithMemberDelivery() {
        // 일반적으로 lazy면 프록시 객체로 객체 껍데기만 가지고 오고
        // 초기화를 하는 순간 그 디비에 데이터를 조회해서 프록시 객체에 값을 넣는데
        // fetch join은
        // lazy를 무시하고 (프록시 말고 진짜 객체를 한번에 가지고옴)
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
        ).getResultList();

    }




    public List<OrderSimpleQueryDto> findOrdersDto() {
       return em.createQuery(
               // jpa는 엔티티나 값타입만 반환할 수 있고 DTO는 반환을 못한다
               // 그래서 new 오퍼레이션을 사용해야함
                "select new jpabook2.jpashop2.repository.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderSimpleQueryDto.class)
               .getResultList();
    }

    public List<Order> findAllWithItem() {
        return em.createQuery(
                // 원래 데이터가 중복된 4개가 나와야하는데
                // 내가 가지고 있는 스프링부트 버전에서는 distinct 옵션을 사용하지 않아도
                // 중복된 row수를 줄여줬다.
                // 근데 db에서는 distinct를 넣어도 완전 중복 데이터가 아니기에
                // 중복의 수를 줄여주지 않는다.
                // jpa에서는 distinct가 db에 select distinct로 쿼리를 날려주기는 하지만
                // Order에 중복된 id값이 있을 떄는 그 중복을 줄여서 데이터를 반환해준다.

                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d" +
                        " join fetch o.orderItems oi" +
                        " join fetch oi.item i", Order.class)
                .getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                        "select o from Order o" +
                                " join fetch o.member m" +
                                " join fetch o.delivery d", Order.class
                ).setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }


    /*
     * 쿼리 dsl
     * */
//    public List<Order> findAll(OrderSearch orderSearch) {
//
//    }
}
