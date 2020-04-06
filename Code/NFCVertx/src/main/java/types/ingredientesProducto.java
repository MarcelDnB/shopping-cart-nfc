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
	public void setIdIngrediente(Integer idIngrediente) throws Exception {
		if(idIngrediente<=2147483647) {
			this.idIngrediente = idIngrediente;
		}else {
			throw new Exception("El maximo numero para el ID de ingrediente es 2147483647");
		}
		
	}
	public Integer getIdProducto() {
		return idProducto;
	}
	public void setIdProducto(Integer idProducto) throws Exception {
		if(idProducto<=2147483647) {
			this.idProducto = idProducto;
		}else {
			throw new Exception("El maximo numero para el ID de producto es 2147483647");
		}
		
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
