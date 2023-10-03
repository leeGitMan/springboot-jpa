package jpabook2.jpashop2.service;

import jpabook2.jpashop2.domain.Member;
import jpabook2.jpashop2.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor // 생성자 인젝션
public class MemberService {

    private final MemberRepository mr;

    // 회원 가입
    public Long join(Member member) {
        validateDuplicateMember(member);
        mr.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = mr.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    @Transactional(readOnly = true)
    public List<Member> findMemberAll() {
        return mr.findAll();
    }

    @Transactional(readOnly = true)
    public Member findOne(Long memberId) {
        return mr.findOne(memberId);
    }



    public void update(Long id, String name) {
        Member member = mr.findOne(id);
        member.setName(name);
    }
}
