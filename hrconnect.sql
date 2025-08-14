-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : mar. 18 fév. 2025 à 23:19
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
-- Structure de la table `admin`
--

CREATE TABLE `admin` (
                         `id` int(11) NOT NULL,
                         `first_name` varchar(255) NOT NULL,
                         `last_name` varchar(255) NOT NULL,
                         `email` varchar(255) NOT NULL,
                         `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `candidat`
--

CREATE TABLE `candidat` (
                            `id` int(11) NOT NULL,
                            `last_name` varchar(100) NOT NULL,
                            `first_name` varchar(100) NOT NULL,
                            `email` varchar(150) NOT NULL,
                            `phone` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `candidat`
--

INSERT INTO `candidat` (`id`, `last_name`, `first_name`, `email`, `phone`) VALUES
                                                                               (4, 'Aymen', 'Falten', 'aymen@gmail.com', '20123123'),
                                                                               (5, 'Salim', 'Mejri', 'Salim@gmail.com', '20123124'),
                                                                               (6, 'Salah', 'Mejri', 'salah@gmail.com', '20123125');

-- --------------------------------------------------------

--
-- Structure de la table `candidature`
--

CREATE TABLE `candidature` (
                               `id` int(11) NOT NULL,
                               `candidat_id` int(11) NOT NULL,
                               `offre_emploi_id` int(11) NOT NULL,
                               `cv` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `candidature`
--

INSERT INTO `candidature` (`id`, `candidat_id`, `offre_emploi_id`, `cv`) VALUES
                                                                             (3, 4, 3, 'C:\\Users\\Haythem\\Downloads\\hrconnect-javafx\\cvs\\TP1-SQL-LDD.pdf'),
                                                                             (4, 4, 4, 'C:\\Users\\Haythem\\Downloads\\hrconnect-javafx\\cvs\\TP1-SQL-LDD.pdf');

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

-- --------------------------------------------------------

--
-- Structure de la table `employe`
--

CREATE TABLE `employe` (
                           `id` int(11) NOT NULL,
                           `cin` int(8) DEFAULT NULL,
                           `nom` varchar(255) NOT NULL,
                           `prenom` varchar(255) NOT NULL,
                           `email` varchar(255) DEFAULT NULL,
                           `password` varchar(255) DEFAULT NULL,
                           `hiring_date` date DEFAULT NULL,
                           `soldeConges` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `employe`
--

INSERT INTO `employe` (`id`, `cin`, `nom`, `prenom`, `email`, `password`, `hiring_date`, `soldeConges`) VALUES
                                                                                                            (1, 0, 'haythem', 'dridi', 'haithemdridiweb@gmail.com', 'haithemdridiweb@gmail.com', '0000-00-00', 0),
                                                                                                            (18, 0, 'Haythem', 'Haythem', 'haithemdridiweb@gmail.com', 'haithemdridiweb@gmail.com', '2025-02-18', 0);

-- --------------------------------------------------------

--
-- Structure de la table `formateurs`
--

CREATE TABLE `formateurs` (
                              `id` int(11) NOT NULL,
                              `first_name` varchar(255) NOT NULL,
                              `last_name` varchar(255) NOT NULL,
                              `email` varchar(255) NOT NULL,
                              `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `formateurs`
--

INSERT INTO `formateurs` (`id`, `first_name`, `last_name`, `email`, `password`) VALUES
                                                                                    (2, 'Haythem', 'Dridi', 'haithemdridiweb@gmail.com', 'haithemdridiweb@gmail.com'),
                                                                                    (3, 'Amine', 'Raisi', 'amine@gmail.com', 'amine@gmail.com'),
                                                                                    (4, 'Ala', 'Ben Terdayt', 'ala@gmail.com', 'ala');

-- --------------------------------------------------------

--
-- Structure de la table `formations`
--

CREATE TABLE `formations` (
                              `id` int(11) NOT NULL,
                              `formateur_id` int(11) NOT NULL,
                              `title` varchar(255) NOT NULL,
                              `image` varchar(255) NOT NULL,
                              `description` varchar(255) NOT NULL,
                              `is_online` tinyint(1) NOT NULL,
                              `place` varchar(255) DEFAULT NULL,
                              `available_for_employee` tinyint(1) NOT NULL,
                              `available_for_intern` tinyint(1) NOT NULL,
                              `start_date` datetime NOT NULL,
                              `end_date` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `formations`
--

INSERT INTO `formations` (`id`, `formateur_id`, `title`, `image`, `description`, `is_online`, `place`, `available_for_employee`, `available_for_intern`, `start_date`, `end_date`) VALUES
    (31, 2, 'Formation JavaFx', 'C:\\Users\\Haythem\\Downloads\\photos\\javafx-formation.jpg', 'Formation JavaFx', 1, '', 1, 1, '2025-02-20 22:54:37', '2025-02-21 22:54:37');

-- --------------------------------------------------------

--
-- Structure de la table `formation_participation`
--

CREATE TABLE `formation_participation` (
                                           `formation_id` int(11) NOT NULL,
                                           `employe_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `hr`
--

CREATE TABLE `hr` (
                      `id` int(11) NOT NULL,
                      `first_name` varchar(255) NOT NULL,
                      `last_name` varchar(255) NOT NULL,
                      `email` varchar(255) NOT NULL,
                      `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `offre_emploi`
--

CREATE TABLE `offre_emploi` (
                                `id` int(11) NOT NULL,
                                `title` varchar(200) NOT NULL,
                                `description` text NOT NULL,
                                `location` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `offre_emploi`
--

INSERT INTO `offre_emploi` (`id`, `title`, `description`, `location`) VALUES
                                                                          (3, 'Développeur Java Senior', 'Nous recherchons un Développeur Java Senior pour rejoindre notreéquipe dynamique à Paris. Le candidat idéal aura une solide expérience endéveloppement Java et sera capable de travailler sur des projets complexes. Vousserez responsable de la conception, du développement et de la maintenance de nosapplications Java. Vous travaillerez en étroite collaboration avec les équipes deproduit et de conception pour créer des solutions innovantes. Vous devez avoir unebonne compréhension des frameworks Java tels que Spring et Hibernate, ainsi quedes bases de données relationnelles. Une expérience avec les microservices et lesarchitectures cloud est un plus. Vous serez également impliqué dans la revue decode et la formation des développeurs juniors. Nous offrons un environnement detravail stimulant avec des opportunités de croissance et de développementprofessionnel. Si vous êtes passionné par le développement Java et que vouscherchez à relever de nouveaux défis, nous aimerions vous rencontrer.', 'Paris, France'),
                                                                          (4, 'Chef de Projet IT', 'Nous sommes à la recherche d&#39;un Chef de Projet IT pour diriger nosprojets technologiques à Lyon. Le candidat idéal aura une expérience avérée dans lagestion de projets IT, de la planification à l&#39;exécution. Vous serez responsable de lacoordination des équipes de développement, de la gestion des budgets et des délais,et de la communication avec les parties prenantes. Vous devez avoir une bonnecompréhension des méthodologies de gestion de projet telles que Agile et Scrum.Une expérience dans le secteur des technologies de l&#39;information est essentielle.Vous serez également responsable de l&#39;identification des risques et de la mise enplace de plans d&#39;atténuation. Nous offrons un environnement de travail collaboratifavec des opportunités de développement professionnel. Si vous êtes un leadernaturel avec une passion pour la technologie, nous aimerions vous rencontrer.', 'Lyon, France'),
                                                                          (5, 'Ingénieur DevOps', 'Nous recherchons un Ingénieur DevOps pour rejoindre notre équipe àToulouse. Le candidat idéal aura une solide expérience en développement et enopérations, avec une expertise en automatisation et en intégration continue. Vousserez responsable de la mise en place et de la gestion de pipelines CI/CD, de lasurveillance des systèmes et de la résolution des problèmes de performance. Vousdevez avoir une bonne compréhension des outils DevOps tels que Jenkins, Dockeret Kubernetes. Une expérience avec les services cloud tels que AWS ou Azure estun plus. Vous travaillerez en étroite collaboration avec les équipes de développementpour assurer une livraison continue et de haute qualité des applications. Nous offrons un environnement de travail dynamique avec des opportunités de croissance et dedéveloppement professionnel. Si vous êtes passionné par l&#39;automatisation etl&#39;amélioration des processus, nous aimerions vous rencontrer.', 'Toulouse, France'),
                                                                          (6, 'Analyste de Données', 'Nous sommes à la recherche d&#39;un Analyste de Données pour rejoindrenotre équipe à Marseille. Le candidat idéal aura une solide expérience en analyse dedonnées et sera capable de transformer des données brutes en informationsexploitables. Vous serez responsable de la collecte, de l&#39;analyse et de l&#39;interprétationdes données pour aider à la prise de décision stratégique. Vous devez avoir unebonne compréhension des outils d&#39;analyse de données tels que SQL, Python et R.Une expérience avec les plateformes de visualisation de données telles que Tableauou Power BI est un plus. Vous travaillerez en étroite collaboration avec les équipesde produit et de marketing pour identifier les tendances et les opportunités. Nousoffrons un environnement de travail stimulant avec des opportunités dedéveloppement professionnel. Si vous êtes passionné par les données et que vouscherchez à avoir un impact, nous aimerions vous rencontrer.', 'Marseille, France');

-- --------------------------------------------------------

--
-- Structure de la table `quiz`
--

CREATE TABLE `quiz` (
                        `id` int(11) NOT NULL,
                        `formation_id` int(11) NOT NULL,
                        `question` varchar(255) NOT NULL,
                        `reponse1` varchar(255) NOT NULL,
                        `reponse2` varchar(255) DEFAULT NULL,
                        `reponse3` varchar(255) DEFAULT NULL,
                        `num_reponse_correct` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `quiz`
--

INSERT INTO `quiz` (`id`, `formation_id`, `question`, `reponse1`, `reponse2`, `reponse3`, `num_reponse_correct`) VALUES
                                                                                                                     (16, 31, 'Quelle classe est utilisée pour créer une fenêtre en JavaFX ?', 'JFrame', 'Stage', 'Window', 2),
                                                                                                                     (17, 31, 'Quel est le langage utilisé pour styliser une interface JavaFX ?', 'CSS', 'XML', 'JavaScript', 1),
                                                                                                                     (18, 31, 'Quelle méthode est utilisée pour lancer une application JavaFX ?', 'launch', 'start', 'run', 1),
                                                                                                                     (19, 31, 'Quel conteneur est utilisé pour organiser les éléments en colonne dans JavaFX ?', 'VBox ', 'HBox', 'GridPane', 1),
                                                                                                                     (20, 31, 'Quel événement est utilisé pour détecter un clic sur un bouton JavaFX ?', 'setOnAction', 'setOnClick', 'setOnPress', 1);

-- --------------------------------------------------------

--
-- Structure de la table `quiz_reponses`
--

CREATE TABLE `quiz_reponses` (
                                 `employe_id` int(11) NOT NULL,
                                 `quiz_id` int(11) NOT NULL,
                                 `num_reponse` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `stagaires`
--

CREATE TABLE `stagaires` (
                             `id` int(11) NOT NULL,
                             `first_name` varchar(255) NOT NULL,
                             `last_name` varchar(255) NOT NULL,
                             `email` varchar(255) NOT NULL,
                             `password` varchar(255) NOT NULL,
                             `debut_stage` date NOT NULL,
                             `fin_stage` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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
-- Index pour les tables déchargées
--

--
-- Index pour la table `admin`
--
ALTER TABLE `admin`
    ADD PRIMARY KEY (`id`);

--
-- Index pour la table `candidat`
--
ALTER TABLE `candidat`
    ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `phone` (`phone`);

--
-- Index pour la table `candidature`
--
ALTER TABLE `candidature`
    ADD PRIMARY KEY (`id`),
  ADD KEY `candidat_id` (`candidat_id`),
  ADD KEY `offre_emploi_id` (`offre_emploi_id`);

--
-- Index pour la table `demande_conge`
--
ALTER TABLE `demande_conge`
    ADD PRIMARY KEY (`id`),
  ADD KEY `employe_id` (`employe_id`);

--
-- Index pour la table `employe`
--
ALTER TABLE `employe`
    ADD PRIMARY KEY (`id`);

--
-- Index pour la table `formateurs`
--
ALTER TABLE `formateurs`
    ADD PRIMARY KEY (`id`);

--
-- Index pour la table `formations`
--
ALTER TABLE `formations`
    ADD PRIMARY KEY (`id`),
  ADD KEY `fk_formateur` (`formateur_id`);

--
-- Index pour la table `formation_participation`
--
ALTER TABLE `formation_participation`
    ADD KEY `fk_formation` (`formation_id`),
  ADD KEY `fk_employe` (`employe_id`);

--
-- Index pour la table `hr`
--
ALTER TABLE `hr`
    ADD PRIMARY KEY (`id`);

--
-- Index pour la table `offre_emploi`
--
ALTER TABLE `offre_emploi`
    ADD PRIMARY KEY (`id`);

--
-- Index pour la table `quiz`
--
ALTER TABLE `quiz`
    ADD PRIMARY KEY (`id`),
  ADD KEY `fk_quiz_formation` (`formation_id`);

--
-- Index pour la table `quiz_reponses`
--
ALTER TABLE `quiz_reponses`
    ADD KEY `fk_quiz_reponses_employe` (`employe_id`),
  ADD KEY `fk_quiz_reponses_quiz` (`quiz_id`);

--
-- Index pour la table `stagaires`
--
ALTER TABLE `stagaires`
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
-- AUTO_INCREMENT pour la table `admin`
--
ALTER TABLE `admin`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `candidat`
--
ALTER TABLE `candidat`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT pour la table `candidature`
--
ALTER TABLE `candidature`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT pour la table `demande_conge`
--
ALTER TABLE `demande_conge`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT pour la table `employe`
--
ALTER TABLE `employe`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT pour la table `formateurs`
--
ALTER TABLE `formateurs`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT pour la table `formations`
--
ALTER TABLE `formations`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- AUTO_INCREMENT pour la table `hr`
--
ALTER TABLE `hr`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `offre_emploi`
--
ALTER TABLE `offre_emploi`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT pour la table `quiz`
--
ALTER TABLE `quiz`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT pour la table `stagaires`
--
ALTER TABLE `stagaires`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `valider_conge`
--
ALTER TABLE `valider_conge`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `candidature`
--
ALTER TABLE `candidature`
    ADD CONSTRAINT `candidature_ibfk_1` FOREIGN KEY (`candidat_id`) REFERENCES `candidat` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `candidature_ibfk_2` FOREIGN KEY (`offre_emploi_id`) REFERENCES `offre_emploi` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `demande_conge`
--
ALTER TABLE `demande_conge`
    ADD CONSTRAINT `demande_conge_ibfk_1` FOREIGN KEY (`employe_id`) REFERENCES `employe` (`id`);

--
-- Contraintes pour la table `formations`
--
ALTER TABLE `formations`
    ADD CONSTRAINT `fk_formateur` FOREIGN KEY (`formateur_id`) REFERENCES `formateurs` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `formation_participation`
--
ALTER TABLE `formation_participation`
    ADD CONSTRAINT `fk_employe` FOREIGN KEY (`employe_id`) REFERENCES `employe` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_formation` FOREIGN KEY (`formation_id`) REFERENCES `formations` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Contraintes pour la table `quiz`
--
ALTER TABLE `quiz`
    ADD CONSTRAINT `fk_quiz_formation` FOREIGN KEY (`formation_id`) REFERENCES `formations` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Contraintes pour la table `quiz_reponses`
--
ALTER TABLE `quiz_reponses`
    ADD CONSTRAINT `fk_quiz_reponses_employe` FOREIGN KEY (`employe_id`) REFERENCES `employe` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_quiz_reponses_quiz` FOREIGN KEY (`quiz_id`) REFERENCES `quiz` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Contraintes pour la table `valider_conge`
--
ALTER TABLE `valider_conge`
    ADD CONSTRAINT `valider_conge_ibfk_1` FOREIGN KEY (`demande_id`) REFERENCES `demande_conge` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
