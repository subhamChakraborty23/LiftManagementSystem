

import java.util.Scanner;

import elevatorsystem.LiftManager;
import models.Lift;
import models.Request;



public class App {
    private static LiftManager liftManager;
    private static Thread liftManagerThread;
    
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter number of lifts");
        int numberOfLifts = scanner.nextInt();

        liftManager = LiftManager.getInstance();
        LiftManager.initializeLifts(numberOfLifts);
        liftManagerThread = new Thread(liftManager);
        liftManagerThread.start();
        System.out.println("Enter number of Floors");
        int numberOfFloors = scanner.nextInt();

        int choice;
        while(true){
            System.out.println("1. Add Request minFloor (0) "+ "maxFloor ("+numberOfFloors+")");
            System.out.println("2. All Lift Status");
            System.out.println("3. Lift Status for a particular lift");
            System.out.println("4. Exit");
            System.out.println("Enter your choice: ");
            
            choice = scanner.nextInt(); 
            if(choice==1){
                scanner = new Scanner(System.in);
                System.out.println("Enter the request floor: ");
                int requestFloor = scanner.nextInt();
                System.out.println("Enter the target floor: ");
                int targetFloor = scanner.nextInt();
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
                scanner = new Scanner(System.in);
                int liftId = scanner.nextInt();
                Lift lift = LiftManager.getInstance().getLifts().get(liftId);
                System.out.println("Lift: " + lift.getId() + " is at floor: " + lift.getCurrentFloor() + " and is in state: " + lift.getLiftState() +"("+ lift.getDoorStatus()+")");
            }
        }
        scanner.close();
    }
}
