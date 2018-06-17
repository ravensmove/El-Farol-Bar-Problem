import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        int repetition = 1;
        List<Double> prob = new ArrayList<>();
        int question = 0;
        String strategy = "";
        int state = 0;
        int crowd = 0;
        int lambda = 0;
        int h_state = 0;
        int weeks = 0;
        int max_t = 0;
        for (int i = 0; i < args.length; i++) {
            if ("-question".equals(args[i])) {
                question = Integer.parseInt(args[i+1]);
            }
            else if ("-prob".equals(args[i])) {
                for(int j = 0; j<args[i+1].split(" ").length; j++){
                    prob.add(Double.parseDouble(args[i+1].split(" ")[j]));
                }
            }else if ("-repetitions".equals(String.valueOf(args[i]))) {
                repetition = Integer.parseInt(args[i + 1]);
            } else if ("-strategy".equals(String.valueOf(args[i]))) {
                strategy = args[i + 1];
            } else if ("-state".equals(String.valueOf(args[i]))) {
                state = Integer.parseInt(args[i + 1]);
            } else if ("-crowded".equals(String.valueOf(args[i]))) {
                crowd = Integer.parseInt(args[i + 1]);
            } else if ("-lambda".equals(String.valueOf(args[i]))) {
                lambda = Integer.parseInt(args[i + 1]);
            } else if ("-h".equals(String.valueOf(args[i]))) {
                h_state = Integer.parseInt(args[i + 1]);
            } else if ("-weeks".equals(String.valueOf(args[i]))) {
                weeks = Integer.parseInt(args[i + 1]);
            } else if ("-max_t".equals(String.valueOf(args[i]))) {
                max_t = Integer.parseInt(args[i + 1]);
            }
        }

        switch (question) {
            case 1:
                double[] probab = new double[prob.size()];
                for (int i = 0; i < prob.size(); i++) {
                    probab[i] = prob.get(i);
                }
                for (int i = 0; i < repetition; i++) {
                    Random rand = new Random();
                    double pro_b = rand.nextDouble();
                    int j;
                    for (j = 0; pro_b > 0; j++) {
                        pro_b -= probab[j];
                    }
                    System.out.println(j - 1);
                }
                break;
            case 2:

                Strategy strat = parse_strategy(strategy);
                for (int i = 0; i < repetition; i++) {
                    int next_state = strat.get_next_state(state, crowd);
                    int decision = strat.get_decision(next_state);
                    System.out.println(String.format("%d\t%d", decision, next_state));
                }
                break;
            case 3:
                for (int i = 0; i < repetition; i++) {
                    Elfarolbar bar = new Elfarolbar(lambda, h_state, weeks, max_t, 0.3, 0.5);
                    bar.run_ga();
                }
                break;
        }
    }

    public static Strategy parse_strategy(String strategy) {
        String[] strategys = strategy.split(" ");
        int h = Integer.parseInt(strategys[0]);
        Strategy s = new Strategy(h);
        for (int i = 0; i < h; i++) {
            int start_index = i * (2 * h + 1) + 1;
            s.get_p()[i] = Double.parseDouble(strategys[start_index]);
            for (int j = 0; j < h; j++) {
                s.get_a()[i][j] = Double.parseDouble(strategys[start_index + j + 1]);
            }
            for (int j = 0; j < h; j++) {
                s.get_b()[i][j] = Double.parseDouble(strategys[start_index + h + j + 1]);
            }
        }
        return s;
    }
}
