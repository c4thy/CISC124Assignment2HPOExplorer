/*Term class: to keep track of each of the concepts defined in ontology
 * 
 */
public class Term {
	private String id; 
	private String isA;
	private String termContent;
	
	//constructor
	public Term() {
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the isA
	 */
	public String getIsA() {
		return isA;
	}

	/**
	 * @param isA the isA to set
	 */
	public void setIsA(String isA) {
		this.isA = isA;
	}

	/**
	 * @return the termContent
	 */
	public String getTermContent() {
		return termContent;
	}

	/**
	 * @param termContent the termContent to set
	 */
	public void setTermContent(String termContent) {
		this.termContent = termContent;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Term [id=" + id + ", isA=" + isA + ", termContent=" + termContent + "]";
	}
	
}
