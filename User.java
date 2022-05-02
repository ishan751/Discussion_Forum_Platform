import javax.swing.*;
import java.io.*;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

/**
 * The User class
 *
 * <p>Purdue University -- CS18000 -- Spring 2021</p>
 *
 * @author Group 92
 * @version April 10, 2022
 */

public class User {

    private String username;
    private String password;
    private String role;


    public User(String username, String password, String role) {
        Objects.requireNonNull(username, "the specified username is null");
        Objects.requireNonNull(password, "the specified password is null");
        Objects.requireNonNull(role, "the specified role is null");
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User() {
        this.username = null;
        this.password = null;
        this.role = null;
    }

    public String getUsername() {
        return username;
    }


    public String getPassword() {
        return password;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void createLoginFile() throws IOException {
        File input;
        Scanner scan = new Scanner(System.in);

        String username = JOptionPane.showInputDialog(null, "Please enter username:", "Username", JOptionPane.QUESTION_MESSAGE);
        String password = JOptionPane.showInputDialog(null, "Please enter password:", "Password", JOptionPane.QUESTION_MESSAGE);
        String role = JOptionPane.showInputDialog(null, "Please enter role ('teacher' or 'student')", "Username", JOptionPane.QUESTION_MESSAGE);

        while (!((role.equalsIgnoreCase("teacher")) || (role.equalsIgnoreCase("student")))) {

            JOptionPane.showMessageDialog(null, "Try Again. Ensure role is 'teacher' or 'student' ", "Username", JOptionPane.ERROR_MESSAGE);
            role = JOptionPane.showInputDialog(null, "Please enter role ('teacher' or 'student')", "Username", JOptionPane.QUESTION_MESSAGE);
        }
        if (role.equalsIgnoreCase("teacher")) {
            input = new File("teacher/" + username + ".txt");
        } else {
            input = new File("student/" + username + ".txt");
        }


        boolean file = false;
        while (!file) {
            if (!input.exists()) {
                input.createNewFile();
                try {
                    PrintWriter pw = new PrintWriter(new FileOutputStream(input, false));
                    pw.println(password);
                    pw.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                JOptionPane.showMessageDialog(null, "Your username is: " + input.getName(), "Username", JOptionPane.QUESTION_MESSAGE);
                return;
            }
            Random r = new Random();
            char addon = (char) (r.nextInt(26) + 'a');
            String filen = username + addon;
            if (role.equalsIgnoreCase("teacher")) {
                input = new File("teacher/" + filen + ".txt");
            } else {
                input = new File("student/" + filen + ".txt");
            }

        }
    }

    public void deletelogin(String username, String password, String role) throws IOException {
        String filename = createfilename(username, role);
        File input = new File(filename);
        if (input.exists()) {
            JOptionPane.showMessageDialog(null, "Deleted the file: " + input.getName(), "Username", JOptionPane.QUESTION_MESSAGE);
            input.delete();

        } else {
            JOptionPane.showMessageDialog(null, "Failed to delete file, ensure username is correct " + input.getName(), "Username", JOptionPane.QUESTION_MESSAGE);
        }
    }

    public void editFile(String username, String password, String role) throws IOException {
        Object[] loginMenu = {"Sign up", "Login"};
        Object[] edits = {"Username", "Password"};
        String filename = createfilename(username, role);
        File inputFile = new File(filename);
        Scanner scan = new Scanner(System.in);

        int userorpass = JOptionPane.showOptionDialog(null, "Edit username or password?", "Role", JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, edits, null);

        if (userorpass == 0) {

            String newusername = JOptionPane.showInputDialog(null, "You are changing the username...What would you like your new username to be?", "Username", JOptionPane.QUESTION_MESSAGE);
            String tryusername = JOptionPane.showInputDialog(null, "Please retype to confirm your new username?", "Username", JOptionPane.QUESTION_MESSAGE);


            if (newusername.equals(tryusername)) {
                File file2 = new File(role + "/" + newusername + ".txt");
                if (inputFile.exists()) {
                    boolean success = inputFile.renameTo(file2);
                } else {
                    JOptionPane.showMessageDialog(null, "The username does not exist, you may have to create an account.", "Username", JOptionPane.QUESTION_MESSAGE);
                }
                JOptionPane.showMessageDialog(null, "Congrats. You have changed your username.", "Username", JOptionPane.QUESTION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Sorry, we were not able to change the username. Ensure inputs match", "Username", JOptionPane.QUESTION_MESSAGE);
            }
        }
        if (userorpass == 1) {

            String newpassword = JOptionPane.showInputDialog(null, "You are changing the password...What would you like your new password to be?", "Password", JOptionPane.QUESTION_MESSAGE);
            String trypassword = JOptionPane.showInputDialog(null, "Please retype to confirm your new password?", "Password", JOptionPane.QUESTION_MESSAGE);


            if (newpassword.equals(trypassword)) {
                if (inputFile.exists()) {
                    PrintWriter pw = new PrintWriter(inputFile);
                    pw.println(newpassword);
                    pw.close();

                } else {
                    JOptionPane.showMessageDialog(null, "The password does not exist, you may have to create an account.", "Password", JOptionPane.QUESTION_MESSAGE);
                }
                JOptionPane.showMessageDialog(null, "Congrats. You have changed your password.", "Username", JOptionPane.QUESTION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Sorry, we were not able to change the password. Ensure inputs match", "Password", JOptionPane.QUESTION_MESSAGE);
            }

        }

    }

    public boolean login() throws IOException {
        boolean loggedin = true;
        String filename = createfilename(username, role);
        File inputFile = new File(filename);
        try {
            if (inputFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                String lookpass = password;
                String currentLine;

                while ((currentLine = reader.readLine()) != null) {
                    // trim newline when comparing with lineToRemove
                    String trimmedLine = currentLine.trim();
                    if (trimmedLine.equals(lookpass)) {
                        loggedin = true;
                        JOptionPane.showMessageDialog(null, "You Logged In!", "Login", JOptionPane.QUESTION_MESSAGE);
                    } else {
                        loggedin = false;
                        JOptionPane.showMessageDialog(null, "Password is Incorrect!", "Login", JOptionPane.QUESTION_MESSAGE);
                    }
                }
            } else {
                loggedin = false;
                JOptionPane.showMessageDialog(null, "Username is Incorrect!", "Login", JOptionPane.QUESTION_MESSAGE);

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return loggedin;

    }

    public static String createfilename(String username, String role) {
        String filename;
        if (role.equalsIgnoreCase("teacher")) {
            filename = "teacher/" + username + ".txt";
        } else {
            filename = "student/" + username + ".txt";
        }
        return filename;

    }

    public static String checkrole(String username, String role) {
        String roleofuser = null;
        String filename = createfilename(username, role);
        File inputFile = new File(filename);
        if (inputFile.exists()) {
            roleofuser = role;
        } else {
            JOptionPane.showMessageDialog(null, "We can't seem to find you. Please reinput your username and role.", "Login", JOptionPane.QUESTION_MESSAGE);
        }
        return roleofuser;
    }

}