-- =====================================================
-- COGNIVITA - Cognitive Activities & Exercises Module
-- Fichier d'import pour la base de données MySQL
-- =====================================================

-- Sélectionner la base de données (à adapter si besoin)
USE cognivita_db;

-- -----------------------------------------------------
-- 1. Supprimer la table si elle existe déjà
-- -----------------------------------------------------
DROP TABLE IF EXISTS cognitive_exercises;

-- -----------------------------------------------------
-- 2. Créer la table cognitive_exercises
-- -----------------------------------------------------
CREATE TABLE cognitive_exercises (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(50) NOT NULL,
    difficulty_level VARCHAR(20) NOT NULL,
    content TEXT,
    time_limit INT,
    max_score INT,
    instructions TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- 3. Insérer des données d'exemple
-- -----------------------------------------------------

-- EXERCICES DE MÉMOIRE (MEMORY)
INSERT INTO cognitive_exercises (title, description, type, difficulty_level, content, time_limit, max_score, instructions) VALUES
('Rappel de mots', 'Mémorisez une liste de mots et rappelez-vous-en après 2 minutes', 'MEMORY', 'EASY', 
'{"words": ["maison", "arbre", "voiture", "livre", "soleil"], "display_time": 30}', 
120, 5, 'Regardez la liste de mots pendant 30 secondes, puis écrivez tous les mots dont vous vous souvenez.'),

('Histoire à compléter', 'Lisez une courte histoire et répondez aux questions sur les détails', 'MEMORY', 'MEDIUM', 
'{"story": "Marie est allée au marché lundi matin. Elle a acheté des pommes, du pain et du lait. En rentrant, elle a rencontré son ami Paul qui lui a donné un livre.", "questions": ["Quel jour Marie est-elle allée au marché ?", "Qu''est-ce que Paul a donné à Marie ?", "Quels fruits Marie a-t-elle achetés ?"]}', 
300, 10, 'Lisez l''histoire attentivement, puis répondez aux questions sans revenir en arrière.'),

('Visages et noms', 'Associez des visages à des noms', 'MEMORY', 'HARD', 
'{"pairs": [{"name": "Sophie", "face": "face1.jpg"}, {"name": "Thomas", "face": "face2.jpg"}, {"name": "Julie", "face": "face3.jpg"}, {"name": "Marc", "face": "face4.jpg"}], "test": "match"}', 
180, 8, 'Mémorisez les noms associés à chaque visage. Ensuite, faites correspondre les noms aux visages présentés dans le désordre.');

-- EXERCICES D'ATTENTION (ATTENTION)
INSERT INTO cognitive_exercises (title, description, type, difficulty_level, content, time_limit, max_score, instructions) VALUES
('Trouver les différences', 'Comparez deux images et trouvez les 5 différences', 'ATTENTION', 'EASY', 
'{"image1": "scene1.jpg", "image2": "scene2.jpg", "differences": 5}', 
180, 5, 'Observez attentivement les deux images et identifiez les 5 différences. Cliquez sur chaque différence quand vous la trouvez.'),

('Test de Stroop', 'Dites la couleur du mot, pas le mot lui-même', 'ATTENTION', 'MEDIUM', 
'{"items": [{"word": "ROUGE", "color": "blue"}, {"word": "VERT", "color": "red"}, {"word": "BLEU", "color": "green"}], "display_time": 2}', 
120, 10, 'Pour chaque mot qui apparaît, dites à voix haute la COULEUR du texte, pas le mot écrit. Exemple: si le mot "ROUGE" est écrit en bleu, répondez "bleu".'),

('Séquence de chiffres', 'Répétez la séquence de chiffres dans l''ordre inverse', 'ATTENTION', 'HARD', 
'{"sequences": [[5, 2, 9], [4, 1, 8, 3], [7, 2, 5, 1, 6]], "reverse": true}', 
240, 15, 'Une séquence de chiffres va apparaître. Votre tâche est de la répéter dans l''ordre INVERSE. La longueur augmente progressivement.');

-- EXERCICES DE LOGIQUE (LOGIC)
INSERT INTO cognitive_exercises (title, description, type, difficulty_level, content, time_limit, max_score, instructions) VALUES
('Suite logique', 'Trouvez le nombre suivant dans la suite', 'LOGIC', 'EASY', 
'{"sequences": [[2, 4, 6, 8, "?"], [5, 10, 15, 20, "?"], [1, 4, 9, 16, "?"]]}', 
180, 3, 'Observez la suite de nombres et trouvez la logique. Quel nombre vient ensuite ?'),

('Sudoku facile', 'Complétez la grille de Sudoku', 'LOGIC', 'MEDIUM', 
'{"grid": [[5, 3, 0, 0, 7, 0, 0, 0, 0], [6, 0, 0, 1, 9, 5, 0, 0, 0], [0, 9, 8, 0, 0, 0, 0, 6, 0], [8, 0, 0, 0, 6, 0, 0, 0, 3], [4, 0, 0, 8, 0, 3, 0, 0, 1], [7, 0, 0, 0, 2, 0, 0, 0, 6], [0, 6, 0, 0, 0, 0, 2, 8, 0], [0, 0, 0, 4, 1, 9, 0, 0, 5], [0, 0, 0, 0, 8, 0, 0, 7, 9]]}', 
600, 20, 'Remplissez la grille de Sudoku. Chaque ligne, colonne et région 3x3 doit contenir les chiffres de 1 à 9 sans répétition.'),

('Problème mathématique', 'Résolvez le problème suivant', 'LOGIC', 'MEDIUM', 
'{"problem": "Un jardinier plante 3 rangées de tomates avec 8 plants par rangée. Combien de plants de tomates a-t-il plantés au total ?", "options": [16, 24, 21, 18], "answer": 24}', 
120, 5, 'Lisez attentivement le problème et choisissez la bonne réponse.');

-- -----------------------------------------------------
-- 4. Vérifier les données importées
-- -----------------------------------------------------
SELECT COUNT(*) AS "Nombre d'exercices importés" FROM cognitive_exercises;
SELECT type, COUNT(*) AS nombre FROM cognitive_exercises GROUP BY type;