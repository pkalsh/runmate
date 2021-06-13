package com.runmate.domain.running;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RunningMessage {
    private Long teamId;
    private Long memberId;
    private String username;
    private LocalTime averagePace;
    private LocalTime instantaneousPace;
    @JsonProperty("position")
    private Position position;
    private float distance;

    @Builder
    public RunningMessage(Long teamId, Long memberId, String username, LocalTime averagePace, LocalTime instantaneousPace, Position position, float distance) {
        this.teamId = teamId;
        this.memberId = memberId;
        this.username = username;
        this.averagePace = averagePace;
        this.instantaneousPace = instantaneousPace;
        this.position = position;
        this.distance = distance;
    }
}