package com.contact.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table
public class Contact {
	//If we are using the geneationtype.Auto then  Id will be automatically generated 
	//and for the automatic generation in the database a contact_seq table will be created 

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String name;
	private String secondName;
	private String work;
	private String email;
	private String phone;

	@Column(length = 500)
	private String description;

	@ManyToOne()
	private User user;

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

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	//here any toString() methode can not be written otherwise it will throw error 
//	@Override
//	public String toString() {
//		So this toString() is not valid at this position 
//	return "Contact [id=" + id + ", name=" + name + ", secondName=" + secondName + ", work=" + work + ", email="
//				+ email + ", phone=" + phone + ", description=" + description + ", user=" + user + "]";
//	}
}
