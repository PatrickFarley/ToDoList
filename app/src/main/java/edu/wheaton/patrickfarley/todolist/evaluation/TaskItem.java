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
    double p_weight;

    public TaskItem(String label, int priority,int time) {
        this.priority = priority;
        this.minutes = time;
        this.label = label;
        this.p_weight = (11-(double)priority) * .4;

        // initialize the nextWorth field with the marginal worth of adding one time unit from zero
    }

    public void updateNextWorth(int assignedTime){
        double oldWorth = calcWorth(assignedTime);
        double newWorth = calcWorth(assignedTime+timeUnit);
        nextWorth = newWorth - oldWorth;
    }

    public double getNextWorth(){
        return nextWorth;
    }


    public double calcWorth(int assignedTime){
        // ??? is fraction the best way? then it automatically favors short tasks. we could replace fraction in the
        // return statements with some other decimal. fraction still has a use (determining when to accelerate
        // the worth values)
        double fraction = ((double)(assignedTime))/minutes;
        //System.out.println("the fraction is " + fraction + " which is "+ assignedTime+ "/" + minutes);


		/*
		if (fraction < .65){
			//System.out.println(fraction*.4);
			return p_weight * .4;
		}else if (fraction <= 1){
			//System.out.println((fraction-(1-(.5)))*2);
			return p_weight* .6;
		}else {
			//System.out.println("zero (completed)");
			return 0;
			*/
        if (fraction <= 1)
            return fraction * p_weight;
        else
            return 0;

    }
}
