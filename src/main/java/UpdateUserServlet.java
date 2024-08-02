import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/UpdateUserServlet")
public class UpdateUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    static {
        // Load the PostgreSQL JDBC driver
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String phoneNumber = request.getParameter("phoneNumber");
        String department = request.getParameter("department");

        if (updateUser(email, firstName, lastName, phoneNumber, department)) {
            // Update successful
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            // Display an alert using JavaScript
            out.println("<script type='text/javascript'>");
            out.println("alert('User details updated successfully.');");
            out.println("window.location.href='profile.jsp';");
            out.println("</script>");
        } else {
            // Update failed
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            // Display an alert using JavaScript
            out.println("<script type='text/javascript'>");
            out.println("alert('Update failed. Please try again.');");
            out.println("window.history.back();"); // Go back to the previous page
            out.println("</script>");
        }
    }

    private boolean updateUser(String email, String firstName, String lastName, String phoneNumber, String department) {
        String url = "jdbc:postgresql://localhost:5432/student_portal";
        String user = "postgres";
        String dbPassword = "PKagame23";

        try {
            // Establish the database connection
            try (Connection connection = DriverManager.getConnection(url, user, dbPassword)) {
                // SQL query to update the user details
                String sql = "UPDATE users SET firstName = ?, lastName = ?, phoneNumber = ?, department = ? WHERE email = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, firstName);
                    preparedStatement.setString(2, lastName);
                    preparedStatement.setString(3, phoneNumber);
                    preparedStatement.setString(4, department);
                    preparedStatement.setString(5, email);

                    // Execute the query
                    int rowsAffected = preparedStatement.executeUpdate();

                    // If at least one row is affected, the update was successful
                    return rowsAffected > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Update failed
        return false;
    }
}
