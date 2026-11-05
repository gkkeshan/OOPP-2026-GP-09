package ftms.model;


public class Venue extends BaseEntity {

    private int venueId;
    private String venueName;
    private String building;
    private int capacity;

    public Venue(int venueId, String venueName, String building, int capacity) {
        this.venueId = venueId;
        setVenueName(venueName);
        setBuilding(building);
        setCapacity(capacity);
    }

    public Venue(String venueName, String building, int capacity) {
        this(0, venueName, building, capacity);
    }

    public int getVenueId() { return venueId; }
    public void setVenueId(int venueId) { this.venueId = venueId; }

    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = requireText(venueName, "Venue name", 60); }

    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = requireText(building, "Building", 60); }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be greater than 0");
        this.capacity = capacity;
    }

    @Override public Object getId() { return venueId; }
    @Override public String display() { return venueName + " - " + building; }
}