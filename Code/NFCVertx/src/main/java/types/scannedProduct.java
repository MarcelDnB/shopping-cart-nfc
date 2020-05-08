package types;

import java.util.List;

public class scannedProduct {
	
	
	
	private List<Integer> idIntolerancia;
	
	public scannedProduct(List<Integer> idIntolerancia) {
		super();
		this.idIntolerancia = idIntolerancia;
	}

	public List<Integer> getIdIntolerancia() {
		return idIntolerancia;
	}

	public void setIdIntolerancia(List<Integer> idIntolerancia) {
		this.idIntolerancia = idIntolerancia;
	}

	@Override
	public String toString() {
		return "scannedProduct [idIntolerancia=" + idIntolerancia + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		scannedProduct other = (scannedProduct) obj;
		if (idIntolerancia == null) {
			if (other.idIntolerancia != null)
				return false;
		} else if (!idIntolerancia.equals(other.idIntolerancia))
			return false;
		return true;
	}




	
	
	
	
}
