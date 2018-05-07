package model;

public class Client {

    public enum Status {
        PREMIUM, STANDARD
    }

    private String name, phone;
    private Email email;
    private int id;
    private int age;
    private Status status;
    private static int maxId = 1;

    public Client(String name, Email email, String phone, int id, int age, Status status) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.id = id;
        this.age = age;
        this.status = status;
        maxId = Math.max(maxId, id) + 1;
    }

    public Client(String name, String emailName, String emailDomain, String phone, int id, int age, Status status) {
        this.name = name;
        this.phone = phone;
        this.email = new Email(emailName, emailDomain);
        this.id = id;
        this.age = age;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Client))
            return false;
        if (o == this)
            return true;
        Client oC = (Client) o;
        return oC.id == this.id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isMaximal() {
        return this.id >= maxId;
    }

    public static void decreaseId() {
        --maxId;
    }

    public static int getMaxId() {
        return maxId;
    }

    public boolean isValid() {
        Validator validator = new Validator();
        boolean name = validator.isValid(getName(), Validator.Modes.WORD),
                address = validator.isValid(getEmail().toString(), Validator.Modes.EMAIL),
                phone = validator.isValid(getPhone(), Validator.Modes.PHONE),
                age = validator.isValid(String.valueOf(getAge()), Validator.Modes.ID),
                status = validator.isValid(getStatus().toString().toLowerCase(), Validator.Modes.USER_STATUS);
        if (!(name && phone && age && address && status))
            return false;
        return true;
    }
}
