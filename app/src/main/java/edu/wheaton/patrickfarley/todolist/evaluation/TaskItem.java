package edu.wheaton.patrickfarley.todolist.evaluation;

/**
 * Created by patrickfarley on 3/24/15.
 */
public class TaskItem {
    int timeUnit = 1;

    int priority;
    int minutes;
    String label;
    double nextWorth;
    double priority_proper;

    public TaskItem(String label, int priority,int time) {
        this.priority = priority;
        this.minutes = time;
        this.label = label;
        // put the 1-10 priority value into a usable form, weighted by .4 for further calculations
        this.priority_proper = (11-(double)priority) * .4;
    }

    // this method is not currently useful
    public void updateNextWorth(int assignedTime){
        double oldWorth = calcWorth(assignedTime);
        double newWorth = calcWorth(assignedTime+timeUnit);
        nextWorth = newWorth - oldWorth;
    }

    // this method is not currently useful
    public double getNextWorth(){
        return nextWorth;
    }



    public double calcWorth(int assignedTime){

        // the fraction of the task that would then be complete by allocated the
        // given amount of time.
        double fraction = ((double)(assignedTime))/minutes;

        if (fraction <= 1)
            return fraction * priority_proper;
        else
            return 0;
    }
}
