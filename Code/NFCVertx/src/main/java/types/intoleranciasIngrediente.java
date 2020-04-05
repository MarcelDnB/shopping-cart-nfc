package types;

public class intoleranciasIngrediente {
	
	private Integer idIntolerancia;
	private Integer idIngrediente;
	
	public intoleranciasIngrediente() {
		
	}

	public intoleranciasIngrediente(Integer idIntolerancia, Integer idIngrediente) {
		super();
		this.idIntolerancia = idIntolerancia;
		this.idIngrediente = idIngrediente;
	}

	public Integer getIdIntolerancia() {
		return idIntolerancia;
	}

	public void setIdIntolerancia(Integer idIntolerancia) {
		this.idIntolerancia = idIntolerancia;
	}

	public Integer getIdIngrediente() {
		return idIngrediente;
	}

	public void setIdIngrediente(Integer idIngrediente) {
		this.idIngrediente = idIngrediente;
	}

	@Override
	public String toString() {
		return "intoleranciasIngrediente [idIntolerancia=" + idIntolerancia + ", idIngrediente=" + idIngrediente + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idIngrediente == null) ? 0 : idIngrediente.hashCode());
		result = prime * result + ((idIntolerancia == null) ? 0 : idIntolerancia.hashCode());
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
		intoleranciasIngrediente other = (intoleranciasIngrediente) obj;
		if (idIngrediente == null) {
			if (other.idIngrediente != null)
				return false;
		} else if (!idIngrediente.equals(other.idIngrediente))
			return false;
		if (idIntolerancia == null) {
			if (other.idIntolerancia != null)
				return false;
		} else if (!idIntolerancia.equals(other.idIntolerancia))
			return false;
		return true;
	}
	
	
	
	
	
}
