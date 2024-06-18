package capstone.relation.api.auth.jwt.response;

public enum WorkspaceStateType {
	HAS_WORKSPACE("hasWorkSpace"),
	NO_SPACE("noSpace"),
	INVITED("invited"),
	OVERFLOW("overflow");

	private final String state;

	WorkspaceStateType(String state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return this.state;
	}

	public static WorkspaceStateType fromString(String text) {
		for (WorkspaceStateType b : WorkspaceStateType.values()) {
			if (b.state.equalsIgnoreCase(text)) {
				return b;
			}
		}
		throw new IllegalArgumentException("No constant with text " + text + " found");
	}
}
