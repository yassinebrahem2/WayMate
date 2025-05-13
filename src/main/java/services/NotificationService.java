package services;

import entities.Notification;
import entities.User;
import javafx.geometry.Pos;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

public class NotificationService {
    private Connection cnx;

    public NotificationService() {
        cnx = DatabaseConnection.getInstance().getConnection();
    }

    // 🗑 Supprimer une notification
    public void supprimer(int id) throws SQLException {
        String req = "delete from notifications where id=?";
        PreparedStatement pre = cnx.prepareStatement(req);
        pre.setInt(1, id);
        pre.executeUpdate();
    }

    // ➕ Ajouter une notification
    public void ajouter(Notification notif) throws SQLException {
        String sql = "INSERT INTO notifications(user_id, message, is_read, created_at) VALUES (?, ?, ?, ?)";
        PreparedStatement pre = cnx.prepareStatement(sql);
        pre.setInt(1, notif.getUser_id());
        pre.setString(2, notif.getMessage());
        pre.setBoolean(3, notif.getIs_read());
        pre.setTimestamp(4, new Timestamp(notif.getCreated_at().getTime())); // Correction ici
        pre.executeUpdate();
    }

    // ✏️ Modifier une notification
    public void modifier(Notification notif) throws SQLException {
        String sql = "UPDATE notifications SET user_id = ?, message = ?, is_read = ?, created_at = ? WHERE id = ?";
        PreparedStatement pre = cnx.prepareStatement(sql);
        pre.setInt(1, notif.getUser_id());
        pre.setString(2, notif.getMessage());
        pre.setBoolean(3, notif.getIs_read());
        pre.setTimestamp(4, new Timestamp(notif.getCreated_at().getTime())); // Correction ici
        pre.setInt(5, notif.getId()); // ⚠️ Était à 1 au lieu de 5
        pre.executeUpdate();
    }
    //afficher liste des utilisateurs qui clients

    public List<Notification> afficherPourUtilisateur(int userId) throws SQLException {
        List<Notification> notifications = new ArrayList<>();

        String sql = "SELECT * FROM notifications WHERE user_id = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Notification n = new Notification(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("message"),
                        rs.getBoolean("is_read"),
                        rs.getTimestamp("created_at")
                );
                notifications.add(n);
            }
        }

        return notifications;
    }



    public void marquerCommeLue(int notificationId) throws SQLException {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE id = ?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, notificationId);
        pst.executeUpdate();
    }

    // 📋 Afficher toutes les notifications
    public List<Notification> afficher() throws SQLException {
        List<Notification> notifs = new ArrayList<>();
        String sql = "SELECT * FROM notifications"; // ⚠️ Correction de la requête SQL

        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            int id = rs.getInt("id");
            int user_id = rs.getInt("user_id");
            String message = rs.getString("message");
            Boolean is_read = rs.getBoolean("is_read");
            Timestamp created_at = rs.getTimestamp("created_at"); // Utilise Timestamp pour plus de précision
            Notification n = new Notification(id, user_id, message, is_read, created_at);
            notifs.add(n);
        }

        return notifs;
    }

    // Nouvelle méthode pour récupérer tous les utilisateurs et les stocker dans une Map
    public Map<Integer, User> recupererTousLesUtilisateurs() throws SQLException {
        Map<Integer, User> usersMap = new HashMap<>();
        String query = "SELECT * FROM users";  // Ajuste cette requête à ta base de données
        try (Statement statement = cnx.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                User user = new User();
                user.setId(id);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                usersMap.put(id, user);
            }
        }
        return usersMap;
    }

    public int getTotalNotifications() throws SQLException {
        String sql = "SELECT COUNT(*) FROM notifications";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getNotificationsLues() throws SQLException {
        String sql = "SELECT COUNT(*) FROM notifications WHERE is_read = 1";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getNotificationsNonLues() throws SQLException {
        String sql = "SELECT COUNT(*) FROM notifications WHERE is_read = 0";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public Map<String, Integer> getNotificationsByUser() throws SQLException {
        String sql = "SELECT u.username, COUNT(*) FROM notifications n JOIN users u ON n.user_id = u.id GROUP BY u.username";
        Map<String, Integer> stats = new LinkedHashMap<>();
        try (Statement st = cnx.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                stats.put(rs.getString(1), rs.getInt(2));
            }
        }
        return stats;
    }

    public Map<String, Integer> getNotificationsByPeriod() throws SQLException {
        Map<String, Integer> stats = new LinkedHashMap<>();
        stats.put("Aujourd'hui", getNotificationsCountByDate("CURDATE()"));
        stats.put("Cette semaine", getNotificationsCountByDate("CURDATE() - INTERVAL 7 DAY"));
        stats.put("Ce mois", getNotificationsCountByDate("CURDATE() - INTERVAL 1 MONTH"));
        stats.put("Total", getTotalNotifications());
        return stats;
    }

    private int getNotificationsCountByDate(String dateCondition) throws SQLException {
        String sql = "SELECT COUNT(*) FROM notifications WHERE created_at >= " + dateCondition;
        try (Statement st = cnx.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public Map<String, Integer> getDailyNotifications() throws SQLException {
        Map<String, Integer> stats = new LinkedHashMap<>();
        String sql = "SELECT DATE(created_at) AS day, COUNT(*) FROM notifications GROUP BY day ORDER BY day DESC LIMIT 30";
        try (Statement st = cnx.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                stats.put(rs.getString("day"), rs.getInt(2));
            }
        }
        return stats;
    }






}
