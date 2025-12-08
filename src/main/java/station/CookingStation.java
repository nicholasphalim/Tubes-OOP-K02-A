package station;

import inventory.CookingDevice;
// import inventory.Preparable;


public class CookingStation extends Station {
    private CookingDevice cookingDevice;

    public CookingStation(CookingDevice cookingDevice) {
        this.cookingDevice = cookingDevice;
    }

    public boolean addItemToDevice(Preparable ingredient) {
        if (cookingDevice.canAccept(ingredient)) {
            cookingDevice.addIngredient(ingredient);
            return true;
        }
        return false;
    }

    @Override
    public void interact(Chef chef) {
        cookingDevice.startCooking();
    }
}