package calendarapp.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;

import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.IntervalVar;
import com.google.ortools.sat.LinearExpr;

import calendarapp.model.CpResult;
import calendarapp.model.Project;
import calendarapp.response.RehearsalResponse;

@Service
public class CalendarCPService {

    // curl -X GET "http://localhost:8080/api/projects/39/calendarCP"

    @Autowired
    private ProjectService projectService;
    @Autowired
    private RehearsalService rehearsalService;
    @Autowired
    private CpResultService cpResultService;

    @Value("${calendar.rehearsal.min-hour}")
    private int minHour;
    @Value("${calendar.rehearsal.max-hour}")
    private int maxHour;
    @Value("${calendar.project.default-end}")
    private Long defaultProjectEnd;

    final int periode_begining = 0;
    final LinearExpr oneDayInHour = LinearExpr.constant(24 * 60);

    class Rehearsal {
        Long id;
        Long duration; // in minutes
        LocalDate date;
        LocalTime time;
        List<Long> participantsId;

        Rehearsal(Long id, Long duration, LocalDate date, LocalTime time, List<Long> participantsId) {
            this.id = id;
            this.duration = duration;
            this.date = date;
            this.time = time;
            this.participantsId = participantsId;
        }
    }

    class RehearsalVariables {
        IntVar start;
        IntVar end;
        IntVar duration;
        IntervalVar interval;
        IntVar hourStart;
        IntVar hourEnd;
        IntervalVar hourInterval;

        RehearsalVariables(IntVar start, IntVar end, IntVar duration, IntervalVar interval, IntVar hour_start,
                IntVar hour_end, IntervalVar hour_interval) {
            this.start = start;
            this.end = end;
            this.duration = duration;
            this.interval = interval;
            this.hourStart = hour_start;
            this.hourEnd = hour_end;
            this.hourInterval = hour_interval;
        }
    }

    /**
     * Return a list of list of rehearsal id, representing all the rehearsal a
     * participant has and therfore can not be schedule at the same time.
     * 
     * @param rehearsals list of rehearsals on the project whit the corresponding
     *                   variable of the model
     * @return the list of list of rehearsal that participant has in commun
     */
    private List<List<IntervalVar>> ParticipantsRehearsals(Map<Long, Rehearsal> allRehearsals,
            Map<Long, RehearsalVariables> rehearsals) {
        List<List<IntervalVar>> res = new ArrayList<>();
        HashMap<Long, List<IntervalVar>> data = new HashMap<>();
        for (Rehearsal rehearsal : allRehearsals.values()) {
            for (Long participant : rehearsal.participantsId) {
                data.putIfAbsent(participant, new ArrayList<>());
                data.get(participant).add(rehearsals.get(rehearsal.id).interval);
            }
        }
        res.addAll(data.values());
        return res;
    }

    /**
     * Get the end value for the model, if we consider the begining of the periode =
     * 0 and than we add minutes. (So get the duration of the project in minutes).
     * If no ending date return Long.MAX_VALUE.
     * 
     * @param project the project
     * @return the end value (number of minutes in the project periode)
     * @throws IllegalArgumentException if the project begining date is not
     *                                  initialize
     */
    private Long getEndValue(Project project) {
        if (project.getBeginningDate() == null) {
            throw new IllegalArgumentException("The project begining date need to be initialize");
        }
        if (project.getEndingDate() == null) {
            return defaultProjectEnd;
        }
        long durationInMinutes = java.time.Duration.between(
                project.getBeginningDate().atStartOfDay(),
                project.getEndingDate().atStartOfDay()).toMinutes();
        return durationInMinutes;
    }

    /**
     * Return the number of minutes from the begining of the project and the given
     * LocalDateTime.
     * 
     * @param project the project
     * @param value   the LocalDateTime
     * @return number of minutes from the begining of the project and the given
     *         `value`
     * @throws IllegalArgumentException if the project begining date is not
     *                                  initialize
     */
    private Long getDateTimeValue(Project project, LocalDateTime value) {
        if (project.getBeginningDate() == null) {
            throw new IllegalArgumentException("The project begining date need to be initialize");
        }
        long durationInMinutes = java.time.Duration.between(
                project.getBeginningDate().atStartOfDay(),
                value).toMinutes();
        return durationInMinutes;
    }

    /**
     * Add variable to the model and contraint so the rehearsal doesn't happend at
     * night betwen `maxHour` and `minHour`
     * 
     * @param model       the model to which add the variables and constraints
     * @param rehearsalId the rehearsal
     * @param rehearsals  a map with the rehearsal in it
     */
    private void notAtNight(CpModel model, Long rehearsalId, Map<Long, RehearsalVariables> rehearsals) {
        RehearsalVariables rehearsalVariables = rehearsals.get(rehearsalId);
        // model.addGreaterOrEqual(schedule.start % 1440, minHour*60) (1440 minutes in
        // on day)
        IntVar hourStart = model.newIntVar(0, 1439, "modulo_start_" + rehearsalId);
        // hour_start = schedule.start % 1440
        model.addModuloEquality(hourStart, rehearsalVariables.start, oneDayInHour);
        model.addGreaterOrEqual(hourStart, minHour * 60);
        model.addLessOrEqual(hourStart, maxHour * 60);
        // model.addLessOrEqual(schedule.end % 1440, maxHour*60)
        IntVar hourEnd = model.newIntVar(0, 1439, "modulo_end_" + rehearsalId);
        model.addModuloEquality(hourEnd, rehearsalVariables.end, oneDayInHour);
        model.addGreaterOrEqual(hourEnd, minHour * 60);
        model.addLessOrEqual(hourEnd, maxHour * 60);
        // debut avant fin sinon sur deux jour et donc la nuit
        model.addLessOrEqual(hourStart, hourEnd);
        // contraintes redondante sur la durée maximal de la répétition sur une journée,
        // sinon réfléchi trop longtemps, permets de propager directement et voir que
        // solution infaisable
        IntervalVar hourInterval = model.newIntervalVar(hourStart, rehearsalVariables.duration, hourEnd,
                "hour_interval_rehearsal_" + rehearsalId);
        rehearsalVariables.hourStart = hourStart;
        rehearsalVariables.hourEnd = hourEnd;
        rehearsalVariables.hourInterval = hourInterval;
    }

    /**
     * Creates and add the variables to the model
     * 
     * @param model         the model to which add the variables
     * @param allRehearsals the reahrsals informations
     * @param project       the project
     * @return the list of all the variables groupe by the rehearsals id they
     *         correspond to
     */
    private Map<Long, RehearsalVariables> createVariables(CpModel model, Map<Long, Rehearsal> allRehearsals,
            Map<Long, Rehearsal> allRehearsalsToConstraint, Project project) {
        Map<Long, RehearsalVariables> rehearsals = new HashMap<>();
        long periode_end = getEndValue(project);
        for (Rehearsal rehearsal : allRehearsals.values()) {
            if (rehearsal.date != null && rehearsal.time != null) {
                LocalDateTime dateTime = rehearsal.date.atTime(rehearsal.time);
                Long startTime = getDateTimeValue(project, dateTime);
                Long endTime = startTime + rehearsal.duration;
                IntVar start = model.newIntVar(startTime, startTime, "start_rehearsal_" + rehearsal.id);
                IntVar end = model.newIntVar(endTime, endTime, "not_end_rehearsal_" + rehearsal.id);
                IntVar duration = model.newIntVar(rehearsal.duration, rehearsal.duration,
                        "duration_rehearsal_" + rehearsal.id);
                IntervalVar interval = model.newIntervalVar(start, duration, end,
                        "interval_rehearsal_" + rehearsal.id);
                rehearsals.put(rehearsal.id,
                        new RehearsalVariables(start, end, duration, interval, null, null, null));
            }
            // TODO if just at a specific date or just at a specific time
            else {
                IntVar start = model.newIntVar(periode_begining, periode_end - rehearsal.duration,
                        "start_rehearsal_" + rehearsal.id);
                IntVar end = model.newIntVar(periode_begining, periode_end, "end_rehearsal_" + rehearsal.id);
                IntVar duration = model.newIntVar(rehearsal.duration, rehearsal.duration,
                        "duration_rehearsal_" + rehearsal.id);
                IntervalVar interval = model.newIntervalVar(start, duration, end,
                        "interval_rehearsal_" + rehearsal.id);
                rehearsals.put(rehearsal.id,
                        new RehearsalVariables(start, end, duration, interval, null, null, null));
            }
            if (allRehearsalsToConstraint.containsKey(rehearsal.id)) {
                notAtNight(model, rehearsal.id, rehearsals);
            }
        }
        return rehearsals;
    }

    /**
     * Get the date and time of the rehersal starting `minutesFromBeginning` minutes
     * after the begining date of the `project`.
     * 
     * @param project              the project
     * @param minutesFromBeginning the number of minutes between the begining of the
     *                             project (beginning date at 00:00) and the date
     *                             time we are looking for
     * @return the LocalDateTime of the reherasal
     * @throws IllegalArgumentException if the project begining date is not
     *                                  initialize
     */
    private LocalDateTime getRehearsalDate(Project project, Long minutesFromBeginning) {
        LocalDate beginningDate = project.getBeginningDate();
        if (beginningDate == null) {
            throw new IllegalArgumentException("The project begining date need to be initialize");
        }
        LocalDateTime startDateTime = beginningDate.atStartOfDay();
        LocalDateTime rehearsalDateTime = startDateTime.plusMinutes(minutesFromBeginning);
        return rehearsalDateTime;
    }

    public List<CpResult> run(Long projectId, boolean recompute) {
        Project project = projectService.getProject(projectId);

        Map<Long, LocalDateTime> rehearsalNotPossible = new HashMap<>();
        if (recompute) {
            System.out.println("here");
            rehearsalNotPossible = cpResultService.checkPreviousResult(projectId);
            System.out.println("here" + rehearsalNotPossible);
        }

        Loader.loadNativeLibraries();

        Map<Long, Rehearsal> allRehearsals = new HashMap<>();
        Map<Long, Rehearsal> allRehearsalsToConstraint = new HashMap<>();
        for (RehearsalResponse rehearsal : rehearsalService.getProjectRehearsals(projectId)) {
            Rehearsal r = new Rehearsal(rehearsal.getId(), rehearsal.getDuration().toMinutes(), rehearsal.getDate(),
                    rehearsal.getTime(), rehearsal.getParticipantsIds());
            allRehearsals.put(rehearsal.getId(), r);
            if (rehearsal.getDate() == null && rehearsal.getTime() == null) {
                allRehearsalsToConstraint.put(rehearsal.getId(), r);
            }
        }

        CpModel model = new CpModel();
        Map<Long, RehearsalVariables> rehearsalsVariables = createVariables(model, allRehearsals,
                allRehearsalsToConstraint, project);

        // Constraints :
        // 1. if a user has several rehearsals then they cannot overlap
        List<List<IntervalVar>> intervalsConstraintsList = ParticipantsRehearsals(allRehearsals, rehearsalsVariables);
        for (List<IntervalVar> intervalsList : intervalsConstraintsList) {
            model.addNoOverlap(intervalsList);
        }
        // 2. cannot happend at a dateTime previously rejected
        if (recompute) {
            rehearsalNotPossible.forEach((id, dateTime) -> {
                if (allRehearsalsToConstraint.containsKey(id)) {
                    RehearsalVariables rehearsalVariables = rehearsalsVariables.get(id);
                    Rehearsal rehearsal = allRehearsals.get(id);
                    Long start = getDateTimeValue(project, dateTime);
                    Long end = start + rehearsal.duration;
                    IntVar intervalStart = model.newIntVar(start, start, "not_start_rehearsal_" + rehearsal.id);
                    IntVar intervalEnd = model.newIntVar(end, end, "not_end_rehearsal_" + rehearsal.id);
                    IntervalVar intervalVar =  model.newIntervalVar(intervalStart, rehearsalVariables.duration, intervalEnd,
                            "not_interval_rehearsal_" + rehearsal.id);
                    List<IntervalVar> intervalsList = new ArrayList<>();
                    intervalsList.add(intervalVar);
                    intervalsList.add(rehearsalVariables.interval);
                    model.addNoOverlap(intervalsList);
                }
            });
        }

        CpSolver solver = new CpSolver();
        solver.getParameters().setLogSearchProgress(true); // logs
        model.validate(); // logs
        CpSolverStatus status = solver.solve(model);

        List<CpResult> res = new ArrayList<>();

        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            for (Rehearsal rehearsal : allRehearsals.values()) {
                RehearsalVariables schedule = rehearsalsVariables.get(rehearsal.id);

                LocalDateTime beginningDateTime = getRehearsalDate(project, solver.value(schedule.start));

                System.out.println("prjectId: " + projectId + " rehearsal id: " + rehearsal.id + " begining date: "
                        + beginningDateTime);

                boolean accepted = false;
                if (!allRehearsalsToConstraint.containsKey(rehearsal.id)) {
                    accepted = true;
                }
                System.out.println("p1 : " + projectId);
                CpResult cp = new CpResult(projectId, rehearsal.id, accepted, beginningDateTime);
                cpResultService.createCp(cp);
                res.add(cp);

            }
        } else {
            // TODO: how to represent this in the response ?
            System.out.println("No solution found");
        }

        return res;
    }
}
