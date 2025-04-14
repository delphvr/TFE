package calendarapp.services;

import java.time.Duration;
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
import com.google.ortools.sat.BoolVar;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.IntervalVar;
import com.google.ortools.sat.LinearExpr;

import calendarapp.model.CpResult;
import calendarapp.model.CpPresenceResult;
import calendarapp.model.Project;
import calendarapp.model.Rehearsal;
import calendarapp.model.Vacation;
import calendarapp.model.WeeklyAvailability;
import calendarapp.response.RehearsalResponse;

@Service
public class CalendarCPService {

    // curl -X GET "http://localhost:8080/api/projects/39/calendarCP"

    @Autowired
    private ProjectService projectService;
    @Autowired
    private RehearsalService rehearsalService;
    @Autowired
    private WeeklyAvailabilityService weeklyAvailabilityService;
    @Autowired
    private VacationService vacationService;
    @Autowired
    private CpResultService cpResultService;
    @Autowired
    private CpPresenceResultService cpPresenceResultService;
    @Autowired
    private RehearsalPrecedenceService rehearsalPrecedenceService;

    @Value("${calendar.rehearsal.min-hour}")
    private int minHour;
    @Value("${calendar.rehearsal.max-hour}")
    private int maxHour;
    @Value("${calendar.project.default-end}")
    private Long defaultProjectEnd;

    final int periode_begining = 0;
    final LinearExpr oneDayInHour = LinearExpr.constant(24 * 60);

    class RehearsalData {
        Long id;
        Long duration; // in minutes
        LocalDate date;
        LocalTime time;
        List<Long> participantsId;

        RehearsalData(Long id, Long duration, LocalDate date, LocalTime time, List<Long> participantsId) {
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
        Map<Long, BoolVar> usersPresence;
        IntVar participationPercentage;

        RehearsalVariables(IntVar start, IntVar end, IntVar duration, IntervalVar interval, IntVar hour_start,
                IntVar hour_end, IntervalVar hour_interval, Map<Long, BoolVar> usersPresence) {
            this.start = start;
            this.end = end;
            this.duration = duration;
            this.interval = interval;
            this.hourStart = hour_start;
            this.hourEnd = hour_end;
            this.hourInterval = hour_interval;
            this.usersPresence = usersPresence;
        }
    }

    class NonAvailability {
        LocalTime startTime;
        LocalTime endTime;

        NonAvailability(LocalTime startTime, LocalTime endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }

    }

    /**
     * Return a Map with all the participants as keys and as value the list of
     * rhearsalsVaribale he participe in.
     * 
     * @param rehearsals list of rehearsals on the project whit the corresponding
     *                   variable of the model
     * @return the Map with all the participants as keys and as value the list of
     *         rhearsalsVaribale he participe in
     */
    private Map<Long, Map<Long, RehearsalVariables>> ParticipantsRehearsals(Map<Long, RehearsalData> allRehearsals,
            Map<Long, RehearsalVariables> rehearsals) {
        Map<Long, Map<Long, RehearsalVariables>> res = new HashMap<>();
        for (RehearsalData rehearsal : allRehearsals.values()) {
            for (Long participant : rehearsal.participantsId) {
                res.putIfAbsent(participant, new HashMap<>());
                res.get(participant).put(rehearsal.id, rehearsals.get(rehearsal.id));
            }
        }
        // res.addAll(data.values());
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
                project.getEndingDate().atTime(LocalTime.MAX)).toMinutes();
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
            throw new IllegalArgumentException("The project begining date need to be initialize"); // TODO takes today ?
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
     * @param rehearsalVariables  the rehearsals model variables
     */
    private void notAtNight(CpModel model, Long rehearsalId, RehearsalVariables rehearsalVariables) {
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
    private Map<Long, RehearsalVariables> createVariables(CpModel model, Map<Long, RehearsalData> allRehearsals, Project project) {
        Map<Long, RehearsalVariables> rehearsals = new HashMap<>();
        long periode_end = getEndValue(project);
        for (RehearsalData rehearsal : allRehearsals.values()) {
            Map<Long, BoolVar> usersPresence = new HashMap<>();
            for (Long participantId : rehearsal.participantsId) {
                usersPresence.put(participantId,
                        model.newBoolVar("user_" + participantId + "_participates_to_" + rehearsal.id));
            }
            //rehearsal already has a date and time set
            if (rehearsal.date != null && rehearsal.time != null) {
                LocalDateTime dateTime = rehearsal.date.atTime(rehearsal.time);
                Long startTime = getDateTimeValue(project, dateTime);
                Long endTime = startTime + rehearsal.duration;
                IntVar start = model.newIntVar(startTime, startTime, "start_rehearsal_" + rehearsal.id);
                IntVar end = model.newIntVar(endTime, endTime, "end_rehearsal_" + rehearsal.id);
                IntVar duration = model.newIntVar(rehearsal.duration, rehearsal.duration,
                        "duration_rehearsal_" + rehearsal.id);
                IntervalVar interval = model.newIntervalVar(start, duration, end,
                        "interval_rehearsal_" + rehearsal.id);
                rehearsals.put(rehearsal.id,
                        new RehearsalVariables(start, end, duration, interval, null, null, null, usersPresence));
            }
            //rehearsal has a specific date but not time
            else if (rehearsal.date != null && rehearsal.time == null) {
                LocalDateTime stratDateTime = rehearsal.date.atTime(LocalTime.of(minHour, 0));
                Long startTime = getDateTimeValue(project, stratDateTime);
                LocalDateTime endDateTime = rehearsal.date.atTime(LocalTime.of(maxHour, 0));
                Long endTime = getDateTimeValue(project, endDateTime);
                IntVar start = model.newIntVar(startTime, endTime-rehearsal.duration, "start_rehearsal_" + rehearsal.id);
                IntVar end = model.newIntVar(startTime+rehearsal.duration, endTime, "end_rehearsal_" + rehearsal.id);
                IntVar duration = model.newIntVar(rehearsal.duration, rehearsal.duration,
                        "duration_rehearsal_" + rehearsal.id);
                IntervalVar interval = model.newIntervalVar(start, duration, end,
                        "interval_rehearsal_" + rehearsal.id);
                rehearsals.put(rehearsal.id,
                        new RehearsalVariables(start, end, duration, interval, null, null, null, usersPresence));
            }
            else {
                IntVar start = model.newIntVar(periode_begining, periode_end - rehearsal.duration,
                        "start_rehearsal_" + rehearsal.id);
                IntVar end = model.newIntVar(periode_begining + rehearsal.duration, periode_end, "end_rehearsal_" + rehearsal.id);
                IntVar duration = model.newIntVar(rehearsal.duration, rehearsal.duration,
                        "duration_rehearsal_" + rehearsal.id);
                IntervalVar interval = model.newIntervalVar(start, duration, end,
                        "interval_rehearsal_" + rehearsal.id);
                rehearsals.put(rehearsal.id,
                        new RehearsalVariables(start, end, duration, interval, null, null, null, usersPresence));
                //rehearsal has a specific time but not date
                if(rehearsal.date == null && rehearsal.time != null){
                    // model.addGreaterOrEqual(schedule.start % 1440, rehearsal.time*60) (1440 minutes in
                    // on day)
                    IntVar hourStart = model.newIntVar(0, 1439, "modulo_define_start_" + rehearsal.id);
                    // hour_start = schedule.start % 1440
                    model.addModuloEquality(hourStart, start, oneDayInHour);
                    model.addEquality(hourStart, rehearsal.time.toSecondOfDay() / 60);
                }
            }
            if (rehearsal.date == null && rehearsal.time == null) {
                notAtNight(model, rehearsal.id, rehearsals.get(rehearsal.id));
            }
        }
        return rehearsals;
    }

    private void AddVacationConstraints(CpModel model, Project project, Long participantId,
            Map<Long, RehearsalVariables> userRehearsalsVariables) {
        List<Vacation> vacations = vacationService.getUserVacations(participantId);
        for (Vacation vacation : vacations) {
            LocalDate endDate = project.getEndingDate();
            if (endDate == null) {
                endDate = project.getBeginningDate().plusDays(defaultProjectEnd / (60 * 24));
            }
            // take only vacation that appears during the project dates
            if (vacation.getStartDate().isBefore(endDate)
                    && vacation.getEndDate().isAfter(project.getBeginningDate())) {

                LocalDateTime startVacation = vacation.getStartDate().atStartOfDay();
                LocalDateTime startProject = project.getBeginningDate().atStartOfDay();
                LocalDateTime startDateTime = (startVacation.compareTo(startProject) > 0) ? startVacation
                        : startProject;
                LocalDateTime endVacation = vacation.getEndDate().atTime(LocalTime.MAX);
                LocalDateTime endProject = endDate.atTime(LocalTime.MAX);
                LocalDateTime endDateTime = endVacation.isBefore(endProject) ? endVacation : endProject;
                Long start = getDateTimeValue(project, startDateTime);
                Long end = getDateTimeValue(project, endDateTime);
                Long duration = Duration.between(startDateTime, endDateTime).toMinutes();
                IntVar intervalStart = model.newIntVar(start, start, "not_start_rehearsal_vacataion_" + participantId);
                IntVar intervalEnd = model.newIntVar(end, end, "not_end_rehearsal_vacation_" + participantId);
                IntVar intervalDuration = model.newIntVar(duration, duration,
                        "duration_not_end_rehearsal_vacation_" + participantId);
                IntervalVar intervalVar = model.newIntervalVar(intervalStart, intervalDuration,
                        intervalEnd, "not_interval_rehearsal_vacation_" + participantId);
                for (Map.Entry<Long, RehearsalVariables> entry : userRehearsalsVariables.entrySet()) {
                    RehearsalVariables rehearsalVariables = entry.getValue();
                    IntervalVar optionalRehearsalInterval = model.newOptionalIntervalVar(
                            rehearsalVariables.start,
                            rehearsalVariables.duration,
                            rehearsalVariables.end,
                            rehearsalVariables.usersPresence.get(participantId),
                            "optional_rehearsal_interval_user_" + participantId + "_rehearsal_" + entry.getKey());
                    model.addNoOverlap(List.of(intervalVar, optionalRehearsalInterval));
                }
            }
        }
    }

    private List<NonAvailability> breakNonAvailabilities(List<NonAvailability> nonAvailabilities,
            WeeklyAvailability weeklyAvailability) {
        LocalTime availabilityStart = weeklyAvailability.getStartTime();
        LocalTime availabilityEnd = weeklyAvailability.getEndTime();
        List<NonAvailability> updatedNonAvailabilities = new ArrayList<>();
        for (NonAvailability nonAvailability : nonAvailabilities) {
            LocalTime nonAvailabilityStart = nonAvailability.startTime;
            LocalTime nonAvailabilityEnd = nonAvailability.endTime;
            if (availabilityEnd.isAfter(nonAvailabilityStart) && availabilityStart.isBefore(nonAvailabilityEnd)) {
                if (availabilityStart.isAfter(nonAvailabilityStart)) {
                    updatedNonAvailabilities.add(new NonAvailability(nonAvailabilityStart, availabilityStart));
                }
                if (availabilityEnd.isBefore(nonAvailabilityEnd)) {
                    updatedNonAvailabilities.add(new NonAvailability(availabilityEnd, nonAvailabilityEnd));
                }
            } else {
                updatedNonAvailabilities.add(nonAvailability);
            }
        }
        return updatedNonAvailabilities;
    }

    private Map<Integer, List<NonAvailability>> getUserNonAvailability(long userId) {
        Map<Integer, List<NonAvailability>> nonAvailability = new HashMap<>();
        List<WeeklyAvailability> weeklyAvailabilities = weeklyAvailabilityService.getUserAvailabilities(userId);
        for (int day = 0; day < 7; day++) {
            List<NonAvailability> dailyNoneAvailability = new ArrayList<>();
            dailyNoneAvailability.add(new NonAvailability(LocalTime.of(minHour, 0), LocalTime.of(maxHour, 0)));
            nonAvailability.put(day, dailyNoneAvailability);
        }
        for (WeeklyAvailability weeklyAvailability : weeklyAvailabilities) {
            List<NonAvailability> dailyNoneAvailability = nonAvailability.get(weeklyAvailability.getWeekday());
            dailyNoneAvailability = breakNonAvailabilities(dailyNoneAvailability, weeklyAvailability);
            nonAvailability.put(weeklyAvailability.getWeekday(), dailyNoneAvailability);
        }
        return nonAvailability;
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

    private void AddDisponibilityContraints(CpModel model, Project project,
            Map<Integer, List<NonAvailability>> userNonDisponibilities,
            Map<Long, RehearsalVariables> userRehearsalsVariables, Long userId) {
        LocalDate currentDate = project.getBeginningDate();
        int currentWeekDay = (currentDate.getDayOfWeek().getValue() - 1) % 7;
        LocalDate endDate = project.getEndingDate();
        if (endDate == null) {
            endDate = project.getBeginningDate().plusDays(defaultProjectEnd / (60 * 24));
        }
        while (currentDate.isBefore(endDate.plusDays(1))) {

            for (NonAvailability nonAvailability : userNonDisponibilities.get(currentWeekDay)) {
                LocalDateTime startDateTime = currentDate.atTime(nonAvailability.startTime);
                LocalDateTime endDateTime = currentDate.atTime(nonAvailability.endTime);
                Long start = getDateTimeValue(project, startDateTime);
                Long end = getDateTimeValue(project, endDateTime);
                Long duration = Duration.between(startDateTime, endDateTime).toMinutes();
                IntVar intervalStart = model.newIntVar(start, start, "not_available_start_user_x_rehearsal_y");
                IntVar intervalEnd = model.newIntVar(end, end, "not_available_end_user_x_rehearsal_y");
                IntVar intervalDuration = model.newIntVar(duration, duration,
                        "not_available_duration_user_x_rehearsal_y");
                IntervalVar intervalVar = model.newIntervalVar(intervalStart, intervalDuration,
                        intervalEnd, "not_available_interval_user_x_rehearsal_y");
                for (Map.Entry<Long, RehearsalVariables> entry : userRehearsalsVariables.entrySet()) {
                    RehearsalVariables rehearsalVariables = entry.getValue();
                    IntervalVar optionalRehearsalInterval = model.newOptionalIntervalVar(
                            rehearsalVariables.start,
                            rehearsalVariables.duration,
                            rehearsalVariables.end,
                            rehearsalVariables.usersPresence.get(userId),
                            "optional_rehearsal_interval_user_" + userId + "_rehearsal_" + entry.getKey());
                    model.addNoOverlap(List.of(intervalVar, optionalRehearsalInterval));
                }

            }
            currentDate = currentDate.plusDays(1);
            currentWeekDay = (currentDate.getDayOfWeek().getValue() - 1) % 7;
        }

    }

    public List<CpResult> run(Long projectId, boolean recompute) {
        Project project = projectService.getProject(projectId);

        Map<Long, LocalDateTime> rehearsalNotPossible = new HashMap<>();
        if (recompute) {
            rehearsalNotPossible = cpResultService.checkPreviousResult(projectId);
        }

        System.out.println("DEBUGG : rehearsalNotPossible " + rehearsalNotPossible);

        Loader.loadNativeLibraries();

        Map<Long, RehearsalData> allRehearsals = new HashMap<>();
        Map<Long, RehearsalData> allRehearsalsToConstraint = new HashMap<>();
        for (RehearsalResponse rehearsal : rehearsalService.getProjectRehearsals(projectId)) {
            RehearsalData r = new RehearsalData(rehearsal.getId(), rehearsal.getDuration().toMinutes(), rehearsal.getDate(),
                    rehearsal.getTime(), rehearsal.getParticipantsIds());
            allRehearsals.put(rehearsal.getId(), r);
            if (rehearsal.getDate() == null || rehearsal.getTime() == null) {
                allRehearsalsToConstraint.put(rehearsal.getId(), r);
            }
        }

        CpModel model = new CpModel();
        Map<Long, RehearsalVariables> rehearsalsVariables = createVariables(model, allRehearsals, project);

        Map<Long, Map<Long, RehearsalVariables>> ParticipantsRehearsals = ParticipantsRehearsals(
                allRehearsals,
                rehearsalsVariables);

        // Constraints :
        // 1. if a user has several rehearsals then they cannot overlap
        for (Map.Entry<Long, Map<Long, RehearsalVariables>> entry : ParticipantsRehearsals.entrySet()) {
            Long participantId = entry.getKey();
            Map<Long, RehearsalVariables> userRehearsalsVariables = entry.getValue();
            for (Map.Entry<Long, RehearsalVariables> rehearsalVariablesEntry1 : userRehearsalsVariables.entrySet()) {
                for (Map.Entry<Long, RehearsalVariables> rehearsalVariablesEntry2 : userRehearsalsVariables
                        .entrySet()) {
                    Long rehearsalId1 = rehearsalVariablesEntry1.getKey();
                    Long rehearsalId2 = rehearsalVariablesEntry2.getKey();
                    if (rehearsalId1 < rehearsalId2) {
                        BoolVar rehearsalParticipation1 = rehearsalVariablesEntry1.getValue().usersPresence
                                .get(participantId);
                        BoolVar rehearsalParticipation2 = rehearsalVariablesEntry2.getValue().usersPresence
                                .get(participantId);
                        BoolVar bothParticipating = model.newBoolVar(
                                "user_" + participantId + "_both_participating_" + rehearsalId1 + "_" + rehearsalId2);

                        // https://github.com/google/or-tools/issues/1056
                        // bothParticipating = rehearsalParticipation1 AND rehearsalParticipation2
                        model.addBoolAnd(List.of(rehearsalParticipation1, rehearsalParticipation2))
                                .onlyEnforceIf(bothParticipating);
                        model.addBoolOr(List.of(rehearsalParticipation1.not(), rehearsalParticipation2.not()))
                                .onlyEnforceIf(bothParticipating.not());
                        model.addImplication(rehearsalParticipation1.not(), bothParticipating.not());
                        model.addImplication(rehearsalParticipation2.not(), bothParticipating.not());
                        IntervalVar rehearsIntervalVar1 = model.newOptionalIntervalVar(
                                rehearsalVariablesEntry1.getValue().start, rehearsalVariablesEntry1.getValue().duration,
                                rehearsalVariablesEntry1.getValue().end, bothParticipating, "rehearsal1");
                        IntervalVar rehearsIntervalVar2 = model.newOptionalIntervalVar(
                                rehearsalVariablesEntry2.getValue().start, rehearsalVariablesEntry2.getValue().duration,
                                rehearsalVariablesEntry2.getValue().end, bothParticipating, "rehearsal2");
                        model.addNoOverlap(List.of(rehearsIntervalVar1, rehearsIntervalVar2));
                    }
                }
            }
        }
        // 2. cannot happend at a dateTime previously rejected
        if (recompute) {
            rehearsalNotPossible.forEach((id, dateTime) -> {
                if (allRehearsalsToConstraint.containsKey(id)) {
                    RehearsalVariables rehearsalVariables = rehearsalsVariables.get(id);
                    RehearsalData rehearsal = allRehearsals.get(id);
                    Long start = getDateTimeValue(project, dateTime);
                    Long end = start + rehearsal.duration;
                    IntVar intervalStart = model.newIntVar(start, start, "not_start_rehearsal_" + rehearsal.id);
                    IntVar intervalEnd = model.newIntVar(end, end, "not_end_rehearsal_" + rehearsal.id);
                    IntervalVar intervalVar = model.newIntervalVar(intervalStart, rehearsalVariables.duration,
                            intervalEnd,
                            "not_interval_rehearsal_" + rehearsal.id);
                    List<IntervalVar> intervalsList = new ArrayList<>();
                    intervalsList.add(intervalVar);
                    intervalsList.add(rehearsalVariables.interval);
                    model.addNoOverlap(intervalsList);
                }
            });
        }
        // 3. has to be when the user is available
        for (Map.Entry<Long, Map<Long, RehearsalVariables>> entry : ParticipantsRehearsals.entrySet()) {
            Long userId = entry.getKey();
            AddDisponibilityContraints(model, project, getUserNonAvailability(userId),
                    entry.getValue(), userId);
            AddVacationConstraints(model, project, userId, entry.getValue());
            //TODO: not when I have other rehearsals from other projects
        }
        // 4. precedences relations
        for(Map.Entry<Long, RehearsalVariables> entry : rehearsalsVariables.entrySet()){
            Long rehearsalId = entry.getKey();
            RehearsalVariables currentRehearsalVariables = entry.getValue();
            List<Rehearsal> rehearsalPrecedence = rehearsalPrecedenceService.getRehersalsPrecedences(rehearsalId).getPrevious();
            for(Rehearsal rehearsal : rehearsalPrecedence){
                //if they both already have a date set then overide the rehearsal precedence
                if(!(allRehearsals.get(rehearsalId).date !=null && allRehearsals.get(rehearsal.getId()).date != null)){
                    RehearsalVariables previousRehearsalVariables = rehearsalsVariables.get(rehearsal.getId());
                    model.addLessOrEqual(previousRehearsalVariables.end, currentRehearsalVariables.start);
                }
            }
        }

        // We want to maximize the number of participations (here over the whole project)
        List<BoolVar> allPresenceVars = new ArrayList<>();
        for (Map.Entry<Long, RehearsalVariables> entry : rehearsalsVariables.entrySet()) {
            RehearsalVariables rehearsalVariables = entry.getValue();
            allPresenceVars.addAll(rehearsalVariables.usersPresence.values());
        }
        // Objective: Maximize the sum of all presence variables
        IntVar totalPresence = model.newIntVar(0, allPresenceVars.size(),
                "totalPresence");
        model.addEquality(totalPresence, LinearExpr.sum(allPresenceVars.toArray(new BoolVar[0])));
        model.maximize(totalPresence);

        CpSolver solver = new CpSolver();
        solver.getParameters().setLogSearchProgress(true); // logs
        model.validate(); // logs
        CpSolverStatus status = solver.solve(model);

        List<CpResult> res = new ArrayList<>();

        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            for (RehearsalData rehearsal : allRehearsals.values()) {
                RehearsalVariables schedule = rehearsalsVariables.get(rehearsal.id);

                LocalDateTime beginningDateTime = getRehearsalDate(project, solver.value(schedule.start));

                System.out.println("DEBUGG : projectId: " + projectId + " rehearsal id: " + rehearsal.id + " begining date: "
                        + beginningDateTime);

                boolean accepted = false;
                if (!allRehearsalsToConstraint.containsKey(rehearsal.id)) {
                    System.out.println("DEBUGG : rehearsal.id" + rehearsal.id);
                    System.out.println("DEBUGG : allRehearsalsToConstraint" + allRehearsalsToConstraint);
                    System.out.println("DEBUGG : allRehearsalsToConstraint.containsKey(rehearsal.id)" + allRehearsalsToConstraint.containsKey(rehearsal.id));
                    accepted = true;
                }
                CpResult cp = new CpResult(projectId, rehearsal.id, accepted, beginningDateTime);
                cpResultService.createCp(cp);
                res.add(cp);
                for (Map.Entry<Long, BoolVar> entry : schedule.usersPresence.entrySet()) {
                    Long userId = entry.getKey();
                    BoolVar userPresence = entry.getValue();
                    boolean presenceValue = solver.value(userPresence) == 1;
                    CpPresenceResult cpPresence = new CpPresenceResult(rehearsal.id, userId, presenceValue);
                    cpPresenceResultService.createCpPresence(cpPresence);
                }
                for (Map.Entry<Long, BoolVar> entry : schedule.usersPresence.entrySet()) {
                    Long user = entry.getKey();
                    BoolVar userPresence = entry.getValue();
                    boolean presenceValue = solver.value(userPresence) == 1;
                    System.out.println("DEBUGG :  User: " + user + " Presence: " + presenceValue + " " + rehearsal.id);
                }
            }
        } else {
            // TODO: how to represent this in the response ?
            System.out.println("No solution found");
        }

        return res;
    }
}
