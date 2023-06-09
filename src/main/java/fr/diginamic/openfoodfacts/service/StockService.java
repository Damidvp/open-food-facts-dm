/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.diginamic.openfoodfacts.service;

import fr.diginamic.openfoodfacts.dao.AdditifDAO;
import fr.diginamic.openfoodfacts.dao.AllergeneDAO;
import fr.diginamic.openfoodfacts.dao.CategorieDAO;
import fr.diginamic.openfoodfacts.dao.IngredientDAO;
import fr.diginamic.openfoodfacts.dao.MarqueDAO;
import fr.diginamic.openfoodfacts.dao.ProduitDAO;
import fr.diginamic.openfoodfacts.model.Additif;
import fr.diginamic.openfoodfacts.model.Allergene;
import fr.diginamic.openfoodfacts.model.Categorie;
import fr.diginamic.openfoodfacts.model.Ingredient;
import fr.diginamic.openfoodfacts.model.Marque;
import fr.diginamic.openfoodfacts.model.Produit;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dmouchagues Classe qui parse le fichier CSV, tout en créant une liste
 * de tous les produits prêts à être insérés en base
 */
public class StockService {

    private final static StockService INSTANCE = new StockService();
    private AdditifDAO additifDao = AdditifDAO.getInstance();
    private AllergeneDAO allergeneDao = AllergeneDAO.getInstance();
    private IngredientDAO ingredientDao = IngredientDAO.getInstance();
    private MarqueDAO marqueDao = MarqueDAO.getInstance();
    private CategorieDAO categorieDao = CategorieDAO.getInstance();
    private ProduitDAO produitDao = ProduitDAO.getInstance();

    private StockService() {
        try {
            Path pathFile = Paths.get("target/open-food-facts.csv"); //Chemin du fichier CSV
            List<String> linesFile = Files.readAllLines(pathFile, StandardCharsets.UTF_8);

            for (int i = 1; i < linesFile.size(); i++) {
                String[] tokens = linesFile.get(i).split("\\|");
                if (tokens.length < 30) {
                    continue;
                }
                Produit produit = new Produit();

                saveCategorie(tokens, produit);
                saveInfo(tokens, produit);
                saveLists(tokens, produit);

                produitDao.save(produit);

            }
            produitDao.closeEM();
        } catch (IOException ex) {
            Logger.getLogger(StockService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @return the instance of StockService
     */
    public static StockService getInstance() {
        return INSTANCE;
    }

    /**
     * @return Float value of String. Return 0 if the String cannot be parsed
     */
    private Float getValueOf(String token) {
        try {
            Float result = Float.valueOf(token);
            return result;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Cleans name of a Marque before insertion
     */
    private String cleanMarqueName(String s) {
        s = s.trim();
        return s;
    }
    
    /**
     * Cleans name of a Categorie before insertion
     */
    private String cleanCategorieName(String s) {
        if (s.length() > 0) {
            s = s.replaceAll("\"", "");
        }
        return s;
    }

    /**
     * Cleans name of an Ingredient before insertion
     */
    private String cleanIngredientName(String s) {
        if (s.length() > 0) {
            s = s.replace("_", "") //Supprime _
                    .replace("*", "") //Supprime *
                    .replace(".", "") //Supprime .
                    .replaceAll("\\(.*?\\)", "") //Supprime (entre parenthèses)
                    .replaceAll("\\[.*?\\]", "") //Supprime [entre crochets]
                    .replaceAll("\\s*\\d+(\\.\\d+)?%\\s*", "") //Supprime pourcentage sans espace
                    .replaceAll("\\s*\\d+(\\.\\d+)? %\\s*", "") //Supprime pourcentage avec espace
                    .replaceAll("\\s*\\d+(\\.\\d+)? g\\s*", "") //Supprime les grammes
                    .replaceAll("[()\\[\\]{}]", "") //Supprime parenthèses seules
                    .replace("FR", "") //Suprimme "FR"
                    .trim();
        }
        return s;
    }
    
    /**
     * Cleans name of an Allergene before insertion
     */
    private String cleanAllergeneName(String s) {
        if (s.length() > 0) {
            s = s.replaceAll("_", "") //Supprime _
                    .replaceAll("\\*", "") //Supprime *
                    .replace("en:", "")
                    .replace("fr:", "")
                    .trim();
        }
        return s;
    }
    
    /**
     * Cleans name of an Additif before insertion
     */
    private String cleanAdditifName(String s) {
        s = s.trim();
        return s;
    }
    
    /**
     * Saves the Categorie of a Produit in database
     */
    private void saveCategorie(String[] tokens, Produit produit) {
        Categorie categorieExistante = categorieDao.getByName(cleanCategorieName(tokens[0]));
        if (categorieExistante != null) {
            produit.setCategorie(categorieExistante);
        } else {
            Categorie categorie = new Categorie();
            categorie.setNom(cleanCategorieName(tokens[0]));
            categorieDao.save(categorie);
            produit.setCategorie(categorie);
        }
    }

    /**
     * Saves other information about a Produit in database
     */
    private void saveInfo(String[] tokens, Produit produit) {
        produit.setNom(tokens[2]);
        produit.setScore(tokens[3].charAt(0));

        produit.setEnergie100g(getValueOf(tokens[5])); //Si la valeur est une String vide, on affecte 0
        produit.setGraisse100g(getValueOf(tokens[6]));
        produit.setSucres100g(getValueOf(tokens[7]));
        produit.setFibres100g(getValueOf(tokens[8]));
        produit.setProteines100g(getValueOf(tokens[9]));
        produit.setSel100g(getValueOf(tokens[10]));
        produit.setVitA100g(getValueOf(tokens[11]));
        produit.setVitD100g(getValueOf(tokens[12]));
        produit.setVitE100g(getValueOf(tokens[13]));
        produit.setVitK100g(getValueOf(tokens[14]));
        produit.setVitC100g(getValueOf(tokens[15]));
        produit.setVitB1100g(getValueOf(tokens[16]));
        produit.setVitB2100g(getValueOf(tokens[17]));
        produit.setVitPP100g(getValueOf(tokens[18]));
        produit.setVitB6100g(getValueOf(tokens[19]));
        produit.setVitB9100g(getValueOf(tokens[20]));
        produit.setVitB12100g(getValueOf(tokens[21]));
        produit.setCalcium100g(getValueOf(tokens[22]));
        produit.setMagnesium100g(getValueOf(tokens[23]));
        produit.setIron100g(getValueOf(tokens[24]));
        produit.setFer100g(getValueOf(tokens[25]));
        produit.setBetaCarotene100g(getValueOf(tokens[26]));

        produit.setPresenceHuilePalme(Boolean.valueOf(tokens[27]));
    }

    /**
     * Saves all lists of a Produit in database
     */
    private void saveLists(String[] tokens, Produit produit) {
        List<Marque> marques = new ArrayList<>();
        List<Ingredient> ingredients = new ArrayList<>();
        List<Allergene> allergenes = new ArrayList<>();
        List<Additif> additifs = new ArrayList<>();

        String[] tokensMarques = tokens[1].split(",");
        String[] tokensIngredients = tokens[4].split(",|;| - ");
        String[] tokensAllergenes = tokens[28].split(",|;");
        String[] tokensAdditifs = tokens[29].split(",|;");

        for (String uneMarque : tokensMarques) {
            uneMarque = cleanMarqueName(uneMarque);
            Marque marqueExistante = marqueDao.getByName(uneMarque);
            if (marqueExistante != null) {
                marques.add(marqueExistante);
            } else {
                Marque marque = new Marque();
                marque.setNom(uneMarque);
                marques.add(marque);
                marqueDao.save(marque);
            }
        }
        for (String unIngredient : tokensIngredients) {
            if (unIngredient.length() > 0) {
                unIngredient = cleanIngredientName(unIngredient);
                Ingredient ingredientExistant = ingredientDao.getByName(unIngredient);
                if (ingredientExistant != null) {
                    ingredients.add(ingredientExistant);
                } else {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setNom(unIngredient);
                    ingredients.add(ingredient);
                    ingredientDao.save(ingredient);
                }
            }
        }
        for (String unAllergene : tokensAllergenes) {
            if (unAllergene.length() > 0) {
                unAllergene = cleanAllergeneName(unAllergene);
                Allergene allergeneExistant = allergeneDao.getByName(unAllergene);
                if (allergeneExistant != null) {
                    allergenes.add(allergeneExistant);
                } else {
                    Allergene allergene = new Allergene();
                    allergene.setNom(unAllergene);
                    allergenes.add(allergene);
                    allergeneDao.save(allergene);
                }
            }
        }
        for (String unAdditif : tokensAdditifs) {
            if (unAdditif.length() > 0) {
                unAdditif = cleanAdditifName(unAdditif);
                Additif additifExistant = additifDao.getByName(unAdditif);
                if (additifExistant != null) {
                    additifs.add(additifExistant);
                } else {
                    Additif additif = new Additif();
                    additif.setNom(unAdditif);
                    additifs.add(additif);
                    additifDao.save(additif);
                }
            }
        }

        produit.setMarques(marques);
        produit.setListeIngredients(ingredients);
        produit.setListeAllergenes(allergenes);
        produit.setListeAdditifs(additifs);
    }

}
