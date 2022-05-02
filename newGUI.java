import javax.swing.*;

public class newGUI {
    public static String viewForum() {
        //options to view each forum
        //change options for each forum depending on the teachers input
        String[] options = {"forum 1", "forum 2", "forum 3", "forum 4"};
        return (String) JOptionPane.showInputDialog(null, "Select a forum to view ", "Project 5",
                JOptionPane.PLAIN_MESSAGE, null, options, null);
    }
    public static String deleteForum() {
        //choose a forum to delete
        //change options for the number of forums
        String[] options = {"forum 1", "forum 2", "forum 3", "forum 4"};
        return (String) JOptionPane.showInputDialog(null, "Select a forum to delete ", "Project 5",
                JOptionPane.PLAIN_MESSAGE, null, options, null);
    }
    public static String postForum() {
        //choose forum to post on
        String[] options = {"forum 1", "forum 2", "forum 3", "forum 4"};
        return (String) JOptionPane.showInputDialog(null, "Select a forum to post on ", "Project 5",
                JOptionPane.PLAIN_MESSAGE, null, options, null);
    }
    public static void post() {
        //user input for the post
        String newPost = JOptionPane.showInputDialog(null, "What would you like to post? ",
                "Project 5", JOptionPane.QUESTION_MESSAGE);
    }
    public static void  reply() {
        //user input for the reply
        String newPost = JOptionPane.showInputDialog(null, "What would you like to reply? ",
                "Project 5", JOptionPane.QUESTION_MESSAGE);
    }
    public static void main(String[] args) {
        //testing
        //connect all to server and menu to run
        String forumnum = viewForum();
        String postNum = postForum();
        post();
    }
}
