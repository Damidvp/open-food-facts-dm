/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.diginamic.openfoodfacts.service;

import fr.diginamic.openfoodfacts.dao.AllergeneDAO;
import fr.diginamic.openfoodfacts.model.Allergene;
import java.util.List;

/**
 *
 * @author dmouchagues
 */
public class AllergeneService implements IService<Allergene> {
    private final static AllergeneService INSTANCE = new AllergeneService();
    private final static AllergeneDAO allergeneDao = AllergeneDAO.getInstance();
    
    private AllergeneService(){}
    
    public static AllergeneService getInstance(){
        return INSTANCE;
    }

    @Override
    public Allergene get(long id) {
        Allergene allergene = null;
        try{
            allergene = allergeneDao.get(id);
            allergeneDao.closeEM();
        } catch(Exception e){
            System.out.println("Allergene inexistant");
        }
        return allergene;
    }

    @Override
    public List<Allergene> getAll() {
        List<Allergene> liste = allergeneDao.getAll();
        allergeneDao.closeEM();
        return liste;
    }

    @Override
    public void update(Allergene allergene, String[] params) {
        allergeneDao.update(allergene, params);
        allergeneDao.closeEM();
    }

    @Override
    public void save(Allergene allergene) {
        allergeneDao.save(allergene);
        allergeneDao.closeEM();
    }

    @Override
    public void delete(Allergene allergene) {
        allergeneDao.delete(allergene);
        allergeneDao.closeEM();
    }
}
