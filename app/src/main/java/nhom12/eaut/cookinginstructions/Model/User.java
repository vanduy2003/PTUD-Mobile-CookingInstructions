package nhom12.eaut.cookinginstructions.Model;


public class User {
    private String username;
    private String sex;
    private String address;
    private String avatar;
    private String birthday;
    private String phone;
    private String email;
    private String password;

    public User(String username, String email, String password, String sex, String address, String avatar, String birthday, String phone) {
        this.username = username;
        this.sex = sex;
        this.address = address;
        this.avatar = avatar;
        this.birthday = birthday;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
}
