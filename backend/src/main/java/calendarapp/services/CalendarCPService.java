package calendarapp.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.IntervalVar;
import com.google.ortools.sat.LinearExpr;

import calendarapp.model.Project;
import calendarapp.response.RehearsalResponse;

@Service
public class CalendarCPService {

    //curl -X GET "http://localhost:8080/api/projects/39/calendarCP"

    @Autowired
    private ProjectService projectService;
    @Autowired
    private RehearsalService rehearsalService;

    final int periode_begining = 0;
    final int periode_end = 1100; // in minutes

    class Rehearsal {
        Long id;
        Long duration; // in minutes
        List<Long> participantsId;

        Rehearsal(Long id, Long duration, List<Long> participantsId) {
            this.id = id;
            this.duration = duration;
            this.participantsId = participantsId;
        }
    }

    class RehearsalSchedule {
        IntVar start;
        IntVar end;

        RehearsalSchedule(IntVar beginning, IntVar end) {
            this.start = beginning;
            this.end = end;
        }
    }

    /**
     * Return a list of list of rehearsal id, representing all the rehearsal a
     * participant has and therfore can not be schedule at the same time.
     * 
     * @param rehearsals         list of rehearsals on the project
     * @param rehearsalIntervals the list of rehearsals interval correspond to the
     *                           rehearsals in `rehearsals`
     * @return the list of list of rehearsal that participant has in commun
     */
    private List<List<IntervalVar>> ParticipantsRehearsals(List<Rehearsal> rehearsals,
            Map<Long, IntervalVar> rehearsalIntervals) {
        List<List<IntervalVar>> res = new ArrayList<>();
        HashMap<Long, List<IntervalVar>> data = new HashMap<>();
        for (Rehearsal rehearsal : rehearsals) {
            for (Long participant : rehearsal.participantsId) {
                data.putIfAbsent(participant, new ArrayList<>());
                data.get(participant).add(rehearsalIntervals.get(rehearsal.id));
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
    private long getEndValue(Project project) {
        if (project.getBeginningDate() == null) {
            throw new IllegalArgumentException("The project begining date need to be initialize");
        }
        if (project.getEndingDate() == null) {
            return Long.MAX_VALUE;
        }
        long durationInMinutes = java.time.Duration.between(
                project.getBeginningDate().atStartOfDay(),
                project.getEndingDate().atStartOfDay()).toMinutes();
        return durationInMinutes;
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

    public String run(Long projectId) {
        Project project = projectService.getProject(projectId);

        Loader.loadNativeLibraries();

        List<Rehearsal> allRehearsals = new ArrayList<>();
        for (RehearsalResponse rehearsal : rehearsalService.getProjectRehearsals(projectId)) {
            System.out
                    .println(rehearsal.getId() + " " + rehearsal.getDuration() + " " + rehearsal.getParticipantsIds());
            Rehearsal r = new Rehearsal(rehearsal.getId(), rehearsal.getDuration().toMinutes(),
                    rehearsal.getParticipantsIds());
            allRehearsals.add(r);
        }

        CpModel model = new CpModel();
        long periode_end = getEndValue(project);

        Map<Long, RehearsalSchedule> rehearsalsSchedule = new HashMap<>();
        Map<Long, IntervalVar> rehearsalIntervals = new HashMap<>();
        for (Rehearsal rehearsal : allRehearsals) {
            IntVar start = model.newIntVar(periode_begining, periode_end - rehearsal.duration,
                    "start_rehearsal_" + rehearsal.id);
            IntVar end = model.newIntVar(periode_begining, periode_end, "end_rehearsal_" + rehearsal.id);
            IntervalVar interval = model.newIntervalVar(start, LinearExpr.constant(rehearsal.duration), end,
                    "interval_rehearsal_" + rehearsal.id);
            rehearsalsSchedule.put(rehearsal.id, new RehearsalSchedule(start, end));
            rehearsalIntervals.put(rehearsal.id, interval);
        }

        //Constraints :
        //1. if a user has several rehearsals then they cannot overlap
        List<List<IntervalVar>> intervalsConstraintsList = ParticipantsRehearsals(allRehearsals,
                rehearsalIntervals);
        for (List<IntervalVar> intervalsList : intervalsConstraintsList) {
            model.addNoOverlap(intervalsList);
        }
        //2. they cannot happend at night betwen 11PM and 7AM
        for (Rehearsal rehearsal : allRehearsals) {
            //https://stackoverflow.com/questions/59215712/opposite-of-addmoduloequality
            RehearsalSchedule schedule = rehearsalsSchedule.get(rehearsal.id);
            //model.addGreaterOrEqual(schedule.start % 1440, 420) (1440 minutes in on day and 420 minutes in 7 hours)
            IntVar remainder_strat = model.newIntVar(0, 1439, "modulo_start_" + rehearsal.id);
            // remainder = schedule.start % 1440
            model.addModuloEquality(remainder_strat, schedule.start, LinearExpr.constant(24*60));
            model.addGreaterOrEqual(remainder_strat, 7*60);
            //model.addLessOrEqual(schedule.end % 1440, 1380) (1280 minutes in 23 hours)
            IntVar remainder_end = model.newIntVar(0, 1439, "modulo_end_" + rehearsal.id);
            model.addModuloEquality(remainder_end, schedule.end, LinearExpr.constant(24*60));
            model.addLessOrEqual(remainder_end, 23*60);

        }

        CpSolver solver = new CpSolver();
        CpSolverStatus status = solver.solve(model);

        String res = "";

        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            for (Rehearsal rehearsal : allRehearsals) {
                RehearsalSchedule schedule = rehearsalsSchedule.get(rehearsal.id);

                res += "\n Rehearsal " + rehearsal.id;
                res += "\n Start: " + solver.value(schedule.start) + " end: " + solver.value(schedule.end);
                res += "\n scheduled at: " + getRehearsalDate(project, solver.value(schedule.start));
            }
        } else {
            res = "No solution found";
        }

        return res;
    }
}
