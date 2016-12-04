import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

// sort qs by priority
// implement algorithm
// push - git

public class CPUScheduling {

    public static class QueueInfo implements Comparable<QueueInfo>{
        int priority;
        String algorithm;
        int numProcess;
        ArrayList<ProcessInfo> processes = new ArrayList<>();
        @Override
        public int compareTo(QueueInfo q) {
            if (this.priority < q.priority) { // descending leh!!
                return 1;
            }
            else if(this.priority > q.priority){
                return -1;
            }
            return 0;
        }
    }

    public static class ProcessInfo {
        String name;
        Integer arrival;
        Integer burst, burstCopy; // copying for round robin
        Integer waiting;
        Integer turnaround;
    }

    public static class Output {
        ArrayList<ProcessInfo> processes;
        double averageTurnaround;
    }

    public static Output FCFS(ArrayList<ProcessInfo> processes) {
        Output o = new Output();
        Collections.sort(processes, (p1, p2) -> {
            return  p1.arrival - p2.arrival; // ascending
        });
        int clock = 0, turnaroundSum = 0;
        for (int i = 0; i < processes.size(); i++) {
            processes.get(i).waiting = clock - processes.get(i).arrival; // waiting = clock - arrival
            clock += processes.get(i).burst;
            clock++; // adding context switch
            processes.get(i).turnaround = processes.get(i).waiting + processes.get(i).burst; // turnaround = waiting + burst
            turnaroundSum += processes.get(i).turnaround;
        }
        o.processes = processes;
        o.averageTurnaround = turnaroundSum / processes.size(); // yay us... nope... yes was gee's bad sorry
        return o;
    }

    public static Output SJF(ArrayList<ProcessInfo> processes) {
        Output o = new Output();
        Collections.sort(processes, (p1, p2) -> {
            return  p1.arrival - p2.arrival; // ascending
        });
        int clock = 0, turnaroundSum = 0, numProcesses = processes.size();
        ArrayList<ProcessInfo> theEnd = new ArrayList<>();
        processes.get(0).waiting = clock - processes.get(0).arrival; // finishing off first process
        clock += processes.get(0).burst;
        clock++; // adding context switch
        processes.get(0).turnaround = processes.get(0).waiting + processes.get(0).burst; // turnaround = waiting + burst
        turnaroundSum += processes.get(0).turnaround;
        theEnd.add(processes.get(0));
        for (int i = 1; i < numProcesses; i++) {
            ArrayList<ProcessInfo> temp = new ArrayList<>();
            for (int j = 0; j < processes.size(); j++) {
                if (processes.get(j).arrival <= clock && processes.get(j).turnaround == 0)
                    temp.add(processes.get(j));
            }
            Collections.sort(temp, (p1, p2) -> {
                return  p1.burst - p2.burst; // ascending
            });
            theEnd.add(temp.get(0));
            theEnd.get(i).waiting = clock - theEnd.get(i).arrival; // waiting = clock - arrival
            clock += theEnd.get(i).burst;
            clock++; // adding context switch
            theEnd.get(i).turnaround = theEnd.get(i).waiting + theEnd.get(i).burst; // turnaround = waiting + burst
            turnaroundSum += theEnd.get(i).turnaround;
        }
        o.processes = theEnd;
        o.averageTurnaround = turnaroundSum / numProcesses;
        return o;
    }

    public static Output RR(ArrayList<ProcessInfo> processes) {
        Output o = new Output();
        Collections.sort(processes, (p1, p2) -> {
            return  p1.arrival - p2.arrival; // ascending
        });
        int clock = 0, turnaroundSum = 0, numProcesses = processes.size();
        boolean done = false;
        ArrayList<ProcessInfo> out = new ArrayList<>(processes); // shallow copying
        while (!processes.isEmpty()) {
            processes.get(0).burst -= 2;
            if (processes.get(0).burst <= 0) {
                processes.get(0).waiting = clock - processes.get(0).arrival; // waiting = clock - arrival
                processes.get(0).turnaround = processes.get(0).waiting + processes.get(0).burstCopy;
                turnaroundSum += processes.get(0).turnaround;
                processes.remove(processes.get(0)); done = true;
            } else {
                done = false;
            }
            clock += 2; // quantum time = 2
            clock++; // context switch
            if (!done) {
                processes.add(processes.get(0)); // adding first process again to end of array
                out.add(processes.get(0)); // adding to temp array to print later lol
                processes.remove(0); // removing first process after decrementing burst time
            }
//            System.out.println("after");
            for (int i = 0; i < processes.size(); i++) {
                System.out.print(processes.get(i).name + " ");
            }
            System.out.println();
        }
        o.processes = out;
        o.averageTurnaround = turnaroundSum / numProcesses;
        return o;
    }

    public static void main(String args[]) {
        File input = new File("C:\\Users\\menna\\Desktop\\input.txt");

        int numQueues = 0;// Scanner cin = new Scanner(System.in);
        ArrayList<QueueInfo> qs = new ArrayList<>();


        try(Scanner cin = new Scanner(input)) {
            while (cin.hasNext()) {
                if (!cin.hasNext()) break;

                numQueues = Integer.parseInt(cin.nextLine());

                for (int i = 0; i < numQueues; i++) { // enter queues and their info
                    QueueInfo q = new QueueInfo();
                    q.priority = Integer.parseInt(cin.nextLine()); // gee does approve now lol
                    q.algorithm = cin.nextLine();
                    q.numProcess = Integer.parseInt(cin.nextLine());
                    qs.add(q);
                }

                for (int i = 0; i < numQueues; i++) { // enter processes of each queue
                    QueueInfo q = qs.get(i);
                    int numProcesses = q.numProcess;
                    ArrayList<ProcessInfo> ps = new ArrayList<>();
                    for (int j = 0; j < numProcesses; j++) {
                        ProcessInfo p = new ProcessInfo(); // enter details of each process then add to array
                        // add array to queue's attribute
                        p.name = cin.nextLine();
                        p.arrival = Integer.parseInt(cin.nextLine());
                        p.burst = Integer.parseInt(cin.nextLine());
                        p.burstCopy = p.burst; // for round robin
                        p.turnaround = 0; // for initializing
                        ps.add(p);
                    }
                    qs.get(i).processes = ps;
                }


            }
        }
        catch(FileNotFoundException f) {
            f.printStackTrace();
        }

        Collections.sort(qs);

/*        for (int i = 0; i < numQueues; i++) { // test print
            QueueInfo q = qs.get(i);
            int numProcesses = q.numProcess;
            for (int j = 0; j < numProcesses; j++) {
                System.out.println(qs.get(i).processes.get(j).name); // [i][j]
            }
        }*/

    String write = "";

        for (int i = 0; i < numQueues; i++) {
            if (qs.get(i).algorithm.equals("FCFS")) {
                Output o = FCFS(qs.get(i).processes);
                for (int j = 0; j < o.processes.size(); j++) {
                    write += "Name: ";
                    write += o.processes.get(j).name;
                    write += " Turnaround time: ";
                    write += o.processes.get(j).turnaround;
                    System.out.println("Name: " + o.processes.get(j).name + " Turnaround time: " + o.processes.get(j).turnaround);
                    write += "\n";
                }
                write += "Average turnaround time: ";
                write += o.averageTurnaround;
                write += "\n";
                System.out.println("Average turnaround time: " + o.averageTurnaround);

            } else if (qs.get(i).algorithm.equals("SJF")) {
                Output o = SJF(qs.get(i).processes);
                for (int j = 0; j < o.processes.size(); j++) {
                    write += "Name: ";
                    write += o.processes.get(j).name;
                    write += " Turnaround time: ";
                    write += o.processes.get(j).turnaround;
                    System.out.println("Name: " + o.processes.get(j).name + " Turnaround time: " + o.processes.get(j).turnaround);
                    write += "\n";
                }
                write += "Average turnaround time: ";
                write += o.averageTurnaround;
                write += "\n";
                System.out.println("Average turnaround time: " + o.averageTurnaround);
            } else if (qs.get(i).algorithm.equals("RR")) {
                Output o = RR(qs.get(i).processes);
                for (int j = 0; j < o.processes.size(); j++) {
                    write += "Name: ";
                    write += o.processes.get(j).name;
                    write += " Turnaround time: ";
                    write += o.processes.get(j).turnaround;
                    System.out.println("Name: " + o.processes.get(j).name + " Turnaround time: " + o.processes.get(j).turnaround);
                    write += "\n";
                }
                write += "Average turnaround time: ";
                write += o.averageTurnaround;
                write += "\n";
                System.out.println("Average turnaround time: " + o.averageTurnaround);
            }
        }
        File output = new File("C:\\Users\\menna\\Desktop\\output.txt");
        try(PrintWriter out = new PrintWriter("C:\\Users\\menna\\Desktop\\output.txt")) {
            out.println(write); // write table
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
