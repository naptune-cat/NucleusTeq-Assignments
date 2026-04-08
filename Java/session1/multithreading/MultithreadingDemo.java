package session1.multithreading;

//multithreading allows us to achieve concurrency i.e. context switching.

// No thread has to wait for resources to be released by another thread before starting its execution.


//all the threads extends Thread class as we can see from signature that Thread should be an abstract class
//we have to always override run() method 
class TaskA extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            System.out.println("^^^^^^TASK A EXECUTING^^^^^^");
        }
    }
}
class TaskB extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            System.out.println("::::::TASK B EXECUTING::::::");
        }
    }
}
public class MultithreadingDemo {
    public static void main(String[] args) {

        //calling a thread
        //firstly we make object for thread
        TaskA tA = new TaskA();

        //always have to use start
        tA.start();


        //tA.run(); //we should never call run directly because then it'll act as a normalclass and execute sequentially

        TaskB tB = new TaskB();
        tB.start();

        //in the output we will see that both the threads will work almost parallely. No thread will wait for another thread to finish its execution first
    }
}
