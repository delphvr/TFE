package calendarapp.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import calendarapp.model.Profession;
import calendarapp.repository.ProfessionRepository;
import jakarta.annotation.PostConstruct;

@Service
@DependsOn("roleService")
public class ProfessionService {

    @Autowired
    private ProfessionRepository professionRepository;
    @Autowired
    private RoleService roleService;

    /**
     * Initialize all the possible values for the professions
     */
    @PostConstruct
    public void initProfessionsValues() {
        // https://fr.wikipedia.org/wiki/Liste_des_m%C3%A9tiers
        List<String> allProfessions = new ArrayList<>(Arrays.asList("Agronomie et alimentation", "Artisanat",
                "Audiovisuel", "Bâtiment", "Culture", "Commerce", "Droit", "Éducation", "Finances et gestion",
                "Politique", "Scientifique", "Santé", "Sécurité", "Technologies", "Transports et logistique"));
        allProfessions.addAll(roleService.getDefaultRoles());
        for (String value : allProfessions) {
            if (!professionRepository.existsById(value)) {
                professionRepository.save(new Profession(value));
            }
        }
    }

    /**
     * Get all the professions in alphabetique order.
     * 
     * @return all the professions
     */
    public List<Profession> getAllProfessions(){
        List<Profession> professions = new ArrayList<Profession>();
		professionRepository.findAll().forEach(professions::add);
        professions.sort(Comparator.comparing(Profession::getProfession));
        return professions;
    }
}
