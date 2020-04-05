package types;

public class productosUsuario {

	private int IdProductosUsuario;
	private int IdProducto;
	private int IdUsuario;
	
	public productosUsuario() {
		super();
	}

	public productosUsuario(int idProductosUsuario, int idProducto, int idUsuario) {
		super();
		IdProductosUsuario = idProductosUsuario;
		IdProducto = idProducto;
		IdUsuario = idUsuario;
	}

	public int getIdProductosUsuario() {
		return IdProductosUsuario;
	}

	public void setIdProductosUsuario(int idProductosUsuario) {
		IdProductosUsuario = idProductosUsuario;
	}

	public int getIdProducto() {
		return IdProducto;
	}

	public void setIdProducto(int idProducto) {
		IdProducto = idProducto;
	}

	public int getIdUsuario() {
		return IdUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		IdUsuario = idUsuario;
	}

	@Override
	public String toString() {
		return "productosUsuario [IdProductosUsuario=" + IdProductosUsuario + ", IdProducto=" + IdProducto
				+ ", IdUsuario=" + IdUsuario + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + IdProducto;
		result = prime * result + IdProductosUsuario;
		result = prime * result + IdUsuario;
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
		productosUsuario other = (productosUsuario) obj;
		if (IdProducto != other.IdProducto)
			return false;
		if (IdProductosUsuario != other.IdProductosUsuario)
			return false;
		if (IdUsuario != other.IdUsuario)
			return false;
		return true;
	}

	
	
	
	
}
