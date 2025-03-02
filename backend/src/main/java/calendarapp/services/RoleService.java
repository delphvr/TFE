package calendarapp.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import calendarapp.model.Role;
import calendarapp.repository.RoleRepository;
import jakarta.annotation.PostConstruct;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    // https://www.jobirl.com/mon-orientation/metiers/spectacle-metiers-de-la-scene
    // a part of https://www.cpnefsv.org/sites/default/files/public/pdf/F-Metiers-du-spectacle/Liste%20des%20principaux%20m%C3%A9tiers%20du%20spectacle%20vivant%20-2017.pdf
    private final List<String> defaultRoles = Arrays.asList("Non défini", "Artificier", "Editeur musical", "Maquilleur", "Habilleur", "Danseur", "Comédien", "Metteur en scène", "Directeur artistique" , "Ingénieur du son", "Costumier", "Tour manager", "Régisseur", "Musicien", "Eclairagiste", "Chanteur", "Artiste de cirque", "Décorateur-scénographe", "Technicien spectacle", "Professeur de Danse", "Formateur théâtre", "Chef d'orchestre", "Opérateur son", "Figurant", "Chorégraphe", "Responsable logistique", "Accessoiriste", "Technicien des effets spéciaux", "Responsable de l'accueil", "Attaché de presse", "Programmateur artistique", "Directeur de production");

    /**
     * Initialize all the possible values for the roles
     */
    @PostConstruct
    public void initProfessionsValues() {
        for (String value : defaultRoles) {
            if (!roleRepository.existsById(value)) {
                roleRepository.save(new Role(value));
            }
        }
        if (!roleRepository.existsById("Organizer")) {
            roleRepository.save(new Role("Organizer"));
        }
    }

    public List<String> getDefaultRoles() {
        return defaultRoles;
    }

    /**
     * Get all the roles in alphabetique order.
     * 
     * @return all the roles
     */
    public List<Role> getAllRoles(){
        List<Role> roles = new ArrayList<Role>();
		roleRepository.findAll().forEach(roles::add);
        roles.sort(Comparator.comparing(Role::getRole));
        return roles;
    }
}
