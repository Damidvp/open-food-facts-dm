/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.diginamic.openfoodfacts.service;

import fr.diginamic.openfoodfacts.dao.MarqueDAO;
import fr.diginamic.openfoodfacts.model.Marque;
import java.util.List;

/**
 *
 * @author dmouchagues
 */
public class MarqueService implements IService<Marque> {
    
    private final static MarqueService INSTANCE = new MarqueService();
    private final static MarqueDAO marqueDao = MarqueDAO.getInstance();
            
    private MarqueService(){}
    
    public static MarqueService getInstance(){
        return INSTANCE;
    }

    @Override
    public Marque get(long id) {
        Marque marque = null;
        try{
            marque = marqueDao.get(id);
            marqueDao.closeEM();
        } catch(Exception e){
            System.out.println("Marque inexistante");
        }
        return marque;
    }

    @Override
    public List<Marque> getAll() {
        List<Marque> liste = marqueDao.getAll();
        marqueDao.closeEM();
        return liste;
    }

    @Override
    public void update(Marque marque, String[] params) {
        marqueDao.update(marque, params);
        marqueDao.closeEM();
    }

    @Override
    public void save(Marque marque) {
        marqueDao.save(marque);
        marqueDao.closeEM();
    }

    @Override
    public void delete(Marque marque) {
        marqueDao.delete(marque);
        marqueDao.closeEM();
    }
}
