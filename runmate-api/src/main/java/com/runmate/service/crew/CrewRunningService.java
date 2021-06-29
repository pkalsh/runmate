package com.runmate.service.crew;

import com.runmate.domain.crew.CrewUser;
import com.runmate.domain.running.Team;
import com.runmate.domain.running.TeamMember;
import com.runmate.domain.user.User;
import com.runmate.dto.running.TeamCreationRequest;
import com.runmate.exception.NotFoundCrewUserException;
import com.runmate.exception.NotFoundUserEmailException;
import com.runmate.repository.crew.CrewUserRepository;
import com.runmate.repository.running.TeamMemberRepository;
import com.runmate.repository.running.TeamRepository;
import com.runmate.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class CrewRunningService {
    private final UserRepository userRepository;
    private final CrewUserRepository crewUserRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    public Team createTeam(TeamCreationRequest request) {
        CrewUser leaderCrewUser = crewUserRepository.findById(request.getLeaderId()).orElseThrow(NotFoundCrewUserException::new);
        Team createdTeam = teamRepository.save(Team.from(request));
        TeamMember leader = TeamMember.builder().team(createdTeam).crewUser(leaderCrewUser).build();
        createdTeam.assignLeader(leader);
        return createdTeam;
    }

    public TeamMember addMember(Team team, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(NotFoundUserEmailException::new);
        CrewUser crewUser = crewUserRepository.findByUser(user).orElseThrow(NotFoundCrewUserException::new);
        team.validateMember(crewUser);
        TeamMember teamMember = TeamMember.builder().team(team).crewUser(crewUser).build();

        // 초대 후처리

        return teamMemberRepository.save(teamMember);
    }
}