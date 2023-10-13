package jpabook2.jpashop2.repository;

import jpabook2.jpashop2.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/*
* 스프링 데이터 JPA는 JpaRepository 라는 인터페이스를 제공하는데, 여기에 기본적인 CRUD 기능이 모두 제공된다.
* findByName 처럼 일반화 하기 어려운 기능도 메서드 이름으로 정확한 JPQL 쿼리를 실행한다.
      select m from Member m where m.name = :name
* */
public interface MemberRepository extends JpaRepository<Member, Long> {

    // select m from Member m where m.name = ?
    // spring data jpa가 Name이란 걸 보고 위 jqpl을 만든다.
    List<Member> findByName(String name);
}
