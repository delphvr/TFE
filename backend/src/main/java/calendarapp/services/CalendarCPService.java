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

    private boolean isCommunParticipant(List<Long> participantsId1, List<Long> participantsId2) {
        for (Long id1 : participantsId1) {
            for (Long id2 : participantsId2) {
                if (id1 == id2) {
                    return true;
                }
            }
        }
        return false;
    }

    // TODO no overlap only one by one or all participant in commun other wise
    // pairwise some rehearsals could be at the same time but arent'
    private List<List<IntervalVar>> rehearsalsCommunParticipant(List<Rehearsal> rehearsals,
            Map<Long, IntervalVar> rehearsalIntervals) {
        List<List<IntervalVar>> res = new ArrayList<>();
        for (int i = 0; i < rehearsals.size(); i++) {
            List<IntervalVar> rehearsalsList = new ArrayList<>();
            for (int j = i + 1; j < rehearsals.size(); j++) {
                Rehearsal rehearsal1 = rehearsals.get(i);
                Rehearsal rehearsal2 = rehearsals.get(j);
                if (isCommunParticipant(rehearsal1.participantsId, rehearsal2.participantsId)) {
                    rehearsalsList.add(rehearsalIntervals.get(rehearsal2.id));
                }
            }
            if (!rehearsalsList.isEmpty()) {
                rehearsalsList.add(rehearsalIntervals.get(rehearsals.get(i).id));
                res.add(rehearsalsList);
            }

        }
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
    private LocalDateTime getRehearsalDate(Project project, long minutesFromBeginning) {
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

        // constraints
        List<List<IntervalVar>> intervalsConstraintsList = rehearsalsCommunParticipant(allRehearsals,
                rehearsalIntervals);
        for (List<IntervalVar> intervalsList : intervalsConstraintsList) {
            model.addNoOverlap(intervalsList);
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
