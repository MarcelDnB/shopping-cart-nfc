package types;

import java.util.regex.Pattern;

public class comercio {

	private String nombreComercio;
	private Long telefono;
	private String CIF;

	public comercio() {
		super();
		this.nombreComercio = null;
		this.telefono = null;
		this.CIF = null;
	}
	
	public comercio(String nombreComercio, Long telefono, String CIF) {
		super();
		this.nombreComercio = nombreComercio;
		this.telefono = telefono;
		this.CIF = CIF;
	}
	public String getNombreComercio() {
		return nombreComercio;
	}
	public void setNombreComercio(String nombreComercio) throws Exception {
		if(nombreComercio.length()<=45) {
			this.nombreComercio = nombreComercio;
		}else {
			throw new Exception("El nombre del comercio no puede tener mas de 45 caracteres");
		}
		
	}
	public Long getTelefono() {
		return telefono;
	}
	public void setTelefono(Long telefono) throws Exception {
		if(String.valueOf(telefono).length() == 9) {
			this.telefono = telefono;
		}else {
			throw new Exception("El numero de telefono debe contener 9 digitos");
		}
		
		
	}
	public String getCIF() {
		return CIF;
	}
	public void setCIF(String cIF) throws Exception {
		if((cIF.length() == 9) && !Pattern.matches("-?\\d+(\\.\\d+)?", (Character.toString(cIF.charAt(0)))))  {
			CIF = cIF;
		}else {
			throw new Exception("El CIF debe contener un caracter y 8 digitos");
		}
		
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((CIF == null) ? 0 : CIF.hashCode());
		result = prime * result + ((nombreComercio == null) ? 0 : nombreComercio.hashCode());
		result = prime * result + ((telefono == null) ? 0 : telefono.hashCode());
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
		comercio other = (comercio) obj;
		if (CIF == null) {
			if (other.CIF != null)
				return false;
		} else if (!CIF.equals(other.CIF))
			return false;
		if (nombreComercio == null) {
			if (other.nombreComercio != null)
				return false;
		} else if (!nombreComercio.equals(other.nombreComercio))
			return false;
		if (telefono == null) {
			if (other.telefono != null)
				return false;
		} else if (!telefono.equals(other.telefono))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "comercio [nombreComercio=" + nombreComercio + ", telefono=" + telefono + ", CIF=" + CIF + "]";
	}
	
	
}
