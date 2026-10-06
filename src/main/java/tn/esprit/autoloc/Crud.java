package tn.esprit.autoloc;

import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.repository.IContratRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class Crud implements CommandLineRunner {

    private final IContratRepository contratRepository;

    @Override
    public void run(String... args) {
        log.info("Nombre de contrats : {}", contratRepository.count());

        Optional<Contrat> contrat = contratRepository.findById(999L);
        log.info("Contrat 999 present ? {}", contrat.isPresent());

        contratRepository.deleteById(999L);
        log.info("deleteById(999) : aucune exception");

        Sort tri = Sort.by("montantTotal").descending();
        Page<Contrat> page = contratRepository.findAll(PageRequest.of(0, 2, tri));
        log.info("Total elements : {}", page.getTotalElements());
        log.info("Total pages : {}", page.getTotalPages());
    }
}