package org.easymoto.data.model;

import org.easymoto.data.repository.operations.OperationAdd;
import org.easymoto.data.repository.operations.OperationDelete;
import org.easymoto.data.repository.operations.OperationUpdate;

public enum Operation {

	ADD(OperationAdd.COMPONENT_NAME), DEL(OperationDelete.COMPONENT_NAME), UPT(OperationUpdate.COMPONENT_NAME);

	String componentName;

	Operation(String componentName) {
		this.componentName = componentName;
	}

	public String component() {
		return componentName;
	}
}
