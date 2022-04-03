package application.views.code; 
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class User implements Serializable {
private static final long serialVersionUID = 1L;
    String name;
    //Image icon - will be implemented in actual app
    LocalDate birthday;
    LocalDateTime created;
    int streak;

    public User(String name) {
        this.name = name;
        this.created = LocalDateTime.now();
        this.streak = 0;
    }


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthday() {
        return this.birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public LocalDateTime getCreated() {
        return this.created;
    }

    
    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public int getStreak() {
        return this.streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }
}