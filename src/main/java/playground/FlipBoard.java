package playground;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlipBoard {

  public static void main(String[] args) {
    int n = 3;
    List<List<Integer>> orders = new ArrayList<>();
    orders.add(new ArrayList<>(Arrays.asList(1, 3, 2, 0)));
    for (int i = 1; i < n; ++i) {
      int dim = 1 << (i + 1);
      int halfDim = dim / 2;
      int[][][] half = new int[dim / 2][dim][2];
      for (int j = 0; j < halfDim; j++) {
        for (int k = 0; k < dim; k++) {
          half[j][k][0] = k + (dim - 1 - j) * dim;
          half[j][k][1] = k + j * dim;
        }
      }
      int[][][] quarter = new int[halfDim][halfDim][4];
      for (int j = 0; j < halfDim; j++) {
        for (int k = 0; k < halfDim; k++) {
          quarter[j][k][0] = half[j][dim - 1 - k][1];
          quarter[j][k][1] = half[j][dim - 1 - k][0];
          quarter[j][k][2] = half[j][k][0];
          quarter[j][k][3] = half[j][k][1];
        }
      }
      List<Integer> order = new ArrayList<>();
      for (int j = 0; j < orders.get(i - 1).size(); ++j) {
        int v = orders.get(i - 1).get(j);
        int x = v / halfDim;
        int y = v % halfDim;
        if ((x + y) % 2 == 0) {
          order.add(quarter[x][y][0]);
          order.add(quarter[x][y][1]);
          order.add(quarter[x][y][2]);
          order.add(quarter[x][y][3]);
        } else {
          order.add(quarter[x][y][3]);
          order.add(quarter[x][y][2]);
          order.add(quarter[x][y][1]);
          order.add(quarter[x][y][0]);
        }
      }
      orders.add(order);
    }
    Integer[] res = orders.get(n-1).toArray(new Integer[0]);
    for (int i = 0; i < res.length; ++i) {
      res[i]++;
    }
    System.out.println(Arrays.toString(res));
  }
}



