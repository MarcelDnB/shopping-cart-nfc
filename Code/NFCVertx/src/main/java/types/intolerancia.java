package types;

public class intolerancia {

	private Integer id;
	private String nombreIntolerancia = null;

	public intolerancia() {
		
	}
	
	public intolerancia(String nombreIntolerancia, Integer id) {
		super();
		this.nombreIntolerancia = nombreIntolerancia;
		this.id = id;
	}

	public String getNombreIntolerancia() {
		return nombreIntolerancia;
	}
	public Integer getId() {
		return this.id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public void setNombreIntolerancia(String nombreIntolerancia) throws Exception {
		if(nombreIntolerancia.length()<=45) {
			this.nombreIntolerancia = nombreIntolerancia;
		}else {
			throw new Exception("El nombre de la intolerancia tiene que contener como maximo 45 caracteres");
		}
		
	}

	@Override
	public String toString() {
		return "intolerancia [id=" + id + ", nombreIntolerancia=" + nombreIntolerancia + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (nombreIntolerancia == null) {
			if (other.nombreIntolerancia != null)
				return false;
		} else if (!nombreIntolerancia.equals(other.nombreIntolerancia))
			return false;
		return true;
	}


	
}
