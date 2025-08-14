-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : mar. 18 fév. 2025 à 17:14
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `hrconnect`
--

-- --------------------------------------------------------

--
-- Structure de la table `demande_conge`
--

CREATE TABLE `demande_conge` (
  `id` int(11) NOT NULL,
  `employe_id` int(11) NOT NULL,
  `typeConge` enum('MALADIE','ANNUEL','MATERNITE','PATERNITE','FORMATION') NOT NULL,
  `dateDebut` date NOT NULL,
  `dateFin` date NOT NULL,
  `statut` enum('EN_ATTENTE','ACCEPTEE','REFUSEE') DEFAULT 'EN_ATTENTE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `demande_conge`
--

INSERT INTO `demande_conge` (`id`, `employe_id`, `typeConge`, `dateDebut`, `dateFin`, `statut`) VALUES
(1, 1, 'MALADIE', '2025-01-28', '2025-01-29', 'EN_ATTENTE'),
(2, 2, 'MALADIE', '2025-02-12', '2025-02-20', 'EN_ATTENTE'),
(3, 2, 'MALADIE', '2025-02-12', '2025-02-22', 'ACCEPTEE');

-- --------------------------------------------------------

--
-- Structure de la table `employe`
--

CREATE TABLE `employe` (
  `id` int(11) NOT NULL,
  `nom` varchar(50) NOT NULL,
  `prenom` varchar(50) NOT NULL,
  `soldeConges` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `employe`
--

INSERT INTO `employe` (`id`, `nom`, `prenom`, `soldeConges`) VALUES
(1, 'Martin', 'Sophie', 28),
(2, 'Martin', 'Sophie', 20),
(3, 'bcvbcvcvb', 'cbvcv', 20),
(4, 'aza', 'dfsdf', 30),
(5, 'azddad', 'aeaes', 20),
(6, 'azddad', 'aeaes', 20);

-- --------------------------------------------------------

--
-- Structure de la table `formations`
--

CREATE TABLE `formations` (
  `id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `image` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `is_online` tinyint(1) NOT NULL,
  `available_for_employee` tinyint(1) NOT NULL,
  `available_for_intern` tinyint(1) NOT NULL,
  `start_date` datetime NOT NULL,
  `end_date` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `formations`
--

INSERT INTO `formations` (`id`, `title`, `image`, `description`, `is_online`, `available_for_employee`, `available_for_intern`, `start_date`, `end_date`) VALUES
(1, 'testing', 'image', 'testing', 1, 1, 1, '2025-02-12 00:00:00', '2025-02-12 00:00:00'),
(2, 'ala', 'image', 'ala', 1, 1, 1, '2025-02-12 00:00:00', '2025-02-12 00:00:00');

-- --------------------------------------------------------

--
-- Structure de la table `valider_conge`
--

CREATE TABLE `valider_conge` (
  `id` int(11) NOT NULL,
  `demande_id` int(11) NOT NULL,
  `statut` enum('EN_ATTENTE','ACCEPTEE','REFUSEE') NOT NULL,
  `commentaire` text DEFAULT NULL,
  `dateValidation` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `valider_conge`
--

INSERT INTO `valider_conge` (`id`, `demande_id`, `statut`, `commentaire`, `dateValidation`) VALUES
(1, 1, 'EN_ATTENTE', 'aaa', '2025-02-13'),
(2, 1, 'ACCEPTEE', 'aaa', '2025-02-13'),
(3, 2, 'ACCEPTEE', 'sibon merci', '2025-02-13'),


--
-- Index pour les tables déchargées
--

--
-- Index pour la table `demande_conge`
--
ALTER TABLE `demande_conge`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_employe_id` (`employe_id`);

--
-- Index pour la table `employe`
--
ALTER TABLE `employe`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `formations`
--
ALTER TABLE `formations`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `valider_conge`
--
ALTER TABLE `valider_conge`
  ADD PRIMARY KEY (`id`),
  ADD KEY `demande_id` (`demande_id`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `demande_conge`
--
ALTER TABLE `demande_conge`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT pour la table `employe`
--
ALTER TABLE `employe`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT pour la table `formations`
--
ALTER TABLE `formations`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT pour la table `valider_conge`
--
ALTER TABLE `valider_conge`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `demande_conge`
--
ALTER TABLE `demande_conge`
  ADD CONSTRAINT `demande_conge_ibfk_1` FOREIGN KEY (`employe_id`) REFERENCES `employe` (`id`);

--
-- Contraintes pour la table `valider_conge`
--
ALTER TABLE `valider_conge`
  ADD CONSTRAINT `valider_conge_ibfk_1` FOREIGN KEY (`demande_id`) REFERENCES `demande_conge` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
CREATE TABLE Absence (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         employe_id INT NOT NULL,
                         motif ENUM('MALADIE', 'CONGE', 'AUTRE') NOT NULL,
                         justificatif TEXT,
                         remarque TEXT,
                         date_enregistrement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (employe_id) REFERENCES Employe(id) ON DELETE CASCADE
);
