package com.runmate.controller;

import com.runmate.domain.crew.Crew;
import com.runmate.dto.crew.CrewPostDto;
import com.runmate.service.crew.CrewService;
import com.runmate.service.crew.CrewUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequiredArgsConstructor
@RequestMapping("api/crews")
@RestController
public class CrewController {

    private final CrewService crewService;
    private final CrewUserService crewUserService;
    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<String> create(@RequestBody CrewPostDto crewPostDto) {
        Crew crew = modelMapper.map(crewPostDto.getData(), Crew.class);
        Crew savedCrew = crewService.createCrew(crew, crewPostDto.getEmail());
        URI uri = WebMvcLinkBuilder.linkTo(CrewController.class).slash(savedCrew.getId()).toUri();
        return ResponseEntity.created(uri).body("success");
    }

    @DeleteMapping("/{crewId}")
    public ResponseEntity<?> delete(@PathVariable("crewId") long crewId,
                                    @RequestBody String requestEmail) {

        crewService.deleteCrew(crewId, requestEmail);
        return ResponseEntity.noContent().build();
    }


}
