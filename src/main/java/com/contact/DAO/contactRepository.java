package com.contact.DAO;

import org.springframework.data.repository.CrudRepository;

import com.contact.entity.Contact;
import java.util.List;
import java.util.Optional;


public interface contactRepository extends CrudRepository<Contact, Integer> {
	
	void deleteById(Integer id);
	Optional<Contact> findById(Integer id);
	
}
