package types;

public class ingredientesProducto {

	
	private Integer idIngrediente;
	private Integer idProducto;
	public ingredientesProducto(Integer idIngrediente, Integer idProducto) {
		super();
		this.idIngrediente = idIngrediente;
		this.idProducto = idProducto;
	}
	public ingredientesProducto() {
		
	}
	public Integer getIdIngrediente() {
		return idIngrediente;
	}
	public void setIdIngrediente(Integer idIngrediente) {
		this.idIngrediente = idIngrediente;
	}
	public Integer getIdProducto() {
		return idProducto;
	}
	public void setIdProducto(Integer idProducto) {
		this.idProducto = idProducto;
	}
	@Override
	public String toString() {
		return "ingredientesProducto [idIngrediente=" + idIngrediente + ", idProducto=" + idProducto + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idIngrediente == null) ? 0 : idIngrediente.hashCode());
		result = prime * result + ((idProducto == null) ? 0 : idProducto.hashCode());
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
		ingredientesProducto other = (ingredientesProducto) obj;
		if (idIngrediente == null) {
			if (other.idIngrediente != null)
				return false;
		} else if (!idIngrediente.equals(other.idIngrediente))
			return false;
		if (idProducto == null) {
			if (other.idProducto != null)
				return false;
		} else if (!idProducto.equals(other.idProducto))
			return false;
		return true;
	}
	
	
}
