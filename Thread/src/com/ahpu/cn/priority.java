package com.ahpu.cn;

/**
 * @author JackWang
 * @date 2020/11/4 0:36
 * @e-mail:JackWang_1018@outlook.com
 * @version:1.0
 * @title:三个窗口买票。总票数为100张
 */

class Window extends Thread{
    private  static int tickets = 100;
    @Override
    public void run() {
        while (true) {
            if (tickets > 0) {
                System.out.println(getName() + "卖票：" + tickets);
                tickets--;
            } else {
                break;
            }
        }
    }
}

public class priority {
    public static void  main(String[] args) {
        Window window1 = new Window();
        Window window2 = new Window();
        Window window3 = new Window();
         window1.setName("窗口1：");
         window2.setName("窗口2：");
         window3.setName("窗口3：");
                window1.start();
                window2.start();
                window3.start();
    }
}
