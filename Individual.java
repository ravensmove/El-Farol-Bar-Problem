import java.util.ArrayList;

public class Individual {
    private Strategy strat;
    private ArrayList<Integer> state;
    private ArrayList<Integer> decision;
    private int payoff;

    public Individual(Strategy strat) {
        this.strat = strat;
        this.state = new ArrayList<>();
        this.decision = new ArrayList<>();
        this.state.add(0);
    }

    public void set_strategy(Strategy strat) {
        this.strat = strat;
    }

    public Strategy get_strategy() {
        return strat;
    }

    public ArrayList<Integer> get_state() {
        return state;
    }

    public ArrayList<Integer> get_decision() {
        return decision;
    }

    public void init_strategy() {
        this.strat.randomInit();
    }

    public int get_decision_from_week(int week, int state) {
        int decision;
        try {
            decision = this.decision.get(week);
        } catch (Exception ex) {
            decision = this.strat.get_decision(state);
            this.decision.add(decision);
        }
        return decision;
    }

    public int get_state_from_week(int week, int crowd) {
        int state;
        try {
            state = this.state.get(week);
        } catch (Exception ex) {
            state = this.strat.get_next_state(this.state.get(this.state.size() - 1), crowd);
            this.state.add(state);
        }
        return state;
    }

    public void set_payoff(int payoff) {
        this.payoff = payoff;
    }

    public int get_payoff() {
        return payoff;
    }
}
