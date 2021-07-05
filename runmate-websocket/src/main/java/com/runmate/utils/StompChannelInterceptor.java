package com.runmate.utils;

import com.runmate.service.JudgeRunningDataService;
import com.runmate.service.RunningDataMoveService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompChannelInterceptor implements ChannelInterceptor {
    private final RunningDataMoveService runningDataMoveService;
    private final JudgeRunningDataService judgeRunningDataService;
    private static final String TEAM_ID_HEADER = "teamId";
    private static final String MEMBER_ID_HEADER = "memberId";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor.getCommand() == StompCommand.CONNECT) {
            final Long teamId = getTeamIdFromStompHeader(accessor, TEAM_ID_HEADER);
            final Long memberId = getTeamIdFromStompHeader(accessor, MEMBER_ID_HEADER);

            runningDataMoveService.persistRunningDataToMem(teamId, memberId);
        } else if (accessor.getCommand() == StompCommand.DISCONNECT) {
            final Long teamId = getTeamIdFromStompHeader(accessor, TEAM_ID_HEADER);
            final Long memberId = getTeamIdFromStompHeader(accessor, MEMBER_ID_HEADER);

            if (judgeRunningDataService.isTeamLeader(teamId, memberId))
                runningDataMoveService.persistRunningResultToDisk(teamId);
        }
        return message;
    }

    private Long getTeamIdFromStompHeader(StompHeaderAccessor accessor, String key) {
        return Long.parseLong(accessor.getFirstNativeHeader(key));
    }

}