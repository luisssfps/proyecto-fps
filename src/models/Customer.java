package models;

public class Customer {
  private String name;
  private String contactInfo;

  public Customer(String name, String contactInfo) {
    this.name = name;
    this.contactInfo = contactInfo;
  }

  // Getters and Setters
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getContactInfo() {
    return contactInfo;
  }

  public void setContactInfo(String contactInfo) {
    this.contactInfo = contactInfo;
  }

  @Override
  public String toString() {
    return "Customer{" +
        "name='" + name + '\'' +
        ", contactInfo='" + contactInfo + '\'' +
        '}';
  }
}
