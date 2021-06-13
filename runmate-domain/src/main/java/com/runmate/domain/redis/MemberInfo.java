package com.runmate.domain.redis;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash("running:member")
public class MemberInfo {
    @Id
    private long memberId;
    private float totalDistance;
    private long teamId;

    @Builder
    public MemberInfo(long memberId, long teamId) {
        this.memberId = memberId;
        this.teamId = teamId;

        this.totalDistance = 0;
    }

    public float increaseTotalDistance(float distance) {
        this.totalDistance += distance;
        return this.totalDistance;
    }
}