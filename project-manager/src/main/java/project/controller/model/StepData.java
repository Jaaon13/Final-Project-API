package project.controller.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import project.entity.Step;

@Data
@NoArgsConstructor
public class StepData {
	
	private Long stepId;
	
	private String stepText;
	
	private Long stepOrder;
	
	public StepData(Step s) {
		
		this.stepId = s.getStepId();
		this.stepText = s.getStepText();
		this.stepOrder = s.getStepOrder();
		
	}

	public Step toStep() { // Returns The Entity
		Step s = new Step();
		
		s.setStepId(stepId);
		s.setStepText(stepText);
		s.setStepOrder(stepOrder);
		
		return s;
	}
	
}