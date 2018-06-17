import java.util.Arrays;

public class Attendance {
    private final int pop_size;
    private int[] individual_attendance;
    private int total_going;

    public Attendance(int population_size) {
        this.total_going = 0;
        this.pop_size = population_size;
        this.individual_attendance = new int[population_size];
    }

    public int[] get_individual_attendance() {return individual_attendance;
    }

    public int get_total_going() {return total_going;
    }

    public void add_attendance(int pos, int val) {
        this.individual_attendance[pos] = val;
        this.total_going += val;
    }

    public int is_crowded() {
        return (double) total_going / this.pop_size >= 0.6 ? 1 : 0;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append(this.total_going).append("\t");
        res.append(this.is_crowded()).append("\t");
        for (int att : this.individual_attendance) {
            res.append(att).append("\t");
        }
        return res.toString();
    }
}
