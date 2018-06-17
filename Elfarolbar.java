import java.util.*;
import java.io.*;

public class Elfarolbar {
    private int lambda;
    private int h;
    private int weeks;
    private int maxT;
    private ArrayList<Individual> population;
    private double crossover_rate;
    private double mutation_rate;
    private ArrayList<Attendance> current_attendance;


    public Elfarolbar(int lambda, int h, int weeks, int maxT, double crossover_rate, double mutation_rate) {
        this.lambda = lambda;
        this.h = h;
        this.weeks = weeks;
        this.maxT = maxT;
        this.crossover_rate = crossover_rate;
        this.mutation_rate = mutation_rate;
    }

    public void generate_init_population() {
        this.population = new ArrayList<>();
        for (int i = 0; i < this.lambda; i++) {
            Individual ind = new Individual(new Strategy(this.h));
            ind.init_strategy();
            population.add(ind);
        }

        this.eval_population(this.population);
    }

    private void eval_population(ArrayList<Individual> individuals) {
        this.current_attendance = this.simulate_attendance(individuals);
        this.computePayoffs(current_attendance, individuals);
    }


    private ArrayList<Attendance> simulate_attendance(ArrayList<Individual> individuals) {
        ArrayList<Attendance> attendance = new ArrayList<>();
        for (int i = 0; i < weeks; i++) {
            Attendance attend = new Attendance(this.lambda);
            for (int j = 0; j < this.lambda; j++) {
                Individual individual = individuals.get(j);
                if (i == 0) {
                    attend.add_attendance(j, individual.get_decision_from_week(i, 0));
                } else {
                    int crowd = attendance.get(i - 1).is_crowded();
                    int next_state = individual.get_state_from_week(i, crowd);
                    attend.add_attendance(j, individual.get_decision_from_week(i, next_state));
                }
            }
            attendance.add(attend);

        }
        return attendance;
    }

    private void computePayoffs(ArrayList<Attendance> attendance, ArrayList<Individual> individuals) {
        for (int i = 0; i < this.lambda; i++) {
            int payoff = 0;
            Individual current_individual = individuals.get(i);
            for (int j = 0; j < this.weeks; j++) {
                int crowd = attendance.get(j).is_crowded();
                if (current_individual.get_decision().get(j) == 1 && crowd == 0 || current_individual.get_decision().get(j) == 0 && crowd == 1) {
                    payoff += 1;
                }
            }
            current_individual.set_payoff(payoff);
        }
    }

    // Fitness Selection
    public Individual fitness_selection() {
        double[] norm_fit = new double[this.lambda];
        double fit_total = 0.0;
        for (int i = 0; i < this.lambda; i++) {
            fit_total += this.population.get(i).get_payoff();
            norm_fit[i] = this.population.get(i).get_payoff();
        }
        for (int i = 0; i < this.lambda; i++) {
            norm_fit[i] = norm_fit[i] / fit_total;
        }

        Random rand = new Random();
        double prob = rand.nextDouble();
        int i;
        for (i = 0; prob > 0; i++) {
            prob -= norm_fit[i];
        }

        return this.population.get(i - 1);
    }

    //    Crossover
    public Strategy crossover(double crossover_rate, Strategy parent_a, Strategy parent_b) {
        Strategy os = new Strategy(this.h);
        for (int i = 0; i < this.h; i++) {
            os.get_p()[i] = parent_a.get_p()[i] * crossover_rate +
                    (1 - crossover_rate) * parent_b.get_p()[i];
            for (int j = 0; j < this.h; j++) {
                os.get_a()[i][j] = parent_a.get_a()[i][j] * crossover_rate +
                        (1 - crossover_rate) * parent_b.get_a()[i][j];
                os.get_b()[i][j] = parent_a.get_b()[i][j] * crossover_rate +
                        (1 - crossover_rate) * parent_b.get_b()[i][j];
            }
        }
        return os;
    }

    public Individual best_individual() {
        Individual best = this.population.get(0);
        for (Individual ind : this.population) {
            if (best.get_payoff() < ind.get_payoff()) {
                best = ind;
            }
        }
        return best;
    }

    public void select_next_population(ArrayList<Individual> oss) {
        ArrayList<Individual> cont = new ArrayList<>();
        cont.addAll(this.population);
        cont.addAll(oss);
        cont = sort_cont(cont);
        this.population.clear();
        for (int i = 0; i < this.lambda; i++) {
            this.population.add(cont.get(i));
        }
    }

    public ArrayList<Individual> sort_cont(ArrayList<Individual> cont) {
        int k;
        do {
            k = 0;
            for (int i = 0; i < cont.size() - 1; i++) {
                if (cont.get(i).get_payoff() < cont.get(i + 1).get_payoff()) {
                    Individual aux = cont.get(i);
                    cont.set(i, cont.get(i + 1));
                    cont.set(i + 1, aux);
                    k = 1;
                }
            }
        } while (k == 1);
        return cont;
    }

    public ArrayList<Object> run_ga() {
        Individual best_individual = null;
        int generation_count = 0;

        this.generate_init_population();
        for (int i = 0; i < this.maxT; i++) {
            best_individual = best_individual();
            ArrayList<Individual> new_population = new ArrayList<>();
            for (int j = 0; j < this.lambda; j++) {
                Individual parent_a = this.fitness_selection();
                Individual parent_b = this.fitness_selection();

                Strategy os_strategy = crossover(this.crossover_rate, parent_a.get_strategy(), parent_b.get_strategy());
                os_strategy.mutate(this.mutation_rate);
                new_population.add(new Individual(os_strategy));
            }
            this.eval_population(new_population);
            this.select_next_population(new_population);
            this.eval_population(this.population);

            for (int k = 0; k < this.current_attendance.size(); k++) {
                System.out.println(k + "\t" + i + "\t" + current_attendance.get(k).toString());

            }
        }
        ArrayList<Attendance> weekly_attendance = this.simulate_attendance(population);
        ArrayList<Object> results = new ArrayList<>();
        results.add(weekly_attendance);
        results.add(maxT);
        return results;
    }
}
