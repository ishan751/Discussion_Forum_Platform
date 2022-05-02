import java.util.ArrayList;

/**
 * The Post class
 *
 * <p>Purdue University -- CS18000 -- Spring 2021</p>
 *
 * @author Group 92
 * @version April 10, 2022
 */

public class Post extends Comment implements java.io.Serializable {
    private ArrayList<Comment> comments;

    public Post(String name, String body) {  // create post w comment arraylists
        super(name, body);
        this.comments = new ArrayList<>();

    }


    public ArrayList<Comment> getComments() {
        return comments;
    }

    @Override
    public String toString() {
        boolean commentval = true;
        if (comments.size() == 0) {
            commentval = false;
        }
        if (!commentval) {
            return "Name: " + super.getName() +
                    "\nTimeStamp: " + super.getTimeStamp() +
                    "\nPost:" + super.getBody() +
                    "\nComments: None" +
                    "\n----------\n";
        } else {
            return "Name: " + super.getName() +
                    "\nTimeStamp: " + super.getTimeStamp() +
                    "\nPost:" + super.getBody() +
                    "\nComments: " +
                    "\n" + this.getCommentsForum() +
                    "\n----------";

        }
    }

    public String getCommentsForum() {
        StringBuilder res = new StringBuilder();
        for (Comment comment : this.comments)
            res.append(comment.toString()).append("\n");
        return res.toString();
    }
}
