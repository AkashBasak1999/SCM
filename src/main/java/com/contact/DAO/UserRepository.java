package com.contact.DAO;

import java.util.List;
import java.util.Optional;

import org.hibernate.sql.Update;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import com.contact.entity.Contact;
import com.contact.entity.User;

public interface UserRepository extends JpaRepository<User,Integer>{
	User getByName(String name);
	
	@Query("select n from User n where n.email=:email")
	User getUserByUserName(@Param("email") String email);
	
	//by using page interface we can send a particular position of the list to the frontend
	//page is a sublist of a provided list
	//pageable stores pagination information- it has two variable that is 
	//currentPage
	//contact per page - 5
	@Query("from Contact as c where c.user.id =:userId")
	public Page<Contact> findContactsByUser(@Param("userId")int userId,Pageable pageable);

	@Query("from Contact as c where c.user.id =:userId")
	public List<Contact> findContactsByUser(@Param("userId")int userId);
	
	@Query("from Contact as c where c.id=:id")
	Contact ContactDetails(@Param("id") Integer id);
	
}
