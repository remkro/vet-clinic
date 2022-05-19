package pl.kurs.vetclinic.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.kurs.vetclinic.command.AddVisitCommand;
import pl.kurs.vetclinic.command.CheckVisitCommand;
import pl.kurs.vetclinic.command.FirstOrder;
import pl.kurs.vetclinic.command.SecondOrder;
import pl.kurs.vetclinic.model.dto.StatusDto;
import pl.kurs.vetclinic.model.dto.VisitExtendedDto;
import pl.kurs.vetclinic.model.dto.VisitSimpleDto;
import pl.kurs.vetclinic.model.entity.Visit;
import pl.kurs.vetclinic.service.VisitManagementService;
import pl.kurs.vetclinic.validation.annotation.CorrectToken;
import pl.kurs.vetclinic.validation.annotation.NotTooLate;
import pl.kurs.vetclinic.validation.annotation.Unconfirmed;

import javax.validation.GroupSequence;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static pl.kurs.vetclinic.config.AppConstants.DATE_TIME_FORMATTER;

@RequiredArgsConstructor
@GroupSequence({FirstOrder.class, SecondOrder.class, VisitController.class})
@Validated
@RestController
@RequestMapping("/visit")
public class VisitController {

    private final VisitManagementService visitManagementService;
    private final ModelMapper mapper;

    @GetMapping("/{id}/confirm")
    public ResponseEntity<StatusDto> confirm(
            @PathVariable
            @CorrectToken(groups = FirstOrder.class)
            @Unconfirmed(groups = SecondOrder.class)
            @NotTooLate(groups = SecondOrder.class) String id) {
        UUID uuid = UUID.fromString(id);
        visitManagementService.confirm(uuid);
        return ResponseEntity.ok(new StatusDto("VISIT_CONFIRMED"));
    }

    @GetMapping("/{id}/cancel")
    public ResponseEntity<StatusDto> cancel(@PathVariable @CorrectToken String id) {
        UUID uuid = UUID.fromString(id);
        visitManagementService.delete(uuid);
        return ResponseEntity.ok(new StatusDto("VISIT_CANCELED"));
    }

    @PostMapping
    public ResponseEntity<VisitSimpleDto> add(@RequestBody @Valid AddVisitCommand addVisitCommand) {
        Visit visit = visitManagementService.createVisit(addVisitCommand);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map(visit, VisitSimpleDto.class));
    }

    @PostMapping("/check")
    public ResponseEntity<List<VisitExtendedDto>> check(@RequestBody @Valid CheckVisitCommand checkVisitCommand,
                                                        @RequestParam int results) {
        LocalDateTime from = LocalDateTime.parse(checkVisitCommand.getFrom(), DATE_TIME_FORMATTER);
        LocalDateTime to = LocalDateTime.parse(checkVisitCommand.getTo(), DATE_TIME_FORMATTER);
        String type = checkVisitCommand.getType();
        String animal = checkVisitCommand.getAnimal();
        List<Visit> filteredVisits = visitManagementService.findFilteredVisits(type, animal, from, to, results);
        List<VisitExtendedDto> filteredVisitsDto = filteredVisits
                .stream()
                .map(v -> mapper.map(v, VisitExtendedDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(filteredVisitsDto);
    }

}
