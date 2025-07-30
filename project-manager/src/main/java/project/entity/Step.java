package project.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Step {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long stepId;
	
	private String stepText;
	
	private Long stepOrder;
	
}
