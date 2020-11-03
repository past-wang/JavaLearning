package com.ahpu.cn;

/**
 * @author JackWang
 * @date 2020/11/3 13:12
 * @e-mail:JackWang_1018@outlook.com
 * @version:1.0
 * @title:主要介绍一下常用的Thread类中的方法
 */

/**
 *  测试Thread类中的常用方法：
 *  1.start():启动当前线程：调用当前线程的run()方法
 *  2.run():通常需要重写Thread类中的此方法，将创建的线程要执行的操作声明在此方法中
 *  3.currentThread():静态方法，返回执行当前代码的线程
 *  4.getName():获取当前线程的名字
 *  5.setName():设置当前线程的名字
 *  6.yield():释放当前cpu的执行权
 *  7.join():在线程A中调用线程B的join()方法，此时线程a进入阻塞状态，知道线程b完全执行完之后，线程a才结束阻塞状态
 *  8.stop():强制结束当前进程,（不建议这么强制结束进程，方法已过时)
 *  9.sleep():让当前线程,静态方法，可以通过Thread直接调用
 */


public class TestThread {
    public static void main(String[] args) {
        TestThread1 t1 = new TestThread1();//创建一个TestThread1对象，来调用run中的操作
        t1.setName("线程1");
        t1.start();

        TestThread2 t2 = new TestThread2();
        t2.setName("线程2");
        t2.start();


        Thread.currentThread().setName("主线程");

        for (int i = 1; i < 100; i++) {
            if (i % 2 == 0) {
                System.out.println(Thread.currentThread().getName() + ":" + i);
            }
            if (i%20==0){
                try {
                    t1.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}

class TestThread1 extends Thread {
    @Override
    public void run() {

        for (int i = 0; i < 100; i++) {
            if (i % 2 == 0) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(currentThread().getName() + ":" + i);
            }
        }
    }
}

class TestThread2 extends Thread {
    @Override
    public void run() {

        for (int i = 0; i < 100; i++) {
            if (i % 2 == 0) {
                System.out.println(currentThread().getName() + ":" + i);
            }if (i%20==0){
                yield();
            }

        }
    }
}
