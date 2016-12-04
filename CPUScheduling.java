import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;

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
        Integer burst;
        Integer waiting;
        Integer turnaround;
    }

    public static class Output {
        ArrayList<ProcessInfo> processes;
        double averageTurnaround;
    }

    public static Output FCFS(ArrayList<ProcessInfo> processes) {
        Output o = new Output();
        Collections.sort(processes, new Comparator<ProcessInfo>() {
            @Override
            public int compare(ProcessInfo fruit1, ProcessInfo fruit2) {
                return  fruit1.arrival - fruit2.arrival; // ascending
            }
        });
        int clock = 0, turnaroundSum = 0;
        for (int i = 0; i < processes.size(); i++) {
            processes.get(i).waiting = clock - processes.get(i).arrival; // waiting = clock - arrival
//            System.out.println("clock before: " + clock);
            clock += processes.get(i).burst;
            clock++; // adding context switch
//            System.out.println("waiting: " + processes.get(i).waiting);
//            System.out.println("clock after: " + clock);
            processes.get(i).turnaround = processes.get(i).waiting + processes.get(i).burst; // turnaround = waiting + burst
            turnaroundSum += processes.get(i).turnaround;
        }
        o.processes = processes;
        o.averageTurnaround = turnaroundSum / processes.size(); // yay us... nope... yes was gee's bad sorry
        return o;
    }

    public static Output SJF(ArrayList<ProcessInfo> processes) {
        Output o = new Output();
        Collections.sort(processes, new Comparator<ProcessInfo>() {
            @Override
            public int compare(ProcessInfo fruit1, ProcessInfo fruit2) {
                return  fruit1.burst - fruit2.burst; // ascending
            }
        });
        int clock = 0, turnaroundSum = 0;
        for (int i = 0; i < processes.size(); i++) {
            processes.get(i).waiting = clock - processes.get(i).arrival; // waiting = clock - arrival
            if (processes.get(i).waiting < 0)
                processes.get(i).waiting = 0;

//            System.out.println("clock before: " + clock);
            clock += processes.get(i).burst;
            clock++; // adding context switch
//            System.out.println("waiting: " + processes.get(i).waiting);
//            System.out.println("clock after: " + clock);
            processes.get(i).turnaround = processes.get(i).waiting + processes.get(i).burst; // turnaround = waiting + burst
            turnaroundSum += processes.get(i).turnaround;
        }
        o.processes = processes;
        o.averageTurnaround = turnaroundSum / processes.size();
        return o;
    }

    public static void main(String args[]) {
        int numQueues; Scanner cin = new Scanner(System.in);
        numQueues = Integer.parseInt(cin.nextLine());
        ArrayList<QueueInfo> qs = new ArrayList<>();
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
                ps.add(p);
            }
            qs.get(i).processes = ps;
        }

        Collections.sort(qs);

        for (int i = 0; i < numQueues; i++) { // test print
            QueueInfo q = qs.get(i);
            int numProcesses = q.numProcess;
            for (int j = 0; j < numProcesses; j++) {
                System.out.println(qs.get(i).processes.get(j).name); // [i][j]
            }
        }

        for (int i = 0; i < numQueues; i++) {
            if (qs.get(i).algorithm.equals("FCFS")) {
                Output o = FCFS(qs.get(i).processes);
                for (int j = 0; j < o.processes.size(); j++) {
                    System.out.println("Name: " + o.processes.get(j).name + " Turnaround time: " + o.processes.get(j).turnaround);
                }
                System.out.println("Average turnaround time: " + o.averageTurnaround);

            } else if (qs.get(i).algorithm.equals("SJF")) {
                Output o = SJF(qs.get(i).processes);
                for (int j = 0; j < o.processes.size(); j++) {
                    System.out.println("Name: " + o.processes.get(j).name + " Turnaround time: " + o.processes.get(j).turnaround);
                }
                System.out.println("Average turnaround time: " + o.averageTurnaround);

            }
        }

    }



}
