import java.util.ArrayList;
import java.util.Random;

public class Strategy {
    private int h;
    private double[] p;
    private double[][] a;
    private double[][] b;

    public Strategy(int h) {
        this.h = h;
        this.p = new double[h];
        this.a = new double[h][h];
        this.b = new double[h][h];
    }

    public double[] get_p() {
        return p;
    }

    public double[][] get_a() {
        return a;
    }

    public double[][] get_b() {
        return b;
    }

    public double[][] get_trans_matrix(int crowd) {
        return crowd == 1 ? a : b;
    }

    public void randomInit() {
        Random rand = new Random();
        for (int i = 0; i < h; i++) {
            this.p[i] = rand.nextDouble();
            double new_a = 1.0;
            double new_b = 1.0;
            for (int j = 0; j < h - 1; j++) {
                double a_val = rand.nextDouble() * new_a;
                double b_val = rand.nextDouble() * new_b;
                a[i][j] = a_val;
                b[i][j] = b_val;
                new_a -= a_val;
                new_b -= b_val;
            }
            a[i][h - 1] = new_a;
            b[i][h - 1] = new_b;
        }
    }

    public int get_next_state(int current_state, int crowd) {
        double[][] trans_matrix = this.get_trans_matrix(crowd);
        Random rand = new Random();
        double pro_b = rand.nextDouble();
        int j;
        for (j = 0; pro_b > 0; j++) {
            pro_b -= trans_matrix[current_state][j];
        }
        return j - 1;

    }

    public int get_decision(int state) {
        double prob = this.p[state];
        Random rand = new Random();
        return prob > rand.nextDouble() ? 1 : 0;
    }

    public void mutate(double mutation_rate) {
        Random rand = new Random();

        for (int i = 0; i < h; i++) {
            if (rand.nextDouble() < mutation_rate) {
                double noise = rand.nextGaussian() * 0.15;
                if (this.p[i] + noise < 0)
                    this.p[i] = 0;
                if (this.p[i] + noise > 1)
                    this.p[i] = 1;
                this.p[i] += noise;
            }
        }

        mutate_matrix(rand, this.a, mutation_rate);
        mutate_matrix(rand, this.b, mutation_rate);
    }

    private void mutate_matrix(Random rand, double[][] mat, double mutation_rate) {
        for (int i = 0; i < h; i++) {
            double sum = 0.0;
            for (int j = 0; j < h; j++) {
                if (rand.nextDouble() < mutation_rate) {
                    double noise = rand.nextGaussian() * 0.15;
                    if (mat[i][j] + noise < 0)
                        mat[i][j] = 0;
                    if (mat[i][j] + noise > 1)
                        mat[i][j] = 1;
                    mat[i][j] += noise;
                }
                sum += mat[i][j];
            }
            for (int j = 0; j < h; j++) {
                mat[i][j] /= sum;
            }
        }
    }
}
