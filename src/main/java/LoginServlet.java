import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());

    static {
        // Load the PostgreSQL JDBC driver
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "PostgreSQL JDBC Driver not found", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        LOGGER.info("Attempting to log in user with email: " + email);

        if (validateUser(email, password)) {
            // Login successful
            HttpSession session = request.getSession();
            session.setAttribute("email", email);

            // Retrieve student name and department from the database
            String studentName = getStudentName(email);
            String department = getDepartment(email);

            // Set session attributes consistently
            session.setAttribute("firstName", studentName); // Change this to "firstName" if needed
            session.setAttribute("lastName", ""); // Since you're using full name, you can leave "lastName" empty

            // Set the department attribute
            session.setAttribute("department", department);

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            // Display an alert using JavaScript
            out.println("<script type='text/javascript'>");
            out.println("alert('Login successful. Welcome!');");
            out.println("window.location.href='admission.jsp';");
            out.println("</script>");

            LOGGER.info("Login successful for user: " + email);
        } else {
            // Login failed
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            // Display an alert using JavaScript
            out.println("<script type='text/javascript'>");
            out.println("alert('Invalid email or password. Please try again.');");
            out.println("window.history.back();"); // Go back to the previous page
            out.println("</script>");

            LOGGER.warning("Login failed for user: " + email);
        }
    }

    private boolean validateUser(String email, String password) {
        String url = "jdbc:postgresql://localhost:5432/student_portal";
        String user = "postgres";
        String dbPassword = "PKagame23";

        try (Connection connection = DriverManager.getConnection(url, user, dbPassword)) {
            // SQL query to check if the user exists
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, password);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    // If any row is returned, the user exists with the given credentials
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during user validation for email: " + email, e);
        }

        // Validation failed
        return false;
    }

    private String getStudentName(String email) {
        String url = "jdbc:postgresql://localhost:5432/student_portal";
        String user = "postgres";
        String dbPassword = "A$aprocky08";

        try (Connection connection = DriverManager.getConnection(url, user, dbPassword)) {
            // SQL query to retrieve student name based on email
            String sql = "SELECT firstname, lastname FROM users WHERE email = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, email);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    // If a row is returned, retrieve the first and last name
                    if (resultSet.next()) {
                        String firstName = resultSet.getString("firstname");
                        String lastName = resultSet.getString("lastname");
                        // Return the concatenated full name
                        return firstName + " " + lastName;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while retrieving student name for email: " + email, e);
        }

        // Return a default value or handle the case when no data is retrieved
        return "Unknown";
    }

    private String getDepartment(String email) {
        String url = "jdbc:postgresql://localhost:5432/student_portal";
        String user = "postgres";
        String dbPassword = "A$aprocky08";

        try (Connection connection = DriverManager.getConnection(url, user, dbPassword)) {
            // SQL query to retrieve department based on email
            String sql = "SELECT department FROM users WHERE email = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, email);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    // If a row is returned, retrieve the department
                    if (resultSet.next()) {
                        // Return the department name
                        return resultSet.getString("department");
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while retrieving department for email: " + email, e);
        }

        // Return a default value or handle the case when no data is retrieved
        return "Unknown Department";
    }
}
