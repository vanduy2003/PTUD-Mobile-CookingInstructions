package nhom12.eaut.cookinginstructions.Model;


public class User {
    public String username;
    public String email;
    public String password;
    public String profile_picture;
    public String preferences;

    public User() {
    }

    public User(String username, String email, String password, String profile_picture, String preferences) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profile_picture = profile_picture;
        this.preferences = preferences;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public String getUsername() {
        return username;
    }

    public String getPreferences() {
        return preferences;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public String getEmail() {
        return email;
    }
}
