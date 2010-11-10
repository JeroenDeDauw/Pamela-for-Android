package pamela.client;

public class PamelaItem {

	protected String name;
	
	public PamelaItem( String name ) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
