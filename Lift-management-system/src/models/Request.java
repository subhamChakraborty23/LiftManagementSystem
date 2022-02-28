package models;

import elevatorsystem.LiftManager;

public class Request{
	
	private int requestFloor;
    private int targetFloor;

    public Request(int requestFloor, int targetFloor){
        this.requestFloor = requestFloor;
        this.targetFloor = targetFloor;
    }

    public int getRequestFloor() {
        return requestFloor;
    }

    public int getTargetFloor() {
        return targetFloor;
    }

    /**
     * Submit the request to the LiftManager to select the
     * optimal elevator for this request
     * @return
     */
    public Lift submitRequest(){
        return LiftManager.getInstance().selectLift(this);
    }
}
