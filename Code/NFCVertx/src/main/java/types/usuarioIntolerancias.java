package types;

import java.util.List;

public class usuarioIntolerancias {
	
	private List<Integer> intolerancias;

	
	public usuarioIntolerancias() {
		
	}
	public usuarioIntolerancias(List<Integer> intolerancias) {
		super();
		this.intolerancias = intolerancias;
	}
	public List<Integer> getIntolerancias() {
		return intolerancias;
	}
	public void setIntolerancias(List<Integer> intolerancias) {
		this.intolerancias = intolerancias;
	}
	@Override
	public String toString() {
		return "usuarioIntolerancias [intolerancias=" + intolerancias + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		if (intolerancias == null) {
			if (other.intolerancias != null)
				return false;
		} else if (!intolerancias.equals(other.intolerancias))
			return false;
		return true;
	}
	
	
	
	
	
	
	
	
}
