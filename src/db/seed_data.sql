-- USERS
INSERT INTO users (first_name, last_name, username, email, phone, password, role)
VALUES
    ('Alice', 'Dupont', 'alice_d', 'alice@example.com', '0600000001', 'hashed_pw_1', 'client'),
    ('Bob', 'Martin', 'bobm', 'bob@example.com', '0600000002', 'hashed_pw_2', 'admin'),
    ('Charlie', 'Durand', 'charlie_d', 'charlie@example.com', '0600000003', 'hashed_pw_3', 'client'),
    ('David', 'Bernard', 'daveb', 'david@example.com', '0600000004', 'hashed_pw_4', 'client'),
    ('Emma', 'Moreau', 'emmam', 'emma@example.com', '0600000005', 'hashed_pw_5', 'client'),
    ('Fanny', 'Blanc', 'fannyb', 'fanny@example.com', '0600000006', 'hashed_pw_6', 'client'),
    ('Georges', 'Petit', 'georgep', 'georges@example.com', '0600000007', 'hashed_pw_7', 'client'),
    ('Hélène', 'Roux', 'helener', 'helene@example.com', '0600000008', 'hashed_pw_8', 'client'),
    ('Isaac', 'Faure', 'isaacf', 'isaac@example.com', '0600000009', 'hashed_pw_9', 'client'),
    ('Julie', 'Perrot', 'juliep', 'julie@example.com', '0600000010', 'hashed_pw_10', 'client');

-- VEHICLES
INSERT INTO vehicles (license_plate, type, brand, model, year, status, price_per_hour, location_lat, location_lng, image_url)
VALUES
    ('AA123BB', 'voiture', 'Peugeot', '208', 2020, 'disponible', 15.50, 48.8566, 2.3522, 'https://example.com/img/peugeot208.jpg'),
    ('CC456DD', 'vélo', 'Decathlon', 'Elops', 2022, 'disponible', 3.00, 48.8584, 2.2945, 'https://example.com/img/elops.jpg'),
    ('EE789FF', 'trotinette', 'Xiaomi', 'M365', 2021, 'maintenance', 2.50, 48.8738, 2.2950, 'https://example.com/img/xiaomi.jpg'),
    ('GG321HH', 'van', 'Renault', 'Kangoo', 2019, 'loué', 20.00, 48.8690, 2.3300, 'https://example.com/img/kangoo.jpg'),
    ('II654JJ', 'vélo', 'Btwin', 'Original', 2023, 'disponible', 2.80, 48.8600, 2.3500, 'https://example.com/img/btwin.jpg'),
    ('KK987LL', 'trotinette', 'Segway', 'Ninebot', 2021, 'disponible', 3.00, 48.8670, 2.3600, 'https://example.com/img/segway.jpg'),
    ('MM147NN', 'voiture', 'Citroën', 'C3', 2022, 'loué', 16.00, 48.8530, 2.3490, 'https://example.com/img/c3.jpg'),
    ('OO258PP', 'van', 'Ford', 'Transit', 2018, 'maintenance', 22.00, 48.8590, 2.3400, 'https://example.com/img/transit.jpg'),
    ('QQ369RR', 'vélo', 'Gazelle', 'Ultimate', 2023, 'disponible', 4.00, 48.8545, 2.3411, 'https://example.com/img/gazelle.jpg'),
    ('SS471TT', 'voiture', 'Tesla', 'Model 3', 2021, 'disponible', 25.00, 48.8510, 2.3400, 'https://example.com/img/tesla3.jpg');

-- BOOKINGS
INSERT INTO bookings (user_id, vehicle_license_plate, start_time, end_time, total_price, status)
VALUES
    (1, 'AA123BB', '2025-04-20 10:00:00', '2025-04-20 12:00:00', 31.00, 'confirmée'),
    (2, 'CC456DD', '2025-04-21 14:00:00', '2025-04-21 16:30:00', 7.50, 'terminée'),
    (3, 'GG321HH', '2025-04-22 08:00:00', '2025-04-22 11:00:00', 60.00, 'en_attente'),
    (4, 'KK987LL', '2025-04-22 12:00:00', '2025-04-22 14:00:00', 6.00, 'confirmée'),
    (5, 'MM147NN', '2025-04-23 09:00:00', '2025-04-23 11:30:00', 40.00, 'terminée'),
    (6, 'SS471TT', '2025-04-24 13:00:00', '2025-04-24 15:00:00', 50.00, 'confirmée'),
    (7, 'II654JJ', '2025-04-24 10:00:00', '2025-04-24 11:00:00', 2.80, 'en_attente'),
    (8, 'QQ369RR', '2025-04-25 14:00:00', '2025-04-25 17:00:00', 12.00, 'confirmée'),
    (9, 'OO258PP', '2025-04-25 09:00:00', '2025-04-25 12:00:00', 66.00, 'annulée'),
    (10, 'EE789FF', '2025-04-26 10:00:00', '2025-04-26 11:30:00', 3.75, 'confirmée');

-- PAYMENTS
INSERT INTO payments (booking_id, amount, method, status)
VALUES
    (1, 31.00, 'credit_card', 'payé'),
    (2, 7.50, 'paypal', 'payé'),
    (3, 60.00, 'cash', 'en_attente'),
    (4, 6.00, 'credit_card', 'payé'),
    (5, 40.00, 'paypal', 'payé'),
    (6, 50.00, 'credit_card', 'payé'),
    (7, 2.80, 'cash', 'en_attente'),
    (8, 12.00, 'paypal', 'payé'),
    (9, 66.00, 'credit_card', 'échoué'),
    (10, 3.75, 'paypal', 'payé');

-- REVIEWS
INSERT INTO reviews (user_id, vehicle_license_plate, rating, comment)
VALUES
    (1, 'AA123BB', 5, 'Très bonne voiture, propre et facile à conduire.'),
    (2, 'CC456DD', 4, 'Bon vélo, mais la selle était un peu dure.'),
    (3, 'GG321HH', 3, 'Le van était spacieux mais un peu sale.'),
    (4, 'KK987LL', 5, 'Très pratique et rapide.'),
    (5, 'MM147NN', 4, 'Bonne voiture, bon service.'),
    (6, 'SS471TT', 5, 'Incroyable expérience avec la Tesla.'),
    (7, 'II654JJ', 4, 'Vélo en bon état.'),
    (8, 'QQ369RR', 5, 'Excellent vélo électrique.'),
    (9, 'OO258PP', 2, 'Trop vieux et bruyant.'),
    (10, 'EE789FF', 3, 'Fonctionnelle, mais batterie faible.');

-- NOTIFICATIONS
INSERT INTO notifications (user_id, message, is_read)
VALUES
    (1, 'Votre réservation est confirmée.', FALSE),
    (2, 'Merci pour votre paiement.', TRUE),
    (3, 'Votre réservation est en attente.', FALSE),
    (4, 'La maintenance est prévue demain.', TRUE),
    (5, 'Nouvelle promotion disponible.', FALSE),
    (6, 'Merci pour votre avis.', TRUE),
    (7, 'Votre réservation a été annulée.', TRUE),
    (8, 'Votre véhicule est prêt.', FALSE),
    (9, 'Erreur de paiement détectée.', FALSE),
    (10, 'Votre trajet est terminé.', TRUE);
