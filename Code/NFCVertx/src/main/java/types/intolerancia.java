package types;

public class intolerancia {

	private String nombreIntolerancia = null;

	public intolerancia() {
		
	}
	
	public intolerancia(String nombreIntolerancia) {
		super();
		this.nombreIntolerancia = nombreIntolerancia;
	}

	public String getNombreIntolerancia() {
		return nombreIntolerancia;
	}

	public void setNombreIntolerancia(String nombreIntolerancia) {
		this.nombreIntolerancia = nombreIntolerancia;
	}

	@Override
	public String toString() {
		return "intolerancia [nombreIntolerancia=" + nombreIntolerancia + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nombreIntolerancia == null) ? 0 : nombreIntolerancia.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		intolerancia other = (intolerancia) obj;
		if (nombreIntolerancia == null) {
			if (other.nombreIntolerancia != null)
				return false;
		} else if (!nombreIntolerancia.equals(other.nombreIntolerancia))
			return false;
		return true;
	}
	
	
	
}
