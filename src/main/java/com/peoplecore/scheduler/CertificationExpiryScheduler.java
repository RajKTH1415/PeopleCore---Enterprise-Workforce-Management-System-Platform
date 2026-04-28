package com.peoplecore.scheduler;
import com.peoplecore.module.EmployeeCertification;
import com.peoplecore.repository.EmployeeCertificationsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CertificationExpiryScheduler {

    private final EmployeeCertificationsRepository repository;

    //@Scheduled(cron = "0 0 1 * * ?") // Every day at 1:00 AM
    @Scheduled(cron = "0 * * * * ?")// everyminute
    @Transactional
    public void markExpiredCertifications() {

        List<EmployeeCertification> certifications =
                repository.findAllExpiredActiveCertifications();

        if (certifications.isEmpty()) {
            log.info("No certifications expired today.");
            return;
        }

        certifications.forEach(cert ->
                cert.setStatus("EXPIRED"));

        repository.saveAll(certifications);

        log.info("Updated {} certifications to EXPIRED.",
                certifications.size());
    }
}