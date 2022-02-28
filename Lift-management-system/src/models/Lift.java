package models;

import java.util.Iterator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentHashMap;

import elevatorsystem.LiftManager;
import enums.DoorStatusEnum;
import enums.LiftState;

public class Lift implements Runnable {
    DoorStatusEnum doorStatus;
    int id;
    private LiftState liftState;
    private int currentFloor;
    int targetFloor;
    int maxFloor;
    int minFloor;
    private boolean operating;
    private NavigableSet<Integer> floorStops;
    public Map<LiftState, NavigableSet<Integer>> floorStopsMap;

    public Lift(int id) {
        this.id = id;
        this.doorStatus = DoorStatusEnum.CLOSED;
        setOperating(true);
    }

    public DoorStatusEnum getDoorStatus() {
        return doorStatus;
    }

    public void setDoorStatus(DoorStatusEnum doorStatus) {
        this.doorStatus = doorStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LiftState getLiftState() {
        return liftState;
    }

    public void setLiftState(LiftState liftState) {
        this.liftState = liftState;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public int getTargetFloor() {
        return targetFloor;
    }

    public void setTargetFloor(int targetFloor) {
        this.targetFloor = targetFloor;
    }

    public int getMaxFloor() {
        return maxFloor;
    }

    public void setMaxFloor(int maxFloor) {
        this.maxFloor = maxFloor;
    }

    public int getMinFloor() {
        return minFloor;
    }

    public void setMinFloor(int minFloor) {
        this.minFloor = minFloor;
    }

    public boolean isOperating() {
        return this.operating;
    }

    public NavigableSet<Integer> getFloorStops() {
        return floorStops;
    }

    public void setFloorStops(NavigableSet<Integer> floorStops) {
        this.floorStops = floorStops;
    }

    public Map<LiftState, NavigableSet<Integer>> getFloorStopsMap() {
        return floorStopsMap;
    }

    public void setFloorStopsMap(Map<LiftState, NavigableSet<Integer>> floorStopsMap) {
        this.floorStopsMap = floorStopsMap;
    }

    public void setOperating(boolean state) {
        this.operating = state;

        if (!state) {
            setLiftState(LiftState.DEFECTIVE);
            this.floorStops.clear();
        } else {
            setLiftState(LiftState.STATIONARY);
            this.floorStopsMap = new ConcurrentHashMap<LiftState, NavigableSet<Integer>>();

            // To let controller know that this elevator is ready to serve
            LiftManager.updateLiftLists(this);
        }

        setCurrentFloor(0);
    }

    // start moving either UP or DOWN
    public void startLift() {
        synchronized (LiftManager.getInstance()) {
            Iterator<LiftState> it = floorStopsMap.keySet().iterator();

            while (it.hasNext()) {
                LiftState state = it.next();
                System.out.println("Lift " + id + " is moving " + state);
                floorStops = floorStopsMap.get(state);
                
                it.remove();
                for (int i = 0; i < floorStops.size(); i++) {
                    if (state == LiftState.UP) {
                        while(currentFloor < floorStops.last()) {
                            currentFloor++;
                            System.out.println("Lift " + id + " is moving " + state + " to floor " + currentFloor);
                            LiftManager.updateLiftLists(this);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        while(currentFloor > floorStops.first()) {
                            currentFloor--;
                            System.out.println("Lift " + id + " is moving " + state + " to floor " + currentFloor);
                            LiftManager.updateLiftLists(this);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                // Lift has reached its destination
                setLiftState(LiftState.STATIONARY);
                LiftManager.updateLiftLists(this);
                
                // Integer currentFloor = null;
                // Integer nextFloor = null;

                // while (!floorStops.isEmpty()) {
                //     if (liftState.equals(LiftState.UP)) {
                //         currentFloor = floorStops.pollFirst();
                //         nextFloor = floorStops.higher(currentFloor);
                //         System.out
                //                 .println("Lift " + this.id + " is moving UP from " + currentFloor + " to " + nextFloor);
                //     } else if (liftState.equals(LiftState.DOWN)) {
                //         currentFloor = floorStops.pollLast();
                //         nextFloor = floorStops.lower(currentFloor);
                //         System.out.println(
                //                 "Lift " + this.id + " is moving DOWN from " + currentFloor + " to " + nextFloor);
                //     }else{
                //         System.out.println("Lift " + this.id + " is moving UP from " + currentFloor + " to " + nextFloor);
                //         return;
                //     }
                //     System.out.println("Lift " + this.id + " is moving from " + currentFloor + " to " + nextFloor);
                //     setCurrentFloor(currentFloor);

                //     if (nextFloor != null) {
                //         // find any new request that might come while on the moving state in certain
                //         // direction
                //         getNextRequestedFloors(currentFloor, nextFloor);
                //     } else {
                //         setLiftState(LiftState.STATIONARY);
                //         LiftManager.updateLiftLists(this);
                //     }
                //     System.out.println("Lift: " + this.id + " is at floor: " + this.currentFloor + " and is in state: "
                //             + this.liftState + this.doorStatus);

                //     try {
                //         setDoorStatus(DoorStatusEnum.OPEN);
                //         Thread.sleep(1000);// let the people go out of the lift
                //         setDoorStatus(DoorStatusEnum.CLOSED);
                //     } catch (InterruptedException e) {
                //         e.printStackTrace();
                //     }

                // }
            }
            try {
                LiftManager.getInstance().wait();
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }

        }
    }

    // list of floors that will be visited by the lift while moving in this
    // direction
    public void getNextRequestedFloors(int start, int end) {
        if (start == end) {

            return;
        }
        int distance = Math.abs(start - end);
        if (distance == 1) {
            return;
        }
        int steps = 1;
        if (end - start < 0) {
            // go down
            steps = -1;
        }
        while (start != end) {
            start += steps;
            if (!floorStops.contains(start)) {
                floorStops.add(start);
            }
        }

    }

    @Override
    public void run() {
        while (true) {
            if (isOperating()) {
                System.out.println("Lift " + this.id + " is running");
                startLift();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }

    }

}
