ALTER TABLE `payments` CHANGE `status` `status` ENUM('payé','en_attente','échoué','en_retard') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'en_attente';
