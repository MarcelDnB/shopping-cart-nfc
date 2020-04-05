package types;

public class ingrediente {

	
	private String nombreIngrediente;
	private Integer idIntolerancia;
	
	public ingrediente() {
		this.nombreIngrediente = null;
		this.idIntolerancia = null;
	}
	
	public ingrediente(String nombreIngrediente, Integer idIntolerancia) {
		super();
		this.nombreIngrediente = nombreIngrediente;
		this.idIntolerancia = idIntolerancia;
	}

	public String getNombreIngrediente() {
		return nombreIngrediente;
	}

	public void setNombreIngrediente(String nombreIngrediente) {
		this.nombreIngrediente = nombreIngrediente;
	}

	public Integer getIdIntolerancia() {
		return idIntolerancia;
	}

	public void setIdIntolerancia(Integer idIntolerancia) {
		this.idIntolerancia = idIntolerancia;
	}

	@Override
	public String toString() {
		return "ingrediente [nombreIngrediente=" + nombreIngrediente + ", idIntolerancia=" + idIntolerancia + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idIntolerancia == null) ? 0 : idIntolerancia.hashCode());
		result = prime * result + ((nombreIngrediente == null) ? 0 : nombreIngrediente.hashCode());
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
		ingrediente other = (ingrediente) obj;
		if (idIntolerancia == null) {
			if (other.idIntolerancia != null)
				return false;
		} else if (!idIntolerancia.equals(other.idIntolerancia))
			return false;
		if (nombreIngrediente == null) {
			if (other.nombreIngrediente != null)
				return false;
		} else if (!nombreIngrediente.equals(other.nombreIngrediente))
			return false;
		return true;
	}
	
	
	
}
