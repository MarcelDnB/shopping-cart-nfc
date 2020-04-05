package types;

import java.util.List;

public class usuarioIntolerancias {
	
	private List<Integer> intolerancias;
	private Integer idComercio;
	
	public usuarioIntolerancias() { 
		
	}
	
	public usuarioIntolerancias(List<Integer> intolerancias) {
		super();
		this.intolerancias = intolerancias;
	}
	
	
	public usuarioIntolerancias(List<Integer> intolerancias, Integer idComercio) {
		super();
		this.intolerancias = intolerancias;
		this.idComercio = idComercio;
	}
	
	public usuarioIntolerancias(Integer comercio) {
		super();
		this.idComercio = comercio;
	}
	
	
	public List<Integer> getIntolerancias() {
		return intolerancias;
	}
	public void setIntolerancias(List<Integer> intolerancias) {
		this.intolerancias = intolerancias;
	}
	public Integer getIdComercio() {
		return idComercio;
	}
	public void setIdComercio(Integer idComercio) {
		this.idComercio = idComercio;
	}
	@Override
	public String toString() {
		return "usuarioIntolerancias [intolerancias=" + intolerancias + ", idComercio=" + idComercio + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idComercio == null) ? 0 : idComercio.hashCode());
		result = prime * result + ((intolerancias == null) ? 0 : intolerancias.hashCode());
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
		usuarioIntolerancias other = (usuarioIntolerancias) obj;
		if (idComercio == null) {
			if (other.idComercio != null)
				return false;
		} else if (!idComercio.equals(other.idComercio))
			return false;
		if (intolerancias == null) {
			if (other.intolerancias != null)
				return false;
		} else if (!intolerancias.equals(other.intolerancias))
			return false;
		return true;
	}
	
	
	
	
	
	
	
	
}
