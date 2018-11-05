package playground;

import java.util.ArrayList;
import java.util.List;

public class MaxDiscount {


  static public class Order {

    public List<Integer> products = new ArrayList<>();

    public Order addProduct(int product) {
      products.add(product);
      return this;
    }

    public Order() {
    }

    public Order(Order other) {
      products.addAll(other.products);
    }
  }

  static public class OrderSolution {

    public List<Order> orders = new ArrayList<>();

    public OrderSolution addOrder(Order order) {
      orders.add(order);
      return this;
    }

    public OrderSolution() {
    }

    public OrderSolution(OrderSolution other) {
      for (int i = 0; i < other.orders.size(); ++i) {
        Order order = new Order(other.orders.get(i));
        orders.add(order);
      }
    }
  }

  static public class FixedOrderNumSolutions {

    public List<OrderSolution> orderSolutions = new ArrayList<>();

    public FixedOrderNumSolutions addSolution(OrderSolution orderSolution) {
      orderSolutions.add(orderSolution);
      return this;
    }
  }

  static public class FixedProductNumSolutions {

    public List<FixedOrderNumSolutions> solutions = new ArrayList<>();

    public FixedProductNumSolutions addFixedOrderNumSolutions(FixedOrderNumSolutions fixedOrderNumSolutions) {
      solutions.add(fixedOrderNumSolutions);
      return this;
    }
  }

  private static FixedProductNumSolutions currentSolutions;

  private static void generateSolutions() {
    // init condition
    // 1 product, 1 order
    Order order = new Order().addProduct(0);
    OrderSolution orderSolution = new OrderSolution().addOrder(order);
    FixedOrderNumSolutions fixedOrderNumSolutions = new FixedOrderNumSolutions().addSolution(orderSolution);
    currentSolutions = new FixedProductNumSolutions().addFixedOrderNumSolutions(fixedOrderNumSolutions);

    FixedProductNumSolutions tempSolutions;
    for (int p = 1; p < products.length; ++p) {
      tempSolutions = new FixedProductNumSolutions();
      for (int o = 0; o <= p; ++o) {
        fixedOrderNumSolutions = new FixedOrderNumSolutions();
        // insert product p to existed order
        if (o < currentSolutions.solutions.size()) {
          for (int i = 0; i < currentSolutions.solutions.get(o).orderSolutions.size(); ++i) {
            OrderSolution temp = currentSolutions.solutions.get(o).orderSolutions.get(i);
            for (int j = 0; j < temp.orders.size(); ++j) {
              OrderSolution cur = new OrderSolution(temp);
              cur.orders.get(j).addProduct(p);
              fixedOrderNumSolutions.addSolution(cur);
            }
          }
        }
        // create a new order for product p
        if (o > 0) {
          for (int i = 0; i < currentSolutions.solutions.get(o - 1).orderSolutions.size(); ++i) {
            OrderSolution temp = currentSolutions.solutions.get(o - 1).orderSolutions.get(i);
            OrderSolution cur = new OrderSolution(temp);
            cur.addOrder(new Order().addProduct(p));
            fixedOrderNumSolutions.addSolution(cur);
          }
        }
        tempSolutions.addFixedOrderNumSolutions(fixedOrderNumSolutions);
      }
      currentSolutions = tempSolutions;
    }
  }

  private static void displaySolutions() {
    for (int i = 0; i < currentSolutions.solutions.size(); ++i) {
      System.out.println((i + 1) + " Orders:");
      FixedOrderNumSolutions fixedOrderNumSolutions = currentSolutions.solutions.get(i);
      for (int j = 0; j < fixedOrderNumSolutions.orderSolutions.size(); ++j) {
        System.out.print("Solution " + j + ": ");
        OrderSolution orderSolution = fixedOrderNumSolutions.orderSolutions.get(j);
        for (int k = 0; k < orderSolution.orders.size(); ++k) {
          Order order = orderSolution.orders.get(k);
          for (int l = 0; l < order.products.size(); ++l) {
            System.out.print(order.products.get(l) + ",");
          }
          System.out.print(" | ");
        }
        System.out.println("");
      }
    }
  }

  public static class CouponSolution {

    public int[] couponIdx;
    public double cost = 1000000.0;

    public CouponSolution() {
    }

    public CouponSolution(int orderNum) {
      couponIdx = new int[orderNum];
    }

    public CouponSolution(int orderNum, double initCost) {
      couponIdx = new int[orderNum];
      cost = initCost;
    }

    public CouponSolution assign(CouponSolution other) {
      if (other.couponIdx == null) {
        couponIdx = null;
      } else {
        if (couponIdx == null || couponIdx.length != other.couponIdx.length) {
          couponIdx = new int[other.couponIdx.length];
        }
        for (int i = 0; i < other.couponIdx.length; ++i) {
          couponIdx[i] = other.couponIdx[i];
        }
      }
      cost = other.cost;
      return this;
    }
  }

  private static void searchOptimizedSolution() {
    CouponSolution globalOptimizeCouponSolution = new CouponSolution();
    OrderSolution globalOptimizedOrderSolution = null;
    for (int i = 0; i < currentSolutions.solutions.size(); ++i) {
      FixedOrderNumSolutions fixedOrderNumSolutions = currentSolutions.solutions.get(i);
      for (int j = 0; j < fixedOrderNumSolutions.orderSolutions.size(); ++j) {
        OrderSolution orderSolution = fixedOrderNumSolutions.orderSolutions.get(j);
        List<Integer> orderCosts = new ArrayList<>();
        for (int k = 0; k < orderSolution.orders.size(); ++k) {
          Order order = orderSolution.orders.get(k);
          int sum = 0;
          for (int l = 0; l < order.products.size(); ++l) {
            sum += products[order.products.get(l)].price;
          }
          orderCosts.add(sum);
        }
        CouponSolution currentCouponSolution = new CouponSolution(orderCosts.size(), 0);
        CouponSolution localOptimizedCouponSolution = new CouponSolution(orderCosts.size());
        searchCoupons(orderCosts, 0, currentCouponSolution, localOptimizedCouponSolution);
        if (localOptimizedCouponSolution.cost < globalOptimizeCouponSolution.cost) {
          globalOptimizeCouponSolution = localOptimizedCouponSolution;
          globalOptimizedOrderSolution = orderSolution;
        }
      }
    }
    System.out.println("Optimized order solution: ");
    for (int i = 0; i < globalOptimizedOrderSolution.orders.size(); ++i) {
      System.out.println("Order " + i + ":");
      Order order = globalOptimizedOrderSolution.orders.get(i);
      int subTotal = 0;
      for (int j = 0; j < order.products.size(); ++j) {
        subTotal += products[order.products.get(j)].price;
        System.out.println(products[order.products.get(j)].name + " price: " + products[order.products.get(j)].price);
      }
      System.out.println("Subtotal: " + subTotal);
      if (globalOptimizeCouponSolution.couponIdx[i] != -1) {
        System.out.println("Used coupon: " + coupons[globalOptimizeCouponSolution.couponIdx[i]].threshold + "-"
                           + coupons[globalOptimizeCouponSolution.couponIdx[i]].discount);
        double actualCost = subTotal - coupons[globalOptimizeCouponSolution.couponIdx[i]].discount;
        if (actualCost < 150) {
          System.out.println("Additional postage: 10");
          actualCost += 10;
        }
        System.out.println("Actual cost:" + actualCost);
      } else {
        System.out.println("No coupon used");
        System.out.println("Actual cost:" + subTotal);
      }
    }
    System.out.println("Total cost:" + globalOptimizeCouponSolution.cost);
  }

  private static void searchCoupons(List<Integer> orderCosts, int idx,
                                    CouponSolution currentSolution,
                                    CouponSolution optimizedSolution) {
    if (idx == orderCosts.size()) {
      if (currentSolution.cost < optimizedSolution.cost) {
        optimizedSolution.assign(currentSolution);
      }
      return;
    }

    // no coupon used
    double cur = orderCosts.get(idx);
    if (cur < 150) {
      cur += 10;
    }
    currentSolution.cost += cur;
    currentSolution.couponIdx[idx] = -1;
    searchCoupons(orderCosts, idx + 1, currentSolution, optimizedSolution);
    currentSolution.cost -= cur;

    for (int i = 0; i < coupons.length; ++i) {
      cur = orderCosts.get(idx);
      if (cur >= coupons[i].threshold && coupons[i].count > 0) {
        cur -= coupons[i].discount;
        if (cur < 150) {
          cur += 10;
        }
        currentSolution.couponIdx[idx] = i;
        currentSolution.cost += cur;
        coupons[i].count--;
        searchCoupons(orderCosts, idx + 1, currentSolution, optimizedSolution);
        currentSolution.cost -= cur;
        coupons[i].count++;
      }
    }
  }

  public static class Coupon {

    public int threshold;
    public double discount;
    public int count;

    public Coupon(int t, double d, int c) {
      threshold = t;
      discount = d;
      count = c;
    }
  }

  public static class Product {

    public String name;
    public int price;

    public Product(String n, int p) {
      name = n;
      price = p;
    }
  }

  private static Product[] products = {new Product("米家 LED 智能台灯", 149), new Product("米家声波电动牙刷头(型)3支装", 59),
                                       new Product("净水器滤芯两年套装", 768), new Product("米家扫地机器人尘盒滤网", 39),
                                       new Product("米家电动剃须刀 黑色", 179), new Product("米家扫地机器人主刷", 39),
                                       new Product("小米插线板5位国标组合插孔", 39)};

  private static Coupon[] coupons = {new Coupon(999, 111, 1), new Coupon(499, 66, 1),
                                     new Coupon(100, 20, 4), new Coupon(100, 18, 3),
                                     new Coupon(100, 16, 1), new Coupon(100, 12, 4),
                                     new Coupon(0, 11.11, 1)};


  public static void main(String[] args) {
    generateSolutions();
    //displaySolutions();
    searchOptimizedSolution();
  }
}
