package elevatorsystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import enums.LiftState;
import models.Request;
import models.Lift;

public class LiftManager implements Runnable {
    
    private boolean stopManager;
    private static Map<Integer, Lift> upMovingMap = new ConcurrentHashMap<Integer, Lift>();
    private static Map<Integer, Lift> downMovingMap = new ConcurrentHashMap<Integer, Lift>();
    private static final int MAX_LIFTS = 50;
    private static List<Lift> lifts;
    //singleton thread safe
    private static LiftManager instance = new LiftManager();
    private static ExecutorService executorService = Executors.newFixedThreadPool(100);
    
    private LiftManager() {
        if(instance!=null){
            throw new IllegalStateException("Already instantiated");
        }
        setStopManager(false);
    }
    
    public static LiftManager getInstance() {
        return instance;
    }

    public static void initializeLifts(int numberOfLifts) {
        lifts = new ArrayList<Lift>(numberOfLifts);
        for(int i=0; i<numberOfLifts; i++){
            Lift elevator = new Lift(i);

            executorService.execute(elevator);

            lifts.add(elevator);
            System.out.println("Lift " + i + " created");
        }
    }

    public void setStopManager(boolean b) {
        this.stopManager = b;
    }
    
    public synchronized Lift selectLift(Request request) {
        Lift lift = null;
        LiftState liftState = getRequestedLiftDirection(request);
        int requestedFloor = request.getRequestFloor();
        int destinationFloor = request.getTargetFloor();

        lift = findTheClosestLift(liftState, requestedFloor, destinationFloor);

        //notify all the waiting threads
        notifyAll();
        return lift;
    }

    private static LiftState getRequestedLiftDirection(Request request) {
        LiftState liftState = null;
        if(request.getRequestFloor() > request.getTargetFloor()){
            liftState = LiftState.DOWN;
        } else if(request.getRequestFloor() < request.getTargetFloor()){
            liftState = LiftState.UP;
        }
        return liftState;

    }

    private static Lift findTheClosestLift(LiftState liftState, int requestedFloor, int destinationFloor) {
        Lift lift = null;
        //keys : distance of a lift from requested floor
        TreeMap<Integer,Integer> sortedDistances = new TreeMap<Integer,Integer>();

        if(liftState.equals(LiftState.UP)){
            for(Lift elevator : upMovingMap.values()){
                int distance = elevator.getCurrentFloor() - requestedFloor;
                if(distance<0 && elevator.getLiftState().equals(LiftState.UP)){
                    continue;
                }else{
                    sortedDistances.put(Math.abs(distance), elevator.getId());
                }
            }
            //check nulls
            int selectedLiftId = sortedDistances.firstEntry().getValue();
            lift = upMovingMap.get(selectedLiftId);
            System.out.println("Lift " + lift.getId() + " selected");
        } else if(liftState.equals(LiftState.DOWN)){
            for(Lift elevator : downMovingMap.values()){
                int distance = elevator.getCurrentFloor() - requestedFloor;
                if(distance>0 && elevator.getLiftState().equals(LiftState.DOWN)){
                    continue;
                }else{
                    sortedDistances.put(Math.abs(distance), elevator.getId());
                }
            }
            int selectedLiftId = sortedDistances.firstEntry().getValue();
            lift = downMovingMap.get(selectedLiftId);
            System.out.println("Lift " + selectedLiftId + " selected");
        }

        //stop or pass by relavant floors
        Request newReq = new Request(lift.getCurrentFloor(), destinationFloor);
        LiftState liftDir = getRequestedLiftDirection(newReq);
        //for moving in opposite direction 
        Request newReq2 = new Request(requestedFloor, destinationFloor);
        LiftState liftDir2 = getRequestedLiftDirection(newReq2);

        NavigableSet<Integer> floorSet = lift.floorStopsMap.get(liftDir);
        if(floorSet==null){
            floorSet = new ConcurrentSkipListSet<Integer>();
        }
        floorSet.add(lift.getCurrentFloor());
        floorSet.add(requestedFloor);
        lift.floorStopsMap.put(liftDir, floorSet);

        NavigableSet<Integer> floorSet2 = lift.floorStopsMap.get(liftDir2);
        if(floorSet2==null){
            floorSet2 = new ConcurrentSkipListSet<Integer>();
        }
        floorSet2.add(requestedFloor);
        floorSet2.add(destinationFloor);
        lift.floorStopsMap.put(liftDir2, floorSet2);

        System.out.println("Lift " + lift.getId() + " stopped at " + lift.getCurrentFloor());
        return lift;


    }

    public synchronized List<Lift> getLifts() {
        return lifts;
    }

    public boolean isStopManager() {
        return stopManager;
    }

    //update lift state and change lift direction
    public static synchronized void updateLiftLists(Lift lift){
        if(lift.getLiftState().equals(LiftState.UP)){
            upMovingMap.put(lift.getId(), lift);
            downMovingMap.remove(lift.getId());
        } else if(lift.getLiftState().equals(LiftState.DOWN)){
            downMovingMap.put(lift.getId(), lift);
            upMovingMap.remove(lift.getId());
        }else if(lift.getLiftState().equals(LiftState.DEFECTIVE)){
            upMovingMap.remove(lift.getId());
            downMovingMap.remove(lift.getId());
        }else if(lift.getLiftState().equals(LiftState.STATIONARY)){
            upMovingMap.put(lift.getId(), lift);
            downMovingMap.put(lift.getId(), lift);
        }
        System.out.println("Lift " + lift.getId() + " updated");
    }

    @Override
    public void run() {
        setStopManager(false);
        while(true){
            try{
                Thread.sleep(1000);
                if(stopManager){
                    break;
                }
               
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
    }

    //show status of all the lifts
    public void showLiftStatus(){
        for(Lift lift : lifts){
            System.out.println("Lift " + lift.getId() + " is at floor " + lift.getCurrentFloor() + " and is " + lift.getLiftState());
        }
    }
}
