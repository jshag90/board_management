package com.rsupport.board.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rsupport.board.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.rsupport.board.entity.QMember.member;

@Repository
@RequiredArgsConstructor
public class MemberDao {
    private final JPAQueryFactory jpaQueryFactory;

    public String getUserNameByAdminRole(){
        return jpaQueryFactory.select(member.username).from(member)
                .where(member.role.eq(Member.Role.ROLE_0))
                .fetchFirst();
    }

}
