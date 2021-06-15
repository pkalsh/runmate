package com.runmate.repository.redis;

import com.runmate.domain.redis.MemberInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class MemberInfoRepositoryTest {
    @Autowired
    MemberInfoRepository memberInfoRepository;

    @Test
    void When_SaveAndFind_MemberInfo_Expect_SameObject() {
        //when
        MemberInfo memberInfo = MemberInfo.builder()
                .teamId(3L)
                .memberId(2L)
                .build();
        memberInfo.increaseTotalDistance(10.0F);
        memberInfoRepository.save(memberInfo);

        MemberInfo result = memberInfoRepository.findById(memberInfo.getMemberId()).get();
        //then
        checkSameMemberInfo(memberInfo, result);
    }

    void checkSameMemberInfo(MemberInfo one, MemberInfo another) {
        assertEquals(one.getMemberId(), another.getMemberId());
        assertEquals(one.getTeamId(), another.getTeamId());
        assertEquals(one.getTotalDistance(), another.getTotalDistance());
    }
}
