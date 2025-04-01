package calendarapp.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import calendarapp.model.Participation;
import calendarapp.model.Project;
import calendarapp.model.Rehearsal;
import calendarapp.model.User;
import calendarapp.repository.ParticipationRepository;
import calendarapp.repository.ProjectRepository;
import calendarapp.repository.RehearsalRepository;
import calendarapp.request.RehearsalRequest;
import calendarapp.response.RehearsalResponse;
import jakarta.transaction.Transactional;

@Service
public class RehearsalService {

    @Autowired
    private RehearsalRepository rehearsalRepository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ParticipationRepository participationRepository;
    @Autowired
    private UserService userService;

    /**
     * Checks if a rehearsal with the given ´id´ exists in the database.
     * If it does not exist, throws an IllegalArgumentException.
     * 
     * @param id: the id of a project
     * @throws IllegalArgumentException if no rehearsal is found with the given ´id´
     */
    public void isRehearsal(Long id) {
        if(id == null){
            throw new IllegalArgumentException("Rehearsal not found with id " + id);
        }
        Optional<Rehearsal> rehearsal = rehearsalRepository.findById(id);
        if (!rehearsal.isPresent()) {
            throw new IllegalArgumentException("Rehearsal not found with id " + id);
        }
    }

    /**
     * Return the list of rehersal in the project with id ´projectId´
     * 
     * @param projectId the id of the project for wich we want to retreive it's
     *                  rehearsal
     * @return the list of rehearsal associated with the project with project id
     *         ´projectId´ sort by date then by name.
     * @throws IllegalArgumentException if no project is found for the given id
     */
    public List<RehearsalResponse> getProjectRehearsals(Long projectId) {
        projectService.isProject(projectId);
        List<Rehearsal> rehearsals = rehearsalRepository.findByProjectId(projectId);
        List<RehearsalResponse> rehearsalsResponse = new ArrayList<>();
        for (Rehearsal rehearsal : rehearsals) {
            List<Participation> participations = participationRepository.findByRehearsalId(rehearsal.getId());
            List<Long> participationIds = participations.stream().map(Participation::getUserId)
                    .collect(Collectors.toList());
            RehearsalResponse rehearsalResponse = new RehearsalResponse(rehearsal.getId(), rehearsal.getName(),
                    rehearsal.getDescription(), rehearsal.getDate(), rehearsal.getTime(), rehearsal.getDuration(), rehearsal.getProjectId(), rehearsal.getLocation(), participationIds);
            rehearsalsResponse.add(rehearsalResponse);
        }
        rehearsalsResponse.sort(Comparator
                .comparing(RehearsalResponse::getDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(RehearsalResponse::getName, Comparator.naturalOrder()));
        return rehearsalsResponse;
    }

    /**
     * Check that the rehearsal date is between the beginning and ending date of the
     * project
     * 
     * @param rehearsalDate the date of the rehearsal (can be null)
     * @param projectId     the project id
     * @throws IllegalArgumentException if no project is found for the given id,
     *                                  or the rehearsal date is not in the project
     *                                  dates
     */
    private void checkRehearsalDate(LocalDate rehearsalDate, Long projectId) {
        projectService.isProject(projectId);
        if (rehearsalDate != null) {
            Optional<Project> project = projectRepository.findById(projectId);
            if (project.get().getEndingDate() != null && rehearsalDate.isAfter(project.get().getEndingDate())) {
                throw new IllegalArgumentException("The rehearsal date cannot be after the project has ended");
            }
            if (project.get().getBeginningDate() != null
                    && rehearsalDate.isBefore(project.get().getBeginningDate())) {
                throw new IllegalArgumentException("The rehearsal date cannot be before the project has started");
            }
        }
    }

    /**
     * Add the ´rehearsal´ to the database
     * 
     * @param request the rehearsal to save in the database
     * @return the newly added rehearsal
     * @throws IllegalArgumentException if no project is found for the given project
     *                                  id,
     *                                  or if the date of the rehearsal is in the
     *                                  past or not dureing the project dates,
     *                                  or if the particpant id of one of the
     *                                  participants does correspond to a user in
     *                                  the database
     */
    @Transactional
    public Rehearsal createRehearsal(RehearsalRequest request) {
        projectService.isProject(request.getProjectId());
        LocalDate now = LocalDate.now();
        if (request.getDate() != null) {
            if (request.getDate().isBefore(now)) {
                throw new IllegalArgumentException("The rehearsal date cannot be in the past");
            }
            checkRehearsalDate(request.getDate(), request.getProjectId());
        }
        Rehearsal rehearsal = new Rehearsal(request.getName(), request.getDescription(), request.getDate(), request.getTime(),
                request.getDuration(), request.getProjectId(), request.getLocation());
        Rehearsal res = rehearsalRepository.save(rehearsal);
        // TODO dois check que les participant à la répète sont bien des participant au
        // project
        for (Long participantId : request.getParticipantsIds()) {
            userService.isUser(participantId);
            Participation participation = new Participation(participantId, rehearsal.getId());
            participationRepository.save(participation);
        }
        return res;
    }

    /**
     * get all the user object that participate in a rehearsal
     * 
     * @param id the id of a reheasral
     * @return list of User that are participant in the rehearsal with id ´id´
     * @throws IllegalArgumentException if no rehearsal is found with the given ´id´
     */
    public List<User> getRehearsalParticipants(Long id) {
        isRehearsal(id);
        List<User> res = new ArrayList<>();
        List<Participation> participations = participationRepository.findByRehearsalId(id);
        for (Participation participation : participations) {
            User user = userService.getUser(participation.getUserId());
            res.add(user);
        }
        return res;
    }

    /**
     * update a rehearsal with id ´id´ in the database
     * 
     * @param id      the id of a rehearsal
     * @param request a rehearsal object
     * @return the updated rehearsal
     * @throws IllegalArgumentException if no rehearsal is found with the given
     *                                  ´id´,
     *                                  or if the date of the rehearsal is not
     *                                  during the project dates,
     *                                  or if the particpant id of one of the
     *                                  participants does correspond to a user in
     *                                  the database
     */
    public Rehearsal updateReheasal(Long id, RehearsalRequest request) {
        projectService.isProject(request.getProjectId());
        checkRehearsalDate(request.getDate(), request.getProjectId());
        Optional<Rehearsal> rehearsalData = rehearsalRepository.findById(id);
        if (rehearsalData.isPresent()) {
            Rehearsal _rehearsal = rehearsalData.get();
            _rehearsal.setName(request.getName());
            _rehearsal.setDescription(request.getDescription());
            _rehearsal.setDate(request.getDate());
            _rehearsal.setTime(request.getTime());
            _rehearsal.setDuration(request.getDuration());
            _rehearsal.setLocation(request.getLocation());

            Rehearsal res = rehearsalRepository.save(_rehearsal);
            // TODO dois check que les participant à la répète sont bien des participant au
            // project
            List<Participation> existingParticipations = participationRepository.findByRehearsalId(id);
            List<Long> existingParticipantIds = existingParticipations.stream().map(Participation::getUserId)
                    .collect(Collectors.toList());
            List<Long> updatedParticipantIds = request.getParticipantsIds();
            // Delet participants not present anymore
            for (Participation participation : existingParticipations) {
                if (!updatedParticipantIds.contains(participation.getUserId())) {
                    participationRepository.delete(participation);
                }
            }
            // add new participant
            for (Long participantId : updatedParticipantIds) {
                if (!existingParticipantIds.contains(participantId)) {
                    userService.isUser(participantId);
                    Participation newParticipation = new Participation(participantId, id);
                    participationRepository.save(newParticipation);
                }
            }
            return res;
        } else {
            throw new IllegalArgumentException("Reherasal not found with id " + id);
        }
    }

    /**
     * Update a rehearsal date and time.
     * 
     * @param id the id of the rehearsal
     * @param projectId the id of the project the rehearsal is part of
     * @param dateTime the new date and time of the rehearsal
     * @return the updated rehearsal
     * @throws IllegalArgumentException if no rehearsal is found with the given
     *                                  ´id´,
     *                                  or if the date of the rehearsal is not
     *                                  during the project dates,
     *                                  or if no project found with the project id given
     */
    public Rehearsal updateReheasalDateAndTime(Long id, Long projectId, LocalDateTime dateTime) {
        projectService.isProject(projectId);
        LocalDate date = null;
        LocalTime time = null;
        if(dateTime != null){
            date = dateTime.toLocalDate();
            time = dateTime.toLocalTime();
        }
        checkRehearsalDate(date, projectId);
        Optional<Rehearsal> rehearsalData = rehearsalRepository.findById(id);
        if (rehearsalData.isPresent()) {
            Rehearsal _rehearsal = rehearsalData.get();
            _rehearsal.setDate(date);
            _rehearsal.setTime(time);

            Rehearsal res = rehearsalRepository.save(_rehearsal);
            return res;
        } else {
            throw new IllegalArgumentException("Reherasal not found with id " + id);
        }
    }

    /**
     * Get the reharsal with id ´id´ from the database.
     * 
     * @param id the id of the rehearsal
     * @return the retreived rehearsal
     * @throws IllegalArgumentException if no reherasal is found with the given id
     */
    public Rehearsal getRehearsal(long id) {
        Optional<Rehearsal> rehearsal = rehearsalRepository.findById(id);
        if (rehearsal.isPresent()) {
            return rehearsal.get();
        } else {
            throw new IllegalArgumentException("Rehearsal not found with id " + id);
        }
    }

    /**
     * Delete rehearsal with the id ´id´ from the database
     * 
     * @param id the id of the rehearsal to delete
     * @throws IllegalArgumentException if no reherasal is found with the given id
     */
    public void deleteRehearsal(Long id) {
        isRehearsal(id);
        rehearsalRepository.deleteById(id);
    }

    /**
     * Get all the rehearsal the user is a part of in the project with id
     * ´projectId´. Ordered base on the date then the rehearsal name.
     * 
     * @param email     the user email
     * @param projectId the id of the project
     * @return the list of rehearsal the user is a part of in the project with id
     *         ´projectid´
     * @throws IllegalArgumentException if no user found with the given email,
     *                                  or if no project found with the given id
     */
    public List<RehearsalResponse> getUserRehearsalsForProject(String email, Long projectId) {
        projectService.isProject(projectId);
        List<RehearsalResponse> res = new ArrayList<>();
        User user = userService.getUser(email);
        List<Rehearsal> rehearsals = rehearsalRepository.findByProjectId(projectId);
        for (Rehearsal rehearsal : rehearsals) {
            List<Participation> participations = participationRepository.findByRehearsalId(rehearsal.getId());
            List<Long> participationIds = participations.stream().map(Participation::getUserId)
                    .collect(Collectors.toList());
            if (participationIds.contains(user.getId())) {
                RehearsalResponse rehearsalResponse = new RehearsalResponse(rehearsal.getId(), rehearsal.getName(),
                        rehearsal.getDescription(), rehearsal.getDate(), rehearsal.getTime(), rehearsal.getDuration(),
                        rehearsal.getProjectId(), rehearsal.getLocation(), participationIds);
                res.add(rehearsalResponse);
            }
        }
        res.sort(Comparator
                .comparing(RehearsalResponse::getDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(RehearsalResponse::getName, Comparator.naturalOrder()));
        return res;
    }

    /**
     * Get all the rehearsal the user is a part of.
     * 
     * @param email the email of the user
     * @return the list og all the rehearsal the user is a part of
     */
    public List<Rehearsal> getUserRehearsals(String email){
        List<Rehearsal> res = new ArrayList<>();
        User user = userService.getUser(email);
        List<Participation> participations = participationRepository.findByUserId(user.getId());
        for(Participation participation : participations){
            Rehearsal rehearsal = rehearsalRepository.findById(participation.getRehearsalId()).get();
            res.add(rehearsal);
        }
        return res;
    }

}
