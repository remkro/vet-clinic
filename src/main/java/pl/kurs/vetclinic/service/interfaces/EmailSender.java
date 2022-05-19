package pl.kurs.vetclinic.service.interfaces;

import pl.kurs.vetclinic.model.entity.Visit;

public interface EmailSender {

    void sendVisitBookedEmail(Visit visit);

    void sendVisitReminderEmail(Visit visit);

}
