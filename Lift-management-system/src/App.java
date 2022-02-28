

import elevatorsystem.LiftManager;
import models.Lift;
import models.Request;



public class App {
    private static LiftManager liftManager;
    private static Thread liftManagerThread;
    public static void main(String[] args) throws Exception {

        liftManager = LiftManager.getInstance();
        LiftManager.initializeLifts(5);
        liftManagerThread = new Thread(liftManager);
        liftManagerThread.start();

        int choice;
        while(true){
            System.out.println("1. Add Request");
            System.out.println("2. All Lift Status");
            System.out.println("3. Lift Status for a particular lift");
            System.out.println("4. Exit");
            System.out.println("Enter your choice: ");
            choice = Integer.parseInt(System.console().readLine());
            if(choice==1){
                System.out.println("Enter the request floor: ");
                int requestFloor = Integer.parseInt(System.console().readLine());
                System.out.println("Enter the target floor: ");
                int targetFloor = Integer.parseInt(System.console().readLine());
                Request request = new Request(requestFloor, targetFloor);
                Lift lift = request.submitRequest();
                Thread.sleep(3000);
                System.out.println("Lift: " + lift.getId() + " is at floor: " + lift.getCurrentFloor() + " and is in state: " + lift.getLiftState() +"("+ lift.getDoorStatus()+")");
            }else if(choice==2){
                liftManager.showLiftStatus();
            }else if(choice==4){
                liftManager.setStopManager(true);
                liftManagerThread.join();
                break;
            }else if(choice==3){
                System.out.println("Enter the lift id: ");
                int liftId = Integer.parseInt(System.console().readLine());
                Lift lift = LiftManager.getInstance().getLifts().get(liftId);
                System.out.println("Lift: " + lift.getId() + " is at floor: " + lift.getCurrentFloor() + " and is in state: " + lift.getLiftState() +"("+ lift.getDoorStatus()+")");
            }
        }

    }
}
