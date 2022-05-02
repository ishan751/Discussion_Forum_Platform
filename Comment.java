import java.util.Date;

/**
 * The Comment class
 *
 * <p>Purdue University -- CS18000 -- Spring 2021</p>
 *
 * @author Group 92
 * @version April 10, 2022
 */

public class Comment implements java.io.Serializable {
    final private String name;
    final private Date timeStamp;
    final private String body;


    public Comment(String name, String body) { // Create comment associated with post
        this.name = name;
        this.body = body;
        this.timeStamp = new Date(System.currentTimeMillis());
    }

    public String getName() {
        return name;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getBody() {
        return body;
    }

    public String toString() {
        return "Comment by: " + name +
                "\nTimeStamp: " + timeStamp +
                "\nResponse: " + body +
                "\n----------";

    }
}
