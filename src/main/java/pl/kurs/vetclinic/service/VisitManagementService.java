package pl.kurs.vetclinic.service;

import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.vetclinic.command.AddVisitCommand;
import pl.kurs.vetclinic.model.entity.Doctor;
import pl.kurs.vetclinic.model.entity.Email;
import pl.kurs.vetclinic.model.entity.Patient;
import pl.kurs.vetclinic.model.entity.Visit;
import pl.kurs.vetclinic.model.enums.MedType;
import pl.kurs.vetclinic.model.enums.PetType;
import pl.kurs.vetclinic.repository.EmailRepository;
import pl.kurs.vetclinic.repository.VisitRepository;
import pl.kurs.vetclinic.service.interfaces.EmailSender;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static pl.kurs.vetclinic.config.AppConstants.DATE_TIME_FORMATTER;

@Service
public class VisitManagementService extends GenericManagementService<Visit, VisitRepository, UUID> {

    private final EmailSender emailSendingService;
    private final EmailRepository emailRepository;
    private final DoctorManagementService doctorManagementService;
    private final PatientManagementService patientManagementService;

    public VisitManagementService(VisitRepository repository, EmailSender emailSendingService, EmailRepository emailRepository, DoctorManagementService doctorManagementService, PatientManagementService patientManagementService) {
        super(repository);
        this.emailSendingService = emailSendingService;
        this.emailRepository = emailRepository;
        this.doctorManagementService = doctorManagementService;
        this.patientManagementService = patientManagementService;
    }

    @Transactional
    @Scheduled(cron = "0 00 22 * * *", zone = "Europe/Warsaw")
    @SchedulerLock(name = "TaskScheduler_scheduledEmailPreparer",
            lockAtLeastForString = "PT5M", lockAtMostForString = "PT15M")
    public void prepareEmails() {
        emailRepository.deleteAll();
        for (Visit visit : repository.findAll()) {
            if (isTomorrow(visit) && visit.getConfirmed()) {
                emailRepository.save(new Email(visit.getPatient().getEmail()));
            }
        }
    }

    @Transactional
    @Scheduled(cron = "0 00 23 * * *", zone = "Europe/Warsaw")
    @SchedulerLock(name = "TaskScheduler_scheduledReminder",
            lockAtLeastForString = "PT5M", lockAtMostForString = "PT15M")
    public void sendReminderEmails() {
        List<Email> emails = emailRepository.findAll();
        if (emails.size() != 0) {
            for (Email email : emails) {
                Visit visit = repository.getByPatientEmail(email.getEmailAddress()).orElseThrow(
                        () -> new EntityNotFoundException("VISIT_NOT_FOUND")
                );
                emailSendingService.sendVisitReminderEmail(visit);
                emailRepository.deleteByEmailAddress(email.getEmailAddress());
            }
        }
    }

    @Transactional
    public void confirm(UUID id) {
        Visit visit = repository.getById(id);
        visit.setConfirmed(true);
        repository.save(visit);
    }

    @Transactional
    public Visit createVisit(AddVisitCommand addVisitCommand) {
        Doctor doctor = doctorManagementService.showWithLocking(addVisitCommand.getDoctorId());
        Patient patient = patientManagementService.show(addVisitCommand.getPatientId());
        LocalDateTime date = LocalDateTime.parse(addVisitCommand.getDate(), DATE_TIME_FORMATTER);
        Visit visit = new Visit(doctor, patient, date, false);
        checkIfVisitExistsAtGivenDate(doctor, date);
        visit = add(visit);
        emailSendingService.sendVisitBookedEmail(visit);
        return visit;
    }

    @Transactional(readOnly = true)
    public List<Visit> findFilteredVisits(String type, String animal, LocalDateTime from, LocalDateTime to, int maxResults) {
        return repository
                .getAllByDateIsAfterAndDateIsBeforeAndDoctor_PetTypeAndDoctor_MedType(from, to,
                        PetType.setFromDescription(animal), MedType.setFromString(type))
                .stream()
                .sorted(Comparator.comparing(Visit::getDate))
                .limit(maxResults)
                .collect(Collectors.toList());
    }

    private boolean isTomorrow(Visit visit) {
        return visit.getDate().isAfter(LocalDateTime.now()) && visit.getDate().isBefore(LocalDateTime.now().plusHours(24));
    }

    private void checkIfVisitExistsAtGivenDate(Doctor doctor, LocalDateTime date) {
        if (doctor.getVisits().stream().anyMatch(v -> v.getDate().equals(date))) {
            throw new IllegalStateException("VISIT_ALREADY_EXISTS");
        }
    }
}
