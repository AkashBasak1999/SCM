
package com.contact.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Service
public class User {
	//If we are using the geneationtype.Auto then  Id will be automatically generated 
	//and for the automatic generation in the database a user_seq table will be created 
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public int id;
	//if we are using the GenerationType.ALL then in the database with User table another table user_seq will be created 
	
	@NotBlank(message="name should not be blank")
	@Size(min=2,max=14, message="name should be in between 2 to 20 letters")
	private String name;
	private String email;
	@Column(unique = true)
	private String password;
	private String role;
	private boolean enabled;
	private String imageURL;
	@Column(length=50)
	private String about;
	
	//mappedBy: normally while running the application a User_Contact table will be also created 
	//but after using this mapped by we can this User_Contact table will not be created and the 
	//relation will get hold by the user User
	
	//orphanRemoval: true.  by using this orphanremoval if we are deleteing any child entry then that should be deleted from the parent table 
	//just like the ON DELETE CASCASE is a TRM specific. orphanRemoval is ORM specific 
	
	//Fetch type: 
	@OneToMany(cascade = CascadeType.ALL,orphanRemoval = true, fetch=FetchType.LAZY, mappedBy = "user")
	private List<Contact> contacts=new ArrayList<>();
	
//	CascadeType.ALL is a cascading type in Hibernate that specifies that all state 
//	transitions (create, update, delete, and refresh) should be cascaded from the parent entity 
//	to the child entities.When a Customer entity is persisted, updated, or deleted, all 
//	associated Order entities will also be persisted, updated, or deleted.
	
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	public List<Contact> getContacts() {
		return contacts;
	}
	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}
	@Override
	public boolean equals(Object obj) {
		return this.id==((Contact)obj).getId();
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", role=" + role
				+ ", enabled=" + enabled + ", imageURL=" + imageURL + ", about=" + about + ", contacts=" + contacts
				+ "]";
	}

}
