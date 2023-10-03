package jpabook2.jpashop2.service;

import jpabook2.jpashop2.domain.Member;
import jpabook2.jpashop2.repository.MemberRepository;

import org.junit.Assert;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService ms;

    @Autowired
    MemberRepository mr;


    @Test
    public void 회원가입() throws Exception {
        // given
        Member member = new Member();
        member.setName("kim");

        // when
        Long saveId = ms.join(member);

        // then
        assertEquals(member, mr.findOne(saveId));
        // jpa에서는 같은 트랜잭션 안에서 같은 pk값을 가지고 있으면 같은 영속성 컨텍스트에 관리된다.

    }

    @Test()
    public void 중복_회원_예외() throws Exception{
        // given
        Member member = new Member();
        member.setName("kim1");

        Member member2 = new Member();
        member2.setName("kim1");

        // when
        ms.join(member);

        // then
        try {
            ms.join(member2);
        } catch (IllegalStateException e) {
            return;
        }

        // 밑으로 코드가 내려가면 안되고, 예외가 발생해서 이 테스트를 빠져나가야 함.
        Assert.fail("예외가 발생해야한다.");
    }



}