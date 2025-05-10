package test;

import entities.Review;
import services.ReviewService;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        ReviewService service = new ReviewService();

        // Création d'un nouvel avis
        Review nouvelAvis = new Review(
                0,                            // id (inutile si auto-incrément)
                "5",                          // user_id
                "AA123BB",                    // vehicle_license_plate
                1,                            // rating
                "expérience traumatisante",   // comment
                "2025-01-01 14:30:00"         // created_at
        );

        try {
            // ➕ Ajouter un avis
            service.ajouter(nouvelAvis);
            System.out.println("Avis ajouté : " + nouvelAvis);

            // 📋 Afficher tous les avis
            System.out.println("Liste des avis :");
            service.afficher().forEach(System.out::println);

        } catch (SQLException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }
}
