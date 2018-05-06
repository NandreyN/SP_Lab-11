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

    public Client(String name, String email,String phone, int id, int age, Status status) {
        this.name = name;
        this.phone = phone;
        this.email = new Email(email);
        this.id = id;
        this.age = age;
        this.status = status;
    }

    public Client(String name, String emailName, String emailDomain, String phone, int id, int age, Status status) {
        this.name = name;
        this.phone = phone;
        this.email = new Email(emailName, emailDomain);
        this.id = id;
        this.age = age;
        this.status = status;
    }
}
