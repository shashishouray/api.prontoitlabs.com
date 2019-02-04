package com.example.jwtangspr;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
@Transactional
public class UserDAO {
    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

  
    
    
    public String registerUser(User usr) {
        Long isSuccess = (Long)getSession().save(usr);
        if(isSuccess >= 1){
            return "Success";
        }else{
            return "Error while Saving User"; 
        }
        
    }


    
    
    @SuppressWarnings("unchecked")
    public List getAllUsers(String pn,String nor) {
        return getSession().createQuery("from User").setFirstResult(Integer.parseInt(pn)*Integer.parseInt(nor)).setMaxResults(Integer.parseInt(nor)).list();
       
    }
    
    public int checkuser(User usr) {
    	int a=0;
		try {
			Session session = sessionFactory.openSession();
			session.beginTransaction();		
			Query q=session.createQuery(" from User where userName=:userName and password=:password");
			q.setParameter("userName", usr.getUserName());
			q.setParameter("password", usr.getPassword());
			List lst=q.list();
			if(lst.size()>0){
				a=1;
			}else{
				a=0;
			}		
			session.getTransaction().commit();
			session.close();
		}catch(Exception e) {
			System.out.println("Exception in Search method: "+e);			
		}
		return a;
	}
    
}