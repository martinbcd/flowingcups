import java.util.Scanner;

public class FlowingCups {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        boolean input = false;
        System.out.println("Välj en rad från 2 till 50");
        int row = scanner.nextInt();

        System.out.println("Välj ett glass från 2 till raden du valde");
        int column = scanner.nextInt();

        //keeps track of the amount of time to fill any particular glass
        double[][] glassMatrix = new double[row][row];
        //a decimal for each cup which keeps track of the percentage of water which would be required to fill the glass which flows into it per second
        //for example the first cup is 1/10, or 10% of the cup is filled per second
        double[][] flowRateMatrix = new double[row][row];

        if (row < 2 || row > 50){
            System.out.println("Raden behöver vara från 2 till 50");
            input = true;
        }
        if(column < 1 || column > row){
            System.out.println("glaset behöver vara från 1 till den angivna raden");
            input = true;
        }
        if(input){
            return;
        }
        System.out.println(timeToFill(row-1,column-1,glassMatrix,flowRateMatrix));

    }
    //main function which calculates the time in which any singular cup becomes filled
    //r is the row, c is the column
    private static double timeToFill(int r, int c, double[][] glassMatrix, double[][] flowRateMatrix){
        for(int i = 0; i < glassMatrix.length; i++){
            for(int j = 0; j <= i; j++){
                //base case
                if(i == 0){
                    glassMatrix[i][j] = 10;
                    flowRateMatrix[i][j] = 0.1;
                }
                //singular parent cases where the glasses are at the edges
                else if(j == 0){
                    glassMatrix[i][j] = glassMatrix[i-1][j] + 1 / (flowRateMatrix[i-1][j] / 2);
                    flowRateMatrix[i][j] = flowRateMatrix[i-1][j] / 2;
                }
                else if(j == i){
                    glassMatrix[i][j] = glassMatrix[i-1][j-1] + 1 / (flowRateMatrix[i-1][j-1] / 2);
                    flowRateMatrix[i][j] = flowRateMatrix[i-1][j-1] / 2;
                }

                else{
                    double leftParent = glassMatrix[i-1][j-1];
                    double leftFlowRate = flowRateMatrix[i-1][j-1];
                    double rightParent = glassMatrix[i-1][j];
                    double rightFlowRate = flowRateMatrix[i-1][j];
                    double smallestParent = 0;
                    //smallest flow doesn't mean that it's the lowest flow rate out of the parents but rather it's the smallest parents flow rate (possesive)
                    double smallestFlow = 0;


                    if(leftParent == rightParent){
                        glassMatrix[i][j] = leftParent + 1 / ((leftFlowRate + rightFlowRate) / 2);
                        flowRateMatrix[i][j] = (leftFlowRate + rightFlowRate) / 2;
                        break;
                    }

                    //if they are not equal determine which parent wil be filled earliest
                    if(leftParent < rightParent){
                        smallestParent = leftParent;
                        smallestFlow = leftFlowRate;
                    }
                    else {
                        smallestParent = rightParent;
                        smallestFlow = rightFlowRate;
                    }

                    /*a fatal flaw occurs here, in the case only one parent would contribute to filling one of its children,
                     only half that parents flow rate would be added to the child's flow rate. However eventually the child's other parent would be filled
                     and when it is the child's flow rate would need to be modified, assuming in this simulation that water flows instantly from a filled glass
                     into one of its children's glasses, if one were to go deep enough into the pyramid of glasses eventually
                     while calculating the flow rate of a particular glass each of its parents flow rates could potentially change by the amount of times
                     equal to half the previous amount of parents they had.

                     the delay in which one of a glass's parents would be delayed can't be accounted for either by a single cumulative integer
                     either since the change in flow rate would change in impact depending on the delay of time from the point the glass was or was not filled.

                     perhaps it would be possible to store each glass which had an empty parent into a priority que and when a particular glass
                     would align with the time the empty glass would be filled, each of the children and subsequent children of this newly filled glass
                     would be updated with accurate flow rates. However, because the accurate times are not already calculated perhaps it could change
                     the time of any glass for which this glass was a parent or grandparent. So rather at the calculation of every new glass all previous
                     glasses would need to be updated and the current glass's flow rate would need to account for the potentially infinite different flow rates
                     its parents could have while filling it.
                     
                     but I don't know how to design this algorithm, and it seems quite complex. Or there is a significantly simpler solution I do not see.
                     */
                    //if one parent would fill the current glass before the other parent was filled
                    if(leftParent +(1 / (leftFlowRate / 2)) < rightParent){
                        glassMatrix[i][j] = leftParent + (1 / (leftFlowRate / 2));
                        flowRateMatrix[i][j] = leftFlowRate / 2;
                    }
                    else if(rightParent + (1 / (rightFlowRate / 2)) < leftParent){
                        glassMatrix[i][j] = rightParent + (1 / (rightFlowRate / 2));
                        flowRateMatrix[i][j] = rightFlowRate / 2;
                    }
                    //the case where both parents would contribute to the glass being filled but one of the parents would start pouring at a delayed time
                    else {
                        double timeDifferance = Math.abs(leftParent - rightParent);
                        //the ratio of amount the that the earliest parents would pour water into the current cup
                        double timePercent = timeDifferance / (1 / (smallestFlow / 2));
                        /*this takes the ratio of amount which it takes to fill the cup for which the first single parent would pour water into the cup alone
                          and then adds the remaining time it takes to fill the cup and adds both parents flow rates */
                        double fillTime = (timePercent / (smallestFlow/ 2)) + ((1 - timePercent) / (((leftFlowRate) + (rightFlowRate))/2));
                        glassMatrix[i][j] = smallestParent + fillTime;
                        flowRateMatrix[i][j] = ((leftFlowRate) + (rightFlowRate))/2;
                    }
                }
            }
        }
        return glassMatrix[r][c];
    }
}
