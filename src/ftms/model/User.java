package ftms.model;

public abstract class User {
    private int userID;
    private String userName;
    private String email;
    private String password;
    private String role;
    private String contactNumber;

    public User(){ }

    public User(String userName, String email, String password, String role, String contactNumber) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.contactNumber = contactNumber;
    }

    public User(int userID, String userName, String email, String password, String role, String contactNumber) {
        this.userID = userID;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.contactNumber = contactNumber;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                '}';
    }
}
