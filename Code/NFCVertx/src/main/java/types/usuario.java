package types;

public class usuario {
	
	
	private Integer idComercio;
	
	public usuario() {
		
	}

	public usuario(Integer idComercio) {
		super();
		this.idComercio = idComercio;
	}

	
	
	public Integer getIdComercio() {
		return idComercio;
	}

	public void setIdComercio(Integer idComercio) {
		this.idComercio = idComercio;
	}

	@Override
	public String toString() {
		return "usuario [idComercio=" + idComercio + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idComercio == null) ? 0 : idComercio.hashCode());
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
		usuario other = (usuario) obj;
		if (idComercio == null) {
			if (other.idComercio != null)
				return false;
		} else if (!idComercio.equals(other.idComercio))
			return false;
		return true;
	}
	
	
	
}
