package com.rsupport.board;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rsupport.board.entity.BoardType;
import com.rsupport.board.utils.BoardTypeEnum;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.rsupport.board.entity.QBoardType.boardType;
@Component
@Slf4j
@RequiredArgsConstructor
public class DatabaseDataLoader implements CommandLineRunner {

    private final EntityManager entityManager;

    private final JPAQueryFactory jpaQueryFactory;

    @Transactional
    @Override
    public void run(String... args) {
        List<BoardType> boardTypeList = jpaQueryFactory.selectFrom(boardType).fetch();

        if(boardTypeList.size() < 1) {
            for (BoardTypeEnum boardTypeEnum : BoardTypeEnum.values()) {
                BoardType boardType = BoardType.builder().name(boardTypeEnum).build();
                entityManager.persist(boardType);
            }
            entityManager.flush();
        }
    }

}
