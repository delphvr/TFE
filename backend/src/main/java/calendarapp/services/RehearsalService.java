package calendarapp.services;

import java.time.LocalDate;
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
import calendarapp.repository.ParticipationRepositiry;
import calendarapp.repository.ProjectRepository;
import calendarapp.repository.RehearsalRepository;
import calendarapp.request.CreateRehearsalRequest;
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
    private ParticipationRepositiry participationRepositiry;
    @Autowired
    private UserService userService;

    /**
     * Return the list of rehersal in the project with id ´projectId´
     * 
     * @param projectId the id of the project for wich we want to retreive it's
     *                  rehearsal
     * @return the list of rehearsal associated with the project with project id
     *         ´projectId´
     *         sort by date.
     */
    public List<RehearsalResponse> getProjectRehearsals(Long projectId) {
        projectService.isProject(projectId);
        List<Rehearsal> rehearsals = rehearsalRepository.findByProjectId(projectId);
        List<RehearsalResponse> rehearsalsResponse = new ArrayList<>();
        for (Rehearsal rehearsal : rehearsals) {
            List<Participation> participations = participationRepositiry.findByRehearsalId(rehearsal.getId());
            List<Long> participationIds = participations.stream().map(Participation::getUserId).collect(Collectors.toList());
            RehearsalResponse rehearsalResponse = new RehearsalResponse(rehearsal.getId(), rehearsal.getName(), rehearsal.getDescription(), rehearsal.getDate(), rehearsal.getDuration(), rehearsal.getProjectId(), participationIds);
            rehearsalsResponse.add(rehearsalResponse);
        }
        rehearsalsResponse.sort(Comparator.comparing(RehearsalResponse::getDate));
        return rehearsalsResponse;
    }

    /**
     * Add the ´rehearsal´ to the database
     * 
     * @param request the rehearsal to save in the database
     * @throws IllegalArgumentException if no project is found for the given project
     *                                  id
     *                                  or if the date of the rehearsal is in the
     *                                  past
     *                                  or if the particpant id of one of the
     *                                  participants does correspond to a user in
     *                                  the database
     * @return the newly added rehearsal
     */
    @Transactional
    public Rehearsal createRehearsal(CreateRehearsalRequest request) {
        projectService.isProject(request.getProjectId());
        LocalDate now = LocalDate.now();
        if (request.getDate() != null) {
            if (request.getDate().isBefore(now)) {
                throw new IllegalArgumentException("The rehearsal date cannot be in the past");
            }
            Optional<Project> project = projectRepository.findById(request.getProjectId());
            if (project.get().getEndingDate() != null && request.getDate().isAfter(project.get().getEndingDate())) {
                throw new IllegalArgumentException("The rehearsal date cannot be after the project has ended");
            }
            if (project.get().getBeginningDate() != null
                    && request.getDate().isBefore(project.get().getBeginningDate())) {
                throw new IllegalArgumentException("The rehearsal date cannot be before the project has started");
            }
        }
        Rehearsal rehearsal = new Rehearsal(request.getName(), request.getDescription(), request.getDate(),
                request.getDuration(), request.getProjectId());
        Rehearsal res = rehearsalRepository.save(rehearsal);

        for (Long participantId : request.getParticipantsIds()) {
            userService.isUser(participantId);
            Participation participation = new Participation(participantId, rehearsal.getId());
            participationRepositiry.save(participation);
        }
        return res;
    }

}
