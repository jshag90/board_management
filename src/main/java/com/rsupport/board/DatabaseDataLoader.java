package com.rsupport.board;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rsupport.board.entity.BoardType;
import com.rsupport.board.entity.Member;
import com.rsupport.board.utils.BoardTypeEnum;
import com.rsupport.board.utils.StringUtil;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.rsupport.board.entity.QBoardType.boardType;
import static com.rsupport.board.entity.QMember.member;

@Component
@Slf4j
@RequiredArgsConstructor
public class DatabaseDataLoader implements CommandLineRunner {

    private final EntityManager entityManager;

    private final JPAQueryFactory jpaQueryFactory;

    @Transactional
    @Override
    public void run(String... args) throws NoSuchAlgorithmException {
        List<BoardType> boardTypeList = jpaQueryFactory.selectFrom(boardType).fetch();
        if (boardTypeList.size() < 1) {
            for (BoardTypeEnum boardTypeEnum : BoardTypeEnum.values()) {
                BoardType boardType = BoardType.builder().name(boardTypeEnum).build();
                entityManager.persist(boardType);
            }
        }

        if (jpaQueryFactory.selectFrom(member).fetch().size() < 1) {
            Member member = Member.builder()
                                .loginId("admin")
                                .password(StringUtil.textToSha256("abc123!@#"))
                                .username("관리자")
                                .role(Member.Role.ROLE_0)
                                .build();
            entityManager.persist(member);
        }

    }

}
