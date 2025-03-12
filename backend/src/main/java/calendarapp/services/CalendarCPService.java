package calendarapp.services;

import java.util.ArrayList;
import java.util.Arrays;
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

@Service
public class CalendarCPService {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private RehearsalService rehearsalService;

    final int periode_begining = 0;
    final int periode_end = 1100; // in minutes

    class Rehearsal {
        int id;
        int duration; // in minutes
        List<Integer> participantsId;

        Rehearsal(int id, int duration, List<Integer> participantsId) {
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

    private boolean isCommunParticipant(List<Integer> participantsId1, List<Integer> participantsId2) {
        for (int id1 : participantsId1) {
            for (int id2 : participantsId2) {
                if (id1 == id2) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<List<IntervalVar>> rehearsalsCommunParticipant(List<Rehearsal> rehearsals,
            Map<Integer, IntervalVar> rehearsalIntervals) {
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
     * 0 and than we add minutes. (So get the duration of the project in minutes)
     * 
     * @param projectId the id of the project
     * @return the end value (number of minutes in the project periode)
     * @throws IllegalArgumentException if no project is found with the given id,
     *                                  or the project begining date is not
     *                                  initialize
     */
    private long getEndValue(Long projectId) {
        Project project = projectService.getProject(projectId);

        if (project.getBeginningDate() == null) {
            throw new IllegalArgumentException("The project begining date need to be initialize");
        }

        long durationInMinutes = java.time.Duration.between(
                project.getBeginningDate().atStartOfDay(),
                project.getEndingDate().atStartOfDay()).toMinutes();

        return durationInMinutes;
    }

    public String run(Long projectId) {
        Loader.loadNativeLibraries();

        final List<Rehearsal> allRehearsals = Arrays.asList(
                new Rehearsal(1, 30, Arrays.asList(1, 2, 3)),
                new Rehearsal(2, 120, Arrays.asList(1, 5, 6, 7, 8)),
                new Rehearsal(3, 160, Arrays.asList(4)),
                new Rehearsal(4, 160, Arrays.asList(2, 4)));

        CpModel model = new CpModel();
        long periode_end = getEndValue(projectId);

        Map<Integer, RehearsalSchedule> rehearsalsSchedule = new HashMap<>();
        Map<Integer, IntervalVar> rehearsalIntervals = new HashMap<>();
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
            }
        } else {
            res = "No solution found";
        }

        return res;
    }
}
