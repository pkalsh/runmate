package com.runmate.repository.crew;

import com.runmate.domain.crew.Crew;
import com.runmate.domain.crew.CrewJoinRequest;
import com.runmate.domain.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CrewJoinRequestRepository extends JpaRepository<CrewJoinRequest, Long> {
    @Query("SELECT CJR FROM CrewJoinRequest CJR where CJR.crew =:crew")
    List<CrewJoinRequest> findAllByCrewWithPageable(@Param("crew") Crew crew, Pageable request);

    Optional<CrewJoinRequest> findCrewJoinRequestByCrewAndUser(Crew crew, User user);
}