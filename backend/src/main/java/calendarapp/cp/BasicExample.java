package calendarapp.cp;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import java.util.HashMap;

import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.IntervalVar;
import com.google.ortools.sat.LinearExpr;

public class BasicExample {

    static final int periode_begining = 0;
    static final int periode_end = 1100; // in minutes

    static class Rehearsal {
        int id;
        int duration; // in minutes
        List<Integer> participantsId;

        Rehearsal(int id, int duration, List<Integer> participantsId) {
            this.id = id;
            this.duration = duration;
            this.participantsId = participantsId;
        }
    }

    static class RehearsalSchedule {
        IntVar start;
        IntVar end;

        RehearsalSchedule(IntVar beginning, IntVar end) {
            this.start = beginning;
            this.end = end;
        }
    }

    public static void main(String[] args) {
        Loader.loadNativeLibraries();

        final List<Rehearsal> allRehearsals = Arrays.asList(
                new Rehearsal(1, 30, Arrays.asList(1, 2, 3)),
                new Rehearsal(2, 120, Arrays.asList(1, 5, 6, 7, 8)));

        CpModel model = new CpModel();

        Map<Integer, RehearsalSchedule> rehearsalsSchedule = new HashMap<>();
        Map<Integer, IntervalVar> rehearsalIntervals = new HashMap<>();
        for (Rehearsal rehearsal : allRehearsals) {
            IntVar start = model.newIntVar(periode_begining, periode_end - rehearsal.duration,
                    "start_rehearsal_" + rehearsal.id);
            IntVar end = model.newIntVar(periode_begining, periode_end, "end_rehearsal_" + rehearsal.id);
            IntervalVar interval = model.newIntervalVar(start, LinearExpr.constant(rehearsal.duration), end, "interval_rehearsal_" + rehearsal.id);
            rehearsalsSchedule.put(rehearsal.id, new RehearsalSchedule(start, end));
            rehearsalIntervals.put(rehearsal.id, interval);
        }

        // constraints
        for (Rehearsal rehearsal : allRehearsals) {
            
        }

        CpSolver solver = new CpSolver();
        CpSolverStatus status = solver.solve(model);

        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            for (Rehearsal rehearsal : allRehearsals) {
                RehearsalSchedule schedule = rehearsalsSchedule.get(rehearsal.id);
                
                System.out.println("Rehearsal " + rehearsal.id);
                System.out.println("Start: " + solver.value(schedule.start) + " end: " + solver.value(schedule.end));
            }
        } else {
            System.out.println("No solution found");
        }
    }
}
